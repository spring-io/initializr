/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.spring.initializr.generator.version.VersionRange;

import org.springframework.context.annotation.Conditional;

/**
 * Condition that matches when a generated project is using a matching version of the
 * platform.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnPlatformVersionCondition.class)
public @interface ConditionalOnPlatformVersion {

	/**
	 * The {@linkplain VersionRange version ranges} to check. The condition matches when
	 * at least one range matches the platform version.
	 * @return the version ranges to check
	 */
	String[] value();

}
