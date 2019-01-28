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

package io.spring.initializr.generator;

import java.util.stream.Stream;

import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.test.generator.ProjectAssert;
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder;
import io.spring.initializr.util.VersionProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.core.io.ClassPathResource;

/**
 * Project generator tests for supported build systems.
 *
 * @author Stephane Nicoll
 */
class ProjectGeneratorBuildTests extends AbstractProjectGeneratorTests {

	public static Stream<Arguments> parameters() {
		return Stream.of(Arguments.arguments("maven", "pom.xml"),
				Arguments.arguments("gradle", "build.gradle"));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void currentGenerationJarJava(String build, String fileName) {
		testCurrentGenerationJar("java", build, fileName);
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void currentGenerationJarGroovy(String build, String fileName) {
		testCurrentGenerationJar("groovy", build, fileName);
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void currentGenerationJarKotlin(String build, String fileName) {
		testCurrentGenerationJar("kotlin", build, fileName);
	}

	private void testCurrentGenerationJar(String language, String build,
			String fileName) {
		ProjectRequest request = createProjectRequestForType(build);
		request.setLanguage(language);
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(fileName).equalsTo(new ClassPathResource(
				"project/" + language + "/standard/" + getAssertFileName(fileName)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void currentGenerationWarJava(String build, String fileName) {
		testCurrentGenerationWar("java", build, fileName);
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void currentGenerationWarGroovy(String build, String fileName) {
		testCurrentGenerationWar("groovy", build, fileName);
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void currentGenerationWarKotlin(String build, String fileName) {
		testCurrentGenerationWar("kotlin", build, fileName);
	}

	private void testCurrentGenerationWar(String language, String build,
			String fileName) {
		ProjectRequest request = createProjectRequestForType(build, "web");
		request.setPackaging("war");
		request.setLanguage(language);
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(fileName).equalsTo(new ClassPathResource(
				"project/" + language + "/standard/war-" + getAssertFileName(fileName)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void previousGenerationJarJava(String build, String fileName) {
		testPreviousGenerationJar("java", build, fileName);
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void previousGenerationJarGroovy(String build, String fileName) {
		testPreviousGenerationJar("groovy", build, fileName);
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void previousGenerationJarKotlin(String build, String fileName) {
		testPreviousGenerationJar("kotlin", build, fileName);
	}

	private void testPreviousGenerationJar(String language, String build,
			String fileName) {
		ProjectRequest request = createProjectRequestForType(build);
		request.setLanguage(language);
		request.setBootVersion("1.5.18.RELEASE");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(fileName).equalsTo(new ClassPathResource(
				"project/" + language + "/previous/" + getAssertFileName(fileName)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void kotlinJava11(String build, String fileName) {
		ProjectRequest request = createProjectRequestForType(build);
		request.setLanguage("kotlin");
		request.setJavaVersion("11");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(fileName).equalsTo(new ClassPathResource(
				"project/" + build + "/kotlin-java11-" + getAssertFileName(fileName)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void versionOverride(String build, String fileName) {
		ProjectRequest request = createProjectRequestForType(build, "web");
		request.getBuildProperties().getVersions().put(
				VersionProperty.of("spring-foo.version", false), () -> "0.1.0.RELEASE");
		request.getBuildProperties().getVersions()
				.put(VersionProperty.of("spring-bar.version"), () -> "0.2.0.RELEASE");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(fileName).equalsTo(new ClassPathResource(
				"project/" + build + "/version-override-" + getAssertFileName(fileName)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void bomWithVersionProperty(String build, String fileName) {
		Dependency foo = Dependency.withId("foo", "org.acme", "foo");
		foo.setBom("the-bom");
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom", "1.3.3");
		bom.setVersionProperty("foo.version");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("foo", foo).addBom("the-bom", bom).build();
		applyMetadata(metadata);
		ProjectRequest request = createProjectRequestForType(build, "foo");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(fileName).equalsTo(new ClassPathResource(
				"project/" + build + "/bom-property-" + getAssertFileName(fileName)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void compileOnlyDependency(String build, String fileName) {
		Dependency foo = Dependency.withId("foo", "org.acme", "foo");
		foo.setScope(Dependency.SCOPE_COMPILE_ONLY);
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("core", "web", "data-jpa")
				.addDependencyGroup("foo", foo).build();
		applyMetadata(metadata);
		ProjectRequest request = createProjectRequestForType(build, "foo", "web",
				"data-jpa");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(fileName).equalsTo(new ClassPathResource("project/"
				+ build + "/compile-only-dependency-" + getAssertFileName(fileName)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void annotationProcessorDependency(String build, String fileName) {
		Dependency annotationProcessor = Dependency.withId("configuration-processor",
				"org.springframework.boot", "spring-boot-configuration-processor");
		annotationProcessor.setScope(Dependency.SCOPE_ANNOTATION_PROCESSOR);
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("core", "web", "data-jpa")
				.addDependencyGroup("configuration-processor", annotationProcessor)
				.build();
		applyMetadata(metadata);
		ProjectRequest request = createProjectRequestForType(build,
				"configuration-processor", "web", "data-jpa");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(fileName)
				.equalsTo(new ClassPathResource(
						"project/" + build + "/annotation-processor-dependency-"
								+ getAssertFileName(fileName)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void bomWithOrdering(String build, String fileName) {
		Dependency foo = Dependency.withId("foo", "org.acme", "foo");
		foo.setBom("foo-bom");
		BillOfMaterials barBom = BillOfMaterials.create("org.acme", "bar-bom", "1.0");
		barBom.setOrder(50);
		BillOfMaterials bizBom = BillOfMaterials.create("org.acme", "biz-bom");
		bizBom.setOrder(40);
		bizBom.getAdditionalBoms().add("bar-bom");
		bizBom.getMappings().add(BillOfMaterials.Mapping.create("1.0.0.RELEASE", "1.0"));
		BillOfMaterials fooBom = BillOfMaterials.create("org.acme", "foo-bom", "1.0");
		fooBom.setOrder(20);
		fooBom.getAdditionalBoms().add("biz-bom");

		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("foo", foo).addBom("foo-bom", fooBom)
				.addBom("bar-bom", barBom).addBom("biz-bom", bizBom).build();
		applyMetadata(metadata);
		ProjectRequest request = createProjectRequestForType(build, "foo");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(fileName).equalsTo(new ClassPathResource(
				"project/" + build + "/bom-ordering-" + getAssertFileName(fileName)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void repositories(String build, String fileName) {
		Dependency foo = Dependency.withId("foo", "org.acme", "foo");
		foo.setRepository("foo-repository");
		Dependency bar = Dependency.withId("bar", "org.acme", "bar");
		bar.setRepository("bar-repository");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", foo, bar)
				.addRepository("foo-repository", "foo-repo", "https://example.com/foo",
						false)
				.addRepository("bar-repository", "bar-repo", "https://example.com/bar",
						true)
				.build();
		applyMetadata(metadata);
		ProjectRequest request = createProjectRequestForType(build, "foo", "bar");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(fileName).equalsTo(new ClassPathResource(
				"project/" + build + "/repositories-" + getAssertFileName(fileName)));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	public void repositoriesMilestone(String build, String fileName) {
		Dependency foo = Dependency.withId("foo", "org.acme", "foo");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", foo).build();
		applyMetadata(metadata);
		ProjectRequest request = createProjectRequestForType(build, "foo");
		request.setBootVersion("2.2.0.M1");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(fileName).equalsTo(new ClassPathResource("project/"
				+ build + "/repositories-milestone-" + getAssertFileName(fileName)));
	}

	public ProjectRequest createProjectRequestForType(String build, String... styles) {
		ProjectRequest request = super.createProjectRequest(styles);
		request.setType(build + "-project");
		return request;
	}

	private String getAssertFileName(String fileName) {
		return fileName + ".gen";
	}

}
