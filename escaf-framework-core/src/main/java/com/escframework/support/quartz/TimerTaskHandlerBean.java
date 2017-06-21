package com.escframework.support.quartz;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.CollectionUtils;

public class TimerTaskHandlerBean implements InitializingBean, ApplicationContextAware, Ordered
{

	public static final String CLUSTER_SCHEDULER_FACTORY_BEAN_NAME = "cluserSchedulerFactoryBean";

	public static final String SCHEDULER_FACTORY_BEAN_NAME = "schedulerFactoryBean";

	@Autowired
	private ConfigurableListableBeanFactory beanFactory;

	@Autowired
	private DataSource dataSource;

	private ApplicationContext applicationContext;

	private static final Logger LOGGER = LoggerFactory.getLogger(TimerTaskHandlerBean.class);

	public void handleTimerTask()
	{
		Map<String, Object> timerTaskBeans = beanFactory.getBeansWithAnnotation(TimerTask.class);
		if (CollectionUtils.isEmpty(timerTaskBeans))
		{
			return;
		}

		Set<Entry<String, Object>> timerTaskBeansSet = timerTaskBeans.entrySet();
		Iterator<Entry<String, Object>> it = timerTaskBeansSet.iterator();

		Map<String, Object> noPersistentJobsMap = new HashMap<String, Object>();

		Map<String, Object> persistentJobsMap = new HashMap<String, Object>();
		while (it.hasNext())
		{
			Entry<String, Object> en = it.next();
			Object taskBean = en.getValue();
			if (taskBean instanceof EscafQuartzJobBean)
			{
				persistentJobsMap.put(en.getKey(), taskBean);
			} else
			{

				noPersistentJobsMap.put(en.getKey(), taskBean);

			}
		}

		noPersistentJob(noPersistentJobsMap);

		persistentJob(persistentJobsMap);

		// Note:放在spring配置文件中，会带来在没有timerTask的项目中，启动的时候因为没注册schedulerFactoryBean
		// 会报初始化escafSchedulerService失败。所以只在注册了schedulerFactoryBean的bean才注入escafSchedulerService
		// beanFactory.registerSingleton("escafSchedulerService", new
		// QuartzScheduleService());
		if (beanFactory instanceof DefaultListableBeanFactory)
		{
			DefaultListableBeanFactory newBeanFactory = (DefaultListableBeanFactory) beanFactory;
			BeanDefinition beanDefinition = new RootBeanDefinition(QuartzScheduleService.class);
			newBeanFactory.registerBeanDefinition("escafSchedulerService", beanDefinition);
		}

	}

	private void persistentJob(Map<String, Object> persistentJobsMap)
	{
		if (CollectionUtils.isEmpty(persistentJobsMap))
		{
			return;
		}

		Set<Entry<String, Object>> timerTaskBeansSet = persistentJobsMap.entrySet();
		Iterator<Entry<String, Object>> it = timerTaskBeansSet.iterator();
		List<Trigger> triggers = new ArrayList<Trigger>();
		while (it.hasNext())
		{
			Entry<String, Object> en = it.next();
			Object taskBean = en.getValue();
			Class<?> clazz = taskBean.getClass();
			if (null == clazz)
			{
				continue;

			}

			Method[] methods = clazz.getDeclaredMethods();
			for (Method method : methods)
			{
				CronTimerTask cronTimerTask = method.getAnnotation(CronTimerTask.class);
				if (null == cronTimerTask)
				{
					continue;
				}
				String name = cronTimerTask.name();
				String group = cronTimerTask.group();
				String cron = cronTimerTask.cron();
				/**
				 * jobDetail的方式需要在实现的job类中加入@PersistJobDataAfterExecution @DisallowConcurrentExecution//
				 * 不允许并发执行
				 */
				// boolean concurrent = cronTimerTask.concurrent();
				long startDelay = cronTimerTask.startDelay();

				JobDetailFactoryBean jobDetailFactoryBean = jobDetailFactoryBean(name, group, clazz);
				CronTriggerFactoryBean cronTriggerFactoryBean = cronTriggerFactoryBean(name, cron, startDelay,
						jobDetailFactoryBean.getObject());
				triggers.add(cronTriggerFactoryBean.getObject());
			}

		}
		String confClassPath = "com/escframework/support/quartz/quartz_job_persistent_conf.properties";
		SchedulerFactoryBean schedulerFactoryBean = schedulerFactoryBean(triggers, dataSource,
				EscafQuartzJobBean.applicationContextSchedulerContextKey, confClassPath);

		// 交给spring管理，这样不需要额外的一些操作：如：在spring容器或程序销毁的时候需要手动去销毁资源。
		beanFactory.registerSingleton(CLUSTER_SCHEDULER_FACTORY_BEAN_NAME, schedulerFactoryBean);

	}

