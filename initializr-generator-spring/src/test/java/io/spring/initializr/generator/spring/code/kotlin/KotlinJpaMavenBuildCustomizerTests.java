/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import io.spring.initializr.generator.spring.build.MetadataBuildItemResolver;
import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinJpaGradleBuildCustomizer}.
 *
 * @author Madhura Bhave
 */
class KotlinJpaMavenBuildCustomizerTests {

	@Test
	void customizeWhenJpaFacetPresentShouldAddKotlinJpaPlugin() {
		Dependency dependency = Dependency.withId("foo");
		dependency.setFacets(Collections.singletonList("jpa"));
		MavenBuild build = getCustomizedBuild(dependency);
		assertThat(build.getPlugins()).hasSize(1);
		MavenPlugin plugin = build.getPlugins().get(0);
		assertThat(plugin.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(plugin.getArtifactId()).isEqualTo("kotlin-maven-noarg");
		assertThat(plugin.getVersion()).isEqualTo("${kotlin.version}");
		MavenPlugin.Setting settings = plugin.getConfiguration().getSettings().get(0);
		assertThat(settings.getValue()).asList().element(0)
				.hasFieldOrPropertyWithValue("name", "plugin")
				.hasFieldOrPropertyWithValue("value", "jpa");
	}

	@Test
	void customizeWhenJpaFacetAbsentShouldNotAddKotlinJpaPlugin() {
		Dependency dependency = Dependency.withId("foo");
		MavenBuild build = getCustomizedBuild(dependency);
		assertThat(build.getPlugins()).hasSize(0);
	}

	private MavenBuild getCustomizedBuild(Dependency dependency) {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", dependency).build();
		KotlinJpaMavenBuildCustomizer customizer = new KotlinJpaMavenBuildCustomizer(
				metadata);
		MavenBuild build = new MavenBuild(new MetadataBuildItemResolver(metadata));
		build.dependencies().add("foo");
		customizer.customize(build);
		return build;
	}

}
