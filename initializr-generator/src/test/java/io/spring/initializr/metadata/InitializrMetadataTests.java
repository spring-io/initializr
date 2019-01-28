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

package io.spring.initializr.metadata;

import java.util.Arrays;
import java.util.List;

import io.spring.initializr.metadata.BillOfMaterials.Mapping;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Kotlin;
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder;
import io.spring.initializr.util.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Stephane Nicoll
 */
class InitializrMetadataTests {

	@Test
	void invalidBom() {
		Dependency foo = Dependency.withId("foo", "org.acme", "foo");
		foo.setBom("foo-bom");
		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom("my-bom", "org.acme", "foo", "1.2.3")
				.addDependencyGroup("test", foo);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(builder::build).withMessageContaining("foo-bom")
				.withMessageContaining("my-bom");
	}

	@Test
	void invalidRepository() {
		Dependency foo = Dependency.withId("foo", "org.acme", "foo");
		foo.setRepository("foo-repo");
		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults()
				.addRepository("my-repo", "repo", "http://example.com/repo", true)
				.addDependencyGroup("test", foo);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(builder::build).withMessageContaining("foo-repo")
				.withMessageContaining("my-repo");
	}

	@Test
	void invalidBomNoVersion() {
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom");

		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom("foo-bom", bom);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(builder::build).withMessageContaining("No version")
				.withMessageContaining("foo-bom");
	}

	@Test
	void invalidBomUnknownRepository() {
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom",
				"1.0.0.RELEASE");
		bom.getRepositories().add("foo-repo");

		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom("foo-bom", bom);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(builder::build)
				.withMessageContaining("invalid repository id foo-repo")
				.withMessageContaining("foo-bom");
	}

	@Test
	void invalidBomUnknownAdditionalBom() {
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom",
				"1.0.0.RELEASE");
		bom.getAdditionalBoms().addAll(Arrays.asList("bar-bom", "biz-bom"));
		BillOfMaterials barBom = BillOfMaterials.create("org.acme", "bar-bom",
				"1.0.0.RELEASE");

		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom("foo-bom", bom).addBom("bar-bom", barBom);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(builder::build)
				.withMessageContaining("invalid additional bom")
				.withMessageContaining("biz-bom");
	}

	@Test
	void invalidBomVersionRangeMapping() {
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom");
		bom.getMappings().add(Mapping.create("[1.2.0.RELEASE,1.3.0.M1)", "1.0.0"));
		bom.getMappings().add(Mapping.create("FOO_BAR", "1.2.0"));

		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom("foo-bom", bom);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(builder::build).withMessageContaining("FOO_BAR")
				.withMessageContaining("foo-bom");
	}

	@Test
	void invalidBomVersionRangeMappingUnknownRepo() {
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom");
		bom.getMappings().add(Mapping.create("[1.0.0.RELEASE,1.3.0.M1)", "1.0.0"));
		Mapping mapping = Mapping.create("1.3.0.M2", "1.2.0");
		mapping.getRepositories().add("foo-repo");
		bom.getMappings().add(mapping);

		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom("foo-bom", bom);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(builder::build)
				.withMessageContaining("invalid repository id foo-repo")
				.withMessageContaining("1.3.0.M2").withMessageContaining("foo-bom");
	}

	@Test
	void invalidBomVersionRangeMappingUnknownAdditionalBom() {
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom");
		bom.getMappings().add(Mapping.create("[1.0.0.RELEASE,1.3.0.M1)", "1.0.0"));
		Mapping mapping = Mapping.create("1.3.0.M2", "1.2.0");
		mapping.getAdditionalBoms().add("bar-bom");
		bom.getMappings().add(mapping);

		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom("foo-bom", bom);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(builder::build)
				.withMessageContaining("invalid additional bom")
				.withMessageContaining("1.3.0.M2").withMessageContaining("bar-bom");
	}

	@Test
	void updateSpringBootVersions() {
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom");
		bom.getMappings().add(Mapping.create("[1.2.0.RELEASE,1.3.x.RELEASE]", "1.0.0"));
		bom.getMappings()
				.add(Mapping.create("1.3.x.BUILD-SNAPSHOT", "1.1.0-BUILD-SNAPSHOT"));
		Dependency dependency = Dependency.withId("bar");
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.3.0.RELEASE, 1.3.x.RELEASE]", null, null, "0.1.0.RELEASE"));
		dependency.getMappings().add(Dependency.Mapping.create("1.3.x.BUILD-SNAPSHOT",
				null, null, "0.2.0.RELEASE"));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", dependency).addBom("foo-bom", bom)
				.setKotlinEnv("1.3",
						createKotlinVersionMapping("[1.2.0.RELEASE,1.3.x.RELEASE]",
								"1.1"),
						createKotlinVersionMapping("1.3.x.BUILD-SNAPSHOT", "1.2"))
				.build();

		List<DefaultMetadataElement> bootVersions = Arrays.asList(
				DefaultMetadataElement.create("1.3.6.RELEASE", "1.3.6", false),
				DefaultMetadataElement.create("1.3.7.BUILD-SNAPSHOT", "1.3.7", false));
		metadata.updateSpringBootVersions(bootVersions);
		assertThat(metadata.getConfiguration().getEnv().getBoms().get("foo-bom")
				.resolve(Version.parse("1.3.6.RELEASE")).getVersion()).isEqualTo("1.0.0");
		assertThat(metadata.getConfiguration().getEnv().getBoms().get("foo-bom")
				.resolve(Version.parse("1.3.7.BUILD-SNAPSHOT")).getVersion())
						.isEqualTo("1.1.0-BUILD-SNAPSHOT");
		assertThat(metadata.getDependencies().get("bar")
				.resolve(Version.parse("1.3.6.RELEASE")).getVersion())
						.isEqualTo("0.1.0.RELEASE");
		assertThat(metadata.getDependencies().get("bar")
				.resolve(Version.parse("1.3.7.BUILD-SNAPSHOT")).getVersion())
						.isEqualTo("0.2.0.RELEASE");
		assertThat(metadata.getConfiguration().getEnv().getKotlin()
				.resolveKotlinVersion(Version.parse("1.3.7.BUILD-SNAPSHOT")))
						.isEqualTo("1.2");
	}

	@Test
	void invalidParentMissingVersion() {
		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().setMavenParent("org.foo", "foo-parent", null, false);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(builder::build).withMessageContaining(
						"Custom maven pom requires groupId, artifactId and version");
	}

	@Test
	void stripInvalidCharsFromPackage() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.build();
		metadata.getGroupId().setContent("org.acme");
		metadata.getArtifactId().setContent("2foo.bar");
		assertThat(metadata.getPackageName().getContent()).isEqualTo("org.acme.foo.bar");

		metadata = InitializrMetadataTestBuilder.withDefaults().build();
		metadata.getGroupId().setContent("org.ac-me");
		metadata.getArtifactId().setContent("foo-bar");
		assertThat(metadata.getPackageName().getContent()).isEqualTo("org.acme.foobar");
	}

	private Kotlin.Mapping createKotlinVersionMapping(String versionRange,
			String kotlinVersion) {
		Kotlin.Mapping mapping = new Kotlin.Mapping();
		mapping.setVersionRange(versionRange);
		mapping.setVersion(kotlinVersion);
		return mapping;
	}

}
