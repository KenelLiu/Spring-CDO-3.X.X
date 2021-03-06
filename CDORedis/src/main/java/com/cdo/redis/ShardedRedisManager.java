package com.cdo.redis;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import com.cdo.redis.bean.RedisACSConfirmMethod;
import com.cdo.redis.bean.AbstractMethod;
import com.cdo.redis.exception.AddQueueException;
import com.cdo.redis.exception.OutOfBoundRequestsException;
import com.cdo.util.common.Zipper;
import com.cdo.util.serial.Serializable;
import com.cdoframework.cdolib.data.cdo.CDO;
import redis.clients.jedis.ShardedJedis;

public class ShardedRedisManager implements IShardRedisMgr{
	private static Log logger=LogFactory.getLog(ShardedRedisManager.class);
	
	
	public static int getQueueLen(String MACAddress){
		return getQueueLen(null,MACAddress);
	}
	
	public static int getQueueLen(ShardedJedis shardedJedis,String MACAddress){
		boolean closeRedis=false;
		if(shardedJedis==null){
			ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();
			shardedJedis = shardPool.getResource();
			closeRedis=true;
		}			
		String key=TR069_QUEUE+"{"+MACAddress+"}";
		try{
			long len=shardedJedis.llen(key);			
			return (int)len;
		}catch(Exception e) {
			  logger.error("getQueueLen:"+key+" occured error: "+e.getMessage(),e);
		  return 0; 
		}finally{
			if(closeRedis){
				  if(shardedJedis!=null){
					  shardedJedis.close();
					  shardedJedis=null;
				  }				
			}			
		}		
	}	
	
