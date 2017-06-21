package com.esctest;

import java.util.List;

import javax.transaction.Transactional;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.escframework.support.quartz.QuartzScheduleService;
import com.esctest.dao.StudentDao;

@Controller
public class LoginController
{

	@Autowired
	private QuartzScheduleService escafSchedulerService;

	@Autowired
	private StudentDao studentDao;

	@Autowired
	private UserService usersevice;

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping(value = "login.action")
	@ResponseBody
	public String login()
	{
		List<Object[]> _list = studentDao.queryListResultBySQL("select * from t_student where stu_name=?",
				new Object[] { "zhangsan" });
		for (Object[] os : _list)
		{
			System.out.println(os[0].toString());
		}

		return "success";
	}

	@RequestMapping(value = "quartz.action")
	@ResponseBody
	@Transactional
	public String testQuartzScheduleService(@RequestParam String jobName, @RequestParam String group)
	{
		JobDetail jobDetail = null;
		try
		{
			jobDetail = escafSchedulerService.getJobDetail(jobName, group);
			escafSchedulerService.pauseJob(jobName, group);
		} catch (SchedulerException e)
		{
			e.printStackTrace();
		}
		logger.info("---------jobDetail={}----------", jobDetail.getJobClass().toString());
		
		
		return "success";
	}
	
	@RequestMapping(value = "resume.action")
	@ResponseBody
	@Transactional
	public String testQuartzScheduleServiceResume(@RequestParam String jobName, @RequestParam String group)
	{
		JobDetail jobDetail = null;
		try
		{
			jobDetail = escafSchedulerService.getJobDetail(jobName, group);
			escafSchedulerService.resumeJob(jobName, group);
		} catch (SchedulerException e)
		{
			e.printStackTrace();
		}
		logger.info("---------jobDetail={}----------", jobDetail.getJobClass().toString());
		
		
		return "success";
	}

}
