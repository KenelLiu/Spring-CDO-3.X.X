package com.cdo.redis;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisManager {
	private static Log logger=LogFactory.getLog(RedisManager.class);
	
	
	public static String getCache(String key){
		Jedis redis = null;
		RedisSentinelPool redisPool=RedisPool.getRedisPool();		
		try{
			redis = redisPool.getResource();
			return redis.get(key);
		}catch(Exception e) {			
		  logger.error("Redis getCache key="+key+" is error: "+e.getMessage(),e);		  
		}finally{
			if(redis!=null)
				redis.close();			
		}	
		return null;
	}
	
	public static void putCache(String key,String value){		
		putCache(key, value, -1);
	}
	
	public static void putCache(String key,String value,int timeout){
		
		RedisLock lock=null;
		RedisSentinelPool redisPool=RedisPool.getRedisPool();
		Jedis redis = null;
		try{			
			lock=new RedisLock();			
			if(lock.tryLock(key,5,TimeUnit.SECONDS)){
				redis = redisPool.getResource();
				redis.set(key, value);
				if(timeout>0)
					redis.expire(key,timeout);				
			}			
		}catch(Exception e) {			
		  logger.error("Redis putCache key="+key+" is error: "+e.getMessage(),e);		  
		}finally{
			if(lock!=null)
				lock.unLock(key);
			if(redis!=null)
				redis.close();			
		}	
	}
	
	public static void publish(String channel,String message){
		
		RedisLock lock=null;
		RedisSentinelPool redisPool=RedisPool.getRedisPool();
		Jedis redis = null;
		try{			
			lock=new RedisLock();			
			if(lock.tryLock(channel,5,TimeUnit.SECONDS)){
				redis = redisPool.getResource();
				redis.publish(channel, message);
			}			
		}catch(Exception e) {			
		  logger.error("Redis putCache key="+channel+" is error: "+e.getMessage(),e);		  
		}finally{
			if(lock!=null)
				lock.unLock(channel);
			if(redis!=null)
				redis.close();			
		}	
	}
	
	public static void subscribe(final JedisPubSub pubSub,final String channel){		
		Thread subscribe=new Thread(new Subscribe(pubSub,channel));
		subscribe.start();
	}
	
}
