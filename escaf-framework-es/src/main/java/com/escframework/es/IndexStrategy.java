package com.escframework.es;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import com.escframework.es.anotation.Document;
import com.escframework.es.auto.ESIndexAutoScanBean;


public abstract class IndexStrategy
{
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexStrategy.class);

	private ElasticsearchRequest elasticsearchRequest;

	private ESIndexAutoScanBean esIndexEntityScan;

	public IndexStrategy()
	{

	}

	public IndexStrategy(ElasticsearchRequest elasticsearchRequest, ESIndexAutoScanBean esIndexEntityScan)
	{
		super();
		this.elasticsearchRequest = elasticsearchRequest;
		this.esIndexEntityScan = esIndexEntityScan;
	}

	/***
	 * 这个设计不太好，后期打算将ESIndexEntityScan扫描到有注解的实体类的信息和页面添加的索引统一存储到数据库。
	 * 
	 */
	public void createIndex()
	{

		/**
		 * {index.creation_date=1492422078041, index.number_of_replicas=0,
		 * index.number_of_shards=5, index.refresh_interval=30s,
		 * index.store.type=fs, index.uuid=aJVYpmcaRqiHlbJdAWD62Q,
		 * index.version.created=2040499}
		 */

		String indexStrategyClassName = this.getClass().getName();
		Map<String, List<String>> map = esIndexEntityScan.getStartCreatedIndexTimerTaskEntities();
		if (CollectionUtils.isEmpty(map))
		{
			return;
		}

		List<String> list = map.get(indexStrategyClassName);

		for (String className : list)
		{
			try
			{
				Class<?> clazz = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
				Document document = clazz.getAnnotation(Document.class);
				String indexName = null;
				if (null != clazz && !clazz.getSimpleName().equals("Object"))
				{

					indexName = getIndexName(document.indexName());
					elasticsearchRequest.createIndexWithSettings(clazz, indexName);
					elasticsearchRequest.putMapping(clazz, indexName);

				} else
				{
					LOGGER.warn("the class={} not found index strategy class", className);

				}

			} catch (ClassNotFoundException e)
			{

				e.printStackTrace();

			} catch (LinkageError e)
			{

				e.printStackTrace();
			}

		}

	}

	/** 按规则生成索引的名字的策略 */
	public abstract String getIndexName(String indexNamePrffix);

	/** 需要改变索引的设置或者mappings */
	protected abstract Map<String, Object> changeMappingOrSettings(Map<String, Object> params);

	public void setElasticsearchRequest(ElasticsearchRequest elasticsearchRequest)
	{
		this.elasticsearchRequest = elasticsearchRequest;
	}

}
