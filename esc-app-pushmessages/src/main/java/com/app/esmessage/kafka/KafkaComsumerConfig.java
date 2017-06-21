package com.app.esmessage.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
@EnableKafka
public class KafkaComsumerConfig
{
	@Value("${kafka.broker.list}")
	private String kafkaBootstrapServers;

	@Value("${kafka.consumer.groupname}")
	private String groupId;

	@Value("${consume.kafka.thread.count}")
	private int concurrency;

	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory()
	{
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
		factory.setConsumerFactory(consumerFactory());
		factory.setConcurrency(concurrency);
		factory.setBatchListener(true);
		factory.getContainerProperties().setPollTimeout(3000);
		return factory;

	}

	public ConsumerFactory<String, String> consumerFactory()
	{
		return new DefaultKafkaConsumerFactory<String, String>(consumerConfigs());
	}

	public Map<String, Object> consumerConfigs()
	{
		Map<String, Object> propsMap = new HashMap<String, Object>();
		propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
		propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		return propsMap;
	}

}
