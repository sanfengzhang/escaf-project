package com.escaf.esservice.search.mapper.resultmapper;

import java.util.List;

public interface SearchResultMapper
{

	<T> List<T> mapResults(Object results, Class<T> clazz) ;
}
