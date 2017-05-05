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

package io.spring.initializr.test.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Various project based assertions.
 *
 * @author Stephane Nicoll
 */
public class ProjectAssert {

	public static final String DEFAULT_PACKAGE_NAME = "com.example.demo";

	public static final String DEFAULT_APPLICATION_NAME = "DemoApplication";

	private final File dir;
	private Boolean mavenProject;

	public File getDir() {
		return dir;
	}

	public Boolean getMavenProject() {
		return mavenProject;
	}

	/**
	 * Create a new instance with the directory holding the generated project.
	 * @param dir the directory of the project
	 */
	public ProjectAssert(File dir) {
		this.dir = dir;
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
		File projectDir = file(name);
		assertTrue("No directory " + name + " found in " + dir.getAbsolutePath(),
				projectDir.exists());
		assertTrue(name + " is not a directory", projectDir.isDirectory());
		// Replacing the root dir so that other assertions match the root
		return new ProjectAssert(projectDir);
	}

	/**
	 * Return a {@link PomAssert} for this project.
	 */
	public PomAssert pomAssert() {
		try {
			return new PomAssert(StreamUtils.copyToString(
					new FileInputStream(file("pom.xml")), Charset.forName("UTF-8")));
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Cannot resolve pom.xml", e);
		}
	}

	/**
	 * Return a {@link GradleBuildAssert} for this project.
	 */
	public GradleBuildAssert gradleBuildAssert() {
		try {
			return new GradleBuildAssert(StreamUtils.copyToString(
					new FileInputStream(file("build.gradle")), Charset.forName("UTF-8")));
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Cannot resolve build.gradle", e);
		}
	}

	/**
	 * Return a {@link SourceCodeAssert} for the specified source code.
	 */
	public SourceCodeAssert sourceCodeAssert(String sourceCodePath) {
		hasFile(sourceCodePath);
		try {
			return new SourceCodeAssert(sourceCodePath, StreamUtils.copyToString(
					new FileInputStream(file(sourceCodePath)), Charset.forName("UTF-8")));
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Cannot resolve path: " + sourceCodePath,
					e);
		}
	}

	public ProjectAssert isMavenProject() {
		hasFile("pom.xml").hasNoFile("build.gradle");
		hasFile("mvnw", "mvnw.cmd", ".mvn/wrapper/maven-wrapper.properties",
				".mvn/wrapper/maven-wrapper.jar");
		mavenProject = true;
		return this;
	}

	public ProjectAssert isGradleProject(String version) {
		hasFile("build.gradle").hasNoFile("pom.xml");
		hasFile("gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.properties",
				"gradle/wrapper/gradle-wrapper.jar");
		mavenProject = false;
		if (StringUtils.hasText(version)) {
			Properties properties = properties(
					"gradle/wrapper/gradle-wrapper.properties");
			String distributionUrl = properties.getProperty("distributionUrl");
			assertTrue("Wrong gradle version for project " + distributionUrl,
					distributionUrl.contains(version));
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
		for (String localPath : localPaths) {
			assertFile(localPath, true);
		}
		return this;
	}

	public ProjectAssert hasExecutableFile(String... localPaths) {
		for (String localPath : localPaths) {
			assertFile(localPath, true);
		}
		return this;
	}

	public ProjectAssert hasNoFile(String... localPaths) {
		for (String localPath : localPaths) {
			assertFile(localPath, false);
		}
		return this;
	}

	public ProjectAssert assertFile(String localPath, boolean exist) {
		File candidate = file(localPath);
		assertEquals("Invalid presence (\"" + exist + "\") for " + localPath, exist,
				candidate.exists());
		return this;
	}

	public ProjectAssert assertExecutableFile(String localPath, boolean executable) {
		File candidate = file(localPath);
		assertEquals("Invalid  (\"" + executable + "\") for " + localPath, executable,
				candidate.exists() && candidate.canExecute());
		return this;
	}

	private File file(String localPath) {
		return new File(dir, localPath);
	}

	private Properties properties(String localPath) {
		File f = file(localPath);
		try {
			return PropertiesLoaderUtils.loadProperties(new FileSystemResource(f));
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot load Properties", e);
		}
	}

}
