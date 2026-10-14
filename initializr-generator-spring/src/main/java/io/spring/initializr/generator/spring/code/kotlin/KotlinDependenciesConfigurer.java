/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * {@link BuildCustomizer} that adds the dependencies required by projects written in
 * Kotlin.
 *
 * @author Andy Wilkinson
 * @author Moritz Halbritter
 */
class KotlinDependenciesConfigurer implements BuildCustomizer<Build> {

	@Override
	public void customize(Build build) {
		build.dependencies()
			.add("kotlin-reflect", Dependency.withCoordinates("org.jetbrains.kotlin", "kotlin-reflect")
				.scope(DependencyScope.COMPILE));
		build.dependencies()
			.add("kotlin-test-junit5", Dependency.withCoordinates("org.jetbrains.kotlin", "kotlin-test-junit5")
				.scope(DependencyScope.TEST_COMPILE));
	}

}
