package com.cdo.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class Subscribe extends Thread {
	private static final Log logger=LogFactory.getLog(Subscribe.class);
	private volatile boolean isRunning=false;
	private String channel;
	private JedisPubSub pubSub;	
	public Subscribe(JedisPubSub pubSub,String channel){		
		this.pubSub=pubSub;
		this.channel=channel;
	}
	@Override
	public void run() {	
		RedisSentinelPool redisPool=RedisPool.getRedisPool();
		Jedis redis = null;
		try{					
			redis = redisPool.getResource();
			isRunning=true;
			if(logger.isInfoEnabled())
				logger.info("subscribe channel="+channel+" isRunning="+isRunning);			
			redis.subscribe(pubSub, channel);
			isRunning=false;			
		    logger.warn("subscribe channel="+channel+" isRunning="+isRunning);
		}catch(Exception e) {			
		  logger.error("Redis Subscribe channel="+channel+" isRunning="+isRunning+" is error: "+e.getMessage(),e);	
		  isRunning=false;
		}finally{
			if(redis!=null)
				redis.close();			
		}			
	}
	
	public boolean isRunning(){
		return isRunning;
	}
}
