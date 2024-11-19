/*
 * Copyright 2012-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
