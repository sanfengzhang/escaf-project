package com.esctest.es;

import java.io.IOException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.Map;

import org.nlpcn.es4sql.exception.SqlParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.json.JSONArray;
import com.alibaba.dubbo.common.json.JSONObject;
import com.escaf.esservice.api.EscafSearchDao;
import com.escaf.esservice.search.ElasticPage;

@Controller
public class EsSearchController
{

	@Autowired
	private EscafSearchDao searchDao;

	@RequestMapping(value = "esquery.action")
	@ResponseBody
	public String testEsQuery()
	{

		String termSql = "select * from my_store where productID='XHDK-A-1293-#fJ3' ";

		List<Map<String, Object>> _list = null;
		try
		{
			_list = (List<Map<String, Object>>) searchDao.queryForList(termSql, Map.class);
		} catch (SQLFeatureNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SqlParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return com.alibaba.fastjson.JSONObject.toJSONString(_list);
	}

	@RequestMapping(value = "esquery1.action")
	@ResponseBody
	public String testEsQueryShakspeeker()
	{
		String termSql = "select * from shakespeare ";

		List<Map<String, Object>> _list = null;
		try
		{
			_list = (List<Map<String, Object>>) searchDao.queryForList(termSql, Map.class);
		} catch (SQLFeatureNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SqlParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return com.alibaba.fastjson.JSONObject.toJSONString(_list);
	}
	
	@RequestMapping(value = "esquerypage.action")
	@ResponseBody
	public String testEsQueryPage(){
		String termSql = "select * from shakespeare limit 60,10";

	
		ElasticPage<Map> page=null;
		try
		{
			 page= (ElasticPage<Map>) searchDao.queryForPage(termSql, Map.class);
		} catch (SQLFeatureNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SqlParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return com.alibaba.fastjson.JSONObject.toJSONString(page.getResult());
	}

}
