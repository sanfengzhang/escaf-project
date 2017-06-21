package com.escframework.support.quartz;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CronTimerTask
{

	String cron() default "";

	String name() default "";

	String group() default "";

	boolean concurrent() default false;

	long startDelay() default 0L;

	boolean enableStore() default false;

}
