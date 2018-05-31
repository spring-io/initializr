/*
 * Copyright 2012-2018 the original author or authors.
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

package io.spring.initializr.web.support;

import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.DependencyMetadata;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder;
import io.spring.initializr.util.Version;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
public class DefaultDependencyMetadataProviderTests {

	private final DependencyMetadataProvider provider = new DefaultDependencyMetadataProvider();

	@Test
	public void filterDependencies() {
		Dependency first = Dependency.withId("first", "org.foo", "first");
		first.setVersionRange("1.1.4.RELEASE");
		Dependency second = Dependency.withId("second", "org.foo", "second");
		Dependency third = Dependency.withId("third", "org.foo", "third");
		third.setVersionRange("1.1.8.RELEASE");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", first, second, third).build();
		DependencyMetadata dependencyMetadata = this.provider.get(metadata,
				Version.parse("1.1.5.RELEASE"));
		assertThat(dependencyMetadata.getDependencies()).hasSize(2);
		assertThat(dependencyMetadata.getRepositories()).isEmpty();
		assertThat(dependencyMetadata.getBoms()).isEmpty();
		assertThat(dependencyMetadata.getDependencies().get("first")).isSameAs(first);
		assertThat(dependencyMetadata.getDependencies().get("second")).isSameAs(second);
	}

	@Test
	public void resolveDependencies() {
		Dependency first = Dependency.withId("first", "org.foo", "first");
		first.getMappings().add(Dependency.Mapping.create(
				"[1.0.0.RELEASE, 1.1.0.RELEASE)", "org.bar", "second", "0.1.0.RELEASE"));
		first.getMappings().add(Dependency.Mapping.create("1.1.0.RELEASE", "org.biz",
				"third", "0.2.0.RELEASE"));
		Dependency second = Dependency.withId("second", "org.foo", "second");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", first, second).build();

		DependencyMetadata dependencyMetadata = this.provider.get(metadata,
				Version.parse("1.0.5.RELEASE"));
		assertThat(dependencyMetadata.getDependencies()).hasSize(2);
		assertThat(dependencyMetadata.getDependencies().get("first").getGroupId())
				.isEqualTo("org.bar");
		assertThat(dependencyMetadata.getDependencies().get("first").getArtifactId())
				.isEqualTo("second");
		assertThat(dependencyMetadata.getDependencies().get("first").getVersion())
				.isEqualTo("0.1.0.RELEASE");

		DependencyMetadata anotherDependencyMetadata = this.provider.get(metadata,
				Version.parse("1.1.0.RELEASE"));
		assertThat(anotherDependencyMetadata.getDependencies()).hasSize(2);
		assertThat(anotherDependencyMetadata.getDependencies().get("first").getGroupId())
				.isEqualTo("org.biz");
		assertThat(
				anotherDependencyMetadata.getDependencies().get("first").getArtifactId())
						.isEqualTo("third");
		assertThat(anotherDependencyMetadata.getDependencies().get("first").getVersion())
				.isEqualTo("0.2.0.RELEASE");
	}

	@Test
	public void addRepoAndRemoveDuplicates() {
		Dependency first = Dependency.withId("first", "org.foo", "first");
		first.setRepository("repo-foo");
		Dependency second = Dependency.withId("second", "org.foo", "second");
		Dependency third = Dependency.withId("third", "org.foo", "third");
		third.setRepository("repo-foo");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addRepository("repo-foo", "my-repo", "http://localhost", false)
				.addDependencyGroup("test", first, second, third).build();
		DependencyMetadata dependencyMetadata = this.provider.get(metadata,
				Version.parse("1.1.5.RELEASE"));
		assertThat(dependencyMetadata.getDependencies()).hasSize(3);
		assertThat(dependencyMetadata.getRepositories()).hasSize(1);
		assertThat(dependencyMetadata.getBoms()).isEmpty();
		assertThat(dependencyMetadata.getRepositories().get("repo-foo")).isSameAs(
				metadata.getConfiguration().getEnv().getRepositories().get("repo-foo"));
	}

	@Test
	public void addBomAndRemoveDuplicates() {
		Dependency first = Dependency.withId("first", "org.foo", "first");
		first.setBom("bom-foo");
		Dependency second = Dependency.withId("second", "org.foo", "second");
		Dependency third = Dependency.withId("third", "org.foo", "third");
		third.setBom("bom-foo");

		BillOfMaterials bom = BillOfMaterials.create("org.foo", "bom");
		bom.getMappings().add(BillOfMaterials.Mapping
				.create("[1.0.0.RELEASE, 1.1.8.RELEASE)", "1.0.0.RELEASE"));
		bom.getMappings()
				.add(BillOfMaterials.Mapping.create("1.1.8.RELEASE", "2.0.0.RELEASE"));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("bom-foo", bom).addDependencyGroup("test", first, second, third)
				.build();
		DependencyMetadata dependencyMetadata = this.provider.get(metadata,
				Version.parse("1.1.5.RELEASE"));
		assertThat(dependencyMetadata.getDependencies()).hasSize(3);
		assertThat(dependencyMetadata.getRepositories()).isEmpty();
		assertThat(dependencyMetadata.getBoms()).hasSize(1);
		assertThat(dependencyMetadata.getBoms().get("bom-foo").getGroupId())
				.isEqualTo("org.foo");
		assertThat(dependencyMetadata.getBoms().get("bom-foo").getArtifactId())
				.isEqualTo("bom");
		assertThat(dependencyMetadata.getBoms().get("bom-foo").getVersion())
				.isEqualTo("1.0.0.RELEASE");
	}

	@Test
	public void repoFromBomAccordingToVersion() {
		DependencyMetadata dependencyMetadata = testRepoFromBomAccordingToVersion(
				"1.0.9.RELEASE");
		assertThat(dependencyMetadata.getBootVersion())
				.isEqualTo(Version.parse("1.0.9.RELEASE"));
		assertThat(dependencyMetadata.getDependencies()).hasSize(3);
		assertThat(dependencyMetadata.getRepositories()).hasSize(2);
		assertThat(dependencyMetadata.getBoms()).hasSize(1);
		assertThat(dependencyMetadata.getRepositories().get("repo-foo").getName())
				.isEqualTo("foo");
		assertThat(dependencyMetadata.getRepositories().get("repo-bar").getName())
				.isEqualTo("bar");
		assertThat(dependencyMetadata.getBoms().get("bom-foo").getGroupId())
				.isEqualTo("org.foo");
		assertThat(dependencyMetadata.getBoms().get("bom-foo").getArtifactId())
				.isEqualTo("bom");
		assertThat(dependencyMetadata.getBoms().get("bom-foo").getVersion())
				.isEqualTo("2.0.0.RELEASE");
	}

	@Test
	public void repoFromBomAccordingToAnotherVersion() {
		DependencyMetadata dependencyMetadata = testRepoFromBomAccordingToVersion(
				"1.1.5.RELEASE");
		assertThat(dependencyMetadata.getBootVersion())
				.isEqualTo(Version.parse("1.1.5.RELEASE"));
		assertThat(dependencyMetadata.getDependencies()).hasSize(3);
		assertThat(dependencyMetadata.getRepositories()).hasSize(2);
		assertThat(dependencyMetadata.getBoms()).hasSize(1);
		assertThat(dependencyMetadata.getRepositories().get("repo-foo").getName())
				.isEqualTo("foo");
		assertThat(dependencyMetadata.getRepositories().get("repo-biz").getName())
				.isEqualTo("biz");
		assertThat(dependencyMetadata.getBoms().get("bom-foo").getGroupId())
				.isEqualTo("org.foo");
		assertThat(dependencyMetadata.getBoms().get("bom-foo").getArtifactId())
				.isEqualTo("bom");
		assertThat(dependencyMetadata.getBoms().get("bom-foo").getVersion())
				.isEqualTo("3.0.0.RELEASE");
	}

	private DependencyMetadata testRepoFromBomAccordingToVersion(String bootVersion) {
		Dependency first = Dependency.withId("first", "org.foo", "first");
		first.setRepository("repo-foo");
		Dependency second = Dependency.withId("second", "org.foo", "second");
		Dependency third = Dependency.withId("third", "org.foo", "third");
		third.setBom("bom-foo");

		BillOfMaterials bom = BillOfMaterials.create("org.foo", "bom");
		bom.getMappings()
				.add(BillOfMaterials.Mapping.create("[1.0.0.RELEASE, 1.1.0.RELEASE)",
						"2.0.0.RELEASE", "repo-foo", "repo-bar"));
		bom.getMappings().add(BillOfMaterials.Mapping.create("1.1.0.RELEASE",
				"3.0.0.RELEASE", "repo-biz"));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("bom-foo", bom)
				.addRepository("repo-foo", "foo", "http://localhost", false)
				.addRepository("repo-bar", "bar", "http://localhost", false)
				.addRepository("repo-biz", "biz", "http://localhost", false)
				.addDependencyGroup("test", first, second, third).build();
		return this.provider.get(metadata, Version.parse(bootVersion));
	}

}
