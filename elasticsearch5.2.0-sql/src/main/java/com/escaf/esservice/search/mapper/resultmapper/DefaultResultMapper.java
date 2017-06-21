package com.escaf.esservice.search.mapper.resultmapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;

import com.escaf.esservice.search.mapper.entitymapper.DefaultEntityMapper;
import com.escaf.esservice.search.mapper.entitymapper.EntityMapper;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * 
 * @author HanLin
 *
 */
public class DefaultResultMapper extends AbstractResultMapper
{

	public DefaultResultMapper()
	{
		super(new DefaultEntityMapper());
	}

	public DefaultResultMapper(EntityMapper entityMapper)
	{
		super(entityMapper);

	}
	
	

	public <T> List<T> mapResults(Object results, Class<T> clazz)
	{

		if (!(results instanceof SearchHits))
		{
			throw new IllegalArgumentException("query is not supported by this method,queryString");
		}
		SearchHits searchHits = (SearchHits) results;
		// long totalHit = searchHits.getTotalHits();

		List<T> list = new ArrayList<T>();
		for (SearchHit hit : searchHits)
		{
			if (hit != null)
			{
				T result = null;
				if (!StringUtils.isBlank(hit.sourceAsString()))
				{
					result = mapEntity(hit.sourceAsString(), clazz);
				} else
				{
					result = mapEntity(hit.getFields().values(), clazz);
				}
				list.add(result);
			}
		}

		return list;
	}

	private <T> T mapEntity(Collection<SearchHitField> values, Class<T> clazz)
	{
		return mapEntity(buildJSONFromFields(values), clazz);
	}

	private String buildJSONFromFields(Collection<SearchHitField> values)
	{
		JsonFactory nodeFactory = new JsonFactory();
		try
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			JsonGenerator generator = nodeFactory.createGenerator(stream, JsonEncoding.UTF8);
			generator.writeStartObject();
			for (SearchHitField value : values)
			{
				if (value.getValues().size() > 1)
				{
					generator.writeArrayFieldStart(value.getName());
					for (Object val : value.getValues())
					{
						generator.writeObject(val);
					}
					generator.writeEndArray();
				} else
				{
					generator.writeObjectField(value.getName(), value.getValue());
				}
			}
			generator.writeEndObject();
			generator.flush();
			return new String(stream.toByteArray(), Charset.forName("UTF-8"));
		} catch (IOException e)
		{
			return null;
		}
	}

}
