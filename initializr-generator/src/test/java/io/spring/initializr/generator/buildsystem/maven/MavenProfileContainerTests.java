/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenResourceContainer}.
 *
 * @author Joshua Xu
 */
class MavenProfileContainerTests {

	@Test
	void mavenProfileCanBeConfigured() {
		MavenProfileContainer container = new MavenProfileContainer();
		container.add("dev", true, (profileBuilder) -> profileBuilder
				.cofiguration((propertiesBuilder) -> propertiesBuilder.add("prop1", "value1")));
		assertThat(container.values()).hasOnlyOneElementSatisfying((profile) -> {
			assertThat(profile.getId()).isEqualTo("dev");
			assertThat(profile.getProperties()).containsOnly(new MavenProfile.Property("prop1", "value1"));
			assertThat(profile.isActiveByDefault()).isTrue();
		});
	}

	@Test
	void mavenProfileDefaultValues() {
		MavenProfileContainer container = new MavenProfileContainer();
		assertThat(container.isEmpty()).isTrue();
	}

}
