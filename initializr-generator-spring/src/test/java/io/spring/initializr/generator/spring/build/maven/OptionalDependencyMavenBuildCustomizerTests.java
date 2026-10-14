/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build.maven;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenDependency;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OptionalDependencyMavenBuildCustomizer}.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
class OptionalDependencyMavenBuildCustomizerTests {

	private static final Dependency WEB_DEPENDENCY = Dependency
		.withCoordinates("org.springframework.boot", "spring-boot-starter-web")
		.build();

	private static final Dependency DEVTOOLS_DEPENDENCY = Dependency
		.withCoordinates("org.springframework.boot", "spring-boot-devtools")
		.build();

	@Test
	void shouldAddOptionalScope() {
		MavenBuild build = new MavenBuild();
		build.dependencies().add("devtools", DEVTOOLS_DEPENDENCY);
		new OptionalDependencyMavenBuildCustomizer("devtools").customize(build);
		Dependency devtools = build.dependencies().get("devtools");
		assertThat(devtools).isInstanceOf(MavenDependency.class);
		assertThat(((MavenDependency) devtools).isOptional()).isTrue();
	}

	@Test
	void shouldNotFailOnDuplicateDependencies() {
		MavenBuild build = new MavenBuild();
		build.dependencies().add("devtools", DEVTOOLS_DEPENDENCY);
		build.dependencies().add("devtools", DEVTOOLS_DEPENDENCY);
		new OptionalDependencyMavenBuildCustomizer("devtools").customize(build);
		Dependency devtools = build.dependencies().get("devtools");
		assertThat(devtools).isInstanceOf(MavenDependency.class);
		assertThat(((MavenDependency) devtools).isOptional()).isTrue();
	}

	@Test
	void shouldIgnoreOtherDependencies() {
		MavenBuild build = new MavenBuild();
		build.dependencies().add("devtools", DEVTOOLS_DEPENDENCY);
		build.dependencies().add("web", WEB_DEPENDENCY);
		new OptionalDependencyMavenBuildCustomizer("devtools").customize(build);
		Dependency devtools = build.dependencies().get("devtools");
		assertThat(devtools).isInstanceOf(MavenDependency.class);
		assertThat(((MavenDependency) devtools).isOptional()).isTrue();
		Dependency web = build.dependencies().get("web");
		assertThat(web).isNotInstanceOf(MavenDependency.class);
	}

	@Test
	void shouldNotChangeDependencies() {
		MavenBuild build = new MavenBuild();
		build.dependencies().add("web", WEB_DEPENDENCY);
		new OptionalDependencyMavenBuildCustomizer("devtools").customize(build);
		assertThat(build.dependencies().ids()).containsOnly("web");
	}

}
