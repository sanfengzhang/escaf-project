package com.escaf.esservice;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.escaf.esservice.api.EscafSearchDao;
import com.escaf.esservice.search.ElasticPage;

@Service
public class UserService/**  implementsApplicationListener<ContextRefreshedEvent>**/
{
	@Autowired
	private EscafSearchDao escafSearchDao;

	/*在Spring容器事件触发的时候如果有未捕捉的异常会导致，spring容器启动失败。可能会引起一连串的错误，使得怀疑是否是代码有问题。解决最先出现的问题，也许其他的问题也就解决了、*/
	public void onApplicationEvent(ContextRefreshedEvent event)
	{
		System.out.println(event.getApplicationContext().getBean(Client.class));
		try
		{
			@SuppressWarnings("unchecked")
			ElasticPage<Shakespeare> page = (ElasticPage<Shakespeare>) escafSearchDao.queryForPage(
					"SELECT * FROM shakespeares/line limit 60,10 order by line_id", Shakespeare.class, 10);
			for (Shakespeare map : page.getResult())
			{

				System.out.println(map.toString());
			}
		} catch (Exception e)
		{
			System.out.println("SQLFeatureNotSupportedException");

		}

	}

}
