/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;

/**
 * Specialization of {@link Configuration} for configuration of project generation, with
 * {@link Configuration#proxyBeanMethods()} set to {@code false} by default.
 * <p>
 * Project generation configuration classes are regular Spring {@link Configuration}
 * beans. They are located using
 * {@code META-INF/spring/io.spring.initializr.generator.project.ProjectGenerationConfiguration.imports}
 * files, one fully qualified class name per line. Registering them in
 * {@code META-INF/spring.factories} is deprecated. Project generation beans can be
 * {@link Conditional @Conditional} beans, usually based on the state of the
 * {@link ProjectDescription} for which the {@link ProjectGenerationContext} was created.
 * <p>
 * {@link ProjectGenerationConfiguration @ProjectGenerationConfiguration}-annotated types
 * should not be processed by the main {@link ApplicationContext} so make sure regular
 * classpath scanning is not enabled for packages where such configuration classes reside.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface ProjectGenerationConfiguration {

	/**
	 * Specify whether {@code @Bean} methods should get proxied in order to enforce bean
	 * lifecycle behavior, e.g. to return shared singleton bean instances even in case of
	 * direct {@code @Bean} method calls in user code.
	 * @see Configuration#proxyBeanMethods()
	 * @return whether a CGLIB proxy should be created to enforce bean lifecycle behavior
	 */
	@AliasFor(annotation = Configuration.class)
	boolean proxyBeanMethods() default false;

}
