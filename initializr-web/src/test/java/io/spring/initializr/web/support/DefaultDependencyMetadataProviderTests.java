/*
 * Copyright 2012-2017 the original author or authors.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Stephane Nicoll
 */
public class DefaultDependencyMetadataProviderTests {

	private final DependencyMetadataProvider provider =
			new DefaultDependencyMetadataProvider();

	@Test
	public void filterDependencies() {
		Dependency first = Dependency.withId("first", "org.foo", "first");
		first.setVersionRange("1.1.4.RELEASE");
		Dependency second = Dependency.withId("second", "org.foo", "second");
		Dependency third = Dependency.withId("third", "org.foo", "third");
		third.setVersionRange("1.1.8.RELEASE");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", first, second, third).build();
		DependencyMetadata dependencyMetadata = provider.get(metadata,
				Version.parse("1.1.5.RELEASE"));
		assertEquals(2, dependencyMetadata.getDependencies().size());
		assertEquals(0, dependencyMetadata.getRepositories().size());
		assertEquals(0, dependencyMetadata.getBoms().size());
		assertSame(first, dependencyMetadata.getDependencies().get("first"));
		assertSame(second, dependencyMetadata.getDependencies().get("second"));
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

		DependencyMetadata dependencyMetadata = provider.get(metadata,
				Version.parse("1.0.5.RELEASE"));
		assertEquals(2, dependencyMetadata.getDependencies().size());
		assertEquals("org.bar",
				dependencyMetadata.getDependencies().get("first").getGroupId());
		assertEquals("second",
				dependencyMetadata.getDependencies().get("first").getArtifactId());
		assertEquals("0.1.0.RELEASE",
				dependencyMetadata.getDependencies().get("first").getVersion());

		DependencyMetadata anotherDependencyMetadata = provider.get(metadata,
				Version.parse("1.1.0.RELEASE"));
		assertEquals(2, anotherDependencyMetadata.getDependencies().size());
		assertEquals("org.biz",
				anotherDependencyMetadata.getDependencies().get("first").getGroupId());
		assertEquals("third",
				anotherDependencyMetadata.getDependencies().get("first").getArtifactId());
		assertEquals("0.2.0.RELEASE",
				anotherDependencyMetadata.getDependencies().get("first").getVersion());
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
		DependencyMetadata dependencyMetadata = provider.get(metadata,
				Version.parse("1.1.5.RELEASE"));
		assertEquals(3, dependencyMetadata.getDependencies().size());
		assertEquals(1, dependencyMetadata.getRepositories().size());
		assertEquals(0, dependencyMetadata.getBoms().size());
		assertSame(metadata.getConfiguration().getEnv().getRepositories().get("repo-foo"),
				dependencyMetadata.getRepositories().get("repo-foo"));
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
		DependencyMetadata dependencyMetadata = provider.get(metadata,
				Version.parse("1.1.5.RELEASE"));
		assertEquals(3, dependencyMetadata.getDependencies().size());
		assertEquals(0, dependencyMetadata.getRepositories().size());
		assertEquals(1, dependencyMetadata.getBoms().size());
		assertEquals("org.foo", dependencyMetadata.getBoms().get("bom-foo").getGroupId());
		assertEquals("bom", dependencyMetadata.getBoms().get("bom-foo").getArtifactId());
		assertEquals("1.0.0.RELEASE",
				dependencyMetadata.getBoms().get("bom-foo").getVersion());
	}

	@Test
	public void repoFromBomAccordingToVersion() {
		DependencyMetadata dependencyMetadata = testRepoFromBomAccordingToVersion(
				"1.0.9.RELEASE");
		assertEquals(Version.parse("1.0.9.RELEASE"), dependencyMetadata.getBootVersion());
		assertEquals(3, dependencyMetadata.getDependencies().size());
		assertEquals(2, dependencyMetadata.getRepositories().size());
		assertEquals(1, dependencyMetadata.getBoms().size());
		assertEquals("foo",
				dependencyMetadata.getRepositories().get("repo-foo").getName());
		assertEquals("bar",
				dependencyMetadata.getRepositories().get("repo-bar").getName());
		assertEquals("org.foo", dependencyMetadata.getBoms().get("bom-foo").getGroupId());
		assertEquals("bom", dependencyMetadata.getBoms().get("bom-foo").getArtifactId());
		assertEquals("2.0.0.RELEASE",
				dependencyMetadata.getBoms().get("bom-foo").getVersion());
	}

	@Test
	public void repoFromBomAccordingToAnotherVersion() {
		DependencyMetadata dependencyMetadata = testRepoFromBomAccordingToVersion(
				"1.1.5.RELEASE");
		assertEquals(Version.parse("1.1.5.RELEASE"), dependencyMetadata.getBootVersion());
		assertEquals(3, dependencyMetadata.getDependencies().size());
		assertEquals(2, dependencyMetadata.getRepositories().size());
		assertEquals(1, dependencyMetadata.getBoms().size());
		assertEquals("foo",
				dependencyMetadata.getRepositories().get("repo-foo").getName());
		assertEquals("biz",
				dependencyMetadata.getRepositories().get("repo-biz").getName());
		assertEquals("org.foo", dependencyMetadata.getBoms().get("bom-foo").getGroupId());
		assertEquals("bom", dependencyMetadata.getBoms().get("bom-foo").getArtifactId());
		assertEquals("3.0.0.RELEASE",
				dependencyMetadata.getBoms().get("bom-foo").getVersion());
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
		InitializrMetadata metadata = InitializrMetadataTestBuilder
				.withDefaults().addBom("bom-foo", bom)
				.addRepository("repo-foo", "foo", "http://localhost", false)
				.addRepository("repo-bar", "bar", "http://localhost", false)
				.addRepository("repo-biz", "biz", "http://localhost", false)
				.addDependencyGroup("test", first, second, third).build();
		return provider.get(metadata, Version.parse(bootVersion));
	}

}
