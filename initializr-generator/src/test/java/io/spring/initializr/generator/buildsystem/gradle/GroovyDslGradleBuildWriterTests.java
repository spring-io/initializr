/*
 * Copyright 2012-2024 the original author or authors.
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

package io.spring.initializr.generator.buildsystem.gradle;

import java.io.StringWriter;
import java.util.Comparator;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.Dependency.Exclusion;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GroovyDslGradleBuildWriter}
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 * @author Stephane Nicoll
 */
class GroovyDslGradleBuildWriterTests extends GradleBuildWriterTests {

	@Test
	void gradleBuildWithCoordinates() {
		GradleBuild build = new GradleBuild();
		build.settings().group("com.example").version("1.0.1-SNAPSHOT");
		assertThat(write(build).lines()).contains("group = 'com.example'", "version = '1.0.1-SNAPSHOT'");
	}

	@Test
	void gradleBuildWithSourceCompatibility() {
		GradleBuild build = new GradleBuild();
		build.settings().sourceCompatibility("17");
		assertThat(write(build)).contains("""
				java {
					toolchain {
						languageVersion = JavaLanguageVersion.of(17)
					}
				}
				""");
	}

	@Test
	void gradleBuildWithBuildscriptDependency() {
		GradleBuild build = new GradleBuild();
		build.repositories().add("maven-central");
		build.buildscript((buildscript) -> buildscript
			.dependency("org.springframework.boot:spring-boot-gradle-plugin:2.1.0.RELEASE"));
		assertThat(write(build)).contains("""
				buildscript {
					repositories {
						mavenCentral()
					}
					dependencies {
						classpath 'org.springframework.boot:spring-boot-gradle-plugin:2.1.0.RELEASE'
					}
				}""");
	}

	@Test
	void gradleBuildWithBuildscriptExtProperty() {
		GradleBuild build = new GradleBuild();
		build.repositories().add("maven-central");
		build.buildscript((buildscript) -> buildscript.ext("kotlinVersion", "'1.2.51'"));
		assertThat(write(build)).contains("""
				buildscript {
					ext {
						kotlinVersion = '1.2.51'
					}""");
	}

	@Test
	void gradleBuildWithPlugin() {
		GradleBuild build = new GradleBuild();
		build.plugins().add("java");
		assertThat(write(build)).contains("""
				plugins {
					id 'java'
				}""");
	}

	@Test
	void gradleBuildWithPluginAndVersion() {
		GradleBuild build = new GradleBuild();
		build.plugins().add("org.springframework.boot", (plugin) -> plugin.setVersion("2.1.0.RELEASE"));
		assertThat(write(build)).contains("""
				plugins {
					id 'org.springframework.boot' version '2.1.0.RELEASE'
				}""");
	}

	@Test
	void gradleBuildWithApplyPlugin() {
		GradleBuild build = new GradleBuild();
		build.plugins().apply("io.spring.dependency-management");
		assertThat(write(build).lines()).contains("apply plugin: 'io.spring.dependency-management'");
	}

	@Test
	void gradleBuildWithMavenCentralRepository() {
		GradleBuild build = new GradleBuild();
		build.repositories().add("maven-central");
		assertThat(write(build)).contains("""
				repositories {
					mavenCentral()
				}""");
	}

	@Test
	void gradleBuildWithRepository() {
		GradleBuild build = new GradleBuild();
		build.repositories().add(MavenRepository.withIdAndUrl("spring-milestones", "https://repo.spring.io/milestone"));
		assertThat(write(build)).contains("""
				repositories {
					maven { url 'https://repo.spring.io/milestone' }
				}""");
	}

	@Test
	void gradleBuildWithSnapshotRepository() {
		GradleBuild build = new GradleBuild();
		build.repositories()
			.add(MavenRepository.withIdAndUrl("spring-snapshots", "https://repo.spring.io/snapshot").onlySnapshots());
		assertThat(write(build)).contains("""
				repositories {
					maven { url 'https://repo.spring.io/snapshot' }
				}""");
	}

