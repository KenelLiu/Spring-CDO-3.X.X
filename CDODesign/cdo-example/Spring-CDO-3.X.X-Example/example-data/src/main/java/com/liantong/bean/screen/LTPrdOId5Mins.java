package com.liantong.bean.screen;

public class LTPrdOId5Mins {
	private long portId;
	private long fetchTime;
	private boolean portStatus;	
	private float  inFlowSpeed;
	private float  outFlowSpeed;
	
	private long oId;
	private int linkId;
	public long getPortId() {
		return portId;
	}
	public void setPortId(long portId) {
		this.portId = portId;
	}
	public long getFetchTime() {
		return fetchTime;
	}
	public void setFetchTime(long fetchTime) {
		this.fetchTime = fetchTime;
	}	
	public boolean isPortStatus() {
		return portStatus;
	}
	public void setPortStatus(boolean portStatus) {
		this.portStatus = portStatus;
	}
	public float getInFlowSpeed() {
		return inFlowSpeed;
	}
	public void setInFlowSpeed(float inFlowSpeed) {
		this.inFlowSpeed = inFlowSpeed;
	}
	public float getOutFlowSpeed() {
		return outFlowSpeed;
	}
	public void setOutFlowSpeed(float outFlowSpeed) {
		this.outFlowSpeed = outFlowSpeed;
	}
	public long getoId() {
		return oId;
	}
	public void setoId(long oId) {
		this.oId = oId;
	}
	public int getLinkId() {
		return linkId;
	}
	public void setLinkId(int linkId) {
		this.linkId = linkId;
	}
	
}
