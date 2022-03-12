package com.cdo.redis;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.InvalidURIException;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.JedisURIHelper;
import redis.clients.util.Pool;

class RedisSentinelPool extends Pool<Jedis>{
	  protected JedisPoolConfig poolConfig;

	  protected int connectionTimeout = Protocol.DEFAULT_TIMEOUT;
	  protected int soTimeout = Protocol.DEFAULT_TIMEOUT;

	  protected String password;

	  protected int database = Protocol.DEFAULT_DATABASE;

	  protected String clientName;

	  protected Set<MasterListener> masterListeners = new HashSet<MasterListener>();

	  protected Log log = LogFactory.getLog(getClass().getName());

	  private volatile JedisFactory factory;
	  private volatile HostAndPort currentHostMaster;

	
	 public RedisSentinelPool(SentinelRedisInfo sentinelRedisInfo,final JedisPoolConfig poolConfig) {
	    	this.poolConfig=poolConfig;
	    	this.connectionTimeout=sentinelRedisInfo.getConnectTimeOut();
	    	this.soTimeout=sentinelRedisInfo.getSoTimeout();
	    	this.password=sentinelRedisInfo.getPassword();
	    	HostAndPort master=null; 
	    	if(sentinelRedisInfo.isSentinel()){
	    		master = initSentinels(sentinelRedisInfo.getSentinelSet(), sentinelRedisInfo.getMaster());
	    	}else{
	    		master=toHostAndPort(Arrays.asList(sentinelRedisInfo.getHostAndPort().split(":")));
	    	}
	    	initPool(master);
	    }
	 
	  public void destroy() {
	    for (MasterListener m : masterListeners) {
	      m.shutdown();
	    }

	    super.destroy();
	  }

	  public HostAndPort getCurrentHostMaster() {
	    return currentHostMaster;
	  }

	  private void initPool(HostAndPort master) {

	   if (!equals(currentHostMaster,master)) {
	      currentHostMaster = master;
	      if (factory == null) {
	        factory = new JedisFactory(master.getHost(), master.getPort(), connectionTimeout,
	            soTimeout, password, database, clientName, false, null, null, null);
	        initPool(poolConfig, factory);
	      } else {
	        factory.setHostAndPort(currentHostMaster);
	        // although we clear the pool, we still have to check the
	        // returned object
	        // in getResource, this call only clears idle instances, not
	        // borrowed instances
	        internalPool.clear();
	      }

	      log.info("Created JedisPool to master at " + master);
	    }
	  }
	    private boolean equals(HostAndPort currentHostMaster,HostAndPort master) {
	    	if (currentHostMaster != null && master != null) {
	    		if (!currentHostMaster.equals(master)) return false;
	    		return true;
	    	}
			return false;
		}
	  
	  private HostAndPort initSentinels(Set<String> sentinels, final String masterName) {

	    HostAndPort master = null;
	    boolean sentinelAvailable = false;

	    log.info("Trying to find master from available Sentinels...");

	    for (String sentinel : sentinels) {
	      final HostAndPort hap = HostAndPort.parseString(sentinel);

	      log.info("Connecting to Sentinel " + hap);

	      Jedis jedis = null;
	      try {
	        jedis = new Jedis(hap.getHost(), hap.getPort());

	        List<String> masterAddr = jedis.sentinelGetMasterAddrByName(masterName);

	        // connected to sentinel...
	        sentinelAvailable = true;

	        if (masterAddr == null || masterAddr.size() != 2) {
	          log.warn("Can not get master addr, master name: " + masterName + ". Sentinel: " + hap
	              + ".");
	          continue;
	        }

	        master = toHostAndPort(masterAddr);
	        log.info("Found Redis master at " + master);
	        break;
	      } catch (JedisException e) {
	        // resolves #1036, it should handle JedisException there's another chance
	        // of raising JedisDataException
	        log.warn("Cannot get master address from sentinel running @ " + hap + ". Reason: " + e
	            + ". Trying next one.");
	      } finally {
	        if (jedis != null) {
	          jedis.close();
	        }
	      }
	    }

	    if (master == null) {
	      if (sentinelAvailable) {
	        // can connect to sentinel, but master name seems to not
	        // monitored
	        throw new JedisException("Can connect to sentinel, but " + masterName
	            + " seems to be not monitored...");
	      } else {
	        throw new JedisConnectionException("All sentinels down, cannot determine where is "
	            + masterName + " master is running...");
	      }
	    }

	    log.info("Redis master running at " + master + ", starting Sentinel listeners...");

	    for (String sentinel : sentinels) {
	      final HostAndPort hap = HostAndPort.parseString(sentinel);
	      MasterListener masterListener = new MasterListener(masterName, hap.getHost(), hap.getPort());
	      // whether MasterListener threads are alive or not, process can be stopped
	      masterListener.setDaemon(true);
	      masterListeners.add(masterListener);
	      masterListener.start();
	    }

	    return master;
	  }

