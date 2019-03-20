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

import java.util.Arrays;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.MetadataElement;
import io.spring.initializr.metadata.support.MetadataBuildItemResolver;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link BuildMetadataResolver}.
 *
 * @author Stephane Nicoll
 */
class BuildMetadataResolverTests {

	@Test
	void dependenciesFiltersDependenciesWithNoMetadata() {
		InitializrMetadata metadata = createSampleMetadata();
		Build build = createBuild(metadata);
		build.dependencies().add("three");
		build.dependencies().add("five", "com.example", "five", DependencyScope.COMPILE);
		build.dependencies().add("one");
		BuildMetadataResolver resolver = new BuildMetadataResolver(metadata);
		assertThat(resolver.dependencies(build)).hasSize(2);
		assertThat(resolver.dependencies(build).map(MetadataElement::getId))
				.containsExactly("three", "one");
	}

	@Test
	void hasFacetWithMatchingFacet() {
		InitializrMetadata metadata = createSampleMetadata();
		Build build = createBuild(metadata);
		build.dependencies().add("one");
		build.dependencies().add("my-web");
		assertThat(new BuildMetadataResolver(metadata).hasFacet(build, "web")).isTrue();
	}

	@Test
	void hasFacetWithNonMatchingFacet() {
		InitializrMetadata metadata = createSampleMetadata();
		Build build = createBuild(metadata);
		build.dependencies().add("my-custom");
		build.dependencies().add("my-web");
		assertThat(new BuildMetadataResolver(metadata).hasFacet(build, "nope")).isFalse();
	}

	private InitializrMetadata createSampleMetadata() {
		Dependency web = Dependency.withId("my-web");
		web.getFacets().addAll(Arrays.asList("test", "web", "another"));
		Dependency custom = Dependency.withId("my-custom");
		custom.getFacets().add("custom");
		return InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("core", "one", "two", "three")
				.addDependencyGroup("test", web, custom).build();
	}

	private Build createBuild(InitializrMetadata metadata) {
		return new MavenBuild(
				new MetadataBuildItemResolver(metadata, Version.parse("2.0.0.RELEASE")));
	}

}
