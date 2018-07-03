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

import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.test.generator.ProjectAssert;
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder;
import io.spring.initializr.util.VersionProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.springframework.core.io.ClassPathResource;

/**
 * Project generator tests for supported build systems.
 *
 * @author Stephane Nicoll
 */
@RunWith(Parameterized.class)
public class ProjectGeneratorBuildTests extends AbstractProjectGeneratorTests {

	@Parameterized.Parameters(name = "{0}")
	public static Object[] parameters() {
		Object[] maven = new Object[] { "maven", "pom.xml" };
		Object[] gradle = new Object[] { "gradle", "build.gradle" };
		return new Object[] { maven, gradle };
	}

	private final String build;

	private final String fileName;

	private final String assertFileName;

	public ProjectGeneratorBuildTests(String build, String fileName) {
		this.build = build;
		this.fileName = fileName;
		this.assertFileName = fileName + ".gen";
	}

	@Test
	public void standardJarJava() {
		testStandardJar("java");
	}

	@Test
	public void standardJarGroovy() {
		testStandardJar("groovy");
	}

	@Test
	public void standardJarKotlin() {
		testStandardJar("kotlin");
	}

	private void testStandardJar(String language) {
		ProjectRequest request = createProjectRequest();
		request.setLanguage(language);
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(this.fileName).equalsTo(new ClassPathResource(
				"project/" + language + "/standard/" + this.assertFileName));
	}

	@Test
	public void standardWarJava() {
		testStandardWar("java");
	}

	@Test
	public void standardWarGroovy() {
		testStandardWar("groovy");
	}

	@Test
	public void standardWarKotlin() {
		testStandardWar("kotlin");
	}

	private void testStandardWar(String language) {
		ProjectRequest request = createProjectRequest("web");
		request.setPackaging("war");
		request.setLanguage(language);
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(this.fileName).equalsTo(new ClassPathResource(
				"project/" + language + "/war/" + this.assertFileName));
	}

	@Test
	public void versionOverride() {
		ProjectRequest request = createProjectRequest("web");
		request.getBuildProperties().getVersions()
				.put(new VersionProperty("spring-foo.version"), () -> "0.1.0.RELEASE");
		request.getBuildProperties().getVersions()
				.put(new VersionProperty("spring-bar.version"), () -> "0.2.0.RELEASE");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(this.fileName).equalsTo(new ClassPathResource(
				"project/" + this.build + "/version-override-" + this.assertFileName));
	}

	@Test
	public void bomWithVersionProperty() {
		Dependency foo = Dependency.withId("foo", "org.acme", "foo");
		foo.setBom("the-bom");
		BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom", "1.3.3");
		bom.setVersionProperty("foo.version");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("foo", foo).addBom("the-bom", bom).build();
		applyMetadata(metadata);
		ProjectRequest request = createProjectRequest("foo");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(this.fileName).equalsTo(new ClassPathResource(
				"project/" + this.build + "/bom-property-" + this.assertFileName));
	}

	@Test
	public void compileOnlyDependency() {
		Dependency foo = Dependency.withId("foo", "org.acme", "foo");
		foo.setScope(Dependency.SCOPE_COMPILE_ONLY);
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("core", "web", "data-jpa")
				.addDependencyGroup("foo", foo).build();
		applyMetadata(metadata);
		ProjectRequest request = createProjectRequest("foo", "web", "data-jpa");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(this.fileName).equalsTo(new ClassPathResource("project/"
				+ this.build + "/compile-only-dependency-" + this.assertFileName));
	}

	@Test
	public void annotationProcessorDependency() {
		Dependency annotationProcessor = Dependency.withId("configuration-processor",
				"org.springframework.boot", "spring-boot-configuration-processor");
		annotationProcessor.setScope(Dependency.SCOPE_ANNOTATION_PROCESSOR);
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("core", "web", "data-jpa")
				.addDependencyGroup("configuration-processor", annotationProcessor)
				.build();
		applyMetadata(metadata);
		ProjectRequest request = createProjectRequest("configuration-processor", "web",
				"data-jpa");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(this.fileName)
				.equalsTo(new ClassPathResource("project/" + this.build
						+ "/annotation-processor-dependency-" + this.assertFileName));
	}

	@Test
	public void bomWithOrdering() {
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
		ProjectRequest request = createProjectRequest("foo");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(this.fileName).equalsTo(new ClassPathResource(
				"project/" + this.build + "/bom-ordering-" + this.assertFileName));
	}

	@Test
	public void kotlinJava6() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage("kotlin");
		request.setJavaVersion("1.6");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(this.fileName).equalsTo(new ClassPathResource(
				"project/" + this.build + "/kotlin-java6-" + this.assertFileName));
	}

	@Test
	public void kotlinJava7() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage("kotlin");
		request.setJavaVersion("1.7");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(this.fileName).equalsTo(new ClassPathResource(
				"project/" + this.build + "/kotlin-java7-" + this.assertFileName));
	}

	@Test
	public void kotlinSpringBoot2Legacy() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage("kotlin");
		request.setBootVersion("2.0.0.M5");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/main/kotlin/com/example/demo/DemoApplication.kt")
				.equalsTo(new ClassPathResource(
						"project/kotlin/spring-boot-2.0/DemoApplicationLegacy.kt.gen"));
		project.sourceCodeAssert(this.fileName).equalsTo(new ClassPathResource("project/"
				+ this.build + "/kotlin-springboot2-legacy-" + this.assertFileName));
	}

	@Test
	public void kotlinSpringBoot2() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage("kotlin");
		request.setBootVersion("2.0.0.M6");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert("src/main/kotlin/com/example/demo/DemoApplication.kt")
				.equalsTo(new ClassPathResource(
						"project/kotlin/spring-boot-2.0/DemoApplication.kt.gen"));
		project.sourceCodeAssert(this.fileName).equalsTo(new ClassPathResource(
				"project/" + this.build + "/kotlin-springboot2-" + this.assertFileName));
	}

	@Test
	public void kotlinJava9() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage("kotlin");
		request.setBootVersion("2.0.0.M6");
		request.setJavaVersion("9");
		ProjectAssert project = generateProject(request);
		project.sourceCodeAssert(this.fileName).equalsTo(new ClassPathResource(
				"project/" + this.build + "/kotlin-java9-" + this.assertFileName));
	}

	@Override
	public ProjectRequest createProjectRequest(String... styles) {
		ProjectRequest request = super.createProjectRequest(styles);
		request.setType(this.build + "-project");
		return request;
	}

}
