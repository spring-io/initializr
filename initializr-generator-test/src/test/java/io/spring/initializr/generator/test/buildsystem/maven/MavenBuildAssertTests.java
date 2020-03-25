/*
 * Copyright 2012-2020 the original author or authors.
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

package io.spring.initializr.generator.test.buildsystem.maven;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.assertj.core.api.AssertProvider;
import org.junit.jupiter.api.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link MavenBuildAssert}.
 *
 * @author Stephane Nicoll
 */
class MavenBuildAssertTests {

	@Test
	void hasParent() {
		assertThat(forSampleMavenBuild()).hasParent("com.example.infrastructure", "infrastructure-parent",
				"1.0.0.RELEASE");
	}

	@Test
	void hasParentWithWrongValue() {
		assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forSampleMavenBuild())
				.hasParent("com.example.wrong", "infrastructure-parent", "1.0.0.RELEASE"));
	}

	@Test
	void hasGroupId() {
		assertThat(forSampleMavenBuild()).hasGroupId("com.example");
	}

	@Test
	void hasGroupIdWithWrongValue() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleMavenBuild()).hasGroupId("com.wrong"));
	}

	@Test
	void hasArtifactId() {
		assertThat(forSampleMavenBuild()).hasArtifactId("demo");
	}

	@Test
	void hasArtifactIdWithWrongValue() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleMavenBuild()).hasArtifactId("wrong"));
	}

	@Test
	void hasVersion() {
		assertThat(forSampleMavenBuild()).hasVersion("0.0.1-SNAPSHOT");
	}

	@Test
	void hasVersionWithWrongValue() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleMavenBuild()).hasVersion("1.0.0"));
	}

	@Test
	void hasPackaging() {
		assertThat(forMavenBuild("sample-packaging-pom.xml")).hasPackaging("zip");
	}

	@Test
	void hasPackagingWithNoValue() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleMavenBuild()).hasPackaging("jar"));
	}

	@Test
	void hasName() {
		assertThat(forSampleMavenBuild()).hasName("demo");
	}

	@Test
	void hasNameWithWrongValue() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleMavenBuild()).hasName("wrong"));
	}

	@Test
	void hasDescription() {
		assertThat(forSampleMavenBuild()).hasDescription("Demo project");
	}

	@Test
	void hasDescriptionWithWrongValue() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleMavenBuild()).hasDescription("Wrong description"));
	}

	@Test
	void hasProperty() {
		assertThat(forSampleMavenBuild()).hasProperty("acme.version", "Brussels.SR2");
	}

	@Test
	void hasPropertyWithWrongValue() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleMavenBuild()).hasProperty("acme.version", "Wrong.SR2"));
	}

	@Test
	void doesNotHaveProperty() {
		assertThat(forSampleMavenBuild()).doesNotHaveProperty("unknown.version");
	}

	@Test
	void doesNotHavePropertyWithExistingProperty() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleMavenBuild()).doesNotHaveProperty("acme.version"));
	}

	@Test
	void hasDependenciesSize() {
		assertThat(forSampleMavenBuild()).hasDependenciesSize(2);
	}

	@Test
	void hasDependenciesSizeWithWrongSize() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleMavenBuild()).hasDependenciesSize(3));
	}

	@Test
	void hasDependency() {
		assertThat(forSampleMavenBuild()).hasDependency("com.example.acme", "library");
	}

	@Test
	void hasDependencyWithWrongScope() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleMavenBuild()).hasDependency("com.example.acme", "library-test"));
	}

	@Test
	void hasDependencyWithVersion() {
		assertThat(forMavenBuild("sample-dependency-version-pom.xml")).hasDependency("com.example.acme", "library",
				"1.2.0");
	}

	@Test
	void hasDependencyWithVersionWithWrongVersion() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forMavenBuild("sample-dependency-version-pom.xml"))
						.hasDependency("com.example.acme", "library", "1.3.0"));
	}

	@Test
	void hasDependencyWithVersionAndScope() {
		assertThat(forMavenBuild("sample-dependency-version-pom.xml")).hasDependency("com.example.acme", "library-test",
				"1.3.0", "test");
	}

	@Test
	void hasDependencyWithVersionAndScopeWithWrongScope() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forMavenBuild("sample-dependency-version-pom.xml"))
						.hasDependency("com.example.acme", "library-test", "1.3.0", "runtime"));
	}

	@Test
	void doesNotHaveDependencyArtifactId() {
		assertThat(forSampleMavenBuild()).doesNotHaveDependency("com.example.acme", "wrong");
	}

	@Test
	void doesNotHaveDependencyGroupId() {
		assertThat(forSampleMavenBuild()).doesNotHaveDependency("com.example.wrong", "library");
	}

	@Test
	void doesNotHaveDependencyWithMatchingDependency() {
		assertThatExceptionOfType(AssertionError.class).isThrownBy(
				() -> assertThat(forSampleMavenBuild()).doesNotHaveDependency("com.example.acme", "library"));
	}

	@Test
	void hasBomsSize() {
		assertThat(forSampleMavenBuild()).hasBomsSize(1);
	}

	@Test
	void hasBomsSizeWithWrongSize() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleMavenBuild()).hasBomsSize(3));
	}

	@Test
	void hasBom() {
		assertThat(forSampleMavenBuild()).hasBom("com.example.acme", "library-bom", "${acme.version}");
	}

	@Test
	void hasBomWithWrongGroupId() {
		assertThatExceptionOfType(AssertionError.class).isThrownBy(
				() -> assertThat(forSampleMavenBuild()).hasBom("com.example.wrong", "library-bom", "${acme.version}"));
	}

	@Test
	void hasBomWithWrongArtifactId() {
		assertThatExceptionOfType(AssertionError.class).isThrownBy(
				() -> assertThat(forSampleMavenBuild()).hasBom("com.example.acme", "library-wrong", "${acme.version}"));
	}

	@Test
	void hasBomWithWrongVersion() {
		assertThatExceptionOfType(AssertionError.class).isThrownBy(
				() -> assertThat(forSampleMavenBuild()).hasBom("com.example.acme", "library-bom", "${wrong.version}"));
	}

	@Test
	void doesNotHaveBomArtifactId() {
		assertThat(forSampleMavenBuild()).doesNotHaveBom("com.example.acme", "wrong");
	}

	@Test
	void doesNotHaveBomGroupId() {
		assertThat(forSampleMavenBuild()).doesNotHaveBom("com.example.wrong", "library-bom");
	}

	@Test
	void doesNotHaveBomWithMatchingBom() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleMavenBuild()).doesNotHaveBom("com.example.acme", "library-bom"));
	}

	@Test
	void hasRepositoriesSize() {
		assertThat(forMavenBuild("sample-repositories-pom.xml")).hasRepositoriesSize(2);
	}

	@Test
	void hasRepositoriesSizeWithNoRepository() {
		assertThat(forSampleMavenBuild()).hasRepositoriesSize(0);
	}

	@Test
	void hasRepositoriesSizeWrongSize() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forMavenBuild("sample-repositories-pom.xml")).hasRepositoriesSize(3));
	}

	@Test
	void hasRepository() {
		assertThat(forMavenBuild("sample-repositories-pom.xml")).hasRepository("acme-milestones", "Acme Milestones",
				"https://repo.example.com/milestone", false);
	}

	@Test
	void hasRepositoryWithSnapshots() {
		assertThat(forMavenBuild("sample-repositories-pom.xml")).hasRepository("acme-snapshots", "Acme Snapshots",
				"https://repo.example.com/snapshot", true);
	}

	@Test
	void hasRepositoryWithWrongId() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forMavenBuild("sample-repositories-pom.xml")).hasRepository("acme-wrong",
						"Acme Milestones", "https://repo.example.com/milestone", false));
	}

	@Test
	void hasRepositoryWithWrongName() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forMavenBuild("sample-repositories-pom.xml"))
						.hasRepository("acme-milestones", "Acme Wrong", "https://repo.example.com/milestone", false));
	}

	@Test
	void hasRepositoryWithWrongUrl() {
		assertThatExceptionOfType(AssertionError.class).isThrownBy(
				() -> assertThat(forMavenBuild("sample-repositories-pom.xml")).hasRepository("acme-milestones",
						"Acme Milestones", "https://repo.wrong.com/milestone", false));
	}

	@Test
	void hasRepositoryWithWrongSnapshotFlag() {
		assertThatExceptionOfType(AssertionError.class).isThrownBy(
				() -> assertThat(forMavenBuild("sample-repositories-pom.xml")).hasRepository("acme-milestones",
						"Acme Milestones", "https://repo.example.com/milestone", true));
	}

	private AssertProvider<MavenBuildAssert> forSampleMavenBuild() {
		return forMavenBuild("sample-pom.xml");
	}

	private AssertProvider<MavenBuildAssert> forMavenBuild(String name) {
		try (InputStream in = new ClassPathResource("project/build/maven/" + name).getInputStream()) {
			String content = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
			return () -> new MavenBuildAssert(content);
		}
		catch (IOException ex) {
			throw new IllegalStateException("No content found at " + name, ex);
		}
	}

}
