package com.escframework.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class EscSpringApplicationBuilder extends SpringApplicationBuilder
{

	@Override
	protected SpringApplication createSpringApplication(Object... sources)
	{

		return new EscBaseSpringApplication(sources);
	}

}
