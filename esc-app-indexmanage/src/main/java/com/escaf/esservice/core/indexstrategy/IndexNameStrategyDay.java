package com.escaf.esservice.core.indexstrategy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.escframework.es.IndexNameStrategy;

public class IndexNameStrategyDay extends IndexNameStrategy

{

	@Override
	protected Map<String, Object> changeMappingOrSettings(Map<String, Object> params)
	{

		return null;
	}

	@Override
	public String getIndexName(String indexNamePrffix)
	{
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String indexSuffix = df.format(date);
		String indexName = indexNamePrffix + "@" + indexSuffix;

		return indexName;
	}

}
