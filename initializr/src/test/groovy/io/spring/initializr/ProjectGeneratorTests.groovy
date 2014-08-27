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

package io.spring.initializr

import io.spring.initializr.support.GradleBuildAssert
import io.spring.initializr.support.InitializrMetadataBuilder
import io.spring.initializr.support.PomAssert
import io.spring.initializr.support.ProjectAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.mockito.Mockito.*

/**
 * @author Stephane Nicoll
 */
class ProjectGeneratorTests {

	@Rule
	public final TemporaryFolder folder = new TemporaryFolder()

	private final ProjectGenerator projectGenerator = new ProjectGenerator()

	@Before
	void setup() {
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('test', 'web', 'security', 'data-jpa', 'aop', 'batch', 'integration').validateAndGet()
		projectGenerator.metadata = metadata
		projectGenerator.tmpdir = folder.newFolder().getAbsolutePath()
	}

	@Test
	void defaultMavenPom() {
		ProjectGenerationListener listener = mock(ProjectGenerationListener)
		projectGenerator.listeners << listener

		ProjectRequest request = createProjectRequest('web')
		generateMavenPom(request).hasStartClass('demo.Application')
				.hasNoRepository().hasSpringBootStarterDependency('web')
		verify(listener, times(1)).onGeneratedProject(request)
	}

	@Test
	void defaultGradleBuild() {
		ProjectGenerationListener listener = mock(ProjectGenerationListener)
		projectGenerator.listeners << listener

		ProjectRequest request = createProjectRequest('web')
		generateGradleBuild(request)
		verify(listener, times(1)).onGeneratedProject(request)
	}

	@Test
	void defaultProject() {
		ProjectGenerationListener listener = mock(ProjectGenerationListener)
		projectGenerator.listeners << listener

		ProjectRequest request = createProjectRequest('web')
		generateProject(request).isJavaProject().isMavenProject().pomAssert()
				.hasStartClass('demo.Application').hasNoRepository().hasSpringBootStarterDependency('web')
		verify(listener, times(1)).onGeneratedProject(request)
	}

	@Test
	void mavenPomWithBootSnapshot() {
		ProjectRequest request = createProjectRequest('web')
		request.bootVersion = '1.0.1.BUILD-SNAPSHOT'
		generateMavenPom(request).hasStartClass('demo.Application')
				.hasSnapshotRepository().hasSpringBootStarterDependency('web')
	}

	@Test
	void mavenPomWithWebFacet() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.id = 'thymeleaf'
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'thymeleaf'
		dependency.facets << 'web'
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('test', dependency).validateAndGet()
		projectGenerator.metadata = metadata

		ProjectRequest request = createProjectRequest('thymeleaf')
		generateMavenPom(request).hasStartClass('demo.Application')
				.hasDependency('org.foo', 'thymeleaf')
				.hasDependenciesCount(2)

	}

	@Test
	void mavenWarPomWithWebFacet() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.id = 'thymeleaf'
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'thymeleaf'
		dependency.facets << 'web'
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('test', dependency).validateAndGet()
		projectGenerator.metadata = metadata

		ProjectRequest request = createProjectRequest('thymeleaf')
		request.packaging = 'war'
		generateMavenPom(request).hasStartClass('demo.Application')
				.hasSpringBootStarterDependency('tomcat')
				.hasDependency('org.foo', 'thymeleaf') // This is tagged as web facet so it brings the web one
				.hasSpringBootStarterDependency('test')
				.hasDependenciesCount(3)

	}

	@Test
	void mavenWarPomWithoutWebFacet() {
		ProjectRequest request = createProjectRequest('data-jpa')
		request.packaging = 'war'
		generateMavenPom(request).hasStartClass('demo.Application')
				.hasSpringBootStarterDependency('tomcat')
				.hasSpringBootStarterDependency('data-jpa')
				.hasSpringBootStarterDependency('web') // Added by war packaging
				.hasSpringBootStarterDependency('test')
				.hasDependenciesCount(4)

	}

	PomAssert generateMavenPom(ProjectRequest request) {
		String content = new String(projectGenerator.generateMavenPom(request))
		new PomAssert(content).validateProjectRequest(request)
	}

	GradleBuildAssert generateGradleBuild(ProjectRequest request) {
		String content = new String(projectGenerator.generateGradleBuild(request))
		new GradleBuildAssert(content).validateProjectRequest(request)
	}

	ProjectAssert generateProject(ProjectRequest request) {
		File dir = projectGenerator.generateProjectStructure(request)
		new ProjectAssert(dir)
	}

	ProjectRequest createProjectRequest(String... styles) {
		ProjectRequest request = new ProjectRequest()
		projectGenerator.metadata.initializeProjectRequest(request)
		request.style.addAll Arrays.asList(styles)
		request
	}

}
