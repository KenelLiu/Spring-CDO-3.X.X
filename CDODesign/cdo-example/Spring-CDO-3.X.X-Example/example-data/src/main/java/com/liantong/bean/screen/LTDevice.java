package com.liantong.bean.screen;

public class LTDevice {
	private long deviceId;
	private long fetchTime;
	private boolean connectStatus;
	private float delayTimeSize;
	private float discardRateSize;
	
	private float cpuUsage;
	private float memoryUsage;
	public long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	public long getFetchTime() {
		return fetchTime;
	}
	public void setFetchTime(long fetchTime) {
		this.fetchTime = fetchTime;
	}

	public boolean isConnectStatus() {
		return connectStatus;
	}
	public void setConnectStatus(boolean connectStatus) {
		this.connectStatus = connectStatus;
	}
	public float getDelayTimeSize() {
		return delayTimeSize;
	}
	public void setDelayTimeSize(float delayTimeSize) {
		this.delayTimeSize = delayTimeSize;
	}
	public float getDiscardRateSize() {
		return discardRateSize;
	}
	public void setDiscardRateSize(float discardRateSize) {
		this.discardRateSize = discardRateSize;
	}
	public float getCpuUsage() {
		return cpuUsage;
	}
	public void setCpuUsage(float cpuUsage) {
		this.cpuUsage = cpuUsage;
	}
	public float getMemoryUsage() {
		return memoryUsage;
	}
	public void setMemoryUsage(float memoryUsage) {
		this.memoryUsage = memoryUsage;
	}
	
	
}
