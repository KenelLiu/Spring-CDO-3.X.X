package com.cdo.redis;

import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.util.Sharded;

 class ShardedMaster {
	private String masterName;
	private HostAndPort hostPort;
	private int connectTimeOut;
	private int soTimeout;
	private String password;
	private Set<String> sentinelSet;
	private int weight=Sharded.DEFAULT_WEIGHT;
	public Set<String> getSentinelSet() {
		return sentinelSet;
	}
	public void setSentinelSet(Set<String> sentinelSet) {
		this.sentinelSet = sentinelSet;
	}
	public String getMasterName() {
		return masterName;
	}
	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}
	public HostAndPort getHostPort() {
		return hostPort;
	}
	public void setHostPort(HostAndPort hostPort) {
		this.hostPort = hostPort;
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
	
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public String toString(){
		return "masterName:"+masterName+",host:["+hostPort+"]"+",sentinelSet:["+sentinelSet+"],weight:"+weight;
	}
	
	public boolean  equals(Object obj){
	       if (this == obj)  
	            return true;  
	        if (obj == null)  
	            return false; 
	        if(obj instanceof ShardedMaster){
	        	 ShardedMaster other = (ShardedMaster) obj;  
	        	 if(masterName!=null && masterName.equals(other.getMasterName()));
	        	  return true;
	        }
	        return false;
	}	
}
