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

package io.spring.initializr.generator;

import java.util.Arrays;

import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.Dependency.Mapping;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
public class ProjectRequestTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	private InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.build();

	@Test
	public void initializeGroupIdAndArtifactId() {
		this.metadata = InitializrMetadataBuilder.create().build();
		this.metadata.getGroupId().setContent("org.acme");
		this.metadata.getArtifactId().setContent("my-project");
		ProjectRequest request = initProjectRequest();
		assertThat(request.getGroupId()).isEqualTo("org.acme");
		assertThat(request.getArtifactId()).isEqualTo("my-project");
	}

	@Test
	public void initializeSetsMetadataDefaults() {
		ProjectRequest request = initProjectRequest();
		assertThat(request.getName()).isEqualTo(this.metadata.getName().getContent());
		assertThat(request.getType())
				.isEqualTo(this.metadata.getTypes().getDefault().getId());
		assertThat(request.getDescription())
				.isEqualTo(this.metadata.getDescription().getContent());
		assertThat(request.getGroupId())
				.isEqualTo(this.metadata.getGroupId().getContent());
		assertThat(request.getArtifactId())
				.isEqualTo(this.metadata.getArtifactId().getContent());
		assertThat(request.getVersion())
				.isEqualTo(this.metadata.getVersion().getContent());
		assertThat(request.getBootVersion())
				.isEqualTo(this.metadata.getBootVersions().getDefault().getId());
		assertThat(request.getPackaging())
				.isEqualTo(this.metadata.getPackagings().getDefault().getId());
	}

	@Test
	public void resolve() {
		this.metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", "web", "security", "spring-data").build();
		ProjectRequest request = initProjectRequest();
		request.setType("maven-project");
		request.getStyle().addAll(Arrays.asList("web", "spring-data"));
		request.resolve(this.metadata);
		assertThat(request.getBuild()).as("Build type not detected").isEqualTo("maven");
		assertBootStarter(request.getResolvedDependencies().get(0), "web");
		assertBootStarter(request.getResolvedDependencies().get(1), "spring-data");
	}

	@Test
	public void resolveWithDependencies() {
		this.metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", "web", "security", "spring-data").build();
		ProjectRequest request = initProjectRequest();
		request.setType("maven-project");
		request.getDependencies().addAll(Arrays.asList("web", "spring-data"));
		request.resolve(this.metadata);
		assertThat(request.getBuild()).as("Build type not detected").isEqualTo("maven");
		assertBootStarter(request.getResolvedDependencies().get(0), "web");
		assertBootStarter(request.getResolvedDependencies().get(1), "spring-data");
	}

	@Test
	public void resolveFullMetadata() {
		this.metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", createDependency("org.foo", "acme", "1.2.0"))
				.build();
		ProjectRequest request = initProjectRequest();
		request.getStyle().add("org.foo:acme");
		request.resolve(this.metadata);
		assertDependency(request.getResolvedDependencies().get(0), "org.foo", "acme",
				"1.2.0");
	}

	@Test
	public void resolveUnknownSimpleId() {
		this.metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", "org.foo:bar").build();
		ProjectRequest request = initProjectRequest();
		request.getStyle().addAll(Arrays.asList("org.foo:bar", "foo-bar"));

		this.thrown.expect(InvalidProjectRequestException.class);
		this.thrown.expectMessage("foo-bar");
		request.resolve(this.metadata);
		assertThat(request.getResolvedDependencies()).hasSize(1);
	}

	@Test
	public void resolveUnknownDependency() {
		this.metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", "org.foo:bar").build();
		ProjectRequest request = initProjectRequest();
		request.getStyle().add("org.foo:acme"); // does not exist

		this.thrown.expect(InvalidProjectRequestException.class);
		this.thrown.expectMessage("org.foo:acme");
		request.resolve(this.metadata);
		assertThat(request.getResolvedDependencies()).hasSize(1);
	}

	@Test
	public void resolveDependencyInRange() {
		Dependency dependency = createDependency("org.foo", "bar", "1.2.0.RELEASE");
		dependency.setVersionRange("[1.0.1.RELEASE, 1.2.0.RELEASE)");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", dependency).build();
		ProjectRequest request = initProjectRequest();
		request.getStyle().add("org.foo:bar");
		request.setBootVersion("1.1.2.RELEASE");
		request.resolve(metadata);
	}

	@Test
	public void resolveDependencyNotInRange() {
		Dependency dependency = createDependency("org.foo", "bar", "1.2.0.RELEASE");
		dependency.setVersionRange("[1.0.1.RELEASE, 1.2.0.RELEASE)");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", dependency).build();
		ProjectRequest request = initProjectRequest();
		request.getStyle().add("org.foo:bar");
		request.setBootVersion("0.9.9.RELEASE");

		this.thrown.expect(InvalidProjectRequestException.class);
		this.thrown.expectMessage("org.foo:bar");
		this.thrown.expectMessage("0.9.9.RELEASE");
		request.resolve(metadata);
	}

	@Test
	public void resolveDependencyVersion() {
		Dependency dependency = createDependency("org.foo", "bar", "1.2.0.RELEASE");
		dependency.getMappings().add(Mapping.create("[1.0.0.RELEASE, 1.1.0.RELEASE)",
				null, null, "0.1.0.RELEASE"));
		dependency.getMappings()
				.add(Mapping.create("1.1.0.RELEASE", null, null, "0.2.0.RELEASE"));
		this.metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", dependency).build();

		ProjectRequest request = initProjectRequest();
		request.setBootVersion("1.0.5.RELEASE");
		request.getStyle().add("org.foo:bar");
		request.resolve(this.metadata);
		assertDependency(request.getResolvedDependencies().get(0), "org.foo", "bar",
				"0.1.0.RELEASE");

		ProjectRequest anotherRequest = new ProjectRequest();
		anotherRequest.setBootVersion("1.1.0.RELEASE");
		anotherRequest.getStyle().add("org.foo:bar");
		anotherRequest.resolve(this.metadata);
		assertDependency(anotherRequest.getResolvedDependencies().get(0), "org.foo",
				"bar", "0.2.0.RELEASE");
	}

	@Test
	public void resolveBuild() {
		ProjectRequest request = initProjectRequest();
		request.setType("gradle-project");
		request.resolve(this.metadata);
		assertThat(request.getBuild()).isEqualTo("gradle");
	}

	@Test
	public void resolveBuildNoTag() {
		this.metadata = InitializrMetadataTestBuilder.withDefaults()
				.addType("foo", false, "/foo.zip", null, null).build();
		ProjectRequest request = initProjectRequest();
		request.setType("foo");
		request.resolve(this.metadata);
		assertThat(request.getBuild()).isNull();
	}

	@Test
	public void resolveUnknownType() {
		ProjectRequest request = initProjectRequest();
		request.setType("foo-project");

		this.thrown.expect(InvalidProjectRequestException.class);
		this.thrown.expectMessage("foo-project");
		request.resolve(this.metadata);
	}

	@Test
	public void resolveApplicationNameWithNoName() {
		ProjectRequest request = initProjectRequest();
		request.setName(null);
		request.resolve(this.metadata);
		assertThat(request.getApplicationName()).isEqualTo(
				this.metadata.getConfiguration().getEnv().getFallbackApplicationName());
	}

	@Test
	public void resolveApplicationName() {
		ProjectRequest request = initProjectRequest();
		request.setName("Foo2");
		request.resolve(this.metadata);
		assertThat(request.getApplicationName()).isEqualTo("Foo2Application");
	}

	@Test
	public void resolveApplicationNameWithApplicationNameSet() {
		ProjectRequest request = initProjectRequest();
		request.setName("Foo2");
		request.setApplicationName("MyApplicationName");

		request.resolve(this.metadata);
		assertThat(request.getApplicationName()).isEqualTo("MyApplicationName");
	}

	@Test
	public void packageNameInferredByGroupIdAndArtifactId() {
		ProjectRequest request = initProjectRequest();
		request.setGroupId("org.acme");
		request.setArtifactId("foo");
		request.resolve(this.metadata);
		assertThat(request.getPackageName()).isEqualTo("org.acme.foo");
	}

	@Test
	public void packageNameInferredByGroupIdAndCompositeArtifactId() {
		ProjectRequest request = initProjectRequest();
		request.setGroupId("org.acme");
		request.setArtifactId("foo-bar");
		request.resolve(this.metadata);
		assertThat(request.getPackageName()).isEqualTo("org.acme.foobar");
	}

	@Test
	public void packageNameUseFallbackIfGroupIdNotSet() {
		ProjectRequest request = initProjectRequest();
		request.setGroupId(null);
		request.setArtifactId("foo");
		request.resolve(this.metadata);
		assertThat(request.getPackageName()).isEqualTo("com.example.demo");
	}

	@Test
	public void packageNameUseFallbackIfArtifactIdNotSet() {
		ProjectRequest request = initProjectRequest();
		request.setGroupId("org.acme");
		request.setArtifactId(null);
		request.resolve(this.metadata);
		assertThat(request.getPackageName()).isEqualTo("com.example.demo");
	}

	@Test
	public void cleanPackageNameLeadingNumbers() {
		ProjectRequest request = new ProjectRequest();
		request.setPackageName("org.foo.42bar");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.build();

		request.resolve(metadata);
		assertThat(request.getPackageName()).isEqualTo("org.foo.bar");
	}

	@Test
	public void cleanPackageNameWithNoName() {
		ProjectRequest request = initProjectRequest();
		request.resolve(this.metadata);
		assertThat(request.getPackageName())
				.isEqualTo(this.metadata.getPackageName().getContent());
	}

	@Test
	public void cleanPackageName() {
		ProjectRequest request = initProjectRequest();
		request.setPackageName("com:foo  bar");
		request.resolve(this.metadata);
		assertThat(request.getPackageName()).isEqualTo("com.foo.bar");
	}

	@Test
	public void resolveAdditionalBoms() {
		Dependency dependency = Dependency.withId("foo");
		dependency.setBom("foo-bom");
		BillOfMaterials bom = BillOfMaterials.create("com.example", "foo-bom", "1.0.0");
		bom.getAdditionalBoms().add("bar-bom");
		BillOfMaterials additionalBom = BillOfMaterials.create("com.example", "bar-bom",
				"1.1.0");
		this.metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("foo-bom", bom).addBom("bar-bom", additionalBom)
				.addDependencyGroup("test", dependency).build();
		ProjectRequest request = initProjectRequest();
		request.getStyle().add("foo");
		request.resolve(this.metadata);
		assertThat(request.getResolvedDependencies()).hasSize(1);
		assertThat(request.getBoms()).hasSize(2);
		assertThat(request.getBoms().get("foo-bom")).isEqualTo(bom);
		assertThat(request.getBoms().get("bar-bom")).isEqualTo(additionalBom);
	}

	@Test
	public void resolveAdditionalBomsDuplicates() {
		Dependency dependency = Dependency.withId("foo");
		dependency.setBom("foo-bom");
		Dependency anotherDependency = Dependency.withId("bar");
		anotherDependency.setBom("bar-bom");
		BillOfMaterials bom = BillOfMaterials.create("com.example", "foo-bom", "1.0.0");
		bom.getAdditionalBoms().add("bar-bom");
		BillOfMaterials additionalBom = BillOfMaterials.create("com.example", "bar-bom",
				"1.1.0");
		this.metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("foo-bom", bom).addBom("bar-bom", additionalBom)
				.addDependencyGroup("test", dependency, anotherDependency).build();
		ProjectRequest request = initProjectRequest();
		request.getStyle().addAll(Arrays.asList("foo", "bar"));
		request.resolve(this.metadata);
		assertThat(request.getResolvedDependencies()).hasSize(2);
		assertThat(request.getBoms()).hasSize(2);
		assertThat(request.getBoms().get("foo-bom")).isEqualTo(bom);
		assertThat(request.getBoms().get("bar-bom")).isEqualTo(additionalBom);
	}

	@Test
	public void resolveAdditionalRepositories() {
		Dependency dependency = Dependency.withId("foo");
		dependency.setBom("foo-bom");
		dependency.setRepository("foo-repo");
		BillOfMaterials bom = BillOfMaterials.create("com.example", "foo-bom", "1.0.0");
		bom.getRepositories().add("bar-repo");
		this.metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("foo-bom", bom)
				.addRepository("foo-repo", "foo-repo", "http://example.com/foo", false)
				.addRepository("bar-repo", "bar-repo", "http://example.com/bar", false)
				.addDependencyGroup("test", dependency).build();
		ProjectRequest request = initProjectRequest();
		request.getStyle().add("foo");
		request.resolve(this.metadata);
		assertThat(request.getResolvedDependencies()).hasSize(1);
		assertThat(request.getBoms()).hasSize(1);
		assertThat(request.getRepositories()).hasSize(2);
		assertThat(request.getRepositories().get("foo-repo")).isEqualTo(this.metadata
				.getConfiguration().getEnv().getRepositories().get("foo-repo"));
		assertThat(request.getRepositories().get("bar-repo")).isEqualTo(this.metadata
				.getConfiguration().getEnv().getRepositories().get("bar-repo"));
	}

	@Test
	public void resolveAdditionalRepositoriesDuplicates() {
		Dependency dependency = Dependency.withId("foo");
		dependency.setBom("foo-bom");
		dependency.setRepository("foo-repo");
		BillOfMaterials bom = BillOfMaterials.create("com.example", "foo-bom", "1.0.0");
		bom.getRepositories().add("bar-repo");
		Dependency anotherDependency = Dependency.withId("bar");
		anotherDependency.setRepository("bar-repo");
		this.metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("foo-bom", bom)
				.addRepository("foo-repo", "foo-repo", "http://example.com/foo", false)
				.addRepository("bar-repo", "bar-repo", "http://example.com/bar", false)
				.addDependencyGroup("test", dependency, anotherDependency).build();
		ProjectRequest request = initProjectRequest();
		request.getStyle().addAll(Arrays.asList("foo", "bar"));
		request.resolve(this.metadata);
		assertThat(request.getResolvedDependencies()).hasSize(2);
		assertThat(request.getBoms()).hasSize(1);
		assertThat(request.getRepositories()).hasSize(2);
		assertThat(request.getRepositories().get("foo-repo")).isEqualTo(this.metadata
				.getConfiguration().getEnv().getRepositories().get("foo-repo"));
		assertThat(request.getRepositories().get("bar-repo")).isEqualTo(this.metadata
				.getConfiguration().getEnv().getRepositories().get("bar-repo"));
	}

	private ProjectRequest initProjectRequest() {
		ProjectRequest request = new ProjectRequest();
		request.initialize(this.metadata);
		return request;
	}

	private static void assertBootStarter(Dependency actual, String name) {
		Dependency expected = new Dependency();
		expected.asSpringBootStarter(name);
		assertDependency(actual, expected.getGroupId(), expected.getArtifactId(),
				expected.getVersion());
		assertThat(actual.getId()).isEqualTo(name);
	}

	private static Dependency createDependency(String groupId, String artifactId,
			String version) {
		return Dependency.create(groupId, artifactId, version, Dependency.SCOPE_COMPILE);
	}

	private static void assertDependency(Dependency actual, String groupId,
			String artifactId, String version) {
		assertThat(actual.getGroupId()).isEqualTo(groupId);
		assertThat(actual.getArtifactId()).isEqualTo(artifactId);
		assertThat(actual.getVersion()).isEqualTo(version);
	}

}
