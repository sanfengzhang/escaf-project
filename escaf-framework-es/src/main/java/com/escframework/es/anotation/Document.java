package com.escframework.es.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.annotation.Persistent;

@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Document
{

	String indexName();
	
	Class<?>  indexStrategyClass() default Object.class;

	String type() default "";

	boolean useServerConfiguration() default false;

	short shards() default 5;

	short replicas() default 1;

	String refreshInterval() default "1s";

	String indexStoreType() default "fs";

	boolean createIndex() default true;
}