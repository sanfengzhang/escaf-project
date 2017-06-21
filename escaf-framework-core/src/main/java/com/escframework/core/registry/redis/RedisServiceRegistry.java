package com.escframework.core.registry.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import com.escframework.core.registry.AbstractServiceRegistry;

/**
 * 
 * @author hanl
 *
 */
public class RedisServiceRegistry extends AbstractServiceRegistry
{

	public static final Logger LOGGER = LoggerFactory.getLogger(RedisServiceRegistry.class);

	@Autowired
	private StringRedisTemplate redis;

	@Override
	public boolean handle(String serviceId, String serviceAddress)
	{
		try
		{
			if (StringUtils.isEmpty(serviceId) || StringUtils.isEmpty(serviceId))
			{
				throw new NullPointerException("serviceName and serviceAddress must not be null");

			}
			SetOperations<String,String> hashOperations = redis.opsForSet();			
			hashOperations.add(serviceId, serviceAddress);

		} catch (Exception e)
		{
			LOGGER.warn("registry serviceId={},serviceAddress={} failed,cause by={}", serviceId, serviceAddress, e);
			return false;

		}

		return true;
	}

}
