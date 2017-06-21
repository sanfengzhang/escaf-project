package com.escframework.support.quartz;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.CollectionUtils;

public class TimerTaskBeanFactoryPostProcessor implements BeanFactoryPostProcessor
{

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
	{
		Map<String, Object> beanMaps = beanFactory.getBeansWithAnnotation(TimerTask.class);
		if (!CollectionUtils.isEmpty(beanMaps))
		{
			if (beanFactory instanceof DefaultListableBeanFactory)
			{
				DefaultListableBeanFactory newBeanFactory = (DefaultListableBeanFactory) beanFactory;
				BeanDefinition beanDefinition = new RootBeanDefinition(TimerTaskHandlerBean.class);
				newBeanFactory.registerBeanDefinition("timerTaskHandlerBean", beanDefinition);

			}

		}

	}

}
