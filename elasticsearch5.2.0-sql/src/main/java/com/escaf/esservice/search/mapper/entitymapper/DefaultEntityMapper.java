package com.escaf.esservice.search.mapper.entitymapper;

import java.io.IOException;

import com.escaf.esservice.search.geo.CustomGeoModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultEntityMapper implements EntityMapper
{

	private ObjectMapper objectMapper;

	public DefaultEntityMapper()
	{
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		objectMapper.registerModule(new CustomGeoModule());

	}

	public String mapToString(Object object) throws IOException
	{

		return objectMapper.writeValueAsString(object);
	}

	public <T> T mapToObject(String source, Class<T> clazz) throws IOException
	{

		return objectMapper.readValue(source, clazz);
	}

}
