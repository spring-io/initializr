/*
 * Copyright 2012-2020 the original author or authors.
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
 * @author Stephane Nicoll
 */
class MavenResourceContainerTests {

	@Test
	void mavenResourceCanBeConfigured() {
		MavenResourceContainer container = new MavenResourceContainer();
		container.add("src/main/resources", (resource) -> {
			resource.targetPath("targetPath");
			resource.filtering(true);
			resource.includes("**/*.yml");
			resource.excludes("**/*.properties");
		});
		assertThat(container.values()).singleElement().satisfies((resource) -> {
			assertThat(resource.getDirectory()).isEqualTo("src/main/resources");
			assertThat(resource.getTargetPath()).isEqualTo("targetPath");
			assertThat(resource.isFiltering()).isTrue();
			assertThat(resource.getIncludes()).containsExactly("**/*.yml");
			assertThat(resource.getExcludes()).containsExactly("**/*.properties");
		});
	}

	@Test
	void mavenResourceCanBeAmended() {
		MavenResourceContainer container = new MavenResourceContainer();
		container.add("src/main/resources", (resource) -> {
			resource.filtering(true);
			resource.includes("**/*.yml");
		});
		container.add("src/main/resources", (resource) -> {
			resource.includes("**/*.yaml");
			resource.excludes("**/*.properties");
		});
		assertThat(container.values()).singleElement().satisfies((resource) -> {
			assertThat(resource.getDirectory()).isEqualTo("src/main/resources");
			assertThat(resource.getTargetPath()).isNull();
			assertThat(resource.isFiltering()).isTrue();
			assertThat(resource.getIncludes()).containsExactly("**/*.yaml");
			assertThat(resource.getExcludes()).containsExactly("**/*.properties");
		});
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void mavenResourceDefaultValues() {
		MavenResourceContainer container = new MavenResourceContainer();
		container.add("src/main/custom");
		assertThat(container.values()).singleElement().satisfies((resource) -> {
			assertThat(resource.getDirectory()).isEqualTo("src/main/custom");
			assertThat(resource.getTargetPath()).isNull();
			assertThat(resource.isFiltering()).isFalse();
			assertThat(resource.getIncludes()).isEmpty();
			assertThat(resource.getExcludes()).isEmpty();
		});
	}

	@Test
	void mavenResourceCanBeSearched() {
		MavenResourceContainer container = new MavenResourceContainer();
		assertThat(container.has("src/main/test")).isFalse();
		container.add("src/main/test");
		assertThat(container.has("src/main/test")).isTrue();
	}

	@Test
	void mavenResourceCanBeRemoved() {
		MavenResourceContainer container = new MavenResourceContainer();
		container.add("src/main/test");
		assertThat(container.has("src/main/test")).isTrue();
		assertThat(container.remove("src/main/test")).isTrue();
		assertThat(container.has("src/main/test")).isFalse();
	}

}
