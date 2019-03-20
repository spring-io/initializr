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

package io.spring.initializr.metadata.support;

import java.net.MalformedURLException;
import java.net.URL;

import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.Dependency.Mapping;
import io.spring.initializr.metadata.DependencyGroup;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Repository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MetadataBuildItemResolver}.
 *
 * @author Stephane Nicoll
 */
class MetadataBuildItemResolverTests {

	private static final Version VERSION_2_0_0 = Version.parse("2.0.0.RELEASE");

	@Test
	void resoleDependencyWithMatchingEntry() {
		InitializrMetadata metadata = new InitializrMetadata();
		DependencyGroup group = DependencyGroup.create("test");
		group.getContent().add(
				Dependency.withId("test-dep", "com.example", "test", "1.0.0", "runtime"));
		metadata.getDependencies().getContent().add(group);
		metadata.validate();
		MetadataBuildItemResolver resolver = new MetadataBuildItemResolver(metadata,
				VERSION_2_0_0);
		io.spring.initializr.generator.buildsystem.Dependency dependency = resolver
				.resolveDependency("test-dep");
		assertThat(dependency.getGroupId()).isEqualTo("com.example");
		assertThat(dependency.getArtifactId()).isEqualTo("test");
		assertThat(dependency.getVersion()).hasToString("1.0.0");
		assertThat(dependency.getScope()).isEqualTo(DependencyScope.RUNTIME);
	}

	@Test
	void resoleDependencyWithMatchingEntryAndVersionRange() {
		InitializrMetadata metadata = new InitializrMetadata();
		DependencyGroup group = DependencyGroup.create("test");
		Dependency dependency = Dependency.withId("test-dep", "com.example", "test");
		dependency.getMappings().add(Mapping.create("[1.0.0.RELEASE, 2.0.0.RELEASE)",
				null, null, "1.0.0.RELEASE"));
		dependency.getMappings().add(Mapping.create("2.0.0.RELEASE",
				"com.example.override", "test-override", null));
		group.getContent().add(dependency);
		metadata.getDependencies().getContent().add(group);
		metadata.validate();
		MetadataBuildItemResolver resolver = new MetadataBuildItemResolver(metadata,
				VERSION_2_0_0);
		io.spring.initializr.generator.buildsystem.Dependency resolveDependency = resolver
				.resolveDependency("test-dep");
		assertThat(resolveDependency.getGroupId()).isEqualTo("com.example.override");
		assertThat(resolveDependency.getArtifactId()).isEqualTo("test-override");
		assertThat(resolveDependency.getVersion()).isNull();
		assertThat(resolveDependency.getScope()).isEqualTo(DependencyScope.COMPILE);
	}

	@Test
	void resoleDependencyWithNotMatchingEntry() {
		InitializrMetadata metadata = new InitializrMetadata();
		DependencyGroup group = DependencyGroup.create("test");
		group.getContent().add(
				Dependency.withId("test-dep", "com.example", "test", "1.0.0", "runtime"));
		metadata.getDependencies().getContent().add(group);
		metadata.validate();
		MetadataBuildItemResolver resolver = new MetadataBuildItemResolver(metadata,
				VERSION_2_0_0);
		assertThat(resolver.resolveDependency("does-not-exist")).isNull();
	}

	@Test
	void resoleBomWithMatchingEntry() {
		InitializrMetadata metadata = new InitializrMetadata();
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "2.0.0");
		metadata.getConfiguration().getEnv().getBoms().put("test-bom", bom);
		metadata.validate();
		MetadataBuildItemResolver resolver = new MetadataBuildItemResolver(metadata,
				VERSION_2_0_0);
		io.spring.initializr.generator.buildsystem.BillOfMaterials resolvedBom = resolver
				.resolveBom("test-bom");
		assertThat(resolvedBom.getGroupId()).isEqualTo("com.example");
		assertThat(resolvedBom.getArtifactId()).isEqualTo("bom");
		assertThat(resolvedBom.getVersion()).hasToString("2.0.0");
	}

	@Test
	void resoleBomWithMatchingEntryAndVersionRange() throws MalformedURLException {
		InitializrMetadata metadata = new InitializrMetadata();
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "0.0.1");
		bom.getMappings().add(BillOfMaterials.Mapping
				.create("[1.0.0.RELEASE, 2.0.0.RELEASE)", "1.0.0"));
		bom.getMappings().add(BillOfMaterials.Mapping.create("2.0.0.RELEASE", "1.1.0"));
		metadata.getConfiguration().getEnv().getBoms().put("test-bom", bom);
		metadata.getConfiguration().getEnv().getRepositories().put("test-repo",
				new Repository("test", new URL("https://example.com/repo"), false));
		metadata.validate();
		MetadataBuildItemResolver resolver = new MetadataBuildItemResolver(metadata,
				VERSION_2_0_0);
		io.spring.initializr.generator.buildsystem.BillOfMaterials resolvedBom = resolver
				.resolveBom("test-bom");
		assertThat(resolvedBom.getGroupId()).isEqualTo("com.example");
		assertThat(resolvedBom.getArtifactId()).isEqualTo("bom");
		assertThat(resolvedBom.getVersion()).hasToString("1.1.0");
	}

	@Test
	void resoleBomWithNotMatchingEntry() {
		InitializrMetadata metadata = new InitializrMetadata();
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "2.0.0");
		metadata.getConfiguration().getEnv().getBoms().put("test-bom", bom);
		metadata.validate();
		MetadataBuildItemResolver resolver = new MetadataBuildItemResolver(metadata,
				VERSION_2_0_0);
		assertThat(resolver.resolveBom("does-not-exost")).isNull();
	}

	@Test
	void resoleRepositoryWithMatchingEntry() throws MalformedURLException {
		InitializrMetadata metadata = new InitializrMetadata();
		metadata.getConfiguration().getEnv().getRepositories().put("test-repo",
				new Repository("test", new URL("https://example.com/repo"), false));
		metadata.validate();
		MetadataBuildItemResolver resolver = new MetadataBuildItemResolver(metadata,
				VERSION_2_0_0);
		MavenRepository repository = resolver.resolveRepository("test-repo");
		assertThat(repository.getId()).isEqualTo("test-repo");
		assertThat(repository.getName()).isEqualTo("test");
		assertThat(repository.getUrl()).isEqualTo("https://example.com/repo");
		assertThat(repository.isSnapshotsEnabled()).isFalse();
	}

	@Test
	void resoleRepositoryWithNonMatchingEntry() throws MalformedURLException {
		InitializrMetadata metadata = new InitializrMetadata();
		metadata.getConfiguration().getEnv().getRepositories().put("test-repo",
				new Repository("test", new URL("https://example.com/repo"), false));
		metadata.validate();
		MetadataBuildItemResolver resolver = new MetadataBuildItemResolver(metadata,
				VERSION_2_0_0);
		assertThat(resolver.resolveRepository("does-not-exist")).isNull();
	}

}
