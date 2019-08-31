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

package io.spring.initializr.generator.test.project;

import java.nio.file.Path;

import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.test.buildsystem.gradle.GroovyDslGradleBuildAssert;
import io.spring.initializr.generator.test.buildsystem.maven.MavenBuildAssert;

/**
 * Base class for module assertions.
 *
 * @param <SELF> the type of the concrete assert implementations
 * @author Stephane Nicoll
 */
public abstract class AbstractModuleAssert<SELF extends AbstractModuleAssert<SELF>>
		extends AbstractProjectAssert<SELF> {

	protected AbstractModuleAssert(Path projectDirectory, Class<?> selfType) {
		super(projectDirectory, selfType);
	}

	/**
	 * Return a {@link JvmModuleAssert JVM module} assertion for the specified
	 * {@link Language}, to allow chaining of jvm module-specific assertions from this
	 * call.
	 * <p>
	 * Does not validate that the module has source code for the specified language.
	 * @param language the language of the module
	 * @return a {@link JvmModuleAssert} for the specified language
	 */
	public JvmModuleAssert asJvmModule(Language language) {
		return new JvmModuleAssert(this.actual, language);
	}

	/**
	 * Assert the module defines a {@code pom.xml}.
	 * @return {@code this} assertion object
	 */
	public SELF hasMavenBuild() {
		filePaths().contains("pom.xml");
		return this.myself;
	}

	/**
	 * Assert the module defines the Maven wrapper.
	 * @return {@code this} assertion object
	 */
	public SELF hasMavenWrapper() {
		filePaths().contains("mvnw", "mvnw.cmd", ".mvn/wrapper/maven-wrapper.properties",
				".mvn/wrapper/maven-wrapper.jar");
		return this.myself;
	}

	/**
	 * Assert this module has a {@code pom.xml} and return an {@link MavenBuildAssert
	 * assert} for the {@code pom.xml} file of this module, to allow chaining of
	 * maven-specific assertions from this call.
	 * @return a {@link MavenBuildAssert}
	 */
	public MavenBuildAssert mavenBuild() {
		hasMavenBuild();
		return new MavenBuildAssert(this.actual.resolve("pom.xml"));
	}

	/**
	 * Assert the module defines a {@code build.gradle}.
	 * @return {@code this} assertion object
	 */
	public SELF hasGroovyDslGradleBuild() {
		filePaths().contains("build.gradle");
		return this.myself;
	}

	/**
	 * Assert the module defines the Gradle wrapper.
	 * @return {@code this} assertion object
	 */
	public SELF hasGradleWrapper() {
		filePaths().contains("gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.properties",
				"gradle/wrapper/gradle-wrapper.jar");
		return this.myself;
	}

	/**
	 * Assert this module has a {@code build.gradle} and return an
	 * {@link GroovyDslGradleBuildAssert assert} for the {@code build.gradle} file of this
	 * module, to allow chaining of gradle-specific assertions from this call.
	 * @return a {@link GroovyDslGradleBuildAssert}
	 */
	public GroovyDslGradleBuildAssert groovyDslGradleBuild() {
		hasGroovyDslGradleBuild();
		return new GroovyDslGradleBuildAssert(this.actual.resolve("build.gradle"));
	}

}
