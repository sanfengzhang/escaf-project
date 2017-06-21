package com.escframework.core.config;

import java.util.List;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

import com.escframework.util.FileUtils;

/**
 * 
 * @author HanLin
 * @Date 2016-10-13
 * @since version1.0
 * @Desc 系统初始化加载自定义的资源
 *
 */
public abstract class ConfigFileService
{

	public static String RESOURCE_CONF_ROOT = "resource";

	private static final String RESOURCE_CONF_FILE = RESOURCE_CONF_ROOT + "/conf/basecfg.properties";

	private static final String DEFAULT_RESOURCE_LOAD_CLASS = "com.escframework.core.config.local.LocalConfigFileService";

	private static ConfigFileService configFileService;

	public static ClassLoader getDefaultClassLoader()
	{

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (null == classLoader)
		{
			classLoader = ClassUtils.getDefaultClassLoader();
		}

		return classLoader;

	}

	public static List<Resource> loadSpringXmlResource()
	{
		List<Resource> resources = null;

		try
		{
			Resource resource = new ClassPathResource("com/escframework/support/quartz/quartz-spring.xml");
			resources = getConfigFileService().doLoadResource(FileUtils.XML_FILE_TTPE);
			resources.add(resource);
		} catch (Exception e)
		{

			e.printStackTrace();
		}

		return resources;

	}

	public static List<Resource> loadConfigResource()
	{

		List<Resource> resources = null;
		try
		{
			resources = getConfigFileService().doLoadResource(FileUtils.PROP_FILE_TYPE);
		} catch (Exception e)
		{

			e.printStackTrace();
		}

		return resources;
	}

	private static synchronized ConfigFileService getConfigFileService()
	{
		if (configFileService == null)
		{

			try
			{
				Properties p = new Properties();
				ClassPathResource resource = new ClassPathResource(RESOURCE_CONF_FILE);
				p.load(resource.getInputStream());

				if (p.isEmpty())
				{
					throw new NullPointerException("basecfg must not be null" + RESOURCE_CONF_FILE);
				}
				String loadResourceClassName = p.getProperty("RESOURCE_LOAD_CLASS", DEFAULT_RESOURCE_LOAD_CLASS);
				Class<?> clazz = ClassUtils.forName(loadResourceClassName, getDefaultClassLoader());
				configFileService = (ConfigFileService) clazz.getConstructor().newInstance();

			} catch (Exception e)
			{

				e.printStackTrace();
			}
		}

		return configFileService;

	}

	protected abstract List<Resource> doLoadResource(String resourceType) throws Exception;

}
