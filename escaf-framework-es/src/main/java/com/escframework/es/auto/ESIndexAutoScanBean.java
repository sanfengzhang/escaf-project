package com.escframework.es.auto;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.elasticsearch.common.collect.MapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import com.escframework.es.ElasticsearchRequest;

import com.escframework.es.IndexNameStrategy;

import com.escframework.es.anotation.Document;

public class ESIndexAutoScanBean implements InitializingBean, Ordered
{
	private static final Logger logger = LoggerFactory.getLogger(ESIndexAutoScanBean.class);

	static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
			this.resourcePatternResolver);

	private ElasticsearchRequest elasticsearchRequest;

	private String[] scanESEntityPackages;

	private Map<String, List<String>> startCreatedIndexTimerTaskEntities = new ConcurrentHashMap<String, List<String>>();

	public ESIndexAutoScanBean()
	{

	}

	public ESIndexAutoScanBean(ElasticsearchRequest elasticsearchRequest, String[] scanESEntityPackages)
	{
		this.elasticsearchRequest = elasticsearchRequest;
		this.scanESEntityPackages = scanESEntityPackages;
	}

	private void scan(String basePackage)
	{
		String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basePackage
				+ DEFAULT_RESOURCE_PATTERN;
		try
		{
			Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
			boolean traceEnabled = logger.isTraceEnabled();
			for (Resource resource : resources)
			{
				if (traceEnabled)
				{
					logger.trace("Scanning " + resource);
				}
				MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
				AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
				boolean has = annotationMetadata.hasAnnotation(Document.class.getName());

				if (has)
				{
					Map<String, Object> annotationMaps = annotationMetadata
							.getAnnotationAttributes(Document.class.getName());

					if (!(Boolean) annotationMaps.get("createIndex"))
					{
						continue;
					}

					Class<?> clazz = (Class<?>) annotationMaps.get("indexStrategyClass");
					String indexName = null;
					if (null != clazz && !clazz.getSimpleName().equals("Object"))
					{

						IndexNameStrategy indexStrategy = (IndexNameStrategy) clazz.newInstance();

						indexName = indexStrategy.getIndexName((String) annotationMaps.get("indexName"));
						String indexStrategyClassName = clazz.getName();

						if (!startCreatedIndexTimerTaskEntities.containsKey(indexStrategyClassName))
						{
							startCreatedIndexTimerTaskEntities.put(indexStrategyClassName, new LinkedList<String>());
						}
						List<String> list = startCreatedIndexTimerTaskEntities.get(indexStrategyClassName);
						list.add(metadataReader.getClassMetadata().getClassName());
						startCreatedIndexTimerTaskEntities.put(indexStrategyClassName, list);

					} else
					{
						indexName = (String) annotationMaps.get("indexName");

					}

					boolean exist = elasticsearchRequest.indexExists(indexName);
					if (exist)
					{
						continue;
					}

					Map<String, String> settings = new MapBuilder<String, String>()
							.put("index.number_of_shards", String.valueOf(annotationMaps.get("shards")))
							.put("index.number_of_replicas", String.valueOf(annotationMaps.get("replicas")))
							.put("index.refresh_interval", String.valueOf(annotationMaps.get("refreshInterval")))
							.put("index.store.type", String.valueOf(annotationMaps.get("indexStoreType"))).map();

					elasticsearchRequest.createIndexWithSettings(indexName, settings);
					Class<?> indexClazz = ClassUtils.resolveClassName(metadataReader.getClassMetadata().getClassName(),
							ClassUtils.getDefaultClassLoader());
					elasticsearchRequest.putMapping(indexClazz, indexName);

				}

			}

		} catch (IOException e)
		{
			e.printStackTrace();

		} catch (LinkageError e)
		{

			e.printStackTrace();
		} catch (InstantiationException e)
		{

			e.printStackTrace();
		} catch (IllegalAccessException e)
		{

			e.printStackTrace();
		}

	}

	public void setElasticsearchRequest(ElasticsearchRequest elasticsearchRequest)
	{
		this.elasticsearchRequest = elasticsearchRequest;
	}

	public void setScanESEntityPackages(String[] scanESEntityPackages)
	{
		this.scanESEntityPackages = scanESEntityPackages;
	}

	public int getOrder()
	{
		return Integer.MAX_VALUE;
	}

	public Map<String, List<String>> getStartCreatedIndexTimerTaskEntities()
	{
		return startCreatedIndexTimerTaskEntities;
	}

	public void setStartCreatedIndexTimerTaskEntities(Map<String, List<String>> startCreatedIndexTimerTaskEntities)
	{
		this.startCreatedIndexTimerTaskEntities = startCreatedIndexTimerTaskEntities;
	}

	public void afterPropertiesSet() throws Exception
	{
		for (String basePackage : scanESEntityPackages)
		{
			this.scan(basePackage);
		}

	}

}
