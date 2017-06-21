package com.escframework.support.quartz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * 可以提供给应用来调用对jobs进行管理
 * 
 * @author HanL
 *
 */
public class QuartzScheduleService
{

	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzScheduleService.class);

	@Resource(name = TimerTaskHandlerBean.CLUSTER_SCHEDULER_FACTORY_BEAN_NAME)
	private Scheduler scheduler;

	public JobDetail getJobDetail(String name, String group) throws SchedulerException
	{
		JobKey jobKey = new JobKey(name, group);
		JobDetail jobDetail = scheduler.getJobDetail(jobKey);
		return jobDetail;
	}

	public void resumeJob(String name, String group) throws SchedulerException
	{
		JobKey jobKey = new JobKey(name, group);
		LOGGER.info("resumeJob job jobKey={}", jobKey.toString());
		scheduler.resumeJob(jobKey);

	}

	public void pauseJob(String name, String group) throws SchedulerException
	{
		JobKey jobKey = new JobKey(name, group);
		LOGGER.info("pauseJob job jobKey={}", jobKey.toString());
		scheduler.pauseJob(jobKey);

	}

	public void unSchedule(String name, String group) throws SchedulerException
	{
		TriggerKey triggerKey = new TriggerKey(name, group);
		LOGGER.info("unSchedule job triggerKey={}", triggerKey.toString());
		scheduler.unscheduleJob(triggerKey);

	}

	public void unSchedule(List<Map<String, String>> params) throws SchedulerException
	{

		List<TriggerKey> triggerKeys = new ArrayList<TriggerKey>();
		for (Map<String, String> map : params)
		{

			if (map.size() > 1)
			{
				LOGGER.debug("unSchedule job then params size>1 params={}", map.toString());
			}
			Set<Map.Entry<String, String>> set = map.entrySet();
			Iterator<Map.Entry<String, String>> it = set.iterator();

			while (it.hasNext())
			{
				Map.Entry<String, String> en = it.next();

				triggerKeys.add(new TriggerKey(en.getKey(), en.getValue()));
			}

		}
		if (!CollectionUtils.isEmpty(triggerKeys))
		{
			LOGGER.info("unSchedule jobs triggerKeys={}", triggerKeys.toString());
			scheduler.unscheduleJobs(triggerKeys);

		}
	}

}
