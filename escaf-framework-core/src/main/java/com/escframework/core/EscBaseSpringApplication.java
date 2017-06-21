package com.escframework.core;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 
 * 在新的版本中重构一下自定义配置文件和spring配置的加载方式{@ EscafConfigApplicationContextInitializer}
 * 这种初始化定制Context的方式进行配置。
 * 
 * @author HanLin
 * @version
 * 
 *
 */
public class EscBaseSpringApplication extends SpringApplication
{

	public EscBaseSpringApplication(Object... primarySources)
	{
		super(primarySources);
		addInitializers(new EscafConfigApplicationContextInitializer());

	}

	public static ConfigurableApplicationContext start(Class<?> clazz, String... args)
	{

		EscBaseSpringApplication springApplication = new EscBaseSpringApplication(clazz);
		return springApplication.run(args);
	}

}