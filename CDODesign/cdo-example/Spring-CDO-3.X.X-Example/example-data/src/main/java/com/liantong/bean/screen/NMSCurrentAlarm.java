package com.liantong.bean.screen;

public class NMSCurrentAlarm {

	private  long id;
	private long ackTime;
	private String ackUser;	
	private long clearTime;
	private long raiseTime;
	
	private int severity;
	private String source;
	private String clearUser;
	private String alarmName;
	private String cause;
	
	private int state;	
	private long resourceId;
	private String resourceName;
	private String category;
	private long parentResourceId;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getAckTime() {
		return ackTime;
	}
	public void setAckTime(long ackTime) {
		this.ackTime = ackTime;
	}
	public String getAckUser() {
		return ackUser;
	}
	public void setAckUser(String ackUser) {
		this.ackUser = ackUser;
	}
	public long getClearTime() {
		return clearTime;
	}
	public void setClearTime(long clearTime) {
		this.clearTime = clearTime;
	}
	public long getRaiseTime() {
		return raiseTime;
	}
	public void setRaiseTime(long raiseTime) {
		this.raiseTime = raiseTime;
	}
	public int getSeverity() {
		return severity;
	}
	public void setSeverity(int severity) {
		this.severity = severity;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getClearUser() {
		return clearUser;
	}
	public void setClearUser(String clearUser) {
		this.clearUser = clearUser;
	}
	public String getAlarmName() {
		return alarmName;
	}
	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public long getParentResourceId() {
		return parentResourceId;
	}
	public void setParentResourceId(long parentResourceId) {
		this.parentResourceId = parentResourceId;
	}

	
}
