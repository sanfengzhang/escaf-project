package com.esctest;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.escframework.support.quartz.CronTimerTask;
import com.escframework.support.quartz.EscafQuartzJobBean;
import com.escframework.support.quartz.TimerTask;

@TimerTask
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class MyTimerTask extends EscafQuartzJobBean
{

	@CronTimerTask(cron = "0/5 * * * * ?", name = "testTimerTask", group = "test", startDelay = 6* 1000L)
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException
	{
		System.out.println("MyTimerTask");
		
	}

}
