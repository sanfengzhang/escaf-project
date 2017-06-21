package com.escaf.esservice;

import java.io.IOException;
import java.sql.SQLFeatureNotSupportedException;

import org.nlpcn.es4sql.exception.SqlParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.escaf.esservice.api.EscafSearchDao;
import com.escaf.esservice.search.ElasticPage;

@Controller
public class IndexSearchController
{

	@Autowired
	private EscafSearchDao escafSearchDao;

	@RequestMapping(value = "search.action")
	@ResponseBody
	public String search()
	{

		try
		{
			@SuppressWarnings("unchecked")
			ElasticPage<Shakespeare> page = (ElasticPage<Shakespeare>) escafSearchDao
					.queryForPage("SELECT * FROM shakespeare/line limit 60,10 order by line_id", Shakespeare.class, 10);
			for (Shakespeare map : page.getResult())
			{

				System.out.println(map.toString());
			}
		} catch (SQLFeatureNotSupportedException | SqlParseException | IOException e)
		{
			
			e.printStackTrace();
		}

		return "SUCCESS";
	}

}
