package com.escframework.es.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InnerField {

	String suffix();

	FieldType type();

	FieldIndex index() default FieldIndex.analyzed;

	boolean store() default false;

	String searchAnalyzer() default "";

	String indexAnalyzer() default "";
}
