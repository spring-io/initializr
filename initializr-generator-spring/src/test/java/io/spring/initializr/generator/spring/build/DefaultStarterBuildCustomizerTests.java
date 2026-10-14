/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DefaultStarterBuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
class DefaultStarterBuildCustomizerTests {

	private ProjectDescription projectDescription;

	@BeforeEach
	void setUp() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("1.0.0"));
		this.projectDescription = description;
	}

	@Test
	void defaultStarterIsAddedIfNoneExists() {
		Dependency dependency = Dependency.withId("acme", "com.example", "acme");
		dependency.setStarter(false);
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("test", dependency)
			.build();
		Build build = createBuild(metadata);
		build.dependencies().add("acme");
		new DefaultStarterBuildCustomizer(metadata, this.projectDescription).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("acme", DefaultStarterBuildCustomizer.DEFAULT_STARTER);
	}

	@Test
	void defaultStarterIsAddedIfNoCompileScopedStarterExists() {
		Dependency dependency = Dependency.withId("runtime", "org.springframework.boot", "runtime-starter", null,
				Dependency.SCOPE_RUNTIME);
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("test", dependency)
			.build();
		Build build = createBuild(metadata);
		build.dependencies().add("runtime");
		new DefaultStarterBuildCustomizer(metadata, this.projectDescription).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("runtime", DefaultStarterBuildCustomizer.DEFAULT_STARTER);
	}

	@Test
	void defaultStarterIsNotAddedIfCompileScopedStarterExists() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("test", "web", "security")
			.build();
		Build build = createBuild(metadata);
		build.dependencies().add("web");
		new DefaultStarterBuildCustomizer(metadata, this.projectDescription).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("web");
	}

	private Build createBuild(InitializrMetadata metadata) {
		return new MavenBuild(new MetadataBuildItemResolver(metadata, Version.parse("2.0.0.RELEASE")));
	}

}
