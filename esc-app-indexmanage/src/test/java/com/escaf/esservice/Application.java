package com.escaf.esservice;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.escframework.core.EscBaseSpringApplication;
import com.escframework.core.access.BaseDaoFactoryBean;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass=BaseDaoFactoryBean.class)
public class Application 
{
	public static void main(String[] args)
	{
		
		EscBaseSpringApplication.start(Application.class, args);

	}

}
