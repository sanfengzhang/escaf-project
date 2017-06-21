package com.escframework.core.registry;

/**
 * 
 * @author hanl
 *
 */
public interface ServiceRegistry
{

	/**
	 * 注册服务
	 * 
	 * @param serviceName
	 * @param serviceAddress
	 */
	public void registry(String serviceId, String serviceAddress);

}
