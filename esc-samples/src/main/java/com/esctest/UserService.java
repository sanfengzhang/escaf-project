package com.esctest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class UserService implements ApplicationContextAware
{
	ApplicationContext applicationContext;

	public void test()
	{
		System.out.println("test");
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		System.out.println(applicationContext);
		this.applicationContext = applicationContext;

	}

}
