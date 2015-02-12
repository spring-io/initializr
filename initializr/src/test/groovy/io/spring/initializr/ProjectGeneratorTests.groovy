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

package io.spring.initializr

import io.spring.initializr.test.GradleBuildAssert
import io.spring.initializr.test.InitializrMetadataBuilder
import io.spring.initializr.test.PomAssert
import io.spring.initializr.test.ProjectAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

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
		def metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('test', 'web', 'security', 'data-jpa', 'aop', 'batch', 'integration').validateAndGet()
		projectGenerator.metadata = metadata
		projectGenerator.tmpdir = folder.newFolder().absolutePath
	}

	@Test
	void defaultMavenPom() {
		def listener = mock(ProjectGenerationListener)
		projectGenerator.listeners << listener

		def request = createProjectRequest('web')
		generateMavenPom(request).hasStartClass('demo.DemoApplication')
				.hasNoRepository().hasSpringBootStarterDependency('web')
		verify(listener, times(1)).onGeneratedProject(request)
	}

	@Test
	void defaultGradleBuild() {
		def listener = mock(ProjectGenerationListener)
		projectGenerator.listeners << listener

		def request = createProjectRequest('web')
		generateGradleBuild(request)
		verify(listener, times(1)).onGeneratedProject(request)
	}

	@Test
	void defaultProject() {
		def listener = mock(ProjectGenerationListener)
		projectGenerator.listeners << listener

		def request = createProjectRequest('web')
		generateProject(request).isJavaProject().isMavenProject().pomAssert()
				.hasStartClass('demo.DemoApplication').hasNoRepository().hasSpringBootStarterDependency('web')
		verify(listener, times(1)).onGeneratedProject(request)
	}

	@Test
	void noDependencyAddsRootStarter() {
		def request = createProjectRequest()
		generateProject(request).isJavaProject().isMavenProject().pomAssert()
				.hasSpringBootStarterRootDependency()
	}

	@Test
	void mavenPomWithBootSnapshot() {
		def request = createProjectRequest('web')
		request.bootVersion = '1.0.1.BUILD-SNAPSHOT'
		generateMavenPom(request).hasStartClass('demo.DemoApplication')
				.hasSnapshotRepository().hasSpringBootStarterDependency('web')
	}

	@Test
	void mavenPomWithWebFacet() {
		def dependency = new InitializrMetadata.Dependency()
		dependency.id = 'thymeleaf'
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'thymeleaf'
		dependency.facets << 'web'
		def metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('test', dependency).validateAndGet()
		projectGenerator.metadata = metadata

		def request = createProjectRequest('thymeleaf')
		generateMavenPom(request).hasStartClass('demo.DemoApplication')
				.hasDependency('org.foo', 'thymeleaf')
				.hasDependenciesCount(2)

	}

	@Test
	void mavenWarPomWithWebFacet() {
		def dependency = new InitializrMetadata.Dependency()
		dependency.id = 'thymeleaf'
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'thymeleaf'
		dependency.facets << 'web'
		def metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('test', dependency).validateAndGet()
		projectGenerator.metadata = metadata

		def request = createProjectRequest('thymeleaf')
		request.packaging = 'war'
		generateMavenPom(request).hasStartClass('demo.DemoApplication')
				.hasSpringBootStarterDependency('tomcat')
				.hasDependency('org.foo', 'thymeleaf') // This is tagged as web facet so it brings the web one
				.hasSpringBootStarterDependency('test')
				.hasDependenciesCount(3)

	}

	@Test
	void mavenWarPomWithoutWebFacet() {
		def request = createProjectRequest('data-jpa')
		request.packaging = 'war'
		generateMavenPom(request).hasStartClass('demo.DemoApplication')
				.hasSpringBootStarterDependency('tomcat')
				.hasSpringBootStarterDependency('data-jpa')
				.hasSpringBootStarterDependency('web') // Added by war packaging
				.hasSpringBootStarterDependency('test')
				.hasDependenciesCount(4)

	}

	@Test
	void springBoot11UseEnableAutoConfigurationJava() {
		def request = createProjectRequest('web')
		request.bootVersion = '1.1.9.RELEASE'
		request.name = 'MyDemo'
		request.packageName = 'foo'
		generateProject(request).sourceCodeAssert('src/main/java/foo/MyDemoApplication.java')
				.hasImports(EnableAutoConfiguration.class.name, ComponentScan.class.name, Configuration.class.name)
				.doesNotHaveImports(SpringBootApplication.class.name)
				.contains('@EnableAutoConfiguration', '@Configuration', '@ComponentScan')
				.doesNotContain('@SpringBootApplication')
	}

	@Test
	void springBootUseSpringBootApplicationJava() {
		def request = createProjectRequest('web')
		request.bootVersion = '1.2.0.RC1'
		request.name = 'MyDemo'
		request.packageName = 'foo'
		generateProject(request).sourceCodeAssert('src/main/java/foo/MyDemoApplication.java')
				.hasImports(SpringBootApplication.class.name)
				.doesNotHaveImports(EnableAutoConfiguration.class.name, ComponentScan.class.name, Configuration.class.name)
				.contains('@SpringBootApplication')
				.doesNotContain('@EnableAutoConfiguration', '@Configuration', '@ComponentScan')
	}

	@Test
	void springBoot11UseEnableAutoConfigurationGroovy() {
		def request = createProjectRequest('web')
		request.language = 'groovy'
		request.bootVersion = '1.1.9.RELEASE'
		request.name = 'MyDemo'
		request.packageName = 'foo'
		generateProject(request).sourceCodeAssert('src/main/java/foo/MyDemoApplication.groovy')
				.hasImports(EnableAutoConfiguration.class.name, ComponentScan.class.name, Configuration.class.name)
				.doesNotHaveImports(SpringBootApplication.class.name)
				.contains('@EnableAutoConfiguration', '@Configuration', '@ComponentScan')
				.doesNotContain('@SpringBootApplication')
	}

	@Test
	void springBootUseSpringBootApplicationGroovy() {
		def request = createProjectRequest('web')
		request.language = 'groovy'
		request.bootVersion = '1.2.0.RC1'
		request.name = 'MyDemo'
		request.packageName = 'foo'
		generateProject(request).sourceCodeAssert('src/main/java/foo/MyDemoApplication.groovy')
				.hasImports(SpringBootApplication.class.name)
				.doesNotHaveImports(EnableAutoConfiguration.class.name, ComponentScan.class.name, Configuration.class.name)
				.contains('@SpringBootApplication')
				.doesNotContain('@EnableAutoConfiguration', '@Configuration', '@ComponentScan')
	}

	@Test
	void customBaseDirectory() {
		def request = createProjectRequest()
		request.baseDir = 'my-project'
		generateProject(request).hasBaseDir('my-project')
				.isJavaProject()
				.isMavenProject()
	}

	@Test
	void groovyWithMavenUsesJavaDir() {
		def request = createProjectRequest('web')
		request.type  = 'maven-project'
		request.language = 'groovy'
		generateProject(request).isMavenProject().isGroovyProject()
	}

	@Test
	void groovyWithGradleUsesGroovyDir() {
		def request = createProjectRequest('web')
		request.type  = 'gradle-project'
		request.language = 'groovy'
		generateProject(request).isGradleProject().isGroovyProject()
	}

	PomAssert generateMavenPom(ProjectRequest request) {
		def content = new String(projectGenerator.generateMavenPom(request))
		new PomAssert(content).validateProjectRequest(request)
	}

	GradleBuildAssert generateGradleBuild(ProjectRequest request) {
		def content = new String(projectGenerator.generateGradleBuild(request))
		new GradleBuildAssert(content).validateProjectRequest(request)
	}

	ProjectAssert generateProject(ProjectRequest request) {
		def dir = projectGenerator.generateProjectStructure(request)
		new ProjectAssert(dir)
	}

	ProjectRequest createProjectRequest(String... styles) {
		def request = new ProjectRequest()
		projectGenerator.metadata.initializeProjectRequest(request)
		request.style.addAll Arrays.asList(styles)
		request
	}

}
