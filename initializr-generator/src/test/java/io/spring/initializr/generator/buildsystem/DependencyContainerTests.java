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

package io.spring.initializr.generator.buildsystem;

import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DependencyContainer}.
 *
 * @author Stephane Nicoll
 */
class DependencyContainerTests {

	@Test
	void addDependency() {
		DependencyContainer container = createTestContainer();
		container.add("web", "org.springframework.boot", "spring-boot-starter-web",
				DependencyScope.COMPILE);
		assertThat(container.ids()).containsOnly("web");
		assertThat(container.items()).hasSize(1);
		assertThat(container.isEmpty()).isFalse();
		assertThat(container.has("web")).isTrue();
		Dependency web = container.get("web");
		assertThat(web).isNotNull();
		assertThat(web.getGroupId()).isEqualTo("org.springframework.boot");
		assertThat(web.getArtifactId()).isEqualTo("spring-boot-starter-web");
		assertThat(web.getVersion()).isNull();
		assertThat(web.getScope()).isEqualTo(DependencyScope.COMPILE);
	}

	@Test
	void addDependencyWithVersion() {
		DependencyContainer container = createTestContainer();
		container.add("custom",
				Dependency.withCoordinates("com.example", "acme")
						.version(VersionReference.ofValue("1.0.0"))
						.scope(DependencyScope.COMPILE));
		assertThat(container.ids()).containsOnly("custom");
		assertThat(container.items()).hasSize(1);
		assertThat(container.isEmpty()).isFalse();
		assertThat(container.has("custom")).isTrue();
		Dependency custom = container.get("custom");
		assertThat(custom).isNotNull();
		assertThat(custom.getGroupId()).isEqualTo("com.example");
		assertThat(custom.getArtifactId()).isEqualTo("acme");
		assertThat(custom.getVersion()).isEqualTo(VersionReference.ofValue("1.0.0"));
		assertThat(custom.getScope()).isEqualTo(DependencyScope.COMPILE);
	}

	private DependencyContainer createTestContainer() {
		return new DependencyContainer((id) -> null);
	}

}
