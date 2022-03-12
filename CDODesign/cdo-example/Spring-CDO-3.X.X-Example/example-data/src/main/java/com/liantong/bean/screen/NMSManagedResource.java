package com.liantong.bean.screen;

public class NMSManagedResource {
	private long resourceId;
	private String resourceName;
	private String ipAddress;
	private String resourceCategory;	
	private String resourceType;
	
	private String resourceVersion;
	private boolean managedstatus;
	private String resourceAlias;	
	private String createTime;	
	private long parentResourceId;
	
	private String fenZhongXin;
	private String jieDaoZhan;
	private String seqNo;
	private String manufacturer;
	private String modelNum;
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
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getResourceCategory() {
		return resourceCategory;
	}
	public void setResourceCategory(String resourceCategory) {
		this.resourceCategory = resourceCategory;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getResourceVersion() {
		return resourceVersion;
	}
	public void setResourceVersion(String resourceVersion) {
		this.resourceVersion = resourceVersion;
	}
	
	public boolean isManagedstatus() {
		return managedstatus;
	}
	public void setManagedstatus(boolean managedstatus) {
		this.managedstatus = managedstatus;
	}
	public String getResourceAlias() {
		return resourceAlias;
	}
	public void setResourceAlias(String resourceAlias) {
		this.resourceAlias = resourceAlias;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public long getParentResourceId() {
		return parentResourceId;
	}
	public void setParentResourceId(long parentResourceId) {
		this.parentResourceId = parentResourceId;
	}
	public String getFenZhongXin() {
		return fenZhongXin;
	}
	public void setFenZhongXin(String fenZhongXin) {
		this.fenZhongXin = fenZhongXin;
	}
	public String getJieDaoZhan() {
		return jieDaoZhan;
	}
	public void setJieDaoZhan(String jieDaoZhan) {
		this.jieDaoZhan = jieDaoZhan;
	}
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModelNum() {
		return modelNum;
	}
	public void setModelNum(String modelNum) {
		this.modelNum = modelNum;
	}
	
	
}
