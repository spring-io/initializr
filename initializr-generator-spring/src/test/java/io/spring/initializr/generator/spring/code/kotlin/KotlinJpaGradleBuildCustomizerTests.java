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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.spring.initializr.generator.buildsystem.gradle.*;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.spring.build.gradle.GradleBuildProjectContributor;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.test.io.TextTestUtils;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemResolver;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinJpaGradleBuildCustomizer}.
 *
 * @author Madhura Bhave
 * @author Sijun Yang
 */
class KotlinJpaGradleBuildCustomizerTests {

	@Test
	void customizeWhenJpaFacetPresentShouldAddKotlinJpaPlugin() {
		Dependency dependency = Dependency.withId("foo");
		dependency.setFacets(Collections.singletonList("jpa"));
		GradleBuild build = getCustomizedBuild(dependency);
		assertThat(build.plugins().values().filter(GradlePlugin::isApply)).isEmpty();
		assertThat(build.plugins().values()).singleElement().satisfies((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("org.jetbrains.kotlin.plugin.jpa");
			assertThat(((StandardGradlePlugin) plugin).getVersion()).isEqualTo("1.2.70");
		});
	}

	@Test
	void customizeWhenJpaFacetAbsentShouldNotAddKotlinJpaPlugin() {
		Dependency dependency = Dependency.withId("foo");
		GradleBuild build = getCustomizedBuild(dependency);
		assertThat(build.plugins().values().filter(GradlePlugin::isApply)).isEmpty();
		assertThat(build.plugins().values()).isEmpty();
	}

	private GradleBuild getCustomizedBuild(Dependency dependency) {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("test", dependency)
			.build();
		SimpleKotlinProjectSettings settings = new SimpleKotlinProjectSettings("1.2.70");
		MutableProjectDescription projectDescription = new MutableProjectDescription();
		projectDescription.setPlatformVersion(Version.parse("1.0.0"));
		KotlinJpaGradleBuildCustomizer customizer = new KotlinJpaGradleBuildCustomizer(metadata, settings,
				projectDescription);
		GradleBuild build = new GradleBuild(new MetadataBuildItemResolver(metadata, Version.parse("2.0.0.RELEASE")));
		build.dependencies().add("foo");
		customizer.customize(build);
		return build;
	}

	@Test
	void customizeWhenJakartaPersistencePresentShouldCustomizeAllOpenWithJakarta() {
		Dependency dependency = Dependency.withId("foo", "jakarta.persistence", "jakarta.persistence-api");
		dependency.setFacets(Collections.singletonList("jpa"));
		GradleBuild build = getCustomizedBuild(dependency);
		assertThat(build.plugins().values()).singleElement().satisfies((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("org.jetbrains.kotlin.plugin.jpa");
		});
		assertThat(build.extensions().values()).singleElement().satisfies((extension) -> {
			assertThat(extension.getName()).isEqualTo("allOpen");
			assertThat(extension.getInvocations())
				.filteredOn(invocation -> Objects.equals(invocation.getTarget(), "annotation"))
				.extracting("arguments")
				.containsExactlyInAnyOrder(List.of("jakarta.persistence.Entity"),
						List.of("jakarta.persistence.MappedSuperclass"), List.of("jakarta.persistence.Embeddable"));
		});
	}

	@Test
	void customizeWhenJavaxPersistencePresentShouldCustomizeAllOpenWithJavax() {
		Dependency dependency = Dependency.withId("foo", "javax.persistence", "javax.persistence-api");
		dependency.setFacets(Collections.singletonList("jpa"));
		GradleBuild build = getCustomizedBuild(dependency);
		assertThat(build.plugins().values()).singleElement().satisfies((plugin) -> {
			assertThat(plugin.getId()).isEqualTo("org.jetbrains.kotlin.plugin.jpa");
		});
		assertThat(build.extensions().values()).singleElement().satisfies((extension) -> {
			assertThat(extension.getName()).isEqualTo("allOpen");
			assertThat(extension.getInvocations())
				.filteredOn(invocation -> Objects.equals(invocation.getTarget(), "annotation"))
				.extracting("arguments")
				.containsExactlyInAnyOrder("javax.persistence.Entity", "javax.persistence.MappedSuperclass",
						"javax.persistence.Embeddable");
		});

	}

	private GradleBuildProjectContributor kotlinDslGradleBuildProjectContributor(GradleBuild build,
			IndentingWriterFactory indentingWriterFactory) {
		return new GradleBuildProjectContributor(new KotlinDslGradleBuildWriter(), build, indentingWriterFactory,
				"build.gradle.kts");
	}

	@Test
	void tempTestForBuildFile1() throws IOException {
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory.create(new SimpleIndentStrategy("    "),
				(factory) -> factory.indentingStrategy("gradle", new SimpleIndentStrategy("  ")));
		Dependency dependency = Dependency.withId("foo", "jakarta.persistence", "jakarta.persistence-api");
		dependency.setFacets(Collections.singletonList("jpa"));
		GradleBuild build = getCustomizedBuild(dependency);
		var gradleBuildProjectContributor = kotlinDslGradleBuildProjectContributor(build, indentingWriterFactory);
		StringWriter writer = new StringWriter();
		gradleBuildProjectContributor.writeBuild(writer);
		System.out.println(TextTestUtils.readAllLines(writer.toString()));
	}

	@Test
	void tempTestForBuildFile2() throws IOException {
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory.create(new SimpleIndentStrategy("    "),
				(factory) -> factory.indentingStrategy("gradle", new SimpleIndentStrategy("  ")));
		Dependency dependency = Dependency.withId("foo", "javax.persistence", "javax.persistence-api");
		dependency.setFacets(Collections.singletonList("jpa"));
		GradleBuild build = getCustomizedBuild(dependency);
		var gradleBuildProjectContributor = kotlinDslGradleBuildProjectContributor(build, indentingWriterFactory);
		StringWriter writer = new StringWriter();
		gradleBuildProjectContributor.writeBuild(writer);
		System.out.println(TextTestUtils.readAllLines(writer.toString()));
	}

}
