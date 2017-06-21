package com.esctest;

import com.escframework.support.quartz.CronTimerTask;
import com.escframework.support.quartz.TimerTask;

//@TimerTask
public class NoPersisentTimerTask
{
	
	@CronTimerTask(cron = "0/6 * * * * ?", name = "NoPersisentTimerTask", group = "test", startDelay = 6* 1000L)
	public  void task() 
	{
		System.out.println("NoPersisentTimerTask");
		
	}


}
