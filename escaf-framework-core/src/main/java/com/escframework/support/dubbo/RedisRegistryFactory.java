package com.escframework.support.dubbo;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.registry.Registry;
import com.alibaba.dubbo.registry.RegistryFactory;

public class RedisRegistryFactory implements RegistryFactory
{

	public Registry getRegistry(URL url)
	{
		return new RedisRegistry(url);
	}

}
