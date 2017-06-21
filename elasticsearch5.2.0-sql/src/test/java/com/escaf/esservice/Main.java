package com.escaf.esservice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;
import org.nlpcn.es4sql.exception.SqlParseException;

import com.escaf.esservice.api.EscafSearchDao;
import com.escaf.esservice.search.mapper.resultmapper.DefaultResultMapper;

public class Main
{
	private TransportClient transportClient;

	private EscafSearchDao searchDao;

	@Test
	public void termTest()
	{
		SearchRequestBuilder sBuilder = transportClient.prepareSearch("my_store");
		// TermQueryBuilder termQueryBuilder = new TermQueryBuilder("price",
		// 20);
		TermQueryBuilder termQueryBuilder = new TermQueryBuilder("productID", "XHDK-A-1293-#fJ3");
		sBuilder.setQuery(termQueryBuilder);

		System.out.println(sBuilder.toString());
		SearchResponse searchResponse = sBuilder.execute().actionGet();
		SearchHits searchHits = searchResponse.getHits();
		if (null != searchHits)
		{
			SearchHit[] searchHitss = searchHits.getHits();
			if (searchHitss.length > 0)
			{
				for (SearchHit searchHit : searchHitss)
				{
					System.out.println(searchHit.getSourceAsString());
				}
			}

		}

		// 注意:在查询productID的精确查询的时候，sql的方式和原生的查询结果不一致。是因为sql的方式在查询语句中使用了 "type":
		// "phrase",并且最主要的原因是两种方式下的查询ES语句是有区别

		try
		{
			String termSql = "select * from my_store where productID='XHDK-A-1293-#fJ3' ";

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> _list = (List<Map<String, Object>>) searchDao.queryForList(termSql, Map.class);
			System.out.println(_list);
			@SuppressWarnings("unchecked")
			List<Product> list = (List<Product>) searchDao.queryForList(termSql, Product.class);

			list.stream().forEach(e ->
			{
				System.out.println(e);
			});
		} catch (Exception e)
		{

			e.printStackTrace();
		}

	}

	@Test
	public void testSearchDao() throws SQLFeatureNotSupportedException, SqlParseException, IOException
	{
		String termSql = "select * from shakespeare ";

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> _list = (List<Map<String, Object>>) searchDao.queryForList(termSql, Map.class);
		System.out.println(_list);
	}

	@Before
	public void setUp()
	{
		Settings settings = Settings.builder().put("cluster.name", "es-cluster").put("client.transport.sniff", true)
				.build();
		transportClient = new PreBuiltTransportClient(settings);

		transportClient.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("127.0.0.1", 9300)));
		transportClient.connectedNodes();

		searchDao = new EscafSearchDao(transportClient, new DefaultResultMapper());

	}

}
