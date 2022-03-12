package com.cdo.redis;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cdo.redis.config.RedisConfig;


import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
	private static Log logger=LogFactory.getLog(ShardedRedisPool.class);
	
	private static Lock lock = new ReentrantLock();// 锁  
	static JedisPoolConfig config=new JedisPoolConfig();		
	static SentinelRedisInfo sentinelRedisInfo=new SentinelRedisInfo();
	static RedisSentinelPool redisSentinelPool;	
	
	/**
	 * redis.general={"master":"10.27.122.249:6379"}
	 * 或者 
	 * redis.general={"master":"master1","sentinel":["10.27.122.35:26379","10.27.122.36:26379","10.27.122.38:26379"]} 
	 * 
	 */		
	static {			
		try {
			//默认
			int defaultMaxIdle=Integer.parseInt(RedisConfig.getConfigValue("redis.maxIdle"));
			int defaultTotal=Integer.parseInt(RedisConfig.getConfigValue("redis.maxTotal"));
			int defaultMaxWaitMills=Integer.parseInt(RedisConfig.getConfigValue("redis.maxWaitMillis"));
			boolean defaultTestOnBorrow=Boolean.parseBoolean(RedisConfig.getConfigValue("redis.testOnBorrow"));
			
			int defaultConnTimeout=Integer.parseInt(RedisConfig.getConfigValue("redis.connectionTimeout"));
			int defaultSoTimeout=Integer.parseInt(RedisConfig.getConfigValue("redis.soTimeout"));
			String defaultPassword=RedisConfig.getConfigValue("redis.password");
			
			JSONObject redisServer;
			String redisGeneral=RedisConfig.getConfigValue("redis.general");
			if(redisGeneral!=null && !redisGeneral.trim().equals("")){
				//--config pool配置
				redisServer = new JSONObject(redisGeneral);
				defaultMaxIdle=redisServer.isNull("maxIdle")?defaultMaxIdle:redisServer.getInt("maxIdle");
				defaultTotal=redisServer.isNull("maxTotal")?defaultTotal:redisServer.getInt("maxTotal");
				defaultMaxWaitMills=redisServer.isNull("maxWaitMillis")?defaultMaxWaitMills:redisServer.getInt("maxWaitMillis");
				defaultTestOnBorrow=redisServer.isNull("testOnBorrow")?defaultTestOnBorrow:redisServer.getBoolean("testOnBorrow");
				//------连接配置---
				defaultConnTimeout=redisServer.isNull("conctionTimeout")?defaultConnTimeout:redisServer.getInt("conctionTimeout");
				defaultSoTimeout=redisServer.isNull("soTimeout")?defaultSoTimeout:redisServer.getInt("soTimeout");
				defaultPassword=redisServer.isNull("password")?defaultPassword:redisServer.getString("password");
				
			
				config.setMaxIdle(defaultMaxIdle);
				config.setMaxTotal(defaultTotal);
				config.setMaxWaitMillis(defaultMaxWaitMills);
				config.setTestOnBorrow(defaultTestOnBorrow);
				
				sentinelRedisInfo.setConnectTimeOut(defaultConnTimeout);
				sentinelRedisInfo.setSoTimeout(defaultSoTimeout);
				sentinelRedisInfo.setPassword(defaultPassword);
				if(redisServer.isNull("sentinel")||redisServer.getJSONArray("sentinel").length()==0){
					sentinelRedisInfo.setSentinel(false);
				}else{
					sentinelRedisInfo.setSentinel(true);
					JSONArray jsonArr=redisServer.getJSONArray("sentinel");
					Set<String> sentinelSet=new HashSet<String>();
					for(int i=0;i<jsonArr.length();i++){
						sentinelSet.add(jsonArr.getString(i));
					}
					sentinelRedisInfo.setSentinelSet(sentinelSet);
				}
				sentinelRedisInfo.setMaster(redisServer.getString("master"));
			}
		} catch (Exception e) {
			logger.error("parse redis config is Error:"+e.getMessage(), e);
		}
	}
	
	public static void init(){		
		try{
			  lock.lock();
			  if(sentinelRedisInfo.getMaster()!=null)
				  redisSentinelPool=new RedisSentinelPool(sentinelRedisInfo, config);	  
			}catch(Exception ex){
				logger.warn("redis init is error: "+ex.getMessage(), ex);
			}finally{
				//无论是否异常 均需释放
				lock.unlock();
			}	
		if(redisSentinelPool==null)
			logger.warn("redis is not exists..................................");
	}
	

	
	public static RedisSentinelPool getRedisPool(){		
		if(redisSentinelPool!=null){
			return redisSentinelPool;			
		}		
		init();
		return redisSentinelPool;
	}
	
}
