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

package io.spring.initializr.metadata;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.BillOfMaterials.Mapping;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Kotlin;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Maven.ParentPom;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link InitializrMetadata}.
 *
 * @author Stephane Nicoll
 */
class InitializrMetadataTests {

	@Test
	void invalidBom() {
		InitializrMetadata metadata = initializeMetadata();
		Dependency foo = Dependency.withId("foo", "org.acme", "foo");
		foo.setBom("foo-bom");
		addTestDependencyGroup(metadata, foo);
		metadata.getConfiguration().getEnv().getBoms().put("my-bom",
				BillOfMaterials.create("org.acme", "foo", "1.2.3"));
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(metadata::validate).withMessageContaining("foo-bom")
				.withMessageContaining("my-bom");
	}

	@Test
	void invalidRepository() throws MalformedURLException {
		InitializrMetadata metadata = initializeMetadata();
		Dependency foo = Dependency.withId("foo", "org.acme", "foo");
		foo.setRepository("foo-repo");
		addTestDependencyGroup(metadata, foo);
		metadata.getConfiguration().getEnv().getRepositories().put("my-repo",
				new Repository("repo", new URL("http://example.com/repo"), true));
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(metadata::validate).withMessageContaining("foo-repo")
				.withMessageContaining("my-repo");
	}

	@Test
	void invalidBomNoVersion() {
		InitializrMetadata metadata = initializeMetadata();
		metadata.getConfiguration().getEnv().getBoms().put("foo-bom",
				BillOfMaterials.create("org.acme", "foo-bom"));
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(metadata::validate).withMessageContaining("No version")
				.withMessageContaining("foo-bom");
	}

	@Test
	void invalidBomUnknownRepository() {
		InitializrMetadata metadata = initializeMetadata();
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom",
				"1.0.0.RELEASE");
		bom.getRepositories().add("foo-repo");
		metadata.getConfiguration().getEnv().getBoms().put("foo-bom", bom);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(metadata::validate)
				.withMessageContaining("invalid repository id foo-repo")
				.withMessageContaining("foo-bom");
	}

	@Test
	void invalidBomUnknownAdditionalBom() {
		InitializrMetadata metadata = initializeMetadata();
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom",
				"1.0.0.RELEASE");
		bom.getAdditionalBoms().addAll(Arrays.asList("bar-bom", "biz-bom"));
		BillOfMaterials barBom = BillOfMaterials.create("org.acme", "bar-bom",
				"1.0.0.RELEASE");
		metadata.getConfiguration().getEnv().getBoms().put("foo-bom", bom);
		metadata.getConfiguration().getEnv().getBoms().put("bar-bom", barBom);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(metadata::validate)
				.withMessageContaining("invalid additional bom")
				.withMessageContaining("biz-bom");
	}

	@Test
	void invalidBomVersionRangeMapping() {
		InitializrMetadata metadata = initializeMetadata();
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom");
		bom.getMappings().add(Mapping.create("[1.2.0.RELEASE,1.3.0.M1)", "1.0.0"));
		bom.getMappings().add(Mapping.create("FOO_BAR", "1.2.0"));
		metadata.getConfiguration().getEnv().getBoms().put("foo-bom", bom);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(metadata::validate).withMessageContaining("FOO_BAR")
				.withMessageContaining("foo-bom");
	}

	@Test
	void invalidBomVersionRangeMappingUnknownRepo() {
		InitializrMetadata metadata = initializeMetadata();
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom");
		bom.getMappings().add(Mapping.create("[1.0.0.RELEASE,1.3.0.M1)", "1.0.0"));
		Mapping mapping = Mapping.create("1.3.0.M2", "1.2.0");
		mapping.getRepositories().add("foo-repo");
		bom.getMappings().add(mapping);
		metadata.getConfiguration().getEnv().getBoms().put("foo-bom", bom);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(metadata::validate)
				.withMessageContaining("invalid repository id foo-repo")
				.withMessageContaining("1.3.0.M2").withMessageContaining("foo-bom");
	}

	@Test
	void invalidBomVersionRangeMappingUnknownAdditionalBom() {
		InitializrMetadata metadata = initializeMetadata();
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom");
		bom.getMappings().add(Mapping.create("[1.0.0.RELEASE,1.3.0.M1)", "1.0.0"));
		Mapping mapping = Mapping.create("1.3.0.M2", "1.2.0");
		mapping.getAdditionalBoms().add("bar-bom");
		bom.getMappings().add(mapping);
		metadata.getConfiguration().getEnv().getBoms().put("foo-bom", bom);
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(metadata::validate)
				.withMessageContaining("invalid additional bom")
				.withMessageContaining("1.3.0.M2").withMessageContaining("bar-bom");
	}

	@Test
	void updateSpringBootVersions() {
		InitializrMetadata metadata = initializeMetadata();
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom");
		bom.getMappings().add(Mapping.create("[1.2.0.RELEASE,1.3.x.RELEASE]", "1.0.0"));
		bom.getMappings()
				.add(Mapping.create("1.3.x.BUILD-SNAPSHOT", "1.1.0-BUILD-SNAPSHOT"));
		Dependency dependency = Dependency.withId("bar");
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.3.0.RELEASE, 1.3.x.RELEASE]", null, null, "0.1.0.RELEASE"));
		dependency.getMappings().add(Dependency.Mapping.create("1.3.x.BUILD-SNAPSHOT",
				null, null, "0.2.0.RELEASE"));

		addTestDependencyGroup(metadata, dependency);
		metadata.getConfiguration().getEnv().getBoms().put("foo-bom", bom);
		Kotlin kotlin = metadata.getConfiguration().getEnv().getKotlin();
		kotlin.setDefaultVersion("1.3");
		kotlin.getMappings()
				.add(createKotlinVersionMapping("[1.2.0.RELEASE,1.3.x.RELEASE]", "1.1"));
		kotlin.getMappings()
				.add(createKotlinVersionMapping("1.3.x.BUILD-SNAPSHOT", "1.2"));
		metadata.validate();

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
		InitializrMetadata metadata = initializeMetadata();
		ParentPom parent = metadata.getConfiguration().getEnv().getMaven().getParent();
		parent.setGroupId("org.foo");
		parent.setArtifactId("foo-parent");
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(metadata::validate).withMessageContaining(
						"Custom maven pom requires groupId, artifactId and version");
	}

	@Test
	void stripInvalidCharsFromPackage() {
		InitializrMetadata metadata = initializeMetadata();
		metadata.getGroupId().setContent("org.acme");
		metadata.getArtifactId().setContent("2foo.bar");
		assertThat(metadata.getPackageName().getContent()).isEqualTo("org.acme.foo.bar");

		metadata = initializeMetadata();
		metadata.getGroupId().setContent("org.ac-me");
		metadata.getArtifactId().setContent("foo-bar");
		assertThat(metadata.getPackageName().getContent()).isEqualTo("org.acme.foobar");
	}

	private InitializrMetadata initializeMetadata() {
		return new InitializrMetadata();
	}

	private void addTestDependencyGroup(InitializrMetadata metadata,
			Dependency... dependencies) {
		DependencyGroup group = DependencyGroup.create("test");
		for (Dependency dependency : dependencies) {
			group.getContent().add(dependency);
		}
		metadata.getDependencies().getContent().add(group);
	}

	private Kotlin.Mapping createKotlinVersionMapping(String versionRange,
			String kotlinVersion) {
		Kotlin.Mapping mapping = new Kotlin.Mapping();
		mapping.setVersionRange(versionRange);
		mapping.setVersion(kotlinVersion);
		return mapping;
	}

}
