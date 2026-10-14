/*
 * Copyright 2012 - present the original author or authors.
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
		container.add("web", "org.springframework.boot", "spring-boot-starter-web", DependencyScope.COMPILE);
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
