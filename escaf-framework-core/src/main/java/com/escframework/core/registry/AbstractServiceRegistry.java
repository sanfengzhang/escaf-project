package com.escframework.core.registry;

import org.springframework.beans.factory.annotation.Value;

/**
 * 
 * @author hal
 *
 */
public abstract class AbstractServiceRegistry implements ServiceRegistry
{

	@Value(value = "${app.name}")
	private String appName;

	public void registry(String serviceId, String serviceAddress)
	{

		boolean success = this.handle(serviceId, serviceAddress);
		if (success)
		{
			this.broadcastService(serviceId, serviceAddress);
		}

	}

	/**
	 * 服务注册的主要逻辑
	 * 
	 * @param serviceName
	 * @param serviceAddress
	 * @return
	 */
	public abstract boolean handle(String serviceId, String serviceAddress);

	/**
	 * 服务注册之后可能需要进行其他的一些广播该服务的一些操作
	 * 
	 * @param serviceName
	 * @param serviceAddress
	 */
	public void broadcastService(String serviceId, String serviceAddress)
	{
	}

	/**
	 * 获取应用的名称
	 * @return
	 */
	public String getAppName()
	{
		return appName;
	}

	public void setAppName(String appName)
	{
		this.appName = appName;
	}

}
