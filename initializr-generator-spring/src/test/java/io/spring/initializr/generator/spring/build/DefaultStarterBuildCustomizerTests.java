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

package io.spring.initializr.generator.spring.build;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemResolver;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DefaultStarterBuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
class DefaultStarterBuildCustomizerTests {

	@Test
	void defaultStarterIsAddedIfNoneExists() {
		Dependency dependency = Dependency.withId("acme", "com.example", "acme");
		dependency.setStarter(false);
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", dependency).build();
		Build build = createBuild(metadata);
		build.dependencies().add("acme");
		new DefaultStarterBuildCustomizer(metadata).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("acme",
				DefaultStarterBuildCustomizer.DEFAULT_STARTER);
	}

	@Test
	void defaultStarterIsAddedIfNoCompileScopedStarterExists() {
		Dependency dependency = Dependency.withId("runtime", "org.springframework.boot",
				"runtime-starter", null, Dependency.SCOPE_RUNTIME);
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", dependency).build();
		Build build = createBuild(metadata);
		build.dependencies().add("runtime");
		new DefaultStarterBuildCustomizer(metadata).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("runtime",
				DefaultStarterBuildCustomizer.DEFAULT_STARTER);
	}

	@Test
	void defaultStarterIsNotAddedIfCompileScopedStarterExists() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", "web", "security").build();
		Build build = createBuild(metadata);
		build.dependencies().add("web");
		new DefaultStarterBuildCustomizer(metadata).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("web");
	}

	private Build createBuild(InitializrMetadata metadata) {
		return new MavenBuild(
				new MetadataBuildItemResolver(metadata, Version.parse("2.0.0.RELEASE")));
	}

}