	@Test
	void gradleBuildWithPluginRepository() {
		GradleBuild build = new GradleBuild();
		build.pluginRepositories()
			.add(MavenRepository.withIdAndUrl("spring-milestones", "https://repo.spring.io/milestone"));
		assertThat(write(build)).doesNotContain("repositories {");
	}

	@Test
	void gradleBuildWithTaskWithTypesCustomizedWithNestedAssignments() {
		GradleBuild build = new GradleBuild();
		build.tasks()
			.customizeWithType("org.jetbrains.kotlin.gradle.tasks.KotlinCompile", (task) -> task.nested("kotlinOptions",
					(kotlinOptions) -> kotlinOptions.attribute("freeCompilerArgs", "['-Xjsr305=strict']")));
		build.tasks()
			.customizeWithType("org.jetbrains.kotlin.gradle.tasks.KotlinCompile", (task) -> task.nested("kotlinOptions",
					(kotlinOptions) -> kotlinOptions.attribute("jvmTarget", "'1.8'")));
		assertThat(write(build)).containsOnlyOnce("import org.jetbrains.kotlin.gradle.tasks.KotlinCompile").contains("""
				tasks.withType(KotlinCompile) {
					kotlinOptions {
						freeCompilerArgs = ['-Xjsr305=strict']
						jvmTarget = '1.8'
					}
				}""");
	}

	@Test
	void gradleBuildWithTaskWithTypesAndShortTypes() {
		GradleBuild build = new GradleBuild();
		build.tasks().customizeWithType("JavaCompile", (javaCompile) -> javaCompile.attribute("options.fork", "true"));
		assertThat(write(build)).doesNotContain("import JavaCompile").contains("""
				tasks.withType(JavaCompile) {
					options.fork = true
				}""");
	}

	@Test
	void gradleBuildWithTaskCustomizedWithInvocations() {
		GradleBuild build = new GradleBuild();
		build.tasks().customize("asciidoctor", (task) -> {
			task.invoke("inputs.dir", "snippetsDir");
			task.invoke("dependsOn", "test");
		});
		assertThat(write(build)).contains("""
				tasks.named('asciidoctor') {
					inputs.dir snippetsDir
					dependsOn test
				}""");
	}

	@Test
	void gradleBuildWithTaskCustomizedWithInvocationAndNoArgument() {
		GradleBuild build = new GradleBuild();
		build.tasks().customize("test", (task) -> task.invoke("myMethod"));
		assertThat(write(build)).contains("""
				tasks.named('test') {
					myMethod()
				}""");
	}

	@Test
	void gradleBuildWithTaskCustomizedWithAssignments() {
		GradleBuild build = new GradleBuild();
		build.tasks().customize("compileKotlin", (task) -> {
			task.attribute("kotlinOptions.freeCompilerArgs", "['-Xjsr305=strict']");
			task.attribute("kotlinOptions.jvmTarget", "'1.8'");
		});
		assertThat(write(build)).contains("""
				tasks.named('compileKotlin') {
					kotlinOptions.freeCompilerArgs = ['-Xjsr305=strict']
					kotlinOptions.jvmTarget = '1.8'
				}""");
	}

	@Test
	void gradleBuildWithTaskCustomizedWithNestedCustomization() {
		GradleBuild build = new GradleBuild();
		build.tasks()
			.customize("compileKotlin", (compileKotlin) -> compileKotlin.nested("kotlinOptions", (kotlinOptions) -> {
				kotlinOptions.attribute("freeCompilerArgs", "['-Xjsr305=strict']");
				kotlinOptions.attribute("jvmTarget", "'1.8'");
			}));
		assertThat(write(build)).contains("""
				tasks.named('compileKotlin') {
					kotlinOptions {
						freeCompilerArgs = ['-Xjsr305=strict']
						jvmTarget = '1.8'
					}
				}""");
	}

	@Test
	void gradleBuildWithExt() {
		GradleBuild build = new GradleBuild();
		build.properties().property("java.version", "'1.8'").property("alpha", "file(\"build/example\")");
		assertThat(write(build)).contains("""
				ext {
					set('alpha', file("build/example"))
					set('java.version', '1.8')
				}""");
	}