	public static long getACSReuqestId(String MACAddress){
		ShardedJedis shardJedis = null;
		ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();
		String key=TR069_ACSReqID+"{"+MACAddress+"}";
		try{
			shardJedis = shardPool.getResource();
			String gRequestId=shardJedis.get(key);
			if(gRequestId==null || gRequestId.trim().equals(""))
				return 0;
			return Long.parseLong(gRequestId);
		}catch(Exception e) {
		  logger.error("Get key:"+key+" occured error: "+e.getMessage(),e);
		  return 0; 
		}finally{
			if(shardJedis!=null)
				shardJedis.close();			
		}		
	}	
	
	
	public static boolean addACSRequestMethod(String MACAddress,AbstractMethod acsMethod,RedisACSConfirmMethod confirmMethod,boolean isExistRequestId){
		return addACSRequestMethod(null, MACAddress, acsMethod, confirmMethod, isExistRequestId);
	}
	/**
	 *
	 * 1  ????????????????????????????????????,
	 * 2  ????????????ACS??????
	 * 3 ??????ACS??????
	 * 4 ????????????????????????
		 acsMehtod,confirmMehtod ??????????????????redis  
	 * 
	 * @param redisIdentify
	 * @param method
	 * @param confirmMethod
	 * @param ACSMaxReuqest
	 * @param isExistRequestId  
	 */
	public static boolean addACSRequestMethod(ShardedJedis shardedJedis,String MACAddress,AbstractMethod acsMethod,RedisACSConfirmMethod confirmMethod,boolean isExistRequestId){		
		boolean closeRedis=false;
		if(shardedJedis==null){
			ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();
			shardedJedis = shardPool.getResource();
			closeRedis=true;
		}						
		String qeque=TR069_QUEUE+"{"+MACAddress+"}";
		String cfm=TR069_ACSConfirm+"{"+MACAddress+"}";
		String reqId=TR069_ACSReqID+"{"+MACAddress+"}";		
		try{
			
			long queueLen=shardedJedis.llen(qeque);
			if(queueLen>=IShardRedisMgr.TR069_ACS_MAX_REQUEST)
				throw new OutOfBoundRequestsException("Queue full,max size="+TR069_ACS_MAX_REQUEST);

	      	if(isExistRequestId){        		
	      		//shardedJedis.lpush(qeque, acsMethod.toZipJson());
	      		shardedJedis.lpush(qeque.getBytes(), Serializable.protoCDO2Byte(acsMethod.getData()));
	      		shardedJedis.hset(cfm.getBytes(), acsMethod.getRequestId().getBytes(),Serializable.protoCDO2Byte(confirmMethod.getConfirmData())); 	      		
	      		return true;
	      	}else{
	  			String requestId=shardedJedis.get(reqId);
	  			long gRequestId;
	  			if(requestId==null || requestId.trim().equals(""))
	  				gRequestId=0;
	  			else 
	  				gRequestId=Long.parseLong(requestId);            	
	  			
	  			acsMethod.setRequestId(gRequestId+"");		
	  			//??????ACS??????ID ???1   
	  			gRequestId++;	
	  			gRequestId=gRequestId>=Integer.MAX_VALUE?1:gRequestId;//?????????       
	          	String result=shardedJedis.set(reqId, gRequestId+"");         	
	          	if(!result.trim().equalsIgnoreCase("OK")){
	          		  logger.warn("set key="+reqId+" value="+gRequestId+" ??????ACS????????????......");
	          		  throw new AddQueueException("??????ACS??????????????????");
	          	}   
	  			
	  			long len=shardedJedis.llen(qeque);//???????????? 	                       
	            long ret=shardedJedis.lpush(qeque.getBytes(), Serializable.protoCDO2Byte(acsMethod.getData()));//??????????????? 
	            if(len>=ret)//
	              	logger.warn("MACAddress="+MACAddress+",lpush key="+qeque+" value="+acsMethod.getData().toJSON()+" add queue failed......");          	
	            ret=shardedJedis.hset(cfm.getBytes(), acsMethod.getRequestId().getBytes(),Serializable.protoCDO2Byte(confirmMethod.getConfirmData()));          	
	          	if(ret<=0)
	          		logger.warn("MACAddress="+MACAddress+",hset key="+cfm+" field="+acsMethod.getRequestId()+" value="+confirmMethod.getConfirmData().toJSON()+" add confirm failed......");
	          	return true;
	      	 }
		}catch(OutOfBoundRequestsException ex){
			throw ex;		 
		}catch(Exception e) {			
		  logger.error("MACAddress="+MACAddress+",addACSRequestMethod occured error: "+e.getMessage()+",close redis connection",e);
		  //========?????????SocketTimeoutException,JedisConnectionException,????????????????????????=======//
		  if(shardedJedis!=null){
			  shardedJedis.close();
			  shardedJedis=null;
		  }
		  throw new AddQueueException(e);
		}finally{
			if(closeRedis){
				  if(shardedJedis!=null){
					  shardedJedis.close();
					  shardedJedis=null;
				  }				
			}
		}			
	}	
	