	  private HostAndPort toHostAndPort(List<String> getMasterAddrByNameResult) {
	    String host = getMasterAddrByNameResult.get(0);
	    int port = Integer.parseInt(getMasterAddrByNameResult.get(1));

	    return new HostAndPort(host, port);
	  }

	  @Override
	  public Jedis getResource() {
	    while (true) {
	      Jedis jedis = super.getResource();
	      jedis.setDataSource(this);

	      // get a reference because it can change concurrently
	      final HostAndPort master = currentHostMaster;
	      final HostAndPort connection = new HostAndPort(jedis.getClient().getHost(), jedis.getClient()
	          .getPort());

	      if (master.equals(connection)) {
	        // connected to the correct master
	        return jedis;
	      } else {
	        returnBrokenResource(jedis);
	      }
	    }
	  }

	  /**
	   * @deprecated starting from Jedis 3.0 this method will not be exposed. Resource cleanup should be
	   *             done using @see {@link redis.clients.jedis.Jedis#close()}
	   */
	  @Override
	  @Deprecated
	  public void returnBrokenResource(final Jedis resource) {
	    if (resource != null) {
	      returnBrokenResourceObject(resource);
	    }
	  }

	  /**
	   * @deprecated starting from Jedis 3.0 this method will not be exposed. Resource cleanup should be
	   *             done using @see {@link redis.clients.jedis.Jedis#close()}
	   */
	  @Override
	  @Deprecated
	  public void returnResource(final Jedis resource) {
	    if (resource != null) {
	      resource.resetState();
	      returnResourceObject(resource);
	    }
	  }

	  protected class MasterListener extends Thread {

	    protected String masterName;
	    protected String host;
	    protected int port;
	    protected long subscribeRetryWaitTimeMillis = 5000;
	    protected volatile Jedis j;
	    protected AtomicBoolean running = new AtomicBoolean(false);

	    protected MasterListener() {
	    }

	    public MasterListener(String masterName, String host, int port) {
	      super(String.format("MasterListener-%s-[%s:%d]", masterName, host, port));
	      this.masterName = masterName;
	      this.host = host;
	      this.port = port;
	    }

	    public MasterListener(String masterName, String host, int port,
	        long subscribeRetryWaitTimeMillis) {
	      this(masterName, host, port);
	      this.subscribeRetryWaitTimeMillis = subscribeRetryWaitTimeMillis;
	    }

	    @Override
	    public void run() {

	      running.set(true);

	      while (running.get()) {

	        j = new Jedis(host, port);

	        try {
	          // double check that it is not being shutdown
	          if (!running.get()) {
	            break;
	          }

	          j.subscribe(new JedisPubSub() {
	            @Override
	            public void onMessage(String channel, String message) {
	              log.info("Sentinel " + host + ":" + port + " published: " + message + ".");

	              String[] switchMasterMsg = message.split(" ");

	              if (switchMasterMsg.length > 3) {

	                if (masterName.equals(switchMasterMsg[0])) {
	                  initPool(toHostAndPort(Arrays.asList(switchMasterMsg[3], switchMasterMsg[4])));
	                } else {
	                  log.info("Ignoring message on +switch-master for master name "
	                      + switchMasterMsg[0] + ", our master name is " + masterName);
	                }

	              } else {
	                log.error("Invalid message received on Sentinel " + host + ":" + port
	                    + " on channel +switch-master: " + message);
	              }
	            }
	          }, "+switch-master");

	        } catch (JedisConnectionException e) {

	          if (running.get()) {
	            log.error("Lost connection to Sentinel at " + host + ":" + port
	                + ". Sleeping 5000ms and retrying.", e);
	            try {
	              Thread.sleep(subscribeRetryWaitTimeMillis);
	            } catch (InterruptedException e1) {
	              log.error("Sleep interrupted: ", e1);
	            }
	          } else {
	            log.info("Unsubscribing from Sentinel at " + host + ":" + port);
	          }
	        } finally {
	          j.close();
	        }
	      }
	    }

