/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code.groovy;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * {@link BuildCustomizer} that adds the dependencies required by projects written in
 * Groovy.
 *
 * @author Stephane Nicoll
 */
class GroovyDependenciesConfigurer implements BuildCustomizer<Build> {

	private final boolean isUsingGroovy4;

	GroovyDependenciesConfigurer(boolean isUsingGroovy4) {
		this.isUsingGroovy4 = isUsingGroovy4;
	}

	@Override
	public void customize(Build build) {
		String groupId = this.isUsingGroovy4 ? "org.apache.groovy" : "org.codehaus.groovy";
		build.dependencies().add("groovy", groupId, "groovy", DependencyScope.COMPILE);
	}

}
