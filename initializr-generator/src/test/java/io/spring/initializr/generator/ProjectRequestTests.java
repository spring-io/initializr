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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Stephane Nicoll
 */
public class ProjectRequestTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void initializeProjectRequest() {
		InitializrMetadata metadata = InitializrMetadataBuilder.create().build();
		metadata.getGroupId().setContent("org.acme");
		metadata.getArtifactId().setContent("my-project");
		ProjectRequest request = new ProjectRequest();
		request.initialize(metadata);
		assertEquals("org.acme", request.getGroupId());
		assertEquals("my-project", request.getArtifactId());
	}

	@Test
	public void initializeProjectRequestWithDefaults() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.build();
		ProjectRequest request = new ProjectRequest();
		request.initialize(metadata);
		assertEquals(metadata.getName().getContent(), request.getName());
		assertEquals(metadata.getTypes().getDefault().getId(), request.getType());
		assertEquals(metadata.getDescription().getContent(), request.getDescription());
		assertEquals(metadata.getGroupId().getContent(), request.getGroupId());
		assertEquals(metadata.getArtifactId().getContent(), request.getArtifactId());
		assertEquals(metadata.getVersion().getContent(), request.getVersion());
		assertEquals(metadata.getBootVersions().getDefault().getId(),
				request.getBootVersion());
		assertEquals(metadata.getPackagings().getDefault().getId(),
				request.getPackaging());
	}

	@Test
	public void resolve() {
		ProjectRequest request = new ProjectRequest();
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", "web", "security", "spring-data").build();

		request.setType("maven-project");
		request.getStyle().addAll(Arrays.asList("web", "spring-data"));
		request.resolve(metadata);
		assertEquals("Build type not detected", "maven", request.getBuild());
		assertBootStarter(request.getResolvedDependencies().get(0), "web");
		assertBootStarter(request.getResolvedDependencies().get(1), "spring-data");
	}

	@Test
	public void resolveWithDependencies() {
		ProjectRequest request = new ProjectRequest();
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", "web", "security", "spring-data").build();

		request.setType("maven-project");
		request.getDependencies().addAll(Arrays.asList("web", "spring-data"));
		request.resolve(metadata);
		assertEquals("Build type not detected", "maven", request.getBuild());
		assertBootStarter(request.getResolvedDependencies().get(0), "web");
		assertBootStarter(request.getResolvedDependencies().get(1), "spring-data");
	}

	@Test
	public void resolveFullMetadata() {
		ProjectRequest request = new ProjectRequest();
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", createDependency("org.foo", "acme", "1.2.0"))
				.build();
		request.getStyle().add("org.foo:acme");
		request.resolve(metadata);
		assertDependency(request.getResolvedDependencies().get(0), "org.foo", "acme",
				"1.2.0");
	}

	@Test
	public void resolveUnknownSimpleId() {
		ProjectRequest request = new ProjectRequest();
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", "org.foo:bar").build();

		request.getStyle().addAll(Arrays.asList("org.foo:bar", "foo-bar"));

		thrown.expect(InvalidProjectRequestException.class);
		thrown.expectMessage("foo-bar");
		request.resolve(metadata);
		assertEquals(1, request.getResolvedDependencies().size());
	}

	@Test
	public void resolveUnknownDependency() {
		ProjectRequest request = new ProjectRequest();
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", "org.foo:bar").build();

		request.getStyle().add("org.foo:acme"); // does not exist

		thrown.expect(InvalidProjectRequestException.class);
		thrown.expectMessage("org.foo:acme");
		request.resolve(metadata);
		assertEquals(1, request.getResolvedDependencies().size());
	}

	@Test
	public void resolveDependencyInRange() {
		ProjectRequest request = new ProjectRequest();
		Dependency dependency = createDependency("org.foo", "bar", "1.2.0.RELEASE");
		dependency.setVersionRange("[1.0.1.RELEASE, 1.2.0.RELEASE)");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", dependency).build();

		request.getStyle().add("org.foo:bar");
		request.setBootVersion("1.1.2.RELEASE");
		request.resolve(metadata);
	}

	@Test
	public void resolveDependencyNotInRange() {
		ProjectRequest request = new ProjectRequest();
		Dependency dependency = createDependency("org.foo", "bar", "1.2.0.RELEASE");
		dependency.setVersionRange("[1.0.1.RELEASE, 1.2.0.RELEASE)");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", dependency).build();

		request.getStyle().add("org.foo:bar");
		request.setBootVersion("0.9.9.RELEASE");

		thrown.expect(InvalidProjectRequestException.class);
		thrown.expectMessage("org.foo:bar");
		thrown.expectMessage("0.9.9.RELEASE");
		request.resolve(metadata);
	}

	@Test
	public void resolveDependencyVersion() {
		Dependency dependency = createDependency("org.foo", "bar", "1.2.0.RELEASE");
		dependency.getMappings().add(Mapping.create(
				"[1.0.0.RELEASE, 1.1.0.RELEASE)", null, null, "0.1.0.RELEASE"));
		dependency.getMappings().add(Mapping.create(
				"1.1.0.RELEASE", null, null, "0.2.0.RELEASE"));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("code", dependency).build();

		ProjectRequest request = new ProjectRequest();
		request.setBootVersion("1.0.5.RELEASE");
		request.getStyle().add("org.foo:bar");
		request.resolve(metadata);
		assertDependency(request.getResolvedDependencies().get(0), "org.foo", "bar",
				"0.1.0.RELEASE");

		ProjectRequest anotherRequest = new ProjectRequest();
		anotherRequest.setBootVersion("1.1.0.RELEASE");
		anotherRequest.getStyle().add("org.foo:bar");
		anotherRequest.resolve(metadata);
		assertDependency(anotherRequest.getResolvedDependencies().get(0), "org.foo",
				"bar", "0.2.0.RELEASE");
	}

	@Test
	public void resolveBuild() {
		ProjectRequest request = new ProjectRequest();
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.build();
		request.setType("gradle-project");

		request.resolve(metadata);
		assertEquals("gradle", request.getBuild());
	}

	@Test
	public void resolveBuildNoTag() {
		ProjectRequest request = new ProjectRequest();
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addType("foo", false, "/foo.zip", null, null).build();
		request.setType("foo");

		request.resolve(metadata);
		assertNull(request.getBuild());
	}

	@Test
	public void resolveUnknownType() {
		ProjectRequest request = new ProjectRequest();
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.build();
		request.setType("foo-project");

		thrown.expect(InvalidProjectRequestException.class);
		thrown.expectMessage("foo-project");
		request.resolve(metadata);
	}

	@Test
	public void resolveApplicationNameWithNoName() {
		ProjectRequest request = new ProjectRequest();
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.build();

		request.resolve(metadata);
		assertEquals(metadata.getConfiguration().getEnv().getFallbackApplicationName(),
				request.getApplicationName());
	}

	@Test
	public void resolveApplicationName() {
		ProjectRequest request = new ProjectRequest();
		request.setName("Foo2");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.build();

		request.resolve(metadata);
		assertEquals("Foo2Application", request.getApplicationName());
	}

	@Test
	public void resolveApplicationNameWithApplicationNameSet() {
		ProjectRequest request = new ProjectRequest();
		request.setName("Foo2");
		request.setApplicationName("MyApplicationName");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.build();

		request.resolve(metadata);
		assertEquals("MyApplicationName", request.getApplicationName());
	}

	@Test
	public void cleanPackageNameWithNoName() {
		ProjectRequest request = new ProjectRequest();
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.build();

		request.resolve(metadata);
		assertEquals(metadata.getPackageName().getContent(), request.getPackageName());
	}

	@Test
	public void cleanPackageName() {
		ProjectRequest request = new ProjectRequest();
		request.setPackageName("com:foo  bar");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.build();

		request.resolve(metadata);
		assertEquals("com.foo.bar", request.getPackageName());
	}

	@Test
	public void resolveAdditionalBoms() {
		ProjectRequest request = new ProjectRequest();
		Dependency dependency = Dependency.withId("foo");
		dependency.setBom("foo-bom");
		BillOfMaterials bom = BillOfMaterials.create("com.example", "foo-bom", "1.0.0");
		bom.getAdditionalBoms().add("bar-bom");
		BillOfMaterials additionalBom = BillOfMaterials.create("com.example", "bar-bom",
				"1.1.0");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("foo-bom", bom).addBom("bar-bom", additionalBom)
				.addDependencyGroup("test", dependency).build();
		request.getStyle().add("foo");
		request.resolve(metadata);
		assertEquals(1, (request.getResolvedDependencies().size()));
		assertEquals(2, request.getBoms().size());
		assertEquals(bom, request.getBoms().get("foo-bom"));
		assertEquals(additionalBom, request.getBoms().get("bar-bom"));
	}

	@Test
	public void resolveAdditionalBomsDuplicates() {
		ProjectRequest request = new ProjectRequest();
		Dependency dependency = Dependency.withId("foo");
		dependency.setBom("foo-bom");
		Dependency anotherDependency = Dependency.withId("bar");
		anotherDependency.setBom("bar-bom");
		BillOfMaterials bom = BillOfMaterials.create("com.example", "foo-bom", "1.0.0");
		bom.getAdditionalBoms().add("bar-bom");
		BillOfMaterials additionalBom = BillOfMaterials.create("com.example", "bar-bom",
				"1.1.0");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("foo-bom", bom).addBom("bar-bom", additionalBom)
				.addDependencyGroup("test", dependency, anotherDependency).build();
		request.getStyle().addAll(Arrays.asList("foo", "bar"));
		request.resolve(metadata);
		assertEquals(2, request.getResolvedDependencies().size());
		assertEquals(2, request.getBoms().size());
		assertEquals(bom, request.getBoms().get("foo-bom"));
		assertEquals(additionalBom, request.getBoms().get("bar-bom"));
	}

	@Test
	public void resolveAdditionalRepositories() {
		ProjectRequest request = new ProjectRequest();
		Dependency dependency = Dependency.withId("foo");
		dependency.setBom("foo-bom");
		dependency.setRepository("foo-repo");
		BillOfMaterials bom = BillOfMaterials.create("com.example", "foo-bom", "1.0.0");
		bom.getRepositories().add("bar-repo");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("foo-bom", bom)
				.addRepository("foo-repo", "foo-repo", "http://example.com/foo", false)
				.addRepository("bar-repo", "bar-repo", "http://example.com/bar", false)
				.addDependencyGroup("test", dependency).build();
		request.getStyle().add("foo");
		request.resolve(metadata);
		assertEquals(1, request.getResolvedDependencies().size());
		assertEquals(1, request.getBoms().size());
		assertEquals(2, request.getRepositories().size());
		assertEquals(
				metadata.getConfiguration().getEnv().getRepositories().get("foo-repo"),
				request.getRepositories().get("foo-repo"));
		assertEquals(
				metadata.getConfiguration().getEnv().getRepositories().get("bar-repo"),
				request.getRepositories().get("bar-repo"));
	}

	@Test
	public void resolveAdditionalRepositoriesDuplicates() {
		ProjectRequest request = new ProjectRequest();
		Dependency dependency = Dependency.withId("foo");
		dependency.setBom("foo-bom");
		dependency.setRepository("foo-repo");
		BillOfMaterials bom = BillOfMaterials.create("com.example", "foo-bom", "1.0.0");
		bom.getRepositories().add("bar-repo");
		Dependency anotherDependency = Dependency.withId("bar");
		anotherDependency.setRepository("bar-repo");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("foo-bom", bom)
				.addRepository("foo-repo", "foo-repo", "http://example.com/foo", false)
				.addRepository("bar-repo", "bar-repo", "http://example.com/bar", false)
				.addDependencyGroup("test", dependency, anotherDependency).build();
		request.getStyle().addAll(Arrays.asList("foo", "bar"));
		request.resolve(metadata);
		assertEquals(2, request.getResolvedDependencies().size());
		assertEquals(1, request.getBoms().size());
		assertEquals(2, request.getRepositories().size());
		assertEquals(
				metadata.getConfiguration().getEnv().getRepositories().get("foo-repo"),
				request.getRepositories().get("foo-repo"));
		assertEquals(
				metadata.getConfiguration().getEnv().getRepositories().get("bar-repo"),
				request.getRepositories().get("bar-repo"));
	}

	private static void assertBootStarter(Dependency actual, String name) {
		Dependency expected = new Dependency();
		expected.asSpringBootStarter(name);
		assertDependency(actual, expected.getGroupId(), expected.getArtifactId(),
				expected.getVersion());
		assertEquals(name, actual.getId());
	}

	private static Dependency createDependency(String groupId, String artifactId,
			String version) {
		return Dependency.create(groupId, artifactId, version, Dependency.SCOPE_COMPILE);
	}

	private static void assertDependency(Dependency actual, String groupId,
			String artifactId, String version) {
		assertEquals(groupId, actual.getGroupId());
		assertEquals(artifactId, actual.getArtifactId());
		assertEquals(version, actual.getVersion());
	}
}
