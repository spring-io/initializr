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

package io.spring.initializr.generator.spring.build;

import java.util.Arrays;
import java.util.List;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
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
 * @author Moritz Halbritter
 */
class BuildMetadataResolverTests {

	private static final Version PLATFORM_VERSION = Version.parse("1.0.0");

	@Test
	void dependenciesFiltersDependenciesWithNoMetadata() {
		InitializrMetadata metadata = createSampleMetadata();
		Build build = createBuild(metadata);
		build.dependencies().add("three");
		build.dependencies().add("five", "com.example", "five", DependencyScope.COMPILE);
		build.dependencies().add("one");
		BuildMetadataResolver resolver = new BuildMetadataResolver(metadata, PLATFORM_VERSION);
		assertThat(resolver.dependencies(build)).hasSize(2);
		assertThat(resolver.dependencies(build).map(MetadataElement::getId)).containsExactly("three", "one");
	}

	@Test
	void shouldResolveDependenciesAgainstMapping() {
		Dependency web = Dependency.withId("web", "group", "artifact");
		web.setMappings(List.of(Dependency.Mapping.create("[1.0.0,2.0.0)", null, null, "1.0.0", null, null, null),
				Dependency.Mapping.create("[2.0.0,3.0.0)", null, null, "2.0.0", null, null, null)));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("web", web)
			.build();
		Build build = createBuild(metadata);
		build.dependencies().add("web");
		BuildMetadataResolver resolver = new BuildMetadataResolver(metadata, Version.parse("1.0.0"));
		assertThat(resolver.dependencies(build)).singleElement()
			.satisfies((dependency) -> assertThat(dependency.getVersion()).isEqualTo("1.0.0"));
		resolver = new BuildMetadataResolver(metadata, Version.parse("2.0.0"));
		assertThat(resolver.dependencies(build)).singleElement()
			.satisfies((dependency) -> assertThat(dependency.getVersion()).isEqualTo("2.0.0"));
	}

	@Test
	void hasFacetWithMatchingFacet() {
		InitializrMetadata metadata = createSampleMetadata();
		Build build = createBuild(metadata);
		build.dependencies().add("one");
		build.dependencies().add("my-web");
		assertThat(new BuildMetadataResolver(metadata, PLATFORM_VERSION).hasFacet(build, "web")).isTrue();
	}

	@Test
	void hasFacetWithNonMatchingFacet() {
		InitializrMetadata metadata = createSampleMetadata();
		Build build = createBuild(metadata);
		build.dependencies().add("my-custom");
		build.dependencies().add("my-web");
		assertThat(new BuildMetadataResolver(metadata, PLATFORM_VERSION).hasFacet(build, "nope")).isFalse();
	}

	private InitializrMetadata createSampleMetadata() {
		Dependency web = Dependency.withId("my-web");
		web.getFacets().addAll(Arrays.asList("test", "web", "another"));
		Dependency custom = Dependency.withId("my-custom");
		custom.getFacets().add("custom");
		return InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("core", "one", "two", "three")
			.addDependencyGroup("test", web, custom)
			.build();
	}

	private Build createBuild(InitializrMetadata metadata) {
		return new MavenBuild(new MetadataBuildItemResolver(metadata, PLATFORM_VERSION));
	}

}
