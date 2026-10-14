/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.spring.initializr.generator.project.ProjectDescription;

import org.springframework.context.annotation.Conditional;

/**
 * Condition that matches when a {@link ProjectDescription} defines a particular
 * dependency. A generated project may ultimately define a different set of dependencies
 * according to the contributors that have been executed. To contribute to the project
 * according to the real set, prefer querying the model itself rather than using this
 * condition.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnRequestedDependencyCondition.class)
public @interface ConditionalOnRequestedDependency {

	/**
	 * The identifier of the dependency.
	 * @return the dependency ID
	 */
	String value();

}
