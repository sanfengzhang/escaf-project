package com.esctest.es;

import org.elasticsearch.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.escaf.esservice.api.EscafSearchDao;
import com.escaf.esservice.search.mapper.resultmapper.DefaultResultMapper;
import com.escframework.es.TransportClientFactoryBean;

@Configuration
public class AppEsConfiguration
{

	@Bean
	public TransportClientFactoryBean transportClientFactoryBean()
	{
		return new TransportClientFactoryBean();
	}

	@Bean
	public EscafSearchDao escafSearchDao(Client client)
	{
		return new EscafSearchDao(client, new DefaultResultMapper());
	}

}
