package com.esctest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.escframework.core.EscBaseSpringApplication;
import com.escframework.core.EscSpringApplicationBuilder;
import com.escframework.core.access.BaseDaoFactoryBean;
import com.escframework.core.web.CorsWebMvcConfiguration;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass=BaseDaoFactoryBean.class)
public class Application extends org.springframework.boot.web.support.SpringBootServletInitializer
{

	/**
	 * jar包运行的方式
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{

		EscBaseSpringApplication springApplication = new EscBaseSpringApplication(Application.class);
		springApplication.run(args);

	}

	/**
	 * war包运行的时候添加配置资源文件的时候是在这里添加。在main方法中添加是无用的！！
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder)
	{
		System.out.println("------------------------App Start--------------------------------------------");

		return builder.sources(Application.class);
	}
	
	

	/**
	 * 在使用扩展后的springApplication的时候需要重写该方法。
	 */
	@Override
	protected SpringApplicationBuilder createSpringApplicationBuilder()
	{
		
		return new EscSpringApplicationBuilder();
	}

	@Bean
	public CorsWebMvcConfiguration corsWebMvcConfiguration()
	{

		return new CorsWebMvcConfiguration();

	}

}
