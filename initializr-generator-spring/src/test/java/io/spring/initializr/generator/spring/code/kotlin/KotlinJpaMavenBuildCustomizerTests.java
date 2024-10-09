/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.generator.spring.code.kotlin;

import java.util.Collections;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemResolver;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KotlinJpaMavenBuildCustomizerTests {

	@Test
	void customizeWhenJpaFacetPresentShouldAddKotlinJpaPlugin() {
		Dependency dependency = Dependency.withId("foo");
		dependency.setFacets(Collections.singletonList("jpa"));
		MavenBuild build = getCustomizedBuild(dependency);
		assertThat(build.plugins().values()).singleElement().satisfies((plugin) -> {
			assertThat(plugin.getGroupId()).isEqualTo("org.jetbrains.kotlin");
			assertThat(plugin.getArtifactId()).isEqualTo("kotlin-maven-plugin");
			MavenPlugin.Setting settings = plugin.getConfiguration().getSettings().get(0);
			assertThat(settings.getValue()).asInstanceOf(InstanceOfAssertFactories.LIST)
				.element(0)
				.hasFieldOrPropertyWithValue("name", "plugin")
				.hasFieldOrPropertyWithValue("value", "jpa");
			assertThat(plugin.getDependencies()).hasSize(1);
			MavenPlugin.Dependency pluginDependency = plugin.getDependencies().get(0);
			assertThat(pluginDependency.getGroupId()).isEqualTo("org.jetbrains.kotlin");
			assertThat(pluginDependency.getArtifactId()).isEqualTo("kotlin-maven-noarg");
			assertThat(pluginDependency.getVersion()).isEqualTo("${kotlin.version}");
		});
	}

	@Test
	void customizeWhenJpaFacetAbsentShouldNotAddKotlinJpaPlugin() {
		Dependency dependency = Dependency.withId("foo");
		MavenBuild build = getCustomizedBuild(dependency);
		assertThat(build.plugins().isEmpty()).isTrue();
	}

	@Test
	void customizeWhenJakartaPersistencePresentShouldCustomizeAllOpenWithJakarta() {
		Dependency dependency = Dependency.withId("foo", "jakarta.persistence", "jakarta.persistence-api");
		dependency.setFacets(Collections.singletonList("jpa"));
		MavenBuild build = getCustomizedBuild(dependency);
		assertThat(build.plugins().values()).singleElement().satisfies((plugin) -> {
			assertThat(plugin.getGroupId()).isEqualTo("org.jetbrains.kotlin");
			assertThat(plugin.getArtifactId()).isEqualTo("kotlin-maven-plugin");
		});
		assertThat(build.plugins().values()).singleElement().satisfies((plugin) -> {
			MavenPlugin.Configuration configuration = plugin.getConfiguration();
			assertThat(configuration.getSettings()
				.stream()
				.filter((setting) -> "option".equals(setting.getName()))
				.map(MavenPlugin.Setting::getValue))
				.containsExactlyInAnyOrder("all-open:annotation=jakarta.persistence.Entity",
						"all-open:annotation=jakarta.persistence.MappedSuperclass",
						"all-open:annotation=jakarta.persistence.Embeddable");
		});
	}

	@Test
	void customizeWhenJavaxPersistencePresentShouldCustomizeAllOpenWithJavax() {
		Dependency dependency = Dependency.withId("foo", "javax.persistence", "javax.persistence-api");
		dependency.setFacets(Collections.singletonList("jpa"));
		MavenBuild build = getCustomizedBuild(dependency);
		assertThat(build.plugins().values()).singleElement().satisfies((plugin) -> {
			assertThat(plugin.getGroupId()).isEqualTo("org.jetbrains.kotlin");
			assertThat(plugin.getArtifactId()).isEqualTo("kotlin-maven-plugin");
		});
		assertThat(build.plugins().values()).singleElement().satisfies((plugin) -> {
			MavenPlugin.Configuration configuration = plugin.getConfiguration();
			assertThat(configuration.getSettings()
				.stream()
				.filter((setting) -> "option".equals(setting.getName()))
				.map(MavenPlugin.Setting::getValue))
				.containsExactlyInAnyOrder("all-open:annotation=javax.persistence.Entity",
						"all-open:annotation=javax.persistence.MappedSuperclass",
						"all-open:annotation=javax.persistence.Embeddable");
		});
	}

	private MavenBuild getCustomizedBuild(Dependency dependency) {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("test", dependency)
			.build();
		MutableProjectDescription projectDescription = new MutableProjectDescription();
		projectDescription.setPlatformVersion(Version.parse("1.0.0"));
		KotlinJpaMavenBuildCustomizer customizer = new KotlinJpaMavenBuildCustomizer(metadata, projectDescription);
		MavenBuild build = new MavenBuild(new MetadataBuildItemResolver(metadata, Version.parse("2.0.0.RELEASE")));
		build.dependencies().add("foo");
		customizer.customize(build);
		return build;
	}

}
