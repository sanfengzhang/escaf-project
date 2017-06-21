package com.app.esmessage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.app.esmessage.kafka.SpringKafkaMessageIndexEsService;

@Configuration
@PropertySource(value = { "classpath:resource/conf/kafka.properties" })
public class AppConfiguration
{

	@Bean
	public BulkProcessorService bulkProcessorService()
	{

		return new BulkProcessorService();

	}

	@Bean
	public SpringKafkaMessageIndexEsService springKafkaMessageIndexEsService(BulkProcessorService bulkProcessorService)
	{
		return new SpringKafkaMessageIndexEsService(bulkProcessorService);
	}

}
