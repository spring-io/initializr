/*
 * Copyright 2012 - present the original author or authors.
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

package io.spring.initializr.generator.spring.build;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link WarPackagingWebStarterBuildCustomizer}.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
class WarPackagingWebStarterBuildCustomizerTests {

	private static final String PLATFORM_VERSION = "1.0.0";

	private MutableProjectDescription projectDescription;

	@BeforeEach
	void setUp() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse(PLATFORM_VERSION));
		this.projectDescription = description;
	}

	@Test
	void addWebStarterWhenNoWebFacetIsPresent() {
		Dependency dependency = Dependency.withId("test", "com.example", "acme", null, Dependency.SCOPE_COMPILE);
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("test", dependency)
			.build();
		Build build = createBuild(metadata);
		build.dependencies().add("test");
		new WarPackagingWebStarterBuildCustomizer(metadata, this.projectDescription).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("test", "web", "tomcat");
	}

	@Test
	void addWebStarterWhenNoWebFacetIsPresentWithCustomWebStarter() {
		Dependency dependency = Dependency.withId("test", "com.example", "acme", null, Dependency.SCOPE_COMPILE);
		Dependency web = Dependency.withId("web", "com.example", "custom-web-starter", null, Dependency.SCOPE_COMPILE);
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("test", dependency, web)
			.build();
		Build build = createBuild(metadata);
		build.dependencies().add("test");
		new WarPackagingWebStarterBuildCustomizer(metadata, this.projectDescription).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("test", "web", "tomcat");
	}

	@Test
	void addWebStarterDoesNotReplaceWebFacetDependency() {
		Dependency dependency = Dependency.withId("test", "com.example", "acme", null, Dependency.SCOPE_COMPILE);
		dependency.setFacets(Collections.singletonList("web"));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("test", dependency)
			.build();
		Build build = createBuild(metadata);
		build.dependencies().add("test");
		new WarPackagingWebStarterBuildCustomizer(metadata, this.projectDescription).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("test", "tomcat");
	}

	@Test
	void shouldUseResolvedDependencies() {
		Dependency web = Dependency.withId("web", "com.example", "web", null, Dependency.SCOPE_COMPILE);
		Dependency.Mapping webMapping = new Dependency.Mapping();
		webMapping.setCompatibilityRange(PLATFORM_VERSION);
		webMapping.setArtifactId("mapped-web");
		web.getMappings().add(webMapping);
		Dependency tomcat = Dependency.withId("tomcat", "com.example", "tomcat", null, Dependency.SCOPE_COMPILE);
		Dependency.Mapping tomcatMapping = new Dependency.Mapping();
		tomcatMapping.setCompatibilityRange(PLATFORM_VERSION);
		tomcatMapping.setArtifactId("mapped-tomcat");
		tomcat.getMappings().add(tomcatMapping);
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("web", web, tomcat)
			.build();
		Build build = createBuild(metadata);
		new WarPackagingWebStarterBuildCustomizer(metadata, this.projectDescription).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("web", "tomcat");
		io.spring.initializr.generator.buildsystem.Dependency webDependency = build.dependencies().get("web");
		assertThat(webDependency.getArtifactId()).isEqualTo("mapped-web");
		io.spring.initializr.generator.buildsystem.Dependency tomcatDependency = build.dependencies().get("tomcat");
		assertThat(tomcatDependency.getArtifactId()).isEqualTo("mapped-tomcat");
	}

	@ParameterizedTest
	@CsvSource(textBlock = """
			3.4.0,org.springframework.boot:spring-boot-starter-tomcat
			3.5.0,org.springframework.boot:spring-boot-starter-tomcat
			4.0.0-M1,org.springframework.boot:spring-boot-starter-tomcat
			4.0.0-M2,org.springframework.boot:spring-boot-starter-tomcat
			4.0.0-M3,org.springframework.boot:spring-boot-starter-tomcat
			4.0.0-RC1,org.springframework.boot:spring-boot-tomcat-runtime
			4.0.0,org.springframework.boot:spring-boot-tomcat-runtime
			""")
	void shouldAddSpringBootTomcatRuntimeForBoot4Rc1(String bootVersion, String coordinates) {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().build();
		this.projectDescription.setPlatformVersion(Version.parse(bootVersion));
		Build build = createBuild(metadata);
		new WarPackagingWebStarterBuildCustomizer(metadata, this.projectDescription).customize(build);
		io.spring.initializr.generator.buildsystem.Dependency tomcat = build.dependencies().get("tomcat");
		String actualCoordinates = tomcat.getGroupId() + ":" + tomcat.getArtifactId();
		assertThat(actualCoordinates).isEqualTo(coordinates);
	}

	private Build createBuild(InitializrMetadata metadata) {
		return new MavenBuild(new MetadataBuildItemResolver(metadata, Version.parse("2.0.0.RELEASE")));
	}

}
