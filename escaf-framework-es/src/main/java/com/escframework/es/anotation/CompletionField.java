package com.escframework.es.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface CompletionField {

	String searchAnalyzer() default "simple";

	String analyzer() default "simple";

	boolean payloads() default false;

	boolean preserveSeparators() default true;

	boolean preservePositionIncrements() default true;

	int maxInputLength() default 50;
}