	    public void shutdown() {
	      try {
	        log.info("Shutting down listener on " + host + ":" + port);
	        running.set(false);
	        // This isn't good, the Jedis object is not thread safe
	        if (j != null) {
	          j.disconnect();
	        }
	      } catch (Exception e) {
	        log.error("Caught exception while shutting down: ", e);
	      }
	    }
	  }
	  
	  
	  class JedisFactory implements PooledObjectFactory<Jedis> {
		  private final AtomicReference<HostAndPort> hostAndPort = new AtomicReference<HostAndPort>();
		  private final int connectionTimeout;
		  private final int soTimeout;
		  private final String password;
		  private final int database;
		  private final String clientName;
		  private final boolean ssl;
		  private final SSLSocketFactory sslSocketFactory;
		  private SSLParameters sslParameters;
		  private HostnameVerifier hostnameVerifier;

		  public JedisFactory(final String host, final int port, final int connectionTimeout,
		      final int soTimeout, final String password, final int database, final String clientName,
		      final boolean ssl, final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters,
		      final HostnameVerifier hostnameVerifier) {
		    this.hostAndPort.set(new HostAndPort(host, port));
		    this.connectionTimeout = connectionTimeout;
		    this.soTimeout = soTimeout;
		    this.password = password;
		    this.database = database;
		    this.clientName = clientName;
		    this.ssl = ssl;
		    this.sslSocketFactory = sslSocketFactory;
		    this.sslParameters = sslParameters;
		    this.hostnameVerifier = hostnameVerifier;
		  }

		  public JedisFactory(final URI uri, final int connectionTimeout, final int soTimeout,
		      final String clientName, final boolean ssl, final SSLSocketFactory sslSocketFactory,
		      final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier) {
		    if (!JedisURIHelper.isValid(uri)) {
		      throw new InvalidURIException(String.format(
		        "Cannot open Redis connection due invalid URI. %s", uri.toString()));
		    }

		    this.hostAndPort.set(new HostAndPort(uri.getHost(), uri.getPort()));
		    this.connectionTimeout = connectionTimeout;
		    this.soTimeout = soTimeout;
		    this.password = JedisURIHelper.getPassword(uri);
		    this.database = JedisURIHelper.getDBIndex(uri);
		    this.clientName = clientName;
		    this.ssl = ssl;
		    this.sslSocketFactory = sslSocketFactory;
		    this.sslParameters = sslParameters;
		    this.hostnameVerifier = hostnameVerifier;
		  }

		  public void setHostAndPort(final HostAndPort hostAndPort) {
		    this.hostAndPort.set(hostAndPort);
		  }

		  @Override
		  public void activateObject(PooledObject<Jedis> pooledJedis) throws Exception {
		    final BinaryJedis jedis = pooledJedis.getObject();
		    if (jedis.getDB() != database) {
		      jedis.select(database);
		    }

		  }

		  @Override
		  public void destroyObject(PooledObject<Jedis> pooledJedis) throws Exception {
		    final BinaryJedis jedis = pooledJedis.getObject();
		    if (jedis.isConnected()) {
		      try {
		        try {
		          jedis.quit();
		        } catch (Exception e) {
		        }
		        jedis.disconnect();
		      } catch (Exception e) {

		      }
		    }

		  }

		  @Override
		  public PooledObject<Jedis> makeObject() throws Exception {
		    final HostAndPort hostAndPort = this.hostAndPort.get();
		    final Jedis jedis = new Jedis(hostAndPort.getHost(), hostAndPort.getPort(), connectionTimeout,
		        soTimeout, ssl, sslSocketFactory, sslParameters, hostnameVerifier);

		    try {
		      jedis.connect();
		      if (null != this.password) {
		        jedis.auth(this.password);
		      }
		      if (database != 0) {
		        jedis.select(database);
		      }
		      if (clientName != null) {
		        jedis.clientSetname(clientName);
		      }
		    } catch (JedisException je) {
		      jedis.close();
		      throw je;
		    }

		    return new DefaultPooledObject<Jedis>(jedis);

		  }

		  @Override
		  public void passivateObject(PooledObject<Jedis> pooledJedis) throws Exception {
		    // TODO maybe should select db 0? Not sure right now.
		  }

		  @Override
		  public boolean validateObject(PooledObject<Jedis> pooledJedis) {
		    final BinaryJedis jedis = pooledJedis.getObject();
		    try {
		      HostAndPort hostAndPort = this.hostAndPort.get();

		      String connectionHost = jedis.getClient().getHost();
		      int connectionPort = jedis.getClient().getPort();

		      return hostAndPort.getHost().equals(connectionHost)
		          && hostAndPort.getPort() == connectionPort && jedis.isConnected()
		          && jedis.ping().equals("PONG");
		    } catch (final Exception e) {
		      return false;
		    }
		  }
	  }
}
