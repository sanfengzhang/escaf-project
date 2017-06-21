package com.escframework.core.web;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 
 * @author hanl
 *
 */
@Configuration
public class CorsWebMvcConfiguration extends WebMvcConfigurerAdapter
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CorsWebMvcConfiguration.class);

	@Value("${cors.url.match}")
	private String corsUrlMatch;

	@Override
	public void addCorsMappings(CorsRegistry registry)
	{
		
		LOGGER.info("the urlMappings={} support cors.", corsUrlMatch);
		if (null == corsUrlMatch || "".equals(corsUrlMatch))
		{
			return;
		}
		String corsUrls[] = StringUtils.split(corsUrlMatch, ",");

		for (String corsUrl : corsUrls)
		{

			registry.addMapping(corsUrl).allowedOrigins("*");
		}

	}

}
