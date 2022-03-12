package com.cdo.redis;

import java.util.Set;

import redis.clients.util.Sharded;

public class SentinelRedisInfo {
	private String master;
	private Set<String> sentinelSet;
	private int connectTimeOut;
	private int soTimeout;
	private String password;
	private int weight=Sharded.DEFAULT_WEIGHT;;
	//若node 不是HA 节点时，直接使用ip:port 连接
	private String hostAndPort;
	
	private boolean isSentinel=true;
	public boolean isSentinel() {
		return isSentinel;
	}
	public void setSentinel(boolean isSentinel) {
		this.isSentinel = isSentinel;
	}
	public String getMaster() {
		return master;
	}
	public void setMaster(String master) {
		this.master = master;
	}
	public Set<String> getSentinelSet() {
		return sentinelSet;
	}
	public void setSentinelSet(Set<String> sentinelSet) {
		this.sentinelSet = sentinelSet;
	}
	public int getConnectTimeOut() {
		return connectTimeOut;
	}
	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}
	public int getSoTimeout() {
		return soTimeout;
	}
	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHostAndPort() {
		return hostAndPort;
	}
	public void setHostAndPort(String hostAndPort) {
		this.hostAndPort = hostAndPort;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}

}
