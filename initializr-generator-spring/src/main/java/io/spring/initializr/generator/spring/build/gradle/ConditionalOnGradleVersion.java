/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build.gradle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

/**
 * Condition that matches when a generated project will use a particular major version of
 * Gradle.
 *
 * @author Andy Wilkinson
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnGradleVersionCondition.class)
public @interface ConditionalOnGradleVersion {

	/**
	 * The Gradle generations that should be checked. The condition matches when at least
	 * one gradle generation matches.
	 * @return the gradle generations to check
	 */
	String[] value();

}
