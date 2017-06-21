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
public @interface Field
{
	FieldType type() default FieldType.Auto;

	FieldIndex index() default FieldIndex.analyzed;

	DateFormat format() default DateFormat.none;

	String pattern() default "";

	boolean store() default false;

	String searchAnalyzer() default "";

	String analyzer() default "";

	String[] ignoreFields() default {};

	boolean includeInParent() default false;

	boolean isId() default false;

}
