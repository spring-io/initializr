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

package io.spring.initializr.generator

import io.spring.initializr.metadata.BillOfMaterials
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.test.GradleBuildAssert
import io.spring.initializr.test.InitializrMetadataTestBuilder
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

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify

/**
 * @author Stephane Nicoll
 */
class ProjectGeneratorTests {

	@Rule
	public final TemporaryFolder folder = new TemporaryFolder()

	private final ProjectGenerator projectGenerator = new ProjectGenerator()

	@Before
	void setup() {
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('test', 'web', 'security', 'data-jpa', 'aop', 'batch', 'integration').build()
		projectGenerator.metadata = metadata
		projectGenerator.tmpdir = folder.newFolder().absolutePath
	}

	@Test
	void defaultMavenPom() {
		def listener = mock(ProjectGenerationListener)
		projectGenerator.listeners << listener

		def request = createProjectRequest('web')
		generateMavenPom(request).hasNoRepository()
				.hasSpringBootStarterDependency('web')
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
				.hasNoRepository().hasSpringBootStarterDependency('web')
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
		generateMavenPom(request).hasSnapshotRepository()
				.hasSpringBootStarterDependency('web')
	}

	@Test
	void mavenPomWithWebFacet() {
		def dependency = new Dependency(id: 'thymeleaf', groupId: 'org.foo', artifactId: 'thymeleaf')
		dependency.facets << 'web'
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('test', dependency).build()
		projectGenerator.metadata = metadata

		def request = createProjectRequest('thymeleaf')
		generateMavenPom(request)
				.hasDependency('org.foo', 'thymeleaf')
				.hasDependenciesCount(2)
	}

	@Test
	void mavenWarWithWebFacet() {
		def dependency = new Dependency(id: 'thymeleaf', groupId: 'org.foo', artifactId: 'thymeleaf')
		dependency.facets << 'web'
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('test', dependency).build()
		projectGenerator.metadata = metadata

		def request = createProjectRequest('thymeleaf')
		request.packaging = 'war'
		generateProject(request).isJavaWarProject().isMavenProject().
				pomAssert()
				.hasSpringBootStarterTomcat()
				.hasDependency('org.foo', 'thymeleaf') // This is tagged as web facet so it brings the web one
				.hasSpringBootStarterTest()
				.hasDependenciesCount(3)
	}

