/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link WebFoldersContributor}
 *
 * @author Stephane Nicoll
 */
class WebFoldersContributorTests {

	private Path projectDir;

	@BeforeEach
	void setup(@TempDir Path path) {
		this.projectDir = path;
	}

	@Test
	void webFoldersCreatedWithWebDependency() throws IOException {
		Dependency simple = Dependency.withId("simple", "com.example", "simple", null, Dependency.SCOPE_COMPILE);
		Dependency web = Dependency.withId("web", "com.example", "web", null, Dependency.SCOPE_COMPILE);
		web.setFacets(Collections.singletonList("web"));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("test", simple, web)
			.build();
		Build build = createBuild(metadata);
		build.dependencies().add("simple");
		build.dependencies().add("web");
		Path projectDir = contribute(build, metadata);
		assertThat(projectDir.resolve("src/main/resources/templates")).isDirectory();
		assertThat(projectDir.resolve("src/main/resources/static")).isDirectory();
	}

	@Test
	void webFoldersNotCreatedWithoutWebDependency() throws IOException {
		Dependency simple = Dependency.withId("simple", "com.example", "simple", null, Dependency.SCOPE_COMPILE);
		Dependency web = Dependency.withId("web", "com.example", "web", null, Dependency.SCOPE_COMPILE);
		web.setFacets(Collections.singletonList("web"));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("test", simple, web)
			.build();
		Build build = createBuild(metadata);
		build.dependencies().add("simple");
		Path projectDir = contribute(build, metadata);
		assertThat(projectDir.resolve("src/main/resources/templates")).doesNotExist();
		assertThat(projectDir.resolve("src/main/resources/static")).doesNotExist();
	}

	private Build createBuild(InitializrMetadata metadata) {
		return new MavenBuild(new MetadataBuildItemResolver(metadata, Version.parse("2.0.0.RELEASE")));
	}

	private Path contribute(Build build, InitializrMetadata metadata) throws IOException {
		MutableProjectDescription projectDescription = new MutableProjectDescription();
		projectDescription.setPlatformVersion(Version.parse("1.0.0"));
		new WebFoldersContributor(build, metadata, projectDescription).contribute(this.projectDir);
		return this.projectDir;
	}

}
