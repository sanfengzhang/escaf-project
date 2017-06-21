package com.escframework.core.web.listener;

import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.escframework.core.registry.ServiceRegistry;

public class ServiceRegitryListener implements ServletContextListener
{

	private String serverIp;

	private int port;

	private ServiceRegistry serviceRegistry;

	public static final Logger logger = LoggerFactory.getLogger(ServiceRegitryListener.class);

	public void contextInitialized(ServletContextEvent sce)
	{
		try

		{
			ServletContext servletContext = sce.getServletContext();
			WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
			RequestMappingHandlerMapping handlerMapping = webApplicationContext.getBean(RequestMappingHandlerMapping.class);
			Map<RequestMappingInfo, HandlerMethod> mappingForMethod = handlerMapping.getHandlerMethods();

			String servers = String.format("%s:%d", serverIp, port);
			String contextPath = servletContext.getContextPath();

			for (RequestMappingInfo requestMappingInfo : mappingForMethod.keySet())
			{
				String serviceName = requestMappingInfo.getName();
				String serviceAddress = servers + contextPath;

				if (null != serviceName)
				{

					Set<String> set = requestMappingInfo.getPatternsCondition().getPatterns();
					serviceAddress = serviceAddress + CollectionUtils.get(set, 0);
					serviceRegistry.registry(serviceName, serviceAddress);
					logger.info("<-----------serviceName={} and serviceAddress={}--------------->", serviceName, serviceAddress);
				}

			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void contextDestroyed(ServletContextEvent sce)
	{

	}

	public String getServerIp()
	{
		return serverIp;
	}

	public void setServerIp(String serverIp)
	{
		this.serverIp = serverIp;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public ServiceRegistry getServiceRegistry()
	{
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry)
	{
		this.serviceRegistry = serviceRegistry;
	}

}
