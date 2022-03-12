package com.cdo.redis;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.ShardedJedis;

public class ShardedRedisLock {
	
	public static final int DEFAULT_SINGLE_EXPIRE_TIME=5;
	private final static Log logger=LogFactory.getLog(ShardedRedisLock.class);
    private final static String PREFIX_LOCK="lock";
    
    private ShardedJedis shardJedis;
    
   
    public ShardedRedisLock(){
    	
    }
    
    public ShardedRedisLock(ShardedJedis shardJedis){
    	this.shardJedis=shardJedis;
    }
    
	 public boolean tryLock(String MACAddress , long timeout, TimeUnit unit) {
		 String lockKey=PREFIX_LOCK+MACAddress;
		 return tryLock(MACAddress,lockKey, timeout, unit);
	    }
		/**
		 * 获取分布式锁
		 * @param cpeId
		 * @param mac
		 * @param timeout
		 * @param unit
		 * @return
		 */	 
	 public boolean tryLock(String MACAddress ,String lockKey, long timeout, TimeUnit unit) {  
		 	boolean closeRedis=false;
	        try{  
	        	if(shardJedis==null){
	        		ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();	
	        		shardJedis =shardPool.getResource();
	        		closeRedis=true;
	        	}
	            long nano = System.currentTimeMillis();  
	            do {  	          
	                Long i = shardJedis.setnx(lockKey,lockKey);  
	                if (i == 1) {   
	                	//获取到锁
	                	shardJedis.expire(lockKey, DEFAULT_SINGLE_EXPIRE_TIME);  	                    
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
	        	if(closeRedis)
	        		shardJedis.close();
	        }  
	        return Boolean.FALSE;  
	    }

	 
	public void unLock(String MACAddress) { 
			 String lockKey=PREFIX_LOCK+MACAddress;
	 		 unLock(MACAddress, lockKey);
	    }
	/**
	 * 释放分布式锁
	 * @param redisIdentify
	 * @param lockKey
	 */
	public void unLock(String MACAddress,String lockKey) {  
		boolean closeRedis=false;   		
        try {
        	if(shardJedis==null){
        		ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();
        		shardJedis =shardPool.getResource();
        		closeRedis=true;
        	}
        	shardJedis.del(lockKey);	        
        } catch(Exception e) {  
            logger.error(e.getMessage(), e);  
        } finally { 
        	if(closeRedis)
        		shardJedis.close();
        }  
    } 	
}
