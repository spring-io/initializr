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

package io.spring.initializr.generator.spring.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import io.spring.initializr.generator.spring.test.build.GradleBuildAssert;
import io.spring.initializr.generator.spring.test.build.GradleSettingsAssert;
import io.spring.initializr.generator.spring.test.build.PomAssert;
import io.spring.initializr.generator.spring.test.code.SourceCodeAssert;
import io.spring.initializr.generator.test.io.TextTestUtils;
import io.spring.initializr.generator.test.project.ProjectStructure;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Various project based assertions.
 *
 * @author Stephane Nicoll
 */
public class ProjectAssert {

	public static final String DEFAULT_PACKAGE_NAME = "com.example.demo";

	public static final String DEFAULT_APPLICATION_NAME = "DemoApplication";

	private final ProjectStructure projectStructure;

	/**
	 * Create a new instance with the directory holding the generated project.
	 * @param dir the directory of the project
	 */
	public ProjectAssert(Path dir) {
		this.projectStructure = new ProjectStructure(dir);
	}

	public ProjectStructure getProjectStructure() {
		return this.projectStructure;
	}

	/**
	 * Validate that the project contains a base directory with the specified name.
	 * <p>
	 * When extracting such archive, a directory with the specified {@code name} will be
	 * created with the content of the project instead of extracting it in the directory
	 * itself.
	 * @param name the expected name of the base directory
	 * @return an updated project assert on that base directory
	 */
	public ProjectAssert hasBaseDir(String name) {
		Path projectDir = this.projectStructure.resolve(name);
		assertThat(projectDir).describedAs("No directory %s found in %s", name,
				this.projectStructure.getProjectDirectory()).exists();
		assertThat(projectDir).isDirectory();
		// Replacing the root dir so that other assertions match the root
		return new ProjectAssert(projectDir);
	}

	/**
	 * Return a {@link PomAssert} for this project.
	 * @return a POM assert
	 */
	public PomAssert pomAssert() {
		return new PomAssert(
				TextTestUtils.readContent(this.projectStructure.resolve("pom.xml")));
	}

	/**
	 * Return a {@link GradleBuildAssert} for this project.
	 * @return a gradle assert
	 */
	public GradleBuildAssert gradleBuildAssert() {
		return new GradleBuildAssert(
				TextTestUtils.readContent(this.projectStructure.resolve("build.gradle")));
	}

	/**
	 * Return a {@link GradleSettingsAssert} for this project.
	 * @return A gradle settings assert
	 */
	public GradleSettingsAssert gradleSettingsAssert() {
		return new GradleSettingsAssert(TextTestUtils
				.readContent(this.projectStructure.resolve("settings.gradle")));
	}

	/**
	 * Return a {@link SourceCodeAssert} for the specified source code.
	 * @param sourceCodePath the source code path
	 * @return a source assert
	 */
	public SourceCodeAssert sourceCodeAssert(String sourceCodePath) {
		hasFile(sourceCodePath);
		return new SourceCodeAssert(sourceCodePath,
				TextTestUtils.readContent(this.projectStructure.resolve(sourceCodePath)));
	}

	public ProjectAssert isMavenProject() {
		hasFile("pom.xml").hasNoFile("build.gradle");
		hasFile("mvnw", "mvnw.cmd", ".mvn/wrapper/maven-wrapper.properties",
				".mvn/wrapper/maven-wrapper.jar");
		return this;
	}

	public ProjectAssert isGradleProject(String version) {
		hasFile("build.gradle").hasNoFile("pom.xml");
		hasFile("gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.properties",
				"gradle/wrapper/gradle-wrapper.jar");
		if (StringUtils.hasText(version)) {
			Properties properties = properties(
					"gradle/wrapper/gradle-wrapper.properties");
			String distributionUrl = properties.getProperty("distributionUrl");
			assertThat(distributionUrl).contains(version);
		}
		return this;
	}

	public ProjectAssert isGradleProject() {
		return isGradleProject(null);
	}

	public ProjectAssert isJavaProject(String expectedPackageName,
			String expectedApplicationName) {
		return isGenericProject(expectedPackageName, expectedApplicationName, "java",
				"java");
	}

	public ProjectAssert isJavaProject() {
		return isJavaProject(DEFAULT_PACKAGE_NAME, DEFAULT_APPLICATION_NAME);
	}

	public ProjectAssert isGroovyProject(String expectedPackageName,
			String expectedApplicationName) {
		return isGenericProject(expectedPackageName, expectedApplicationName, "groovy",
				"groovy");
	}

	public ProjectAssert isKotlinProject(String expectedPackageName,
			String expectedApplicationName) {
		return isGenericProject(expectedPackageName, expectedApplicationName, "kotlin",
				"kt");
	}

	public ProjectAssert isGroovyProject() {
		return isGroovyProject(DEFAULT_PACKAGE_NAME, DEFAULT_APPLICATION_NAME);
	}

	public ProjectAssert isKotlinProject() {
		return isKotlinProject(DEFAULT_PACKAGE_NAME, DEFAULT_APPLICATION_NAME);
	}

	public ProjectAssert isGenericProject(String expectedPackageName,
			String expectedApplicationName, String codeLocation, String extension) {
		String packageName = expectedPackageName.replace(".", "/");
		return hasFile(
				"src/main/" + codeLocation + "/" + packageName + "/"
						+ expectedApplicationName + "." + extension,
				"src/test/" + codeLocation + "/" + packageName + "/"
						+ expectedApplicationName + "Tests." + extension,
				"src/main/resources/application.properties");
	}

	public ProjectAssert isJavaWarProject(String expectedApplicationName) {
		return isGenericWarProject(DEFAULT_PACKAGE_NAME, expectedApplicationName, "java",
				"java");
	}

	public ProjectAssert isJavaWarProject() {
		return isJavaWarProject(DEFAULT_APPLICATION_NAME);
	}

	public ProjectAssert isGenericWarProject(String expectedPackageName,
			String expectedApplicationName, String codeLocation, String extension) {
		String packageName = expectedPackageName.replace(".", "/");
		return isGenericProject(expectedPackageName, expectedApplicationName,
				codeLocation, extension).hasStaticAndTemplatesResources(true)
						.hasFile("src/main/" + codeLocation + "/" + packageName
								+ "/ServletInitializer." + extension);
	}

	public ProjectAssert hasStaticAndTemplatesResources(boolean web) {
		assertFile("src/main/resources/templates", web);
		return assertFile("src/main/resources/static", web);
	}

	public ProjectAssert hasFile(String... localPaths) {
		assertThat(this.projectStructure.getRelativePathsOfProjectFiles())
				.contains(localPaths);
		return this;
	}

	public ProjectAssert hasExecutableFile(String... localPaths) {
		for (String localPath : localPaths) {
			assertFile(localPath, true);
		}
		return this;
	}

	public ProjectAssert hasNoFile(String... localPaths) {
		assertThat(this.projectStructure.getRelativePathsOfProjectFiles())
				.doesNotContain(localPaths);
		return this;
	}

	public ProjectAssert assertFile(String localPath, boolean exist) {
		Path candidate = this.projectStructure.resolve(localPath);
		assertThat(Files.exists(candidate))
				.describedAs("Invalid presence (%s) exist for %s", exist, localPath)
				.isEqualTo(exist);
		return this;
	}

	private Properties properties(String localPath) {
		Path file = this.projectStructure.resolve(localPath);
		try {
			return PropertiesLoaderUtils.loadProperties(new FileSystemResource(file));
		}
		catch (Exception ex) {
			throw new IllegalStateException("Cannot load Properties", ex);
		}
	}

}