	@Test
	void mavenWarPomWithoutWebFacet() {
		def request = createProjectRequest('data-jpa')
		request.packaging = 'war'
		generateMavenPom(request)
				.hasSpringBootStarterTomcat()
				.hasSpringBootStarterDependency('data-jpa')
				.hasSpringBootStarterDependency('web') // Added by war packaging
				.hasSpringBootStarterTest()
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
	void customBaseDirectoryNested() {
		def request = createProjectRequest()
		request.baseDir = 'foo-bar/my-project'
		generateProject(request).hasBaseDir('foo-bar/my-project')
				.isJavaProject()
				.isMavenProject()
	}

	@Test
	void groovyWithMavenUsesJavaDir() {
		def request = createProjectRequest('web')
		request.type = 'maven-project'
		request.language = 'groovy'
		generateProject(request).isMavenProject().isGroovyProject()
	}

	@Test
	void groovyWithGradleUsesGroovyDir() {
		def request = createProjectRequest('web')
		request.type = 'gradle-project'
		request.language = 'groovy'
		generateProject(request).isGradleProject().isGroovyProject()
	}

	@Test
	void mavenPomWithCustomVersion() {
		def whatever = new Dependency(id: 'whatever', groupId: 'org.acme', artifactId: 'whatever', version: '1.2.3')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('foo', whatever).build()
		projectGenerator.metadata = metadata
		def request = createProjectRequest('whatever', 'data-jpa', 'web')
		generateMavenPom(request).hasDependency(whatever)
				.hasSpringBootStarterDependency('data-jpa')
				.hasSpringBootStarterDependency('web')
	}

	@Test
	void gradleBuildWithBootSnapshot() {
		def request = createProjectRequest('web')
		request.bootVersion = '1.0.1.BUILD-SNAPSHOT'
		generateGradleBuild(request).hasSnapshotRepository()
	}

	@Test
	void gradleBuildWithCustomVersion() {
		def whatever = new Dependency(id: 'whatever', groupId: 'org.acme', artifactId: 'whatever', version: '1.2.3')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('foo', whatever).build()
		projectGenerator.metadata = metadata
		def request = createProjectRequest('whatever', 'data-jpa', 'web')
		generateGradleBuild(request)
				.contains("compile('org.springframework.boot:spring-boot-starter-web')")
				.contains("compile('org.springframework.boot:spring-boot-starter-data-jpa')")
				.contains("compile('org.acme:whatever:1.2.3')")
	}

	@Test
	void mavenPomWithCustomScope() {
		def h2 = new Dependency(id: 'h2', groupId: 'org.h2', artifactId: 'h2', scope: 'runtime')
		def hamcrest = new Dependency(id: 'hamcrest', groupId: 'org.hamcrest',
				artifactId: 'hamcrest', scope: 'test')
		def servlet = new Dependency(id: 'servlet-api', groupId: 'javax.servlet',
				artifactId: 'servlet-api', scope: 'provided')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('database', h2)
				.addDependencyGroup('container', servlet)
				.addDependencyGroup('test', hamcrest).build()
		projectGenerator.metadata = metadata
		def request = createProjectRequest('hamcrest', 'h2', 'servlet-api', 'data-jpa', 'web')
		generateMavenPom(request).hasDependency(h2).hasDependency(hamcrest).hasDependency(servlet)
				.hasSpringBootStarterDependency('data-jpa')
				.hasSpringBootStarterDependency('web')
	}

	@Test
	void gradleBuildWithCustomScope() {
		def h2 = new Dependency(id: 'h2', groupId: 'org.h2', artifactId: 'h2', scope: 'runtime')
		def hamcrest = new Dependency(id: 'hamcrest', groupId: 'org.hamcrest',
				artifactId: 'hamcrest', scope: 'test')
		def servlet = new Dependency(id: 'servlet-api', groupId: 'javax.servlet',
				artifactId: 'servlet-api', scope: 'provided')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('database', h2)
				.addDependencyGroup('container', servlet)
				.addDependencyGroup('test', hamcrest).build()
		projectGenerator.metadata = metadata
		def request = createProjectRequest('hamcrest', 'h2', 'servlet-api', 'data-jpa', 'web')
		generateGradleBuild(request)
				.contains("compile('org.springframework.boot:spring-boot-starter-web')")
				.contains("compile('org.springframework.boot:spring-boot-starter-data-jpa')")
				.contains("runtime('org.h2:h2')")
				.contains("providedRuntime('javax.servlet:servlet-api')")
				.contains("testCompile('org.hamcrest:hamcrest')")
	}

	@Test
	void gradleBuildBeforeWithSpringBoot13() {
		def request = createProjectRequest('web')
		request.bootVersion = '1.2.3.RELEASE'
		generateGradleBuild(request)
				.contains("springBootVersion = '1.2.3.RELEASE'")
				.contains("classpath('io.spring.gradle:dependency-management-plugin:0.5.2.RELEASE')")
				.contains("apply plugin: 'spring-boot'")
				.contains("apply plugin: 'io.spring.dependency-management'")
	}

	@Test
	void gradleBuildAsFromSpringBoot13() {
		def request = createProjectRequest('web')
		request.bootVersion = '1.3.0.BUILD-SNAPSHOT'
		generateGradleBuild(request)
				.contains("springBootVersion = '1.3.0.BUILD-SNAPSHOT'")
				.contains("apply plugin: 'spring-boot'")
				.doesNotContain("classpath('io.spring.gradle:dependency-management-plugin:0.5.2.RELEASE')")
				.doesNotContain("apply plugin: 'io.spring.dependency-management'")
	}

	@Test
	void mavenBom() {
		def foo = new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', bom: 'foo-bom')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('foo', foo)
				.addBom('foo-bom', 'org.acme', 'foo-bom', '1.2.3').build()
		projectGenerator.metadata = metadata
		def request = createProjectRequest('foo')
		generateMavenPom(request).hasDependency(foo)
				.hasBom('org.acme', 'foo-bom', '1.2.3')
	}

	@Test
	void mavenBomWithSeveralDependenciesOnSameBom() {
		def foo = new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', bom: 'the-bom')
		def bar = new Dependency(id: 'bar', groupId: 'org.acme', artifactId: 'bar', bom: 'the-bom')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('group', foo, bar)
				.addBom('the-bom', 'org.acme', 'the-bom', '1.2.3').build()
		projectGenerator.metadata = metadata
		def request = createProjectRequest('foo', 'bar')
		generateMavenPom(request).hasDependency(foo)
				.hasBom('org.acme', 'the-bom', '1.2.3')
				.hasBomsCount(1)
	}

	@Test
	void mavenBomWithVersionMapping() {
		def foo = new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', bom: 'the-bom')
		def bom = new BillOfMaterials(groupId: 'org.acme', artifactId: 'foo-bom')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '[1.2.0.RELEASE,1.3.0.M1)', version: '1.0.0')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '1.3.0.M1', version: '1.2.0')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('foo', foo)
				.addBom('the-bom', bom).build()
		projectGenerator.metadata = metadata

