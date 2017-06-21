package com.escaf.esservice.api;

import java.io.IOException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.nlpcn.es4sql.SearchDao;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.query.QueryAction;

import com.escaf.esservice.search.ElasticPage;
import com.escaf.esservice.search.QueryActionElasticExecutor;
import com.escaf.esservice.search.mapper.resultmapper.ResultMapper;

/***
 * 该类主要提供使用ES-SQL的方式对ES数据查询的功能，并能返回指定的泛型。
 * @author HanLin
 *
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EscafSearchDao
{
	private Client client;

	private ResultMapper resultMapper;

	public EscafSearchDao()
	{
		super();
	}

	public EscafSearchDao(Client client, ResultMapper resultMapper)
	{

		this.client = client;
		this.resultMapper = resultMapper;
	}

	/**
	 * 返回原始的查询结果, 不做任何的数据结果类型转换，是ES的原始返回数据类型
	 * @param query
	 * @return
	 * @throws SQLFeatureNotSupportedException
	 * @throws SqlParseException
	 * @throws IOException
	 */
	public Object executeQuery(String query) throws SQLFeatureNotSupportedException, SqlParseException, IOException
	{

		SearchDao searchDao = new SearchDao(client);
		QueryAction queryAction = searchDao.explain(query);
		Object execution = QueryActionElasticExecutor.executeAnyAction(searchDao.getClient(), queryAction);
		return execution;
	}
	
	
	/**
	 * 默认返回List<Map>类型
	 * @param query 查询的SQL语句
	 * @return
	 * @throws SQLFeatureNotSupportedException
	 * @throws SqlParseException
	 * @throws IOException
	 */
	public List<Map<String,Object>> queryForList(String query)throws SQLFeatureNotSupportedException, SqlParseException, IOException
	{
		return (List<Map<String, Object>>) queryForList(query, Map.class);
	}

	/**
	 * 索引的基本查询 支持，返回指定实体的结果集 note:实体对象的field需要和索引的字段名称对应。	 * 
	 * @param query 查询的SQL语句
	 * @param clazz 指定返回的泛型类型
	 * @return
	 * @throws SQLFeatureNotSupportedException
	 * @throws SqlParseException
	 * @throws IOException
	 */
	public List<?> queryForList(String query, Class<?> clazz) throws SQLFeatureNotSupportedException, SqlParseException, IOException
	{
		Object results = executeQuery(query);
		return resultMapper.mapResults(results, clazz);

	}

	/**
	 * 分页查询，默认分页返回的的数目是每页15条数据。
	 * @param query 查询的SQL语句
	 * @param clazz 指定返回的泛型类型
	 * @return
	 * @throws SQLFeatureNotSupportedException
	 * @throws SqlParseException
	 * @throws IOException
	 */
	public ElasticPage<?> queryForPage(String query, Class<?> clazz) throws SQLFeatureNotSupportedException, SqlParseException, IOException
	{

		return queryForPage(query, clazz, 15);

	}

	/**
	 * 分页查询，可自己指定返回的条数
	 * 
	 * @param query 查询的SQL语句
	 * @param clazz  指定返回的泛型类型
	 * @param pageSize  返回的条数
	 * @return
	 * @throws SQLFeatureNotSupportedException
	 * @throws SqlParseException
	 * @throws IOException
	 */
	public ElasticPage<?> queryForPage(String query, Class<?> clazz, int pageSize)
			throws SQLFeatureNotSupportedException, SqlParseException, IOException
	{
		Object results = executeQuery(query);
		if (!(results instanceof SearchHits))
		{
			throw new IllegalArgumentException("query is not supported by this method,queryString");
		}
		SearchHits searchHits = (SearchHits) results;
		long totalHit = searchHits.getTotalHits();
		List<?> _list = resultMapper.mapResults(results, clazz);

		return new ElasticPage(_list, totalHit, pageSize);

	}

	/**
	 * 聚合、统计查询
	 * @param query
	 * @return
	 * @throws SQLFeatureNotSupportedException
	 * @throws SqlParseException
	 * @throws IOException
	 */
	public Map<String, Aggregation> queryAggregation(String query) throws SQLFeatureNotSupportedException, SqlParseException, IOException
	{
		Object results = executeQuery(query);
		if (!(results instanceof Aggregations))
		{
			throw new IllegalArgumentException("query is not aggregation action");
		}

		Aggregations aggregations = (Aggregations) results;
		
		return aggregations.getAsMap();

	}

}
