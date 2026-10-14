/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.spring.initializr.generator.packaging.Packaging;

import org.springframework.context.annotation.Conditional;

/**
 * Condition that matches when a generated project will use a particular
 * {@link Packaging}.
 *
 * @author Andy Wilkinson
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnPackagingCondition.class)
public @interface ConditionalOnPackaging {

	String value();

}