	/**
	 * ???????????????????????????????????????
	 * ??????????????? ?????????
	 * acsMehtod ???????????????  ???????????????
	 * @param redisIdentify
	 * @param method
	 * @return
	 */
	public static String doACSEMethods(String MACAddress){
		ShardedJedis shardJedis = null;
		ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();
		String acsQeque=TR069_QUEUE+"{"+MACAddress+"}";
		try{
			shardJedis = shardPool.getResource();
			String ret= shardJedis.rpop(acsQeque);
			if(ret==null){
				//??????????????????????????????????????????????????????
				return ret;
			}	
			return new String(Zipper.unzipBytes(Base64.decodeBase64(ret)));
		}catch(Exception e) {			
		  logger.error("Redis doACSEMethods occured error: "+e.getMessage(),e);		  
		}finally{
			if(shardJedis!=null)
				shardJedis.close();			
		}	
		return null;
	}
	/**
	 * 1 ??????????????????
	 * 2  ???cpe??????????????????   ??????acs????????? ?????????????????????object
	 * acsConfirmObj ?????????
	 * @see {@link com.cdo.redis.ShardedRedisManager#addACSRequestMethod(ShardedRedisIdentify,AbstractMethod,ACSConfirmMethod,int,boolean)}
	 * @param redisIdentify
	 * @param method
	 * @return
	 */
	public static RedisACSConfirmMethod revAndConfirmMethod(String MACAddress,AbstractMethod method){
		ShardedJedis shardJedis = null;
		ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();
		String acsConfirm=TR069_ACSConfirm+"{"+MACAddress+"}";
		RedisACSConfirmMethod acsConfirmMethod=null;
		byte[] acsConfirmObj=null;
		try{
			    shardJedis = shardPool.getResource();
				acsConfirmObj=shardJedis.hget(acsConfirm.getBytes(), method.getRequestId().getBytes());
				if(acsConfirmObj==null){
					return null;
				}			
				shardJedis.hdel(acsConfirm, method.getRequestId());								
				acsConfirmMethod=new RedisACSConfirmMethod(); 
				acsConfirmMethod.setConfirmData(Serializable.byte2ProtoCDO(acsConfirmObj));
				return acsConfirmMethod;
		}catch(Exception e) {			
		  logger.error("revAndConfirmMethod occured error: "+e.getMessage()+",acsConfirmObj="+acsConfirmObj,e);		  
		}finally{
			if(shardJedis!=null)
				shardJedis.close();			
		}	
		return null;
	}
	
	/**
	 * cpe??????ACS?????????????????????redis,?????????ui?????????
	 * ????????????
	 * @param redisIdentify
	 * @param gRequestId
	 * @param method
	 */
	public static void addCPEResponse(String MACAddress,String gRequestId,AbstractMethod method){
		ShardedJedis shardJedis = null;
		ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();
		String cpeResKey="";

		try{
			cpeResKey=TR069_CPERes+"{"+MACAddress+"}";
			shardJedis = shardPool.getResource();
			if(method==null)
				shardJedis.hset(cpeResKey.getBytes(), gRequestId.getBytes(),"".getBytes());
			else
				shardJedis.hset(cpeResKey.getBytes(), gRequestId.getBytes(),Serializable.protoCDO2Byte(method.getData()));
			shardJedis.expire(cpeResKey, 5*3600); //??????5??????        	
		}catch(Exception e) {			
		  logger.error("Redis addCPEResponse occured error: "+e.getMessage(),e);		  
		}finally{
			if(shardJedis!=null)
				shardJedis.close();			
		}		
	}
	
	public static boolean containsCPEResponseKey(String MACAddress,String gRequestId){
		ShardedJedis shardJedis = null;
		ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();
		String cpeResKey=TR069_CPERes+"{"+MACAddress+"}";
		try{
			
			shardJedis = shardPool.getResource();
			return shardJedis.hexists(cpeResKey, gRequestId);
		}catch(Exception e) {			
		  logger.error("Redis containsCPEResponseKey occured error: "+e.getMessage(),e);	
		  return false;
		}finally{
			if(shardJedis!=null)
				shardJedis.close();			
		}		
	}
	
