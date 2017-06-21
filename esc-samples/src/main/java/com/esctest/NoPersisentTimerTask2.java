package com.esctest;

import com.escframework.support.quartz.CronTimerTask;
import com.escframework.support.quartz.TimerTask;

//@TimerTask
public class NoPersisentTimerTask2
{
	
	@CronTimerTask(cron = "0/4 * * * * ?", name = "NoPersisentTimerTask2", group = "test1", startDelay = 6* 1000L)
	public  void task() 
	{
		System.out.println("NoPersisentTimerTask2");
		
	}


}
