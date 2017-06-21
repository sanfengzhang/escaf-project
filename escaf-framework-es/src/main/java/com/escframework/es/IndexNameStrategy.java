package com.escframework.es;

import java.util.Map;

public abstract class IndexNameStrategy
{

	/** 按规则生成索引的名字的策略 */
	public abstract String getIndexName(String indexNamePrffix);

	/** 需要改变索引的设置或者mappings */
	protected abstract Map<String, Object> changeMappingOrSettings(Map<String, Object> params);

}
