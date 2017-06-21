package com.app.esmessage.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.elasticsearch.action.index.IndexRequest;
import org.springframework.kafka.annotation.KafkaListener;

import com.app.esmessage.BulkProcessorService;

/**
 * 1.在spring
 * kafka的框架中，在有注解{@KafkaListener}的方法时候，会按照配置的消费者线程数量创建对应的{@KafkaMessageListenerContainer}消息监听器，
 * 消息监听器在初始化后调用{@KafkaMessageListenerContainer.doStart()}方法开始对标记有注解{@KafkaListener}(此时spring容器将所有的注解扫描集合
 * 完成，并将该注解的方法包装成为GenericAcknowledgingMessageListener对象的实例)注解的方法提交到任务执行器中（线程池）。
 * 这样便可以在spring
 * kafka的消费任务执行器中通过GenericAcknowledgingMessageListener这个对象来完成业务方法的调用。
 * 2.例如在该bean中注入{BulkProcessorService}这个对象的时候并且在代理方法{@KafkaListener}中使用的时候，那么是在所有的{@KafkaMessageListenerContainer} 对象中共享还是线程本地变量的使用呢？从代码来看应该还是共享的。在该使用的情况下：对于同一主题的消费者线程组共享BulkProcessorService对象。
 * spring-kafka在使用注解来调用业务逻辑的主要原理是使用了方法代理执行的原理。
 * 3.如果将{@KafkaComsumerConfig}中factory.setBatchListener(true);添加这句批处理的代码，调用processMessage方法则会报错，因为数据类型不一样。该出的
 * 入参是将消息解析成一条消费者记录，而批处理时候应为集合数组类型。那么设置为true的时候只需要将方法入参改为List<ConsumerRecord<String,
 * String>> consumerRecords 这种类型入参就可以处理了。
 * 4.关于kafka的自动提交offset的功能，在spring-kafka中实现的是：支持自动那就不做任何处理，由kafka去管理offset,如果关闭了自动提交，那么会交由spring实现的offset管理。
 * 
 */
public class SpringKafkaMessageIndexEsService

{

	/**
	 * 这里多线程共享对象是安全的，因为在{@org.elasticsearch.action.bulk.BulkProcessor}中做了线程同步。
	 * 可以在此直接使用
	 */

	private BulkProcessorService bulkProcessorService;

	public SpringKafkaMessageIndexEsService(BulkProcessorService bulkProcessorService)
	{
		this.bulkProcessorService = bulkProcessorService;
	}

	@KafkaListener(containerFactory = "kafkaListenerContainerFactory", topics = { "${kafka.topics}" })
	public void processMessage(ConsumerRecord<String, String> consumerRecord)
	{

		String key = consumerRecord.key();
		String value = consumerRecord.value();
		IndexRequest indexRequest = new IndexRequest(key);
		indexRequest.source(value);
		bulkProcessorService.addIndexRequest(indexRequest);

	}

	public void setBulkProcessorService(BulkProcessorService bulkProcessorService)
	{
		this.bulkProcessorService = bulkProcessorService;
	}

}