	/**
	 * 无持久化的job执行，单机执行任务。在集群环境下不保证同一时刻只有一个任务实例在运行。 开发者使用的时候可以直接通过spring方式注入类。
	 */
	private void noPersistentJob(Map<String, Object> noPersistentJobsMap)
	{

		if (CollectionUtils.isEmpty(noPersistentJobsMap))
		{
			return;
		}
		Set<Entry<String, Object>> timerTaskBeansSet = noPersistentJobsMap.entrySet();
		Iterator<Entry<String, Object>> it = timerTaskBeansSet.iterator();
		List<Trigger> triggers = new ArrayList<Trigger>();
		while (it.hasNext())
		{
			Entry<String, Object> en = it.next();
			Object taskBean = en.getValue();
			Class<?> clazz = taskBean.getClass();
			if (null == clazz)
			{
				continue;

			}

			Method[] methods = clazz.getDeclaredMethods();
			for (Method method : methods)
			{
				CronTimerTask cronTimerTask = method.getAnnotation(CronTimerTask.class);
				if (null == cronTimerTask)
				{
					continue;
				}
				String name = cronTimerTask.name();
				String group = cronTimerTask.group();
				String cron = cronTimerTask.cron();
				boolean concurrent = cronTimerTask.concurrent();
				long startDelay = cronTimerTask.startDelay();

				MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean = methodInvokingJobDetailFactoryBean(
						name, group, concurrent, taskBean, method);

				methodInvokingJobDetailFactoryBean.setBeanFactory(beanFactory);
				CronTriggerFactoryBean cronTriggerFactoryBean = cronTriggerFactoryBean(name, cron, startDelay,
						methodInvokingJobDetailFactoryBean.getObject());

				triggers.add(cronTriggerFactoryBean.getObject());

			}

		}

		SchedulerFactoryBean schedulerFactoryBean = schedulerFactoryBean(triggers, null, null, null);
		// schedulerFactoryBean.start();
		// 要注入到容器中交给容器管理
		beanFactory.registerSingleton(SCHEDULER_FACTORY_BEAN_NAME, schedulerFactoryBean);
	}

	private SchedulerFactoryBean schedulerFactoryBean(List<Trigger> triggers, DataSource dataSource,
			String applicationContextSchedulerContextKey, String configLocation)
	{
		final SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

		Trigger[] triggerArr = new Trigger[triggers.size()];
		schedulerFactoryBean.setTriggers(triggers.toArray(triggerArr));
		if (null != dataSource)
		{
			schedulerFactoryBean.setDataSource(dataSource);
		}

		if (null != applicationContextSchedulerContextKey)
		{
			schedulerFactoryBean.setApplicationContextSchedulerContextKey(applicationContextSchedulerContextKey);
			schedulerFactoryBean.setApplicationContext(applicationContext);
		}

		if (null != configLocation)
		{
			ClassPathResource quartzClusterConfResource = new ClassPathResource(configLocation);
			schedulerFactoryBean.setConfigLocation(quartzClusterConfResource);
		}
		try
		{
			schedulerFactoryBean.afterPropertiesSet();
		} catch (Exception e)
		{

			e.printStackTrace();
		}

		return schedulerFactoryBean;
	}

	private JobDetailFactoryBean jobDetailFactoryBean(String name, String group, Class<?> jobClass)
	{
		JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
		jobDetailFactoryBean.setName(name);
		jobDetailFactoryBean.setGroup(group);
		jobDetailFactoryBean.setJobClass(jobClass);
		jobDetailFactoryBean.setDurability(true);
		jobDetailFactoryBean.setRequestsRecovery(true);

		jobDetailFactoryBean.afterPropertiesSet();

		return jobDetailFactoryBean;

	}

	private MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean(String name, String group,
			boolean concurrent, Object taskBean, Method method)
	{
		MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();

		methodInvokingJobDetailFactoryBean.setName(name);
		methodInvokingJobDetailFactoryBean.setGroup(group);
		methodInvokingJobDetailFactoryBean.setConcurrent(concurrent);

		methodInvokingJobDetailFactoryBean.setTargetObject(taskBean);
		methodInvokingJobDetailFactoryBean.setTargetMethod(method.getName());
		try
		{
			methodInvokingJobDetailFactoryBean.afterPropertiesSet();
		} catch (ClassNotFoundException e)
		{

			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{

			e.printStackTrace();
		}

		return methodInvokingJobDetailFactoryBean;

	}

	private CronTriggerFactoryBean cronTriggerFactoryBean(String name, String cron, long startDelay,
			JobDetail jobDetail)
	{
		CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
		cronTriggerFactoryBean.setJobDetail(jobDetail);
		cronTriggerFactoryBean.setCronExpression(cron);
		cronTriggerFactoryBean.setStartDelay(startDelay);
		cronTriggerFactoryBean.setName(name);

		try
		{
			cronTriggerFactoryBean.afterPropertiesSet();

		} catch (ParseException e)
		{

			e.printStackTrace();
		}

		return cronTriggerFactoryBean;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;

	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		LOGGER.info("timerTaskBean execute...");
		this.handleTimerTask();

	}

	@Override
	public int getOrder()
	{

		return Integer.MAX_VALUE;
	}

}
