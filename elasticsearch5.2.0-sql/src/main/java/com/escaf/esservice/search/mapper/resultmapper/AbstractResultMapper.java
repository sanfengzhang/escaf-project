package com.escaf.esservice.search.mapper.resultmapper;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.IOException;

import com.escaf.esservice.search.EscafElasticException;
import com.escaf.esservice.search.mapper.entitymapper.EntityMapper;

public abstract class AbstractResultMapper implements ResultMapper
{

	private EntityMapper entityMapper;

	public AbstractResultMapper(EntityMapper entityMapper)
	{
		this.entityMapper = entityMapper;
	}

	public <T> T mapEntity(String source, Class<T> clazz)
	{
		if (isBlank(source))
		{
			return null;
		}
		try
		{
			return entityMapper.mapToObject(source, clazz);
		} catch (IOException e)
		{
			throw new EscafElasticException("failed to map source [ " + source + "] to class " + clazz.getSimpleName(), e);
		}
	}

	public EntityMapper getEntityMapper()
	{
		return this.entityMapper;
	}

}
