package com.cdo.redis;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.JedisPoolConfig;

public class ShardConfigSentinel {

	private JedisPoolConfig poolConfig=new JedisPoolConfig();
	private List<SentinelRedisInfo> shardsSentinelList=new ArrayList<SentinelRedisInfo>();

	public JedisPoolConfig getPoolConfig() {
		return poolConfig;
	}
	public void setPoolConfig(JedisPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}
	public List<SentinelRedisInfo> getShardsSentinelList() {
		return shardsSentinelList;
	}
	public void setShardsSentinelList(List<SentinelRedisInfo> shardsSentinelList) {
		this.shardsSentinelList = shardsSentinelList;
	}
		
}
