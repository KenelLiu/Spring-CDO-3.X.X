package com.cdo.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cdo.redis.exception.RedisException;
import com.cdo.redis.config.RedisConfig;

import redis.clients.jedis.JedisPoolConfig;

public class ShardedRedisPool {
	private static Log logger=LogFactory.getLog(ShardedRedisPool.class);
	
//	static Map<String, ShardedRedisSentinelPool> shardedJedisPoolMap=Collections.synchronizedMap(new LinkedHashMap<String, ShardedRedisSentinelPool>());
//	static Map<String, ShardConfigSentinel> jedisShardSentinelMap=new LinkedHashMap<String, ShardConfigSentinel>();
	private static Lock lock = new ReentrantLock();// 锁  
	
	static JedisPoolConfig poolConfig=new JedisPoolConfig();	
	
	static  List<SentinelRedisInfo>  shardsSentinelList=new ArrayList<SentinelRedisInfo>();
	
	static ShardedRedisSentinelPool shardedRedisSentinelPool;	
	
	
	/**
	 * redis.shard.1={master:master,sentinel:[],conctionTimeout:timeout,soTimeout:soTimeout,password}
	 * redis.shard.2={master:master,sentinel:[],conctionTimeout:timeout,soTimeout:soTimeout,password}
	 * 
	 * redis.shard.4={master:master,sentinel:[],conctionTimeout:timeout,soTimeout:soTimeout,password}
	 * redis.shard.5={master:master,sentinel:[],conctionTimeout:timeout,soTimeout:soTimeout,password}
	 * .......
	 * redis.shard.9={master:master,sentinel:[],conctionTimeout:timeout,soTimeout:soTimeout,password}	
	 * redis.shard.10={master:master,sentinel:[],conctionTimeout:timeout,soTimeout:soTimeout,password}
	 */
	
	
static {
		//默认
		int defaultMaxIdle=Integer.parseInt(RedisConfig.getConfigValue("redis.maxIdle"));
		int defaultTotal=Integer.parseInt(RedisConfig.getConfigValue("redis.maxTotal"));
		long defaultMaxWaitMills=Long.parseLong(RedisConfig.getConfigValue("redis.maxWaitMillis"));
		boolean defaultTestOnBorrow=Boolean.parseBoolean(RedisConfig.getConfigValue("redis.testOnBorrow"));
		
		int defaultConnTimeout=Integer.parseInt(RedisConfig.getConfigValue("redis.connectionTimeout"));
		int defaultSoTimeout=Integer.parseInt(RedisConfig.getConfigValue("redis.soTimeout"));
		String defaultPassword=RedisConfig.getConfigValue("redis.password");
		int defualtWeight=1;
		String hostPort=null;
		//初始数据
		poolConfig.setMaxIdle(defaultMaxIdle);
		poolConfig.setMaxTotal(defaultTotal);
		poolConfig.setMaxWaitMillis(defaultMaxWaitMills);
		poolConfig.setTestOnBorrow(defaultTestOnBorrow);	
		
		Set<Entry<String, String>> entrySet=RedisConfig.entrySet();		
		for(Iterator<Entry<String, String>> it=entrySet.iterator();it.hasNext();){
			Entry<String,String> entry=it.next();
			String key=entry.getKey();			
			if(key.startsWith("redis.") && key.contains(".shard.")){								
				JSONObject redisServer;
				try {
					redisServer = new JSONObject(entry.getValue());
					//--config pool配置
					defaultMaxIdle=redisServer.isNull("maxIdle")?poolConfig.getMaxIdle():redisServer.getInt("maxIdle");
					defaultTotal=redisServer.isNull("maxTotal")?poolConfig.getMaxTotal():redisServer.getInt("maxTotal");
					defaultMaxWaitMills=redisServer.isNull("maxWaitMillis")?poolConfig.getMaxWaitMillis():redisServer.getInt("maxWaitMillis");
					defaultTestOnBorrow=redisServer.isNull("testOnBorrow")?poolConfig.getTestOnBorrow():redisServer.getBoolean("testOnBorrow");
					//--设置config pool值 
					poolConfig.setMaxIdle(defaultMaxIdle);
					poolConfig.setMaxTotal(defaultTotal);
					poolConfig.setMaxWaitMillis(defaultMaxWaitMills);
					poolConfig.setTestOnBorrow(defaultTestOnBorrow);	
					
					//---连接配置					
					defaultConnTimeout=redisServer.isNull("conctionTimeout")?defaultConnTimeout:redisServer.getInt("conctionTimeout");
					defaultSoTimeout=redisServer.isNull("soTimeout")?defaultSoTimeout:redisServer.getInt("soTimeout");
					defaultPassword=redisServer.isNull("password")?defaultPassword:redisServer.getString("password");
					defualtWeight=redisServer.isNull("weight")?defualtWeight:redisServer.getInt("weight");
					hostPort=redisServer.isNull("host")?hostPort:redisServer.getString("host");
					
					SentinelRedisInfo shardSentinelInfo=new SentinelRedisInfo();
					shardSentinelInfo.setConnectTimeOut(defaultConnTimeout);
					shardSentinelInfo.setSoTimeout(defaultSoTimeout);
					shardSentinelInfo.setPassword(defaultPassword);
					shardSentinelInfo.setWeight(defualtWeight);
					
					//非HA node
					if(redisServer.isNull("sentinel")||redisServer.getJSONArray("sentinel").length()==0){
						shardSentinelInfo.setSentinel(false);
						shardSentinelInfo.setHostAndPort(hostPort);
						if(hostPort==null)
							throw new RedisException("非HA Node,需配置host可选项");
						
					}else{
						//HA node
						shardSentinelInfo.setSentinel(true);
						JSONArray jsonArr=redisServer.getJSONArray("sentinel");
						Set<String> sentinelSet=new HashSet<String>();
						for(int i=0;i<jsonArr.length();i++){
							sentinelSet.add(jsonArr.getString(i));
						}
						shardSentinelInfo.setSentinelSet(sentinelSet);
					}
					
					shardSentinelInfo.setMaster(redisServer.getString("master"));					
					shardsSentinelList.add(shardSentinelInfo);								
				} catch (Exception e) {					
					logger.error("parse redis config occured Error:"+e.getMessage(), e);
				}
			}						
		}
	}
	


	public static void init(){		
		try{
			  lock.lock();
			  if(shardsSentinelList.size()>0 )
				  shardedRedisSentinelPool=new ShardedRedisSentinelPool(shardsSentinelList, poolConfig);	
			  else
				  logger.warn("shardsSentinelList size=0,don't init ShardedRedisSentinelPool......");
			}catch(Exception ex){
				logger.warn("shardRedisPool init occured error: "+ex.getMessage(), ex);
			}finally{
				//无论是否异常 均需释放
				lock.unlock();
			}	
	}
	
	
	
	public static ShardedRedisSentinelPool getRedisShardPool(){		
		if(shardedRedisSentinelPool!=null){
			return shardedRedisSentinelPool;			
		}		
		init();
		return shardedRedisSentinelPool;
	}
	
}

