package com.escaf.esservice.core.task;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import com.escframework.es.ElasticsearchRequest;
import com.escframework.es.IndexNameStrategy;
import com.escframework.es.anotation.Document;
import com.escframework.es.auto.ESIndexAutoScanBean;
import com.escframework.support.quartz.CronTimerTask;
import com.escframework.support.quartz.EscafQuartzJobBean;
import com.escframework.support.quartz.TimerTask;

@TimerTask
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CreateIndexTimerTask extends EscafQuartzJobBean
{

	private static final Logger logger = LoggerFactory.getLogger(CreateIndexTimerTask.class);

	@CronTimerTask(cron = "0 0 23 * * ?", name = "createIndexTimerTask", group = "ES_ClusterTask", startDelay = 60* 1000L)
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException
	{
		ApplicationContext applicationContext = getApplicationContext(context);

		try
		{
			ESIndexAutoScanBean esIndexEntityScanBean = (ESIndexAutoScanBean) applicationContext
					.getBean(ESIndexAutoScanBean.class);
			if (null != esIndexEntityScanBean)
			{

				Map<String, List<String>> entities = esIndexEntityScanBean.getStartCreatedIndexTimerTaskEntities();
				if (!CollectionUtils.isEmpty(entities))
				{
					ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
					ElasticsearchRequest elasticsearchRequest = applicationContext.getBean(ElasticsearchRequest.class);

					Set<Entry<String, List<String>>> set = entities.entrySet();
					Iterator<Entry<String, List<String>>> it = set.iterator();
					while (it.hasNext())
					{
						Entry<String, List<String>> en = it.next();
						String indexStrategyClassName = en.getKey();
						List<String> indexClassNames = en.getValue();

						Class<?> clazz = ClassUtils.forName(indexStrategyClassName, classLoader);
						IndexNameStrategy indexStrategy = (IndexNameStrategy) clazz.newInstance();
						for (String indexClassName : indexClassNames)
						{
							Class<?> indexClazz = ClassUtils.forName(indexClassName, classLoader);
							Document document = indexClazz.getAnnotation(Document.class);
							String indexName = null;
							if (null != indexClazz && !indexClazz.getSimpleName().equals("Object"))
							{

								indexName = indexStrategy.getIndexName(document.indexName());
								elasticsearchRequest.createIndexWithSettings(clazz, indexName);
								elasticsearchRequest.putMapping(clazz, indexName);

							} else
							{
								logger.warn("the class={} not found index strategy class", indexClassName);

							}
						}

					}
				}

			}
		} catch (Exception e)
		{
			logger.warn("execute timer task failed", e);
		}

	}

}
