/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProjectGenerationConfigurationTypeFilter}.
 *
 * @author Moritz Halbritter
 */
class ProjectGenerationConfigurationTypeFilterTests {

	@Test
	void include() {
		ProjectGenerationConfigurationTypeFilter filter = ProjectGenerationConfigurationTypeFilter.include(A.class,
				B.class);
		assertThat(filter).accepts(A.class, B.class);
		assertThat(filter).rejects(C.class);
	}

	@Test
	void exclude() {
		ProjectGenerationConfigurationTypeFilter filter = ProjectGenerationConfigurationTypeFilter.exclude(A.class,
				B.class);
		assertThat(filter).rejects(A.class, B.class);
		assertThat(filter).accepts(C.class);
	}

	@Test
	void allMatch() {
		ProjectGenerationConfigurationTypeFilter filterA = (clazz) -> clazz.equals(A.class);
		ProjectGenerationConfigurationTypeFilter filterAorB = (clazz) -> clazz.equals(A.class) || clazz.equals(B.class);
		ProjectGenerationConfigurationTypeFilter filterNotC = (clazz) -> !clazz.equals(C.class);
		ProjectGenerationConfigurationTypeFilter combined = ProjectGenerationConfigurationTypeFilter.allMatch(filterA,
				filterAorB, filterNotC);
		assertThat(combined).accepts(A.class);
		assertThat(combined).rejects(B.class, C.class);
	}

	private static final class A {

	}

	private static final class B {

	}

	private static final class C {

	}

}
