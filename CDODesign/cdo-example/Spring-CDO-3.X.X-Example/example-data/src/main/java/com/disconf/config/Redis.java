package com.disconf.config;


import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;
import com.baidu.disconf.client.common.annotations.DisconfUpdateService;
import com.baidu.disconf.client.common.update.IDisconfUpdate;


@DisconfFile(filename = "redis.properties")
@DisconfUpdateService(classes = {Redis.class})
public class Redis implements IDisconfUpdate {

	public void reload() throws Exception {		
		
	}


}