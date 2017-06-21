package com.escframework.support.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

public abstract class EscafQuartzJobBean extends QuartzJobBean
{
	
	public static final String applicationContextSchedulerContextKey="applicationContextKey";

	protected ApplicationContext getApplicationContext(final JobExecutionContext jobexecutioncontext)
	{
		try
		{
			return (ApplicationContext) jobexecutioncontext.getScheduler().getContext().get(applicationContextSchedulerContextKey);
		} catch (SchedulerException e)
		{

			e.printStackTrace();
		}

		throw new NullPointerException("quartz job get applicationContext null.");
	}

}