	@Test
	void gradleBuildWithVersionProperties() {
		GradleBuild build = new GradleBuild();
		build.properties()
			.version(VersionProperty.of("version.property", false), "1.2.3")
			.version(VersionProperty.of("internal.property", true), "4.5.6")
			.version("external.property", "7.8.9");
		assertThat(write(build)).contains("""
				ext {
					set('external.property', "7.8.9")
					set('internalProperty', "4.5.6")
					set('version.property', "1.2.3")
				}""");
	}

	@Test
	void gradleBuildWithVersionedDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies()
			.add("kotlin-stdlib",
					Dependency.withCoordinates("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
						.version(VersionReference.ofProperty("kotlin.version"))
						.scope(DependencyScope.COMPILE));
		assertThat(write(build)).contains("""
				dependencies {
					implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}"
				}""");
	}

	@Test
	void gradleBuildWithExternalVersionedDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies()
			.add("acme",
					Dependency.withCoordinates("com.example", "acme")
						.version(VersionReference.ofProperty(VersionProperty.of("acme.version", false)))
						.scope(DependencyScope.COMPILE));
		assertThat(write(build)).contains("""
				dependencies {
					implementation "com.example:acme:${property('acme.version')}"
				}""");
	}

	@Test
	void gradleBuildWithExtAndVersionProperties() {
		GradleBuild build = new GradleBuild();
		build.properties()
			.version(VersionProperty.of("test-version"), "1.0")
			.version("alpha-version", "0.1")
			.property("myProperty", "'42'");
		assertThat(write(build)).contains("""
				ext {
					set('myProperty', '42')
					set('alpha-version', "0.1")
					set('testVersion', "1.0")
				}""");
	}

	@Test
	void gradleBuildWithConfiguration() {
		GradleBuild build = new GradleBuild();
		build.configurations().add("developmentOnly");
		assertThat(write(build)).contains("""
				configurations {
					developmentOnly
				}""");
	}

	@Test
	void gradleBuildWithConfigurationCustomization() {
		GradleBuild build = new GradleBuild();
		build.configurations().customize("developmentOnly", (configuration) -> configuration.extendsFrom("compile"));
		build.configurations()
			.customize("developmentOnly", (configuration) -> configuration.extendsFrom("testCompile"));
		assertThat(write(build)).contains("""
				configurations {
					developmentOnly {
						extendsFrom compile, testCompile
					}
				}""");
	}

	@Test
	void gradleBuildWithConfigurationCustomizations() {
		GradleBuild build = new GradleBuild();
		build.configurations().customize("developmentOnly", (configuration) -> configuration.extendsFrom("compile"));
		build.configurations().customize("testOnly", (configuration) -> configuration.extendsFrom("testCompile"));
		assertThat(write(build)).contains("""
				configurations {
					developmentOnly {
						extendsFrom compile
					}
					testOnly {
						extendsFrom testCompile
					}
				}""");
	}

	@Test
	void gradleBuildWithAnnotationProcessorDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies()
			.add("annotation-processor", "org.springframework.boot", "spring-boot-configuration-processor",
					DependencyScope.ANNOTATION_PROCESSOR);
		assertThat(write(build)).contains("""
				dependencies {
					annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
				}""");
	}

	@Test
	void gradleBuildWithCompileDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("root", "org.springframework.boot", "spring-boot-starter", DependencyScope.COMPILE);
		assertThat(write(build)).contains("""
				dependencies {
					implementation 'org.springframework.boot:spring-boot-starter'
				}""");
	}

	@Test
	void gradleBuildWithNoScopeDependencyDefaultsToCompile() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("root", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter"));
		assertThat(write(build)).contains("""
				dependencies {
					implementation 'org.springframework.boot:spring-boot-starter'
				}""");
	}

	@Test
	void gradleBuildWithRuntimeDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies()
			.add("driver",
					Dependency.withCoordinates("com.example", "jdbc-driver")
						.version(VersionReference.ofValue("1.0.0"))
						.scope(DependencyScope.RUNTIME));
		assertThat(write(build)).contains("""
				dependencies {
					runtimeOnly 'com.example:jdbc-driver:1.0.0'
				}""");
	}

	@Test
	void gradleBuildWithProvidedRuntimeDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies()
			.add("tomcat", "org.springframework.boot", "spring-boot-starter-tomcat", DependencyScope.PROVIDED_RUNTIME);
		assertThat(write(build)).contains("""
				dependencies {
					providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
				}""");
	}

	@Test
	void gradleBuildWithTestCompileDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies()
			.add("test", "org.springframework.boot", "spring-boot-starter-test", DependencyScope.TEST_COMPILE);
		assertThat(write(build)).contains("""
				dependencies {
					testImplementation 'org.springframework.boot:spring-boot-starter-test'
				}""");
	}

	@Test
	void gradleBuildWithCompileOnlyDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies()
			.add("test", "org.springframework.boot", "spring-boot-starter-foobar", DependencyScope.COMPILE_ONLY);
		assertThat(write(build)).contains("""
				dependencies {
					compileOnly 'org.springframework.boot:spring-boot-starter-foobar'
				}""");
	}

	@Test
	void gradleBuildWithTestRuntimeDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies()
			.add("embed-mongo", "de.flapdoodle.embed", "de.flapdoodle.embed.mongo", DependencyScope.TEST_RUNTIME);
		assertThat(write(build)).contains("""
				dependencies {
					testRuntimeOnly 'de.flapdoodle.embed:de.flapdoodle.embed.mongo'
				}""");
	}

	@Test
	void gradleBuildWithClassifierDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("root", Dependency.withCoordinates("com.example", "acme").classifier("test-jar"));
		assertThat(write(build)).contains("""
				dependencies {
					implementation 'com.example:acme:test-jar'
				}""");
	}

	@Test
	void gradleBuildWithExclusions() {
		GradleBuild build = new GradleBuild();
		build.dependencies()
			.add("test",
					Dependency.withCoordinates("com.example", "test")
						.scope(DependencyScope.COMPILE)
						.exclusions(new Exclusion("com.example.legacy", "legacy-one"),
								new Exclusion("com.example.another", "legacy-two")));
		assertThat(write(build)).contains("""
				dependencies {
					implementation('com.example:test') {
						exclude group: 'com.example.legacy', module: 'legacy-one'
						exclude group: 'com.example.another', module: 'legacy-two'
					}
				}""");
	}

	@Test
	void gradleBuildWithCustomDependencyConfiguration() {
		GradleBuild build = new GradleBuild();
		build.dependencies()
			.add("test",
					GradleDependency.withCoordinates("org.springframework.boot", "spring-boot-starter-foobar")
						.scope(DependencyScope.RUNTIME)
						.configuration("myRuntime"));
		assertThat(write(build)).contains("""
				dependencies {
					myRuntime 'org.springframework.boot:spring-boot-starter-foobar'
				}""");
	}

	@Test
	void gradleBuildWithNonNullArtifactTypeDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies()
			.add("root",
					Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter")
						.scope(DependencyScope.COMPILE)
						.type("tar.gz"));
		assertThat(write(build)).contains("""
				dependencies {
					implementation 'org.springframework.boot:spring-boot-starter@tar.gz'
				}""");
	}

	@Test
	void gradleBuildWithNonNullArtifactTypeAndClassifierDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies()
			.add("root",
					Dependency.withCoordinates("com.example", "acme")
						.scope(DependencyScope.COMPILE)
						.type("tar.gz")
						.classifier("test-jar"));
		assertThat(write(build)).contains("""
				dependencies {
					implementation 'com.example:acme:test-jar@tar.gz'
				}""");
	}

	@Test
	void gradleBuildWithOrderedDependencies() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("beta", Dependency.withCoordinates("com.example", "beta"));
		build.dependencies().add("alpha", Dependency.withCoordinates("com.example", "alpha"));
		build.dependencies()
			.add("web", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter-web"));
		build.dependencies().add("root", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter"));
		assertThat(write(build)).contains("""
				dependencies {
					implementation 'org.springframework.boot:spring-boot-starter'
					implementation 'org.springframework.boot:spring-boot-starter-web'
					implementation 'com.example:alpha'
					implementation 'com.example:beta'
				}""");
	}

	@Test
	void gradleBuildWithOrderedDependenciesAndCustomComparator() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("beta", Dependency.withCoordinates("com.example", "beta"));
		build.dependencies().add("alpha", Dependency.withCoordinates("com.example", "alpha"));
		build.dependencies()
			.add("web", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter-web"));
		build.dependencies().add("root", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter"));
		GroovyDslGradleBuildWriter writer = new GroovyDslGradleBuildWriter() {
			@Override
			protected Comparator<Dependency> getDependencyComparator() {
				return Comparator.comparing(Dependency::getArtifactId);
			}
		};
		assertThat(write(writer, build)).contains("""
				dependencies {
					implementation 'com.example:alpha'
					implementation 'com.example:beta'
					implementation 'org.springframework.boot:spring-boot-starter'
					implementation 'org.springframework.boot:spring-boot-starter-web'
				}""");
	}

	@Test
	void gradleBuildWithBom() {
		GradleBuild build = new GradleBuild();
		build.boms()
			.add("test", BillOfMaterials.withCoordinates("com.example", "my-project-dependencies")
				.version(VersionReference.ofValue("1.0.0.RELEASE")));
		assertThat(write(build)).contains("""
				dependencyManagement {
					imports {
						mavenBom 'com.example:my-project-dependencies:1.0.0.RELEASE'
					}
				}""");
	}

	@Test
	void gradleBuildWithOrderedBoms() {
		GradleBuild build = new GradleBuild();
		build.boms()
			.add("bom1",
					BillOfMaterials.withCoordinates("com.example", "my-project-dependencies")
						.version(VersionReference.ofValue("1.0.0.RELEASE"))
						.order(5));
		build.boms()
			.add("bom2",
					BillOfMaterials.withCoordinates("com.example", "root-dependencies")
						.version(VersionReference.ofProperty("root.version"))
						.order(2));
		assertThat(write(build)).contains("""
				dependencyManagement {
					imports {
						mavenBom 'com.example:my-project-dependencies:1.0.0.RELEASE'
						mavenBom "com.example:root-dependencies:${rootVersion}"
					}
				}""");
	}

	@Test
	void gradleBuildWithCustomVersion() {
		GradleBuild build = new GradleBuild();
		build.settings().version("1.2.4.RELEASE");
		assertThat(write(build)).contains("version = '1.2.4.RELEASE'");
	}

	@Test
	void shouldCustomizeExtensions() {
		GradleBuild build = new GradleBuild();
		build.extensions().customize("kotlin", (kotlin) -> kotlin.nested("compilerOptions", (compilerOptions) -> {
			compilerOptions.attributeWithType("jvmTarget", "JvmTarget.JVM_21",
					"org.jetbrains.kotlin.gradle.dsl.JvmTarget");
			compilerOptions.invoke("freeCompilerArgs.addAll", "'-Xjsr305=strict'", "'-Xexport-kdoc'");
		}));
		String written = write(build);
		assertThat(written).contains("import org.jetbrains.kotlin.gradle.dsl.JvmTarget");
		assertThat(written).contains("""
				kotlin {
					compilerOptions {
						freeCompilerArgs.addAll '-Xjsr305=strict', '-Xexport-kdoc'
						jvmTarget = JvmTarget.JVM_21
					}
				}""");
	}

	protected String write(GradleBuild build) {
		return write(new GroovyDslGradleBuildWriter(), build);
	}

	private String write(GroovyDslGradleBuildWriter writer, GradleBuild build) {
		StringWriter out = new StringWriter();
		writer.writeTo(new IndentingWriter(out, new SimpleIndentStrategy("\t")), build);
		return out.toString().replace("\r\n", "\n");
	}

}