		// First version
		def request = createProjectRequest('foo')
		request.bootVersion = '1.2.5.RELEASE'
		generateMavenPom(request).hasDependency(foo)
				.hasBom('org.acme', 'foo-bom', '1.0.0')

		// Second version
		def request2 = createProjectRequest('foo')
		request2.bootVersion = '1.3.0.M1'
		generateMavenPom(request2).hasDependency(foo)
				.hasBom('org.acme', 'foo-bom', '1.2.0')
	}

	@Test
	void mavenBomWithVersionMappingAndExtraRepositories() {
		def foo = new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', bom: 'the-bom')
		def bom = new BillOfMaterials(groupId: 'org.acme', artifactId: 'foo-bom', repositories: ['foo-repo'])
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '[1.2.0.RELEASE,1.3.0.M1)', version: '1.0.0')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '1.3.0.M1', version: '1.2.0', repositories: ['foo-repo', 'bar-repo'])
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('foo', foo)
				.addBom('the-bom', bom)
				.addRepository('foo-repo', 'repo', 'http://example.com/foo', true)
				.addRepository('bar-repo', 'repo', 'http://example.com/bar', false).build()
		projectGenerator.metadata = metadata

		// Second version
		def request = createProjectRequest('foo')
		request.bootVersion = '1.3.0.RELEASE'
		generateMavenPom(request).hasDependency(foo)
				.hasBom('org.acme', 'foo-bom', '1.2.0')
				.hasRepository('foo-repo', 'repo', 'http://example.com/foo', true)
				.hasRepository('bar-repo', 'repo', 'http://example.com/bar', false)
				.hasRepositoriesCount(2)
	}

	@Test
	void gradleBom() {
		def foo = new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', bom: 'foo-bom')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('foo', foo)
				.addBom('foo-bom', 'org.acme', 'foo-bom', '1.2.3').build()
		projectGenerator.metadata = metadata
		def request = createProjectRequest('foo')
		generateGradleBuild(request)
				.contains("dependencyManagement {")
				.contains("imports {")
				.contains("mavenBom \"org.acme:foo-bom:1.2.3\"")
	}

	@Test
	void mavenRepository() {
		def foo = new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', repository: 'foo-repo')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('foo', foo)
				.addRepository('foo-repo', 'foo', 'http://example.com/repo', false).build()
		projectGenerator.metadata = metadata
		def request = createProjectRequest('foo')
		generateMavenPom(request).hasDependency(foo)
				.hasRepository('foo-repo', 'foo', 'http://example.com/repo', false)
	}

	@Test
	void mavenRepositoryWithSeveralDependenciesOnSameRepository() {
		def foo = new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', repository: 'the-repo')
		def bar = new Dependency(id: 'bar', groupId: 'org.acme', artifactId: 'bar', repository: 'the-repo')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('group', foo, bar)
				.addRepository('the-repo', 'repo', 'http://example.com/repo', true).build()
		projectGenerator.metadata = metadata
		def request = createProjectRequest('foo', 'bar')
		generateMavenPom(request).hasDependency(foo)
				.hasRepository('the-repo', 'repo', 'http://example.com/repo', true)
				.hasRepositoriesCount(1)
	}

	@Test
	void gradleRepository() {
		def foo = new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', repository: 'foo-repo')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('foo', foo)
				.addRepository('foo-repo', 'foo', 'http://example.com/repo', false).build()
		projectGenerator.metadata = metadata
		def request = createProjectRequest('foo')
		generateGradleBuild(request)
				.hasRepository('http://example.com/repo')
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
		request.initialize(projectGenerator.metadata)
		request.style.addAll Arrays.asList(styles)
		request
	}

}
