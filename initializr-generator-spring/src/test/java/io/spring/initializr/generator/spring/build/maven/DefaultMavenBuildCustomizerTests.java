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

package io.spring.initializr.generator.spring.build.maven;

import io.spring.initializr.generator.buildsystem.BomContainer;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenParent;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.metadata.InitializrMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link DefaultMavenBuildCustomizer}.
 *
 * @author Madhura Bhave
 */
class DefaultMavenBuildCustomizerTests {

	@Test
	void customizeSetNameAndDescription() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().build();
		MutableProjectDescription description = initializeDescription();
		description.setName("my-demo");
		description.setDescription("Demonstration project");
		MavenBuild build = customizeBuild(metadata, description);
		assertThat(build.getSettings().getName()).isEqualTo("my-demo");
		assertThat(build.getSettings().getDescription()).isEqualTo("Demonstration project");
	}

	@Test
	void customizeRegisterSpringBootPlugin() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().build();
		MavenBuild build = customizeBuild(metadata);
		assertThat(build.plugins().values()).singleElement().satisfies((mavenPlugin) -> {
			assertThat(mavenPlugin.getGroupId()).isEqualTo("org.springframework.boot");
			assertThat(mavenPlugin.getArtifactId()).isEqualTo("spring-boot-maven-plugin");
			assertThat(mavenPlugin.getVersion()).isNull();
		});
	}

	@Test
	void customizeSetJavaVersion() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().build();
		MutableProjectDescription description = initializeDescription();
		description.setLanguage(new JavaLanguage("11"));
		MavenBuild build = customizeBuild(metadata, description);
		assertThat(build.properties().values()).contains(entry("java.version", "11"));
	}

	@Test
	void customizeWhenNoParentShouldUseSpringBootParent() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().build();
		MavenBuild build = customizeBuild(metadata);
		MavenParent parent = build.getSettings().getParent();
		assertThat(parent.getGroupId()).isEqualTo("org.springframework.boot");
		assertThat(parent.getArtifactId()).isEqualTo("spring-boot-starter-parent");
		assertThat(parent.getVersion()).isEqualTo("2.0.0");
	}

	@Test
	void customizeWithCustomParentAndSpringBootBomShouldAddBom() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.setMavenParent("com.foo", "foo-parent", "1.0.0-SNAPSHOT", true).build();
		MavenBuild build = customizeBuild(metadata);
		MavenParent parent = build.getSettings().getParent();
		assertThat(parent.getGroupId()).isEqualTo("com.foo");
		assertThat(parent.getArtifactId()).isEqualTo("foo-parent");
		assertThat(parent.getVersion()).isEqualTo("1.0.0-SNAPSHOT");
		BomContainer boms = build.boms();
		assertThat(boms.items()).hasSize(1);
		assertThat(boms.ids()).contains("spring-boot");
		assertThat(build.properties().versions(VersionProperty::toStandardFormat))
				.contains(entry("spring-boot.version", "2.0.0"));
	}

	@Test
	void customizeWithNoSpringBootBomShouldNotAddBom() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.setMavenParent("com.foo", "foo-parent", "1.0.0-SNAPSHOT", false).build();
		MavenBuild build = customizeBuild(metadata);
		BomContainer boms = build.boms();
		assertThat(boms.items()).hasSize(0);
	}

	private MutableProjectDescription initializeDescription() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.0.0"));
		description.setLanguage(new JavaLanguage());
		return description;
	}

	private MavenBuild customizeBuild(InitializrMetadata metadata) {
		MutableProjectDescription description = initializeDescription();
		return customizeBuild(metadata, description);
	}

	private MavenBuild customizeBuild(InitializrMetadata metadata, MutableProjectDescription description) {
		MavenBuild build = new MavenBuild();
		DefaultMavenBuildCustomizer customizer = new DefaultMavenBuildCustomizer(description, metadata);
		customizer.customize(build);
		return build;
	}

}
