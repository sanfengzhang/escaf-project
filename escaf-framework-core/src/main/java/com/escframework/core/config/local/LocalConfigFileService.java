package com.escframework.core.config.local;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.escframework.core.config.ConfigFileService;
import com.escframework.util.FileUtils;

public class LocalConfigFileService extends ConfigFileService
{

	private static final Logger logger = LoggerFactory.getLogger(LocalConfigFileService.class);

	@Override
	protected List<Resource> doLoadResource(String resourceType) throws FileNotFoundException, IOException
	{
		List<Resource> resources = new ArrayList<Resource>();
		switch (resourceType)
		{
		case FileUtils.XML_FILE_TTPE:
			// 加载spring配置文件
			resources.addAll(getResources(FileUtils.XML_FILE_TTPE));
			break;
		case FileUtils.PROP_FILE_TYPE:
			// 加载spring的资源文件配置文件
			resources.addAll(getResources(FileUtils.PROP_FILE_TYPE));
			break;
		default:
			throw new IllegalArgumentException("unsupport the file type exception type=" + resourceType);

		}
		logger.info("the resource type={} and size={}", resourceType, resources.size());
		return resources;

	}

	private List<Resource> getResources(String resourceType) throws IOException
	{

		String classPath = null;
		Resource[] resources = null;
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(
				getDefaultClassLoader());
		if (resourceType.equals(FileUtils.XML_FILE_TTPE))
		{
			classPath = RESOURCE_CONF_ROOT + "/spring";
			resources = resourcePatternResolver.getResources(classPath + "/*.xml");

		} else if (resourceType.equals(FileUtils.PROP_FILE_TYPE))
		{
			classPath = RESOURCE_CONF_ROOT + "/conf";
			resources = resourcePatternResolver.getResources(classPath + "/*.properties");
		}

		List<Resource> result = new ArrayList<Resource>();
		result.addAll(Arrays.asList(resources));

		return result;

	}

}
