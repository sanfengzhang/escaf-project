package com.app.esmessage;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class Application
{

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) throws IOException
	{
		PropertyConfigurator.configure(new ClassPathResource("resource/log4j.properties").getInputStream());
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);

		logger.info("context={} start finished", context);
		System.in.read();

	}

}
