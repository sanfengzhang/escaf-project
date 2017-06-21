package com.escaf.esservice;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.escaf.esservice.api.EscafSearchDao;
import com.escaf.esservice.search.mapper.resultmapper.DefaultResultMapper;
import com.escframework.es.ElasticsearchRequest;
import com.escframework.es.TransportClientFactoryBean;
import com.escframework.es.auto.ESIndexAutoScanBean;

@Configuration
public class ElasticAppConfiguration
{
	@Value("${es.entity.packages}")
	private String scanIndexEntityPackages;

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

	@Bean
	public ESIndexAutoScanBean esIndexEntityScan(ElasticsearchRequest request)
	{
		return new ESIndexAutoScanBean(request, StringUtils.split(scanIndexEntityPackages, ","));
	}

	@Bean
	public ElasticsearchRequest elasticsearchRequest(Client client)
	{
		return new ElasticsearchRequest(client);
	}

}
