/*
 * Copyright 2012-2014 the original author or authors.
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

package io.spring.initializr.support

import static org.junit.Assert.assertEquals

/**
 * Various project based assertions.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class ProjectAssert {

	final File dir

	/**
	 * Create a new instance with the directory holding the generated project.
	 * @param dir
	 */
	ProjectAssert(File dir) {
		this.dir = dir
	}

	/**
	 * Return a {@link PomAssert} for this project.
	 */
	PomAssert pomAssert() {
		new PomAssert(file('pom.xml').text)
	}

	ProjectAssert isMavenProject() {
		hasFile('pom.xml').hasNoFile('build.gradle')
	}

	ProjectAssert isGradleProject() {
		hasFile('build.gradle').hasNoFile('pom.xml')
	}

	ProjectAssert isJavaProject() {
		hasFile('src/main/java/demo/Application.java',
				'src/test/java/demo/ApplicationTests.java',
				'src/main/resources/application.properties')
	}

	ProjectAssert isJavaWarProject() {
		isJavaProject().hasStaticAndTemplatesResources(true)
				.hasFile('src/main/java/demo/ServletInitializer.java')
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
		assertEquals 'Invalid presence (' + exist + ') for ' + localPath, exist, candidate.exists()
		this
	}

	private File file(String localPath) {
		new File(dir, localPath)
	}

}