	/**
	 * ????????????
	 * @param MACAddress
	 * @param gRequestId
	 * @return
	 */
	public static CDO getCPEResponse(String  MACAddress,String gRequestId,boolean isPing){
		ShardedJedis shardJedis = null;
		ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();		
		String cpeResKey="";
		try{
			if(isPing){
				 cpeResKey=TR069_CPERes_Ping+"{"+MACAddress+"}";
			}else{
				 cpeResKey=TR069_CPERes+"{"+MACAddress+"}";
			}			
			shardJedis = shardPool.getResource();			
			byte[] res=shardJedis.hget(cpeResKey.getBytes(), gRequestId.getBytes());
			if(res==null){
				return null;
			}			
			shardJedis.hdel(cpeResKey, gRequestId);
			return Serializable.byte2ProtoCDO(res);			
		}catch(Exception e) {			
		  logger.error("Redis getCPEResponse occured error: "+e.getMessage(),e);	
		  return null;
		}finally{
			if(shardJedis!=null)
				shardJedis.close();			
		}		
	}
	/**
	 * ????????????
	 * @param MACAddress
	 * @return
	 */
	public static JSONArray getACSMethodQueue(String MACAddress){
		return getACSMethodQueue(null,MACAddress);
	}
	/**
	 * ??????????????????
	 * @param MACAddress
	 * @return
	 */
	public static JSONArray getACSMethodQueue(ShardedJedis shardedJedis,String MACAddress){
		boolean closeRedis=false;
		if(shardedJedis==null){
			ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();
			shardedJedis = shardPool.getResource();
			closeRedis=true;
		}	
		String acsQeque=TR069_QUEUE+"{"+MACAddress+"}";
		JSONArray arr=new JSONArray();
		try{			
			List<String> list= shardedJedis.lrange(acsQeque, 0, -1);
			if(list!=null && list.size()>0){
				for(int i=0;i<list.size();i++){					
					Object acsMethod=new String(Zipper.unzipBytes(Base64.decodeBase64(list.get(i))));					
					try{
						acsMethod=new JSONObject((String)acsMethod);
					}catch(Exception ex){
						logger.error(ex.getMessage(),ex);
					}
					arr.put(acsMethod);					
				}
			}			
		  return arr;
		}catch(Exception e) {			
		  logger.error("Redis getACSMethodQueue occured error: "+e.getMessage(),e);		  
		}finally{
			if(closeRedis){
				  if(shardedJedis!=null){
					  shardedJedis.close();
					  shardedJedis=null;
				  }				
			}		
		}	
		return null;
	}	
	
	/**
	 * ???????????????????????????(??????)
	 * @param shardedJedis
	 * @param MACAddress
	 * @param methodName
	 * @return
	 */
	public static boolean isExistMethodOfQueue(ShardedJedis shardedJedis,String MACAddress,String methodName){
		boolean closeRedis=false;
		boolean existMenthod=false;
		if(shardedJedis==null){
			ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();
			shardedJedis = shardPool.getResource();
			closeRedis=true;
		}	
		String acsQeque=TR069_QUEUE+"{"+MACAddress+"}";
		try{			
			List<byte[]> list= shardedJedis.lrange(acsQeque.getBytes(), 0, -1);			
			if(list!=null && list.size()>0){
				for(int i=0;i<list.size();i++){											
					try{
						CDO acsMethod=Serializable.byte2ProtoCDO(list.get(i));	
						
						String method=acsMethod.exists(AbstractMethod.JSON_KEY_Method)?acsMethod.getStringValue(AbstractMethod.JSON_KEY_Method):"";
						if(method.equals(methodName)){
							existMenthod=true;
							break;
						}		
					}catch(Exception ex){
						logger.error(ex.getMessage(),ex);
					}							
				}
			}			
		  return existMenthod;
		}catch(Exception e) {			
		  logger.error("Redis isExistMethodOfQueue occured error: "+e.getMessage(),e);		  
		}finally{
			if(closeRedis){
				  if(shardedJedis!=null){
					  shardedJedis.close();
					  shardedJedis=null;
				  }				
			}		
		}	
		return existMenthod;
	}
	
	/**
	 * ???????????????????????????(??????)
	 * @param shardedJedis
	 * @param MACAddress
	 * @return
	
	public static boolean isExistMethodOfQueue(ShardedJedis shardedJedis,String MACAddress,String methodName){
		JSONArray jsonArray=getACSMethodQueue(shardedJedis, MACAddress);
		boolean existMenthod=false;
		for(int k=0;k<jsonArray.length();k++){
			Object obj=jsonArray.opt(k);
			if(obj==null) continue;
			if(obj instanceof JSONObject){
				JSONObject json=(JSONObject)obj;
				String method=json.optString(AbstractMethod.JSON_KEY_Method,"");
				if(method.equals(methodName)){
					existMenthod=true;
					break;
				}									
			}
		}
		return existMenthod;
	} */
}
