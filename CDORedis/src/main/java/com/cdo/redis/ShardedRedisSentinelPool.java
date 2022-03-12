package com.cdo.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.regex.Pattern;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Hashing;
import redis.clients.util.Pool;
/**
 * @author KenelLiu
 *
 */
 class ShardedRedisSentinelPool extends Pool<ShardedJedis> {

	public static final int MAX_RETRY_SENTINEL = 10;
	
	protected final Log log = LogFactory.getLog(getClass().getName());
	
	protected GenericObjectPoolConfig poolConfig;

    private int sentinelRetry = 0;
    
    protected int database = Protocol.DEFAULT_DATABASE;

    protected Set<MasterListener> masterListeners = new HashSet<MasterListener>();
    
    private volatile List<ShardedMaster> currentHostMasters;
    private List<ShardedMaster> nonSentinelMasters;
//    private String partitionKey;
    
    public ShardedRedisSentinelPool(List<SentinelRedisInfo>  shardedJedisSentinelList,final GenericObjectPoolConfig poolConfig) {
    		
    	    this.nonSentinelMasters=new ArrayList<ShardedMaster>();    	    

    		List<ShardedMaster> sentinelMasters=new ArrayList<ShardedMaster>();
    		for(int i=0;i<shardedJedisSentinelList.size();i++){
    			SentinelRedisInfo shardSentinelInfo=shardedJedisSentinelList.get(i);
    			
    			ShardedMaster shardMaster=new ShardedMaster();    			
    			shardMaster.setConnectTimeOut(shardSentinelInfo.getConnectTimeOut());
    			shardMaster.setSoTimeout(shardSentinelInfo.getSoTimeout());
    			shardMaster.setPassword(shardSentinelInfo.getPassword());
    			shardMaster.setMasterName(shardSentinelInfo.getMaster());
    			shardMaster.setWeight(shardSentinelInfo.getWeight());
    			
    			if(shardSentinelInfo.isSentinel()){     				
    				shardMaster.setSentinelSet(shardSentinelInfo.getSentinelSet());
    				//存储sentinel 监控的master 名称
    				sentinelMasters.add(shardMaster);
    			}else{
    				//存储单节点shard
    				shardMaster.setHostPort(toHostAndPort(Arrays.asList(shardSentinelInfo.getHostAndPort().split(":"))));
    				nonSentinelMasters.add(shardMaster); 
    			}
    		}
    		//赋值
    		this.poolConfig = poolConfig;
//    		this.partitionKey=partitionKey;
    		
    		sentinelMasters= initSentinels(sentinelMasters);    		
    		sentinelMasters.addAll(nonSentinelMasters);    		
    		initPool(sentinelMasters);
        }
    

    public void destroy() {
		for (MasterListener m : masterListeners) {
		    m.shutdown();
		}
		
		super.destroy();
    }

    public List<ShardedMaster> getCurrentHostMaster() {
    	return currentHostMasters;
    }
    
    public List<ShardedMaster> getNonSentinelMasters() {
		return nonSentinelMasters;
	}


	private void initPool(List<ShardedMaster> masters) {
    	if (!equals(currentHostMasters, masters)) {
    		StringBuffer sb = new StringBuffer();
    		for (ShardedMaster master : masters) {
    			sb.append(master.getHostPort());
    			sb.append(" ");
    		}
    		log.info("Created ShardedJedisPool to master at [" + sb.toString() + "]");
    		List<JedisShardInfo> shardMasters = makeShardInfoList(masters);
    		initPool(poolConfig, new ShardedJedisFactory(shardMasters, Hashing.MURMUR_HASH,ShardedJedis.DEFAULT_KEY_TAG_PATTERN));    		
    		currentHostMasters = masters;
    		
    		//分区 再分shard
//    		ShardedRedisPool.addPool(this.partitionKey,this);
    		
    	}
    }
    
    private boolean equals(List<ShardedMaster> currentShardMasters, List<ShardedMaster> shardMasters) {
    	if (currentShardMasters != null && shardMasters != null) {
    		if (currentShardMasters.size() == shardMasters.size()) {
    			for (int i = 0; i < currentShardMasters.size(); i++) {
    				if (!currentShardMasters.get(i).getHostPort().equals(shardMasters.get(i).getHostPort())) return false;
    			}
    			return true;
    		}
    	}
		return false;
	}

	private List<JedisShardInfo> makeShardInfoList(List<ShardedMaster> masters) {
		List<JedisShardInfo> shardMasters = new ArrayList<JedisShardInfo>();
		for (ShardedMaster master : masters) {
			JedisShardInfo jedisShardInfo = new JedisShardInfo(master.getHostPort().getHost(),
					master.getMasterName(),master.getHostPort().getPort(),master.getConnectTimeOut(),master.getWeight());			
			//(String host, String name, int port, int timeout, int weight) 
			//jedisShardInfo.setConnectionTimeout(master.getConnectTimeOut());
			jedisShardInfo.setSoTimeout(master.getSoTimeout());
			jedisShardInfo.setPassword(master.getPassword());			
			
			shardMasters.add(jedisShardInfo);
		}
		return shardMasters;
	}

	private List<ShardedMaster> initSentinels(final List<ShardedMaster> sentinelMasters) {
		
    	//保存能够通过sentinel 找到master的 ip:port机器					    		
    	List<ShardedMaster> shardMasters = new ArrayList<ShardedMaster>();    	
	    log.info("Trying to find all master from available Sentinels...");
	    //根据sentinel 配置  查找master 机器的ip和port
	    for (ShardedMaster master: sentinelMasters) {
	    	HostAndPort masterHostPort = null;
	    	boolean fetched = false;
	    	sentinelRetry=0;
	    	Set<String> sentinels=master.getSentinelSet();
	    
	    	while (!fetched && sentinelRetry < MAX_RETRY_SENTINEL) {
	    		for (String sentinel : sentinels) {
					final HostAndPort senHostPort = toHostAndPort(Arrays.asList(sentinel.split(":")));
					log.info("Connecting to Sentinel " + senHostPort);
					Jedis jedis=null;
					try {
						jedis = new Jedis(senHostPort.getHost(), senHostPort.getPort());
						
				        List<String> masterAddr = jedis.sentinelGetMasterAddrByName(master.getMasterName());
				      
				        if (masterAddr == null || masterAddr.size() != 2) {
				          log.warn("Can not get master addr, master name: " + master.getMasterName() + ". Sentinel: " + senHostPort
				              + ".");
				  			if(jedis!=null)							
				  				jedis.close();
				          continue;
				        }
				        //通过sentinel获取到master ip:port值	
			    		masterHostPort = toHostAndPort(masterAddr);
			    		master.setHostPort(masterHostPort);
			    		shardMasters.add(master);				    					    		
			    		fetched=true;
			    		break;				    		
					} catch (JedisConnectionException e) {
					    log.warn("Cannot connect to sentinel running @ " + senHostPort + ". Trying next one.");
					}finally{
						if(jedis!=null){							
							jedis.close();
						}
					}
		    	}
		    	//若没有获取到master的ip:port值,则重新循环
		    	if (null == masterHostPort) {
		    		try {
						log.error("All sentinels down, cannot determine where is "
							+ master.getMasterName() + " master is running... sleeping 1500ms, Will try again.");
						Thread.sleep(1500);
				    } catch (InterruptedException e) {
				    	e.printStackTrace();
				    }
		    		fetched = false;
		    		sentinelRetry++;
		    	}
	    	}
	    	
	    	// Try MAX_RETRY_SENTINEL times.
	    	if (!fetched && sentinelRetry >= MAX_RETRY_SENTINEL) {
	    		log.error("master["+master.getMasterName()+"] All sentinels down and try " + MAX_RETRY_SENTINEL + " times, Abort.");
	    		throw new JedisConnectionException("Cannot connect all sentinels, Abort.");
	    	}
	    }
	    
	    // All shards master must been accessed.
	    if (sentinelMasters.size() != 0 && sentinelMasters.size() == shardMasters.size()) {
	    	
	    	log.info("Starting Sentinel listeners...");
	    	for (ShardedMaster master: sentinelMasters){
				for (String sentinel : master.getSentinelSet()) {
				    final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel.split(":")));			    
				    MasterListener masterListener = new MasterListener(shardMasters, hap.getHost(), hap.getPort());
				    masterListener.setDaemon(true);			     
				    masterListeners.add(masterListener);
				    masterListener.start();
				}
			}
	    }
	    
		return shardMasters;
    }

    private HostAndPort toHostAndPort(List<String> getMasterAddrByNameResult) {
    	String host = getMasterAddrByNameResult.get(0);
    	int port = Integer.parseInt(getMasterAddrByNameResult.get(1));
    	return new HostAndPort(host, port);
    }

    protected class MasterListener extends Thread {

		protected List<ShardedMaster> masterSentinel;
		protected String host;
		protected int port;
		protected long subscribeRetryWaitTimeMillis = 5000;
		protected Jedis jedis;
		protected AtomicBoolean running = new AtomicBoolean(false);
		private List<String> mastersName;

		public MasterListener(List<ShardedMaster> masterSentinel, String host, int port) {
		    this.masterSentinel = masterSentinel;
		    this.host = host;
		    this.port = port;
		    mastersName=new ArrayList<String>();
		    for(int i=0;i<masterSentinel.size();i++){
		    	mastersName.add(masterSentinel.get(i).getMasterName());
		    }
		}
	
		public MasterListener(List<ShardedMaster> masters, String host, int port,
			long subscribeRetryWaitTimeMillis) {
		    this(masters, host, port);
		    this.subscribeRetryWaitTimeMillis = subscribeRetryWaitTimeMillis;
		}
	
		public void run() {
	
		    running.set(true);
	
		    while (running.get()) {
	
			jedis = new Jedis(host, port);
	
			try {
			    jedis.subscribe(new JedisPubSub() {
					@Override
					public void onMessage(String channel, String message) {
					    log.info("Sentinel " + host + ":" + port + " published: " + message + ".");
		
					    String[] switchMasterMsg = message.split(" ");
		
					    if (switchMasterMsg.length > 3) {
					    	//mastersName 与 masterSentinel 下标值对应
					    	int index = mastersName.indexOf(switchMasterMsg[0]);
						    if (index >= 0) {						    							    	
						    	HostAndPort newHost = toHostAndPort(Arrays.asList(switchMasterMsg[3], switchMasterMsg[4]));
						    	List<ShardedMaster> newHostMasters = new ArrayList<ShardedMaster>();
						    	for (int i = 0; i < masterSentinel.size(); i++) {
						    		newHostMasters.add(null);
						    	}
						    	//复制有主从的机器监控的
						    	Collections.copy(newHostMasters, currentHostMasters.subList(0, masterSentinel.size()-1));						    	
						    	
						    	ShardedMaster newHostMaster=masterSentinel.get(index);
						    	newHostMaster.setHostPort(newHost);						    	
						    	newHostMasters.set(index, newHostMaster);	
						    	newHostMasters.addAll(nonSentinelMasters);
						    	initPool(newHostMasters);
						    } else {
						    	StringBuffer sb = new StringBuffer();
						    	for (ShardedMaster master : masterSentinel) {
						    		sb.append(master.getMasterName());
						    		sb.append(",");
						    	}
							    log.info("Ignoring message on +switch-master for master name "
								    + switchMasterMsg[0]
								    + ", our monitor master name are ["
								    + sb + "]");
							}
		
					    } else {
							log.error("Invalid message received on Sentinel "
								+ host
								+ ":"
								+ port
								+ " on channel +switch-master: "
								+ message);
					    }
					}
			    }, "+switch-master");
	
			} catch (JedisConnectionException e) {
	
			    if (running.get()) {
					log.error("Lost connection to Sentinel at " + host+ ":" + port+ ". Sleeping 5000ms and retrying.");
					try {
					    Thread.sleep(subscribeRetryWaitTimeMillis);
					} catch (InterruptedException e1) {
					    e1.printStackTrace();
					}
			    } else {
					log.info("Unsubscribing from Sentinel at " + host + ":"+ port);
			    }
			}
		   }
		}
	
		public void shutdown() {
		    try {
				log.info("Shutting down listener on " + host + ":" + port);
				running.set(false);
				// This isn't good, the Jedis object is not thread safe
				if(jedis!=null)
					jedis.disconnect();
		    } catch (Exception e) {
		    	log.error("Caught exception while shutting down: " + e.getMessage());
		    }
		}
    }
    
 
    //--------------------------------------参见shardedJedisPool-----------------------------//   
    @Override
    public ShardedJedis getResource() {
      ShardedJedis jedis = super.getResource();
      jedis.setDataSource(this);
      return jedis;
    }

    @Override
    public void returnBrokenResource(final ShardedJedis resource) {
      if (resource != null) {
        returnBrokenResourceObject(resource);
      }
    }
    
    @Override
    public void returnResource(final ShardedJedis resource) {
      if (resource != null) {
        resource.resetState();
        returnResourceObject(resource);
      }
    }
    /**
     * PoolableObjectFactory custom impl.
     */
    private static class ShardedJedisFactory implements PooledObjectFactory<ShardedJedis> {
      private List<JedisShardInfo> shards;
      private Hashing algo;
      private Pattern keyTagPattern;

      public ShardedJedisFactory(List<JedisShardInfo> shards, Hashing algo, Pattern keyTagPattern) {
        this.shards = shards;
        this.algo = algo;
        this.keyTagPattern = keyTagPattern;
      }

      @Override
      public PooledObject<ShardedJedis> makeObject() throws Exception {
        ShardedJedis jedis = new ShardedJedis(shards, algo, keyTagPattern);
        return new DefaultPooledObject<ShardedJedis>(jedis);
      }

      @Override
      public void destroyObject(PooledObject<ShardedJedis> pooledShardedJedis) throws Exception {
        final ShardedJedis shardedJedis = pooledShardedJedis.getObject();
        for (Jedis jedis : shardedJedis.getAllShards()) {
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
      public boolean validateObject(PooledObject<ShardedJedis> pooledShardedJedis) {
        try {
          ShardedJedis jedis = pooledShardedJedis.getObject();
          for (Jedis shard : jedis.getAllShards()) {
            if (!shard.ping().equals("PONG")) {
              return false;
            }
          }
          return true;
        } catch (Exception ex) {
          return false;
        }
      }

      @Override
      public void activateObject(PooledObject<ShardedJedis> p) throws Exception {

      }

      @Override
      public void passivateObject(PooledObject<ShardedJedis> p) throws Exception {

      }
    }           
}