package com.escframework.core;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.CollectionUtils;

import com.escframework.core.config.ConfigFileService;

public class EscafConfigApplicationContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext>
{
	private static Logger logger = LoggerFactory.getLogger(EscafConfigApplicationContextInitializer.class);

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext)
	{
		loadPropertySources(applicationContext);
		loadSpringSources(applicationContext);

	}

	private void loadPropertySources(ConfigurableApplicationContext applicationContext)
	{
		MutablePropertySources mutablePropertySources = applicationContext.getEnvironment().getPropertySources();
		List<Resource> resources = ConfigFileService.loadConfigResource();
		if (!CollectionUtils.isEmpty(resources))
		{

			for (Resource resource : resources)
			{
				try
				{
					PropertySource<?> propertySource = new ResourcePropertySource(resource.getFilename(), resource);
					mutablePropertySources.addLast(propertySource);
					logger.info("*******load property source fileName={}********", resource.getFilename());
				} catch (IOException e)
				{

					e.printStackTrace();
				}

			}

			autoInitSpringBeans(resources);

		}
	}

	private void autoInitSpringBeans(List<Resource> resources)
	{

	}

	private void loadSpringSources(ConfigurableApplicationContext applicationContext)
	{
		BeanDefinitionRegistry beanDefinitionRegistry = getBeanDefinitionRegistry(applicationContext);

		XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanDefinitionRegistry);

		List<Resource> _resources = ConfigFileService.loadSpringXmlResource();

		xmlBeanDefinitionReader.loadBeanDefinitions(_resources.toArray(new Resource[_resources.size()]));

	}

	private BeanDefinitionRegistry getBeanDefinitionRegistry(ApplicationContext context)
	{
		if (context instanceof BeanDefinitionRegistry)
		{
			return (BeanDefinitionRegistry) context;
		}
		if (context instanceof AbstractApplicationContext)
		{
			return (BeanDefinitionRegistry) ((AbstractApplicationContext) context).getBeanFactory();
		}
		throw new IllegalStateException("Could not locate BeanDefinitionRegistry");
	}

}
