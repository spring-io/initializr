/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * Various project based assertions.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class ProjectAssert {

	private static final DEFAULT_APPLICATION_NAME = generateDefaultApplicationName()

	final File dir
	Boolean mavenProject

	/**
	 * Create a new instance with the directory holding the generated project.
	 * @param dir
	 */
	ProjectAssert(File dir) {
		this.dir = dir
	}

	/**
	 * Validate that the project contains a base directory with the specified name.
	 * <p>When extracting such archive, a directory with the specified {@code name}
	 * will be created with the content of the project instead of extracting it in
	 * the directory itself.
	 * @param name the expected name of the base directory
	 * @return an updated project assert on that base directory
	 */
	ProjectAssert hasBaseDir(String name) {
		File projectDir = file(name)
		assertTrue "No directory $name found in $dir.absolutePath", projectDir.exists()
		assertTrue "$name is not a directory", projectDir.isDirectory()
		new ProjectAssert(projectDir) // Replacing the root dir so that other assertions match the root
	}

	/**
	 * Return a {@link PomAssert} for this project.
	 */
	PomAssert pomAssert() {
		new PomAssert(file('pom.xml').text)
	}

	/**
	 * Return a {@link GradleBuildAssert} for this project.
	 */
	GradleBuildAssert gradleBuildAssert() {
		new GradleBuildAssert(file('build.gradle').text)
	}

	/**
	 * Return a {@link SourceCodeAssert} for the specified source code.
	 */
	SourceCodeAssert sourceCodeAssert(String sourceCodePath) {
		hasFile(sourceCodePath)
		new SourceCodeAssert(sourceCodePath, file(sourceCodePath).text)
	}

	ProjectAssert isMavenProject() {
		hasFile('pom.xml').hasNoFile('build.gradle')
		hasFile('mvnw', 'mvnw.cmd',
				'.mvn/wrapper/maven-wrapper.properties',
				'.mvn/wrapper/maven-wrapper.jar')
		mavenProject = true
		this
	}

	ProjectAssert isGradleProject() {
		hasFile('build.gradle').hasNoFile('pom.xml')
		hasFile('gradlew', 'gradlew.bat',
				'gradle/wrapper/gradle-wrapper.properties',
				'gradle/wrapper/gradle-wrapper.jar')
		mavenProject = false
		this
	}

	ProjectAssert isJavaProject(String expectedApplicationName) {
		hasFile("src/main/java/com/example/${expectedApplicationName}.java",
				"src/test/java/com/example/${expectedApplicationName}Tests.java",
				'src/main/resources/application.properties')
	}

	ProjectAssert isJavaProject() {
		isJavaProject(DEFAULT_APPLICATION_NAME)
	}

	ProjectAssert isGroovyProject(String expectedApplicationName) {
		String codeLocation = (mavenProject ? 'java' : 'groovy')
		hasFile("src/main/$codeLocation/com/example/${expectedApplicationName}.groovy",
				"src/test/$codeLocation/com/example/${expectedApplicationName}Tests.groovy",
				'src/main/resources/application.properties')
	}

	ProjectAssert isGroovyProject() {
		isGroovyProject(DEFAULT_APPLICATION_NAME)
	}

	ProjectAssert isJavaWarProject(String expectedApplicationName) {
		isJavaProject(expectedApplicationName).hasStaticAndTemplatesResources(true)
				.hasFile('src/main/java/com/example/ServletInitializer.java')
	}

	ProjectAssert isJavaWarProject() {
		isJavaWarProject(DEFAULT_APPLICATION_NAME)
	}

	ProjectAssert hasStaticAndTemplatesResources(boolean web) {
		assertFile('src/main/resources/templates', web)
		assertFile('src/main/resources/static', web)
	}

	ProjectAssert hasFile(String... localPaths) {
		for (String localPath : localPaths) {
			assertFile(localPath, true)
		}
		this
	}

	ProjectAssert hasNoFile(String... localPaths) {
		for (String localPath : localPaths) {
			assertFile(localPath, false)
		}
		this
	}

	ProjectAssert assertFile(String localPath, boolean exist) {
		def candidate = file(localPath)
		assertEquals "Invalid presence ('$exist') for $localPath", exist, candidate.exists()
		this
	}

	private File file(String localPath) {
		new File(dir, localPath)
	}

	private static generateDefaultApplicationName() {
		'DemoApplication'
	}

}
