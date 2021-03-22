/*
 * Copyright 2012-2021 the original author or authors.
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

import io.spring.initializr.generator.buildsystem.Dependency;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link MutableProjectDescription}.
 *
 * @author Stephane Nicoll
 */
class MutableProjectDescriptionTests {

	@Test
	void removeDependencyWithExistingDependencyReturnsDependency() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.addDependency("core", mock(Dependency.class));
		Dependency testDependency = mock(Dependency.class);
		description.addDependency("test", testDependency);
		assertThat(description.removeDependency("test")).isSameAs(testDependency);
		assertThat(description.getRequestedDependencies()).containsOnlyKeys("core");
	}

	@Test
	void removeDependencyWithUnknownDependencyReturnsNull() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.addDependency("core", mock(Dependency.class));
		assertThat(description.removeDependency("unknown")).isNull();
		assertThat(description.getRequestedDependencies()).containsOnlyKeys("core");
	}

}
