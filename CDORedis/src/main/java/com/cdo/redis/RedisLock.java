package com.cdo.redis;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;

public class RedisLock {
	
	public static final int DEFAULT_SINGLE_EXPIRE_TIME=5;
	private final static Log logger=LogFactory.getLog(RedisLock.class);
	 private final static String PREFIX_LOCK="lock";

	 public boolean tryLock(String key, long timeout, TimeUnit unit) {
		 	String lockKey=PREFIX_LOCK+key;
		 	Jedis jedis = null;  
		 	RedisSentinelPool pool=RedisPool.getRedisPool();
	        try {  
	        	jedis =pool.getResource();  	        	
	            long nano = System.currentTimeMillis();  
	            do {  	          
	                Long i = jedis.setnx(lockKey,lockKey);  
	                if (i == 1) {   
	                	//获取到锁
	                	jedis.expire(lockKey, DEFAULT_SINGLE_EXPIRE_TIME);  	                    
	                    return Boolean.TRUE;  
	                }
	                if (timeout == 0) {  
	                    break;  
	                }  
	                Thread.sleep(500);  
	            } while ((System.nanoTime() - nano) < unit.toNanos(timeout));  
	            return Boolean.FALSE;  	        
	        } catch (Exception e) {  
	            logger.error(e.getMessage(), e);  
	        } finally { 
	        	if(jedis!=null)
	        		jedis.close();
	        }  
	        return Boolean.FALSE;  
	    }

	/**
	 * 释放锁
	 * @param redisIdentify
	 * @param lockKey
	 */
	public void unLock(String key) {  
		String lockKey=PREFIX_LOCK+key;
		Jedis jedis = null;  
 		RedisSentinelPool shardPool=RedisPool.getRedisPool();
        try {  
        	jedis =shardPool.getResource(); 
        	jedis.del(lockKey);	        
        } catch(Exception e) {  
            logger.error(e.getMessage(), e);  
        } finally {
        	if(jedis!=null)
        		jedis.close();
        }  
    } 	
}
