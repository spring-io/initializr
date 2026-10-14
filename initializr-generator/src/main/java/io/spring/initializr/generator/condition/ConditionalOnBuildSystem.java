/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.spring.initializr.generator.buildsystem.BuildSystem;

import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AliasFor;

/**
 * Condition that matches when a generated project will use a particular
 * {@link BuildSystem}.
 *
 * @author Andy Wilkinson
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnBuildSystemCondition.class)
public @interface ConditionalOnBuildSystem {

	/**
	 * The ID of the {@link BuildSystem} that should be checked.
	 * @return the build system to check. An alias for {@link #id()}.
	 */
	@AliasFor("id")
	String value() default "";

	/**
	 * The ID of the {@link BuildSystem} that should be checked.
	 * @return the build system to check
	 */
	@AliasFor("value")
	String id() default "";

	/**
	 * The dialect of the {@link BuildSystem} that should be checked. When not specified,
	 * any dialect will be matched.
	 * @return the dialect to check, if any
	 */
	String dialect() default "";

}
