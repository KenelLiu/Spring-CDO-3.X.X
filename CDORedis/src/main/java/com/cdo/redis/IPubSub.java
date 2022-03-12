package com.cdo.redis;

public interface IPubSub {
	//某一机器上  订阅   下发参数[规则，规则下的参数及参数值,全局默认参数] channel,当数据发生变化时，通过消息推送  更新其他机器上的数据
	public static final String channel_STB="CH_STB";
	//rule id
	public static final String RuleId="RuleId";
	//订阅
	public static final String Rule_OPEATOR="RuleOperator";
	public static final String Rule_OPEATOR_RELOAD="reload";//重新加载
	public static final String Rule_OPEATOR_UpdateParam="uptParam";//更新参数值
	//规则   对应 rule.ruleContent内容
	public static final String Rule="Rule";//规则
	//规则下的参数及参数值
	public static final String Rule_Param="cdoParam";
	//默认全局参数ruleId
	public static final String RuleId_DefSTB="DefSTB";
	
	//API机器  发送tr069请求后，等待返回结果,订阅 tr069 返回结果
	public static final String channel_API_CPE_RESULT="CH_API_RESULT";
}
