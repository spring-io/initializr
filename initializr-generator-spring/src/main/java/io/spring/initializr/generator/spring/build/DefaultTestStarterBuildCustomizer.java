/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;

import org.springframework.core.Ordered;

/**
 * A {@link BuildCustomizer} that adds the default test starter if none is detected.
 *
 * @author Moritz Halbritter
 */
class DefaultTestStarterBuildCustomizer implements BuildCustomizer<Build> {

	private static final Dependency SPRING_BOOT_STARTER_TEST = Dependency
		.withCoordinates("org.springframework.boot", "spring-boot-starter-test")
		.scope(DependencyScope.TEST_COMPILE)
		.build();

	/**
	 * The id of the test starter to use if no dependency is defined.
	 */
	static final String STARTER_ID = "root_test_starter";

	@Override
	public void customize(Build build) {
		boolean hasTestStarter = build.dependencies().items().anyMatch(this::isValidTestStarter);
		if (!hasTestStarter) {
			build.dependencies().add(STARTER_ID, SPRING_BOOT_STARTER_TEST);
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	private boolean isValidTestStarter(Dependency dependency) {
		boolean isTestStarter = dependency.getGroupId().equals("org.springframework.boot")
				&& dependency.getArtifactId().startsWith("spring-boot-starter-")
				&& dependency.getArtifactId().endsWith("-test");
		return isTestStarter && DependencyScope.TEST_COMPILE.equals(dependency.getScope());
	}

}
