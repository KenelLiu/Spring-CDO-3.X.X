package com.cdo.redis;

/**
 * 
 * @author KenelLiu
 *
 */
public class ShardedRedisIdentify {
	//mac地址
	private String MACAddress;
	
	private String cpeId;
	
	public ShardedRedisIdentify(String cpeId,String MACAddress){
		this.cpeId=cpeId;
		this.MACAddress=MACAddress;			
	}
	
	/**
	 * 保存数据到redis,需要一个唯一标识,MACAddress地址是唯一，则使用MACAddress地址，否则使用设备唯一号
	 * @return
	 */
	public String getIdentify(){
	
	   try{
		   if(MACAddress.toUpperCase().startsWith("0X")){
			   MACAddress=MACAddress.substring(2);
			}
		   Long.parseLong(MACAddress,16);
		   return MACAddress;
		}catch(Throwable ex){			
			return cpeId;
		}	
	}

	public String getMACAddress() {
		return MACAddress;
	}

	public void setMACAddress(String mac) {
		this.MACAddress = mac;
	}

	public String getCPEId() {
		return cpeId;
	}

	public void setCPEId(String cpeId) {
		this.cpeId = cpeId;
	}
	
	
}
