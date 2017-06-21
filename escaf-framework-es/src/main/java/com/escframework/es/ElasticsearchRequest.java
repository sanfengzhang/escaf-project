package com.escframework.es;

import static org.elasticsearch.client.Requests.indicesExistsRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import com.escframework.es.anotation.Document;
import com.escframework.es.anotation.Mapping;
import com.escframework.es.auto.MappingBuilder;

public class ElasticsearchRequest
{

	private Client client;

	private static final Logger logger = LoggerFactory.getLogger(ElasticsearchRequest.class);

	public ElasticsearchRequest()
	{

	}

	public ElasticsearchRequest(Client client)
	{
		super();
		this.client = client;
	}

	public boolean indexExists(String indexName)
	{
		return client.admin().indices().exists(indicesExistsRequest(indexName)).actionGet().isExists();
	}

	public boolean createIndexWithSettings(Class<?> clazz)
	{
		Document document = (Document) clazz.getAnnotation(Document.class);

		if (null == document)
		{

			return false;
		}

		Map<String, String> settings = new MapBuilder<String, String>().put("index.number_of_shards", String.valueOf(document.shards()))
				.put("index.number_of_replicas", String.valueOf(document.replicas()))
				.put("index.refresh_interval", String.valueOf(document.refreshInterval()))
				.put("index.store.type", String.valueOf(document.indexStoreType())).map();

		return createIndexWithSettings(document.indexName(), settings);

	}

	public boolean createIndexWithSettings(Class<?> clazz, String indexName)
	{

		Document document = (Document) clazz.getAnnotation(Document.class);

		if (null == document)
		{

			return false;
		}

		Map<String, String> settings = new MapBuilder<String, String>().put("index.number_of_shards", String.valueOf(document.shards()))
				.put("index.number_of_replicas", String.valueOf(document.replicas()))
				.put("index.refresh_interval", String.valueOf(document.refreshInterval()))
				.put("index.store.type", String.valueOf(document.indexStoreType())).map();

		return createIndexWithSettings(indexName, settings);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean createIndexWithSettings(String indexName, Object settings)
	{
		CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);
		if (settings instanceof String)
		{
			createIndexRequestBuilder.setSettings(String.valueOf(settings));
		} else if (settings instanceof Map)
		{
			createIndexRequestBuilder.setSettings((Map) settings);
		} else if (settings instanceof XContentBuilder)
		{
			createIndexRequestBuilder.setSettings((XContentBuilder) settings);
		}
		return createIndexRequestBuilder.execute().actionGet().isAcknowledged();
	}

	public <T> boolean putMapping(Class<?> clazz, String indexName)
	{

		Document document = (Document) clazz.getAnnotation(Document.class);

		if (null == document)
		{

			return false;
		}

		String type = document.type();

		// 如果配置了Mapping的mappingPath属性那么就会使用路径指定文件的方式来生成mapping
		if (clazz.isAnnotationPresent(Mapping.class))
		{
			String mappingPath = clazz.getAnnotation(Mapping.class).mappingPath();
			if (StringUtils.isNotBlank(mappingPath))
			{
				String mappings = readFileFromClasspath(mappingPath);
				if (StringUtils.isNotBlank(mappings))
				{
					return putMapping(indexName, type, mappings);
				}
			} else
			{
				logger.info("mappingPath in @Mapping has to be defined. Building mappings using @Field");
			}
		}

		XContentBuilder xContentBuilder = null;
		try
		{
			xContentBuilder = MappingBuilder.buildMapping(clazz, type);

		} catch (Exception e)
		{
			logger.error("Failed to build mapping for " + clazz.getSimpleName(), e);
		}
		return putMapping(indexName, type, xContentBuilder);

	}

	@SuppressWarnings("rawtypes")
	public boolean putMapping(String indexName, String type, Object mapping)
	{
		Assert.notNull(indexName, "No index defined for putMapping()");
		Assert.notNull(type, "No type defined for putMapping()");
		PutMappingRequestBuilder requestBuilder = client.admin().indices().preparePutMapping(indexName).setType(type);
		if (mapping instanceof String)
		{
			requestBuilder.setSource(String.valueOf(mapping));
		} else if (mapping instanceof Map)
		{
			requestBuilder.setSource((Map) mapping);
		} else if (mapping instanceof XContentBuilder)
		{
			requestBuilder.setSource((XContentBuilder) mapping);
		}
		return requestBuilder.execute().actionGet().isAcknowledged();
	}

	public static String readFileFromClasspath(String url)
	{
		StringBuilder stringBuilder = new StringBuilder();

		BufferedReader bufferedReader = null;

		try
		{
			ClassPathResource classPathResource = new ClassPathResource(url);
			InputStreamReader inputStreamReader = new InputStreamReader(classPathResource.getInputStream());
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;

			String lineSeparator = System.getProperty("line.separator");
			while ((line = bufferedReader.readLine()) != null)
			{
				stringBuilder.append(line).append(lineSeparator);
			}
		} catch (Exception e)
		{
			logger.debug(String.format("Failed to load file from url: %s: %s", url, e.getMessage()));
			return null;
		} finally
		{
			if (bufferedReader != null)
				try
				{
					bufferedReader.close();
				} catch (IOException e)
				{
					logger.debug(String.format("Unable to close buffered reader.. %s", e.getMessage()));
				}
		}

		return stringBuilder.toString();
	}

}
