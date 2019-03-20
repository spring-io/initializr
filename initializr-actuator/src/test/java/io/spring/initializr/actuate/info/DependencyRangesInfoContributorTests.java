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

package io.spring.initializr.actuate.info;

import java.util.Map;

import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.SimpleInitializrMetadataProvider;
import org.junit.jupiter.api.Test;

import org.springframework.boot.actuate.info.Info;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link DependencyRangesInfoContributor}.
 *
 * @author Stephane Nicoll
 */
class DependencyRangesInfoContributorTests {

	@Test
	void noDependencyWithVersion() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.build();
		Info info = getInfo(metadata);
		assertThat(info.getDetails()).doesNotContainKeys("dependency-ranges");
	}

	@Test
	void dependencyWithNoMapping() {
		Dependency dependency = Dependency.withId("foo", "com.example", "foo",
				"1.2.3.RELEASE");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("foo", dependency).build();
		Info info = getInfo(metadata);
		assertThat(info.getDetails()).doesNotContainKeys("dependency-ranges");
	}

	@Test
	void dependencyWithRangeOnArtifact() {
		Dependency dependency = Dependency.withId("foo", "com.example", "foo",
				"1.2.3.RELEASE");
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, "foo2", null));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("foo", dependency).build();
		Info info = getInfo(metadata);
		assertThat(info.getDetails()).doesNotContainKeys("dependency-ranges");
	}

	@Test
	void dependencyWithRangeAndBom() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		Dependency dependency = Dependency.withId("foo", "com.example", "foo",
				"1.2.3.RELEASE");
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "0.1.0.RELEASE"));
		dependency.setBom("bom");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("bom", bom).addDependencyGroup("foo", dependency).build();
		Info info = getInfo(metadata);
		assertThat(info.getDetails()).doesNotContainKeys("dependency-ranges");
	}

	@Test
	void dependencyNoMappingSimpleRange() {
		Dependency dependency = Dependency.withId("foo", "com.example", "foo",
				"1.2.3.RELEASE");
		dependency.setVersionRange("[1.1.0.RELEASE, 1.5.0.RELEASE)");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("foo", dependency).build();
		Info info = getInfo(metadata);
		assertThat(info.getDetails()).containsKeys("dependency-ranges");
		@SuppressWarnings("unchecked")
		Map<String, Object> ranges = (Map<String, Object>) info.getDetails()
				.get("dependency-ranges");
		assertThat(ranges).containsOnlyKeys("foo");
		@SuppressWarnings("unchecked")
		Map<String, Object> foo = (Map<String, Object>) ranges.get("foo");
		assertThat(foo).containsExactly(
				entry("1.2.3.RELEASE", "Spring Boot >=1.1.0.RELEASE and <1.5.0.RELEASE"));
	}

	@Test
	void dependencyWithMappingAndOpenRange() {
		Dependency dependency = Dependency.withId("foo", null, null, "0.3.0.RELEASE");
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "0.1.0.RELEASE"));
		dependency.getMappings().add(
				Dependency.Mapping.create("1.2.0.RELEASE", null, null, "0.2.0.RELEASE"));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", dependency).build();
		Info info = getInfo(metadata);
		assertDependencyId(info, "foo");
		Map<String, Object> foo = getDependencyRangeInfo(info, "foo");
		assertThat(foo).containsExactly(
				entry("0.1.0.RELEASE", "Spring Boot >=1.1.0.RELEASE and <1.2.0.RELEASE"),
				entry("0.2.0.RELEASE", "Spring Boot >=1.2.0.RELEASE"));
	}

	@Test
	void dependencyWithMappingAndNoOpenRange() {
		Dependency dependency = Dependency.withId("foo", null, null, "0.3.0.RELEASE");
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "0.1.0.RELEASE"));
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.2.0.RELEASE, 1.3.0.RELEASE)", null, null, "0.2.0.RELEASE"));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", dependency).build();
		Info info = getInfo(metadata);
		assertDependencyId(info, "foo");
		Map<String, Object> foo = getDependencyRangeInfo(info, "foo");
		assertThat(foo).containsExactly(
				entry("0.1.0.RELEASE", "Spring Boot >=1.1.0.RELEASE and <1.2.0.RELEASE"),
				entry("0.2.0.RELEASE", "Spring Boot >=1.2.0.RELEASE and <1.3.0.RELEASE"),
				entry("managed", "Spring Boot >=1.3.0.RELEASE"));
	}

	@SuppressWarnings("unchecked")
	private void assertDependencyId(Info info, String... dependencyIds) {
		assertThat(info.getDetails()).containsKeys("dependency-ranges");
		Map<String, Object> ranges = (Map<String, Object>) info.getDetails()
				.get("dependency-ranges");
		assertThat(ranges).containsOnlyKeys(dependencyIds);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getDependencyRangeInfo(Info info, String id) {
		assertThat(info.getDetails()).containsKeys("dependency-ranges");
		Map<String, Object> ranges = (Map<String, Object>) info.getDetails()
				.get("dependency-ranges");
		return (Map<String, Object>) ranges.get(id);
	}

	private static Info getInfo(InitializrMetadata metadata) {
		Info.Builder builder = new Info.Builder();
		new DependencyRangesInfoContributor(
				new SimpleInitializrMetadataProvider(metadata)).contribute(builder);
		return builder.build();
	}

}
