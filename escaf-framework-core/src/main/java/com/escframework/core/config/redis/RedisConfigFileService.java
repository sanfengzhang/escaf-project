package com.escframework.core.config.redis;

import java.util.List;

import org.springframework.core.io.Resource;

import com.escframework.core.config.ConfigFileService;

public class RedisConfigFileService extends ConfigFileService
{

	public static final String KEY_REDIS_SUFFIX = ".REDIS.CONFIG";

	public RedisConfigFileService()
	{

	}

	@Override
	protected List<Resource> doLoadResource(String resourceType) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

}
