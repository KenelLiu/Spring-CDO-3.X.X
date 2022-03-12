package com.liantong.common;


public interface Constants {	
	/*==============================================================*/
	/* 域名,领域摘要加密使用                                          								*/
	/*==============================================================*/
	public static final String REALM="realm";
	public static final String AUTHOR_REALM="www.china-cos.com";		
	
	/*==============================================================*/
	/* activitiAdmin.conf 配置Key                                    */
	/*==============================================================*/
	public static class Config {
		public static final String   domain = "domain";
		public static final String   version="version";//版本			
		public  final static String  uploadPath="uploadPath";//上传地址Key
		public  final static String  downloadUrl="downloadUrl";//图片前缀地址
	}
	/*==============================================================*/
	/* 功能页面按钮控制                                         								*/
	/*==============================================================*/
	public  static class MenuOperator{
		public  final static String  Key_Select="select";
		public	final static String  Key_Insert="insert";
		public	final static String  Key_Update="update";
		public	final static String  Key_Delete="delete";
		public  final static String  Key_UptBoss="uptboss";
		public	final static String  Key_All="all";
	}
	/*==============================================================*/
	/* 流水号                                         								*/
	/*==============================================================*/
	public  static class Serial{
		public  final static String RoleId_Prefix="r";
		public  final static int  RoleId_Length=9;
		public  final static int  OrgId_Length=4;
		public  final static int  OrgId_MAX=9999;
	}
	/*==============================================================*/
	/* 施工单的状态                            	            							*/
	/*==============================================================*/
	public static class BuildState{
		public  final static int  New=1;//新建
		public  final static int  Wait=2;//待施工
		public  final static int  Recycling=3;//待回收
		public  final static int  Finish=4;//已完成
		
		public  final static int  Rebut=99;//驳回 ,相当于 新建状态
	}
	/*==============================================================*/
	/* 施工单的节点状态                            	            							*/
	/*==============================================================*/
	public static class BuildNodeState{
		public  final static int  Cancel=-4;//-4 撤销 (处理 施工单对某一个施工单的撤销 老数据情况)
		public  final static int  Init=-3;//初始
		public  final static int  Build=-2;//工程在建
		public  final static int  Stop=-1;//未建设终止
		public  final static int  Blank=1;//空白
		public  final static int  Normal=2;//正常
				
	}
	
	/*==============================================================*/
	/* 业务单的状态                            	            							*/
	/*==============================================================*/
	public static class BusinessState{
		public  final static int  New=1;//新建
		public  final static int  Wait=2;//待施工
		public  final static int  Recycling=3;//待回收
		public  final static int  Finish=4;//已完成
		
		public  final static int  Rebut=99;//驳回 ,相当于 新建状态
	}
	/*==============================================================*/
	/* 业务单的节点状态                            	            							*/
	/*==============================================================*/
	public static class BusinessNodeState{		
		public  final static int  Init=-3;//初始
		public  final static int  Build=-2;//工程在建
		public  final static int  Stop=-1;//未建设终止
		public  final static int  Blank=1;//空白
		public  final static int  Normal=2;//正常
				
	}
	/*==============================================================*/
	/* 割接单的状态                            	            							*/
	/*==============================================================*/
	public static class HandOverState{
		public  final static int  New=1;//新建
		public  final static int  Wait=2;//待施工
		public  final static int  Recycling=3;//待回收
		public  final static int  Finish=4;//已完成
		
		public  final static int  Rebut=99;//驳回 ,相当于 新建状态
	}
	
	/*==============================================================*/
	/* 政务外网接入的状态                            	            							*/
	/*==============================================================*/
	public static class NodeApplyState{
		public  final static int  New=1;//新建		
		public  final static int  Wait=2;//待预处理
		public  final static int  WaitAudit=3;//待OA审核
		public  final static int  WaitGen=4;//待生成
		public  final static int  Finish=5;
		
		public  final static int  RebutAudit=98;//OA审核不通过 ,相当于待预处理
		public  final static int  Rebut=99;//待预处退回 ,相当于 新建状态
		
	}
	/*==============================================================*/
	/* 安全策略管理状态                            	            							*/
	/*==============================================================*/
	public static class PolicyState{
		public  final static int  NEW=1;//新建
		public  final static int  WaitAudit=2;//待审核
		public  final static int  WaitOperate=3;//待操作
		public  final static int  WaitConfirm=4;//待确认
		public  final static int  Finish=5;//已完成
		public  final static int  RebutAudit=11;//审核退回
		public  final static int  RebutOperate=12;//操作退回
				
	}
	/*==============================================================*/
	/* 云网联动监测(威胁处置单)的状态                            	            							*/
	/*==============================================================*/
	public static class ThreatenState{
		public  final static int  New=1;//新建		
		public  final static int  WaitContact=2;//待联系用户
		public  final static int  WaitFeedbacks=3;//待反馈
		public  final static int  WaitAudit=4;//待审核
		public  final static int  Finish=5;
		
		public  final static int  RebutWaitContact=98;//审核不通过 ,相当于待联系用户
		public  final static int  Rebut=99;//待联系用户退回,相当于 新建状态
		
	}	
	/*==============================================================*/
	/* T_GE_Config配置type                    	            							*/
	/*==============================================================*/
	public static class ConfigType{
		public  final static int  Common=1;//普通参数 ,暂无数据		
		public  final static int  Login_SafePolicy=2;//登陆安全策略配置参数
		public  final static int  Threshold_Flow=3;//默认流量预警值 百分比
		public  final static int  Build_OrgCate_Public=4;//由530原来定义组织分类[公费]
		public  final static int  Build_OrgCate_Self=5;//由530原来定义组织分类[自费]
		
		public  final static int  Repair_ReportType=6;//报修类型
		public  final static int  Repair_IssueType=7;//故障类型
		public  final static int  Repair_IssueCause=8;//故障分类
		public  final static int  Repair_Solution=9;//解决方式
		public  final static int  Repair_Exemption=10;//免责原因
		
		public  final static int  Repair_IssueSource=11;//故障源
		public  final static int  Repair_IssueSubsection=12;//故障细分
		public  final static int  Repair_Opinion=13;//报修反馈意见
		public  final static int  MTResouce_Type=14;//530chart.MTResouce资源类别
		public  final static int  MT_AreaCode=15;//530chart.表.AreaCode区代码
		
		public  final static int  MT_ITSMStatus=16;//3rdItsm状态
		public 	final static int MT_SpecialGroup=17;//530chart.MTResouce专项分组代码
		public	final static int MT_NodeProperty=18;//530chart.MTNode 运维节点性质代码
		public	final static int MT_AlarmNetDevice=19;//530chart.MTAlarm 告警类型：网络设备和端口(资源类型)
		public	final static int MT_AlarmMainDevice=20;//530chart.MTAlarm 告警类型：主机设备
		public	final static int MT_AlarmIDCRoom=21;//530chart.MTAlarm 告警类型：机房
		
		public  final static int  Upload_FileType=99;//支持接收上传文件类型
	}		
	/*==============================================================*/
	/* 返回数据Key信息及状态值                             	            				*/
	/*==============================================================*/
	public final static String RET_STATES="states";//状态
	public final static String RET_DATA="data";
	public final static String RET_DATA_LIST="dataList";
	public final static String RET_MESSAGE="message";	

	public final static int RET_JSON_SUCCESS=2000;//正常
	public final static int RET_JSON_NOT_FOUND=2001;//数据不存在
	public final static int RET_JSON_EXCEPTION=2002;//发生异常
	public final static int RET_JSON_Valid_Fail=2100;//数据校验验证失败
	public final static int RET_JSON_EXISTS=2200;//数据已存在,用于重复检查
	public final static int RET_JSON_ImpExcel_SUCCESS=2300;//excel部分导入成功
	public final static int RET_JSON_Depend_NodeState_Fail=2400;//依赖的施工单或业务单 必须先提交【待施工->待回收】
	/*==============================================================*/
	/* API 接口返回数据 状态                         	            							*/
	/*==============================================================*/
	public final static String RET_API_CODE="code";//状态
	
	public final static int RET_API_CODE_SUCCESS=0;//正常
	public final static int RET_API_PARAM_NOT_EXISTS=1;//缺少必需的参数
	public final static int RET_API_Valid_SIGN_Fail=2;//签名验证失败
	public final static int RET_API_NOT_FOUND=3;//数据不存在
	public final static int RET_API_EXCEPTION=4;//其他异常
	
	
	
	
	/*==============================================================*/
	/* 分页数据保存在PAGE_BAR                             	            */
	/*==============================================================*/
	public static final String PAGE_BAR = "page_bar";	
	public static final String PAGE_ROWKEY="page_rowkey";
	
	/*==============================================================*/
	/* Cookie里保存的数据                             	            					*/
	/*==============================================================*/
	public static final String COOKIE_Token="ssoToken";
	/*==============================================================*/
	/* Session里保存的数据                             	            					*/
	/*==============================================================*/
	public static final String REQUEST_Current_MenuId="currentMenuId";
	public static final String REQUEST_Permission_Menu="permissionMenu";	
	
	public static final String SESSION_Current_MenuId="currentMenuId";
	public static final String SESSION_USER="user";	
	public static final String SESSION_RandCode="randCode";
	public static final String SESSION_Permission_Menu="permissionMenu";	 
	//public static final String SESSION_Permission_Menu_Operator="permissionMenuOperator";
	//处理查询问题,同一列表,通过按钮跳转到别的页面，返回时，保持原有数据查询条件
	public static final String SESSION_FrmQuery="sessionFrmQuery";	
	//处理Iframe 刷新问题
	public static final String SESSION_IFrameUrl="iframeUrl";
	public static final String SESSION_IFramePage="iframePage";
	public static final String SESSION_IFrameActive="navActiveSub";
	/*================================================================*/
	/* cdo数据类型前缀规则定义 ,页面传过来的值，根据key值名称定义进行转换数据类型 */
	/*================================================================*/
	public static String LONG_TYPE = "l";
	public static String INT_TYPE = "n";
	public static String DOUBLE_TYPE = "d";
	public static String STRING_TYPE = "str";
	public static String DATE_TYPE = "dt";
	public static String DATETIME_TYPE = "dtm";
	public static String BOOLEAN_TYPE = "b";
	public static final String UPLOAD_STATE = "uploadState";


	
}

