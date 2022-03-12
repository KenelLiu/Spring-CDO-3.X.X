package com.cdo.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.ShardedJedis;

public interface IShardRedisMgr {
	final Log logger=LogFactory.getLog(IShardRedisMgr.class);
	//tr069 协议 使用缓存	
	public final static String TR069_QUEUE="q";//tr069 ACS请求队列前缀
	public final static String TR069_ACSReqID="id";//tr069   单个cpe 全局acs请求 id值 的前缀
	//tr069   保存在服务端  与ACS请求的对应附属属性,当CPE响应ACS请求数据时，
	// 需要取出附属属性，与CPE数据组装成完整的信息,进行后台逻辑处理
	public final static String TR069_ACSConfirm="c";	
	//设备cpe  响应ACS请求应答信息，根据需求,暂存在缓存里，方便ui用户获取到机顶盒的应答信息,显示给客户
	public final static String TR069_CPERes="res";
	//设备cpe  响应ACS请求Ping 诊断结果应答信息，暂存缓存里，方便ui用户获取到机顶盒的应答信息,显示给客户
	public final static String TR069_CPERes_Ping="ping";
	//设备cpe 参数模板设置标志,标志是否有新的setParameter参数需要设置
	public final static String DEVICE_PARAM_FLAG="f";
	//相当于 inform_parameter_schedue.status=1  还未执行，需要执行机顶盒参数下发
	public final static int PARAM_FLAG_TRUE=1; 
	//相当于 inform_parameter_schedue.status=2   已执行，无需要执行机顶盒参数下发
	public final static int PARAM_FLAG_FLASE=2;
	//tr069 单个机顶盒 ，最大数据
	public final static int TR069_ACS_MAX_REQUEST=10;
	
	 /**
	  * 获取分布式redis连接池	
	  * @return
	  */
	 public static ShardedJedis getShardedJedis(){
		 ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();
		 return shardPool.getResource();
	 }
	 /**
	  * 关闭redis连接
	  * @param shardJedis
	  */
	 public static void closeShardedJedis(ShardedJedis shardedJedis){
		 if(shardedJedis!=null)
			 shardedJedis.close();
	 }
	 
	 /**
	  * 设置  是否有业务模板参数下发 标识	   
	  * flag=1 表示有业务模板下发,在tr069 inform事件触发时,异步处理下发参数
	  * @see com.tr069.async.InformExecutor#executeSchedueSetParam(String)
	  * flag=2 表示没有
	  * @param MACAddress
	  * @param flag
	  * @return
	  */
	 public static boolean setDeviceFlag(String MACAddress,int flag){						
		return setDeviceFlag(null, MACAddress, flag);	
	}
	 /**
	  * 共有一个redis连接,设置多个MACAddress的业务模板参数下发标识	   
	  * flag=1 表示有业务模板下发,在tr069 inform事件触发时,异步处理下发参数
	  * @see com.tr069.async.InformExecutor#executeSchedueSetParam(String)
	  * 批量设置时用到 JobDeviceService.sendDeviceFlag使用到
	  * 注：连接需在外层关闭
	  * @param shardJedis
	  * @param MACAddress
	  * @param flag
	  * @return
	  */
	 public static boolean setDeviceFlag(ShardedJedis shardedJedis,String MACAddress,int flag){
		 	boolean closeRedis=false;
		 	if(shardedJedis==null){
		 		ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();
		 		shardedJedis=shardPool.getResource();
		 		closeRedis=true;
		 	}			
			String key=DEVICE_PARAM_FLAG+"{"+MACAddress+"}";
			try{
				String isOk=shardedJedis.set(key, flag+"");
				if(isOk!=null&& isOk.equals("OK"))
					return true;
				return false;
			}catch(Exception e) {
			  logger.error("set key:"+key+" error: "+e.getMessage(),e);
			  //========会发生SocketTimeoutException,JedisConnectionException,强制将连接关闭释放,下次获取连接=======//
			  if(shardedJedis!=null){
				  shardedJedis.close();
				  shardedJedis=null;
			  }
			  return false;
			}finally{
				if(closeRedis){
					closeShardedJedis(shardedJedis);						
				}
			}
	 }
	 /**
	  * 获取是否有业务模板参数下发 标识	   
	  * true 表示有业务模板下发,在tr069 inform事件触发时,异步处理下发参数
	  * @see com.tr069.async.InformExecutor#executeSchedueSetParam(String)
	  * false 表示没有
	  * @param MACAddress
	  * @param flag
	  * @return
	  */
	public static boolean getDeviceFlag(String MACAddress){
			return getDeviceFlag(null, MACAddress);	
	   }	
	/**
	  * 共有一个redis连接,设置多个MACAddress的业务模板参数下发标识	   
	  * flag=1 表示有业务模板下发,在tr069 inform事件触发时,异步处理下发参数
	  * @see com.tr069.async.InformExecutor#executeSchedueSetParam(String)
	  * 注：连接需在外层关闭
	  * @param shardJedis
	  * @param MACAddress
	  * @param flag
	  * @return
	  */
	public static boolean getDeviceFlag(ShardedJedis shardedJedis,String MACAddress){	
	 	boolean closeRedis=false;
	 	if(shardedJedis==null){
	 		ShardedRedisSentinelPool shardPool=ShardedRedisPool.getRedisShardPool();
	 		shardedJedis=shardPool.getResource();
	 		closeRedis=true;
	 	}		 	
		String key=DEVICE_PARAM_FLAG+"{"+MACAddress+"}";
		try{			
			String flag=shardedJedis.get(key);
			if(flag==null || flag.trim().equals("") || !flag.trim().equals(IShardRedisMgr.PARAM_FLAG_TRUE+""))
				return false;
			return true;
		}catch(Exception e) {
		  logger.error("get key:"+key+" error: "+e.getMessage(),e);
		  if(shardedJedis!=null){
			  shardedJedis.close();
			  shardedJedis=null;
		  }		  
		  return false; 
		}finally{
			if(closeRedis){
				closeShardedJedis(shardedJedis);
			}
		}	
	}
}
