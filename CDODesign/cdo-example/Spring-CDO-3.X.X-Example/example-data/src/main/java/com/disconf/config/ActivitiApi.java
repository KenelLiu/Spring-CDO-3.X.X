package com.disconf.config;



import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfUpdateService;
import com.baidu.disconf.client.common.update.IDisconfUpdate;


@DisconfFile(filename = "activitiApi.conf")
@DisconfUpdateService(classes = {ActivitiApi.class})
public class ActivitiApi implements IDisconfUpdate {

	public void reload() throws Exception {		
		
	}


}
