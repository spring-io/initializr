/*
 * Copyright 2012-2016 the original author or authors.
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
import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

import static org.hamcrest.CoreMatchers.containsString
import static org.junit.Assert.assertThat
import static org.junit.Assert.fail

/**
 * Tests for {@link ProjectGenerator}
 *
 * @author Stephane Nicoll
 */
class ProjectGeneratorTests extends AbstractProjectGeneratorTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none()

	@Test
	void defaultMavenPom() {
		def request = createProjectRequest('web')
		generateMavenPom(request).hasNoRepository()
				.hasSpringBootStarterDependency('web')
		verifyProjectSuccessfulEventFor(request)
	}

	@Test
	void defaultGradleBuild() {
		def request = createProjectRequest('web')
		generateGradleBuild(request)
		verifyProjectSuccessfulEventFor(request)
	}

	@Test
	void defaultProject() {
		def request = createProjectRequest('web')
		generateProject(request).isJavaProject().isMavenProject().pomAssert()
				.hasNoRepository().hasSpringBootStarterDependency('web')
		verifyProjectSuccessfulEventFor(request)
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
				.hasSpringBootParent('1.0.1.BUILD-SNAPSHOT')
				.hasSpringBootStarterDependency('web')
	}

	@Test
	void mavenPomWithTarDependency() {
		def dependency = new Dependency(id: 'custom-artifact', groupId: 'org.foo', artifactId: 'custom-artifact', type: "tar.gz")
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('test', dependency).build()
		applyMetadata(metadata)

		def request = createProjectRequest('custom-artifact')
		generateMavenPom(request).hasDependency(dependency)
				.hasDependenciesCount(2)
	}

	@Test
	void gradleBuildWithTarDependency() {
		def dependency = new Dependency(id: 'custom-artifact', groupId: 'org.foo', artifactId: 'custom-artifact', type: "tar.gz")
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('test', dependency).build()
		applyMetadata(metadata)

		def request = createProjectRequest('custom-artifact')
		generateGradleBuild(request)
				.contains("compile('org.foo:custom-artifact@tar.gz')")
	}

	@Test
	void mavenPomWithWebFacet() {
		def dependency = new Dependency(id: 'thymeleaf', groupId: 'org.foo', artifactId: 'thymeleaf')
		dependency.facets << 'web'
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('test', dependency).build()
		applyMetadata(metadata)

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
		applyMetadata(metadata)

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
	void gradleWarWithWebFacet() {
		def dependency = new Dependency(id: 'thymeleaf', groupId: 'org.foo', artifactId: 'thymeleaf')
		dependency.facets << 'web'
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.addDependencyGroup('test', dependency).build()
		applyMetadata(metadata)

		def request = createProjectRequest('thymeleaf')
		request.packaging = 'war'
		request.type = 'gradle-project'
		generateProject(request).isJavaWarProject().isGradleProject().
				gradleBuildAssert()
				.contains("compile('org.foo:thymeleaf')") // This is tagged as web facet so it brings the web one
				.doesNotContain("compile('org.springframework.boot:spring-boot-starter-web')")
				.contains("testCompile('org.springframework.boot:spring-boot-starter-test')")
				.contains("configurations {") // declare providedRuntime config
				.contains("providedRuntime")
				.contains("providedRuntime('org.springframework.boot:spring-boot-starter-tomcat')")
	}

	@Test
	void gradleWarPomWithoutWebFacet() {
		def request = createProjectRequest('data-jpa')
		request.packaging = 'war'
		generateGradleBuild(request)
				.contains("compile('org.springframework.boot:spring-boot-starter-data-jpa')")
				.contains("compile('org.springframework.boot:spring-boot-starter-web')") // Added by war packaging
				.contains("testCompile('org.springframework.boot:spring-boot-starter-test')")
				.contains("configurations {") // declare providedRuntime config
				.contains("providedRuntime")
				.contains("providedRuntime('org.springframework.boot:spring-boot-starter-tomcat')")
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
		generateProject(request).sourceCodeAssert('src/main/groovy/foo/MyDemoApplication.groovy')
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
		generateProject(request).sourceCodeAssert('src/main/groovy/foo/MyDemoApplication.groovy')
				.hasImports(SpringBootApplication.class.name)
				.doesNotHaveImports(EnableAutoConfiguration.class.name, ComponentScan.class.name, Configuration.class.name)
				.contains('@SpringBootApplication')
				.doesNotContain('@EnableAutoConfiguration', '@Configuration', '@ComponentScan')
	}

	@Test
	void springBoot11UseEnableAutoConfigurationKotlin() {
		def request = createProjectRequest('web')
		request.language = 'kotlin'
		request.bootVersion = '1.1.9.RELEASE'
		request.name = 'MyDemo'
		request.packageName = 'foo'
		generateProject(request).sourceCodeAssert('src/main/kotlin/foo/MyDemoApplication.kt')
				.hasImports(EnableAutoConfiguration.class.name, ComponentScan.class.name, Configuration.class.name)
				.doesNotHaveImports(SpringBootApplication.class.name)
				.contains('@EnableAutoConfiguration', '@Configuration', '@ComponentScan')
				.doesNotContain('@SpringBootApplication')
	}

	@Test
	void springBootUseSpringBootApplicationKotlin() {
		def request = createProjectRequest('web')
		request.language = 'kotlin'
		request.bootVersion = '1.2.0.RC1'
		request.name = 'MyDemo'
		request.packageName = 'foo'
		generateProject(request).sourceCodeAssert('src/main/kotlin/foo/MyDemoApplication.kt')
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
	void groovyWithMavenUsesGroovyDir() {
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
		applyMetadata(metadata)
		def request = createProjectRequest('whatever', 'data-jpa', 'web')
		generateMavenPom(request).hasDependency(whatever)
				.hasSpringBootStarterDependency('data-jpa')
				.hasSpringBootStarterDependency('web')
	}

	@Test
	void defaultMavenPomHasSpringBootParent() {
		def request = createProjectRequest('web')
		generateMavenPom(request).hasSpringBootParent(request.bootVersion)
	}

	@Test
	void mavenPomWithCustomParentPom() {
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.setMavenParent('com.foo', 'foo-parent', '1.0.0-SNAPSHOT', false)
				.build()
		applyMetadata(metadata)
		def request = createProjectRequest('web')
		generateMavenPom(request)
				.hasParent('com.foo', 'foo-parent', '1.0.0-SNAPSHOT')
				.hasBomsCount(0)
	}

	@Test
	void mavenPomWithCustomParentPomAndSpringBootBom() {
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.setMavenParent('com.foo', 'foo-parent', '1.0.0-SNAPSHOT', true)
				.build()
		applyMetadata(metadata)
		def request = createProjectRequest('web')
		request.bootVersion = '1.0.2.RELEASE'
		generateMavenPom(request)
				.hasParent('com.foo', 'foo-parent', '1.0.0-SNAPSHOT')
				.hasProperty('spring-boot.version', '1.0.2.RELEASE')
				.hasBom('org.springframework.boot', 'spring-boot-dependencies', '${spring-boot.version}')
				.hasBomsCount(1)
	}

	@Test
	void gradleBuildWithCustomParentPomAndSpringBootBom() {
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'security', 'data-jpa')
				.setMavenParent('com.foo', 'foo-parent', '1.0.0-SNAPSHOT', true)
				.build()
		applyMetadata(metadata)
		def request = createProjectRequest('web')
		request.bootVersion = '1.0.2.RELEASE'
		generateGradleBuild(request)
				.doesNotContain("ext['spring-boot.version'] = '1.0.2.RELEASE'")
				.doesNotContain("mavenBom \"org.springframework.boot:spring-boot-dependencies:1.0.2.RELEASE\"")
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
		applyMetadata(metadata)
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
		applyMetadata(metadata)
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
		applyMetadata(metadata)
		def request = createProjectRequest('hamcrest', 'h2', 'servlet-api', 'data-jpa', 'web')
		generateGradleBuild(request)
				.contains("compile('org.springframework.boot:spring-boot-starter-web')")
				.contains("compile('org.springframework.boot:spring-boot-starter-data-jpa')")
				.contains("runtime('org.h2:h2')")
				.contains("configurations {") // declare providedRuntime config
				.contains("providedRuntime")
				.contains("providedRuntime('javax.servlet:servlet-api')")
				.contains("testCompile('org.hamcrest:hamcrest')")
	}

	@Test
	void gradleBuildBeforeWithSpringBoot13() {
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'jpa')
				.setGradleEnv('0.5.9.RELEASE').build()
		applyMetadata(metadata)
		def request = createProjectRequest('web')
		request.bootVersion = '1.2.3.RELEASE'
		generateGradleBuild(request)
				.contains("springBootVersion = '1.2.3.RELEASE'")
				.contains("classpath('io.spring.gradle:dependency-management-plugin:0.5.9.RELEASE')")
				.contains("apply plugin: 'spring-boot'")
				.contains("apply plugin: 'io.spring.dependency-management'")
	}

	@Test
	void gradleBuildAsFromSpringBoot13() {
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', 'web', 'jpa')
				.setGradleEnv('0.5.9.RELEASE').build()
		applyMetadata(metadata)
		def request = createProjectRequest('web')
		request.bootVersion = '1.3.0.BUILD-SNAPSHOT'
		generateGradleBuild(request)
				.contains("springBootVersion = '1.3.0.BUILD-SNAPSHOT'")
				.contains("apply plugin: 'spring-boot'")
				.doesNotContain("classpath('io.spring.gradle:dependency-management-plugin:0.5.9.RELEASE')")
				.doesNotContain("apply plugin: 'io.spring.dependency-management'")
	}

	@Test
	void gradleBuildAsFromSpringBoot142() {
		def request = createProjectRequest('web')
		request.bootVersion = '1.4.2.BUILD-SNAPSHOT'
		generateGradleBuild(request)
				.contains("springBootVersion = '1.4.2.BUILD-SNAPSHOT'")
				.contains("apply plugin: 'org.springframework.boot'")
				.doesNotContain("apply plugin: 'spring-boot'")
	}

	@Test
	void mavenBom() {
		def foo = new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', bom: 'foo-bom')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('foo', foo)
				.addBom('foo-bom', 'org.acme', 'foo-bom', '1.2.3').build()
		applyMetadata(metadata)
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
		applyMetadata(metadata)
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
		applyMetadata(metadata)

		// First version
		def request = createProjectRequest('foo')
		request.bootVersion = '1.2.5.RELEASE'
		generateMavenPom(request).hasDependency(foo)
				.hasSpringBootParent('1.2.5.RELEASE')
				.hasBom('org.acme', 'foo-bom', '1.0.0')

		// Second version
		def request2 = createProjectRequest('foo')
		request2.bootVersion = '1.3.0.M1'
		generateMavenPom(request2).hasDependency(foo)
				.hasSpringBootParent('1.3.0.M1')
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
		applyMetadata(metadata)

		// Second version
		def request = createProjectRequest('foo')
		request.bootVersion = '1.3.0.RELEASE'
		generateMavenPom(request).hasDependency(foo)
				.hasSpringBootParent('1.3.0.RELEASE')
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
		applyMetadata(metadata)
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
		applyMetadata(metadata)
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
		applyMetadata(metadata)
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
		applyMetadata(metadata)
		def request = createProjectRequest('foo')
		generateGradleBuild(request)
				.hasRepository('http://example.com/repo')
	}

	@Test
	void projectWithOnlyStarterDependency() {
		def foo = new Dependency(id: 'foo', groupId: 'org.foo', artifactId: 'custom-my-starter')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('foo', foo).build()
		applyMetadata(metadata)

		def request = createProjectRequest('foo')
		generateMavenPom(request)
				.hasDependency('org.foo', 'custom-my-starter')
				.hasSpringBootStarterTest()
				.hasDependenciesCount(2)
	}

	@Test
	void projectWithOnlyNonStarterDependency() {
		def foo = new Dependency(id: 'foo', groupId: 'org.foo', artifactId: 'foo')
		foo.starter = false
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('foo', foo).build()
		applyMetadata(metadata)

		def request = createProjectRequest('foo')
		generateMavenPom(request)
				.hasDependency('org.foo', 'foo')
				.hasSpringBootStarterRootDependency()
				.hasSpringBootStarterTest()
				.hasDependenciesCount(3)
	}

	@Test
	void buildPropertiesMaven() {
		def request = createProjectRequest('web')
		request.buildProperties.maven['name'] = { 'test' }
		request.buildProperties.versions['foo.version'] = { '1.2.3' }
		request.buildProperties.gradle['ignore.property'] = { 'yes' }

		generateMavenPom(request)
				.hasProperty('name', 'test')
				.hasProperty('foo.version', '1.2.3')
				.hasNoProperty('ignore.property')
	}

	@Test
	void buildPropertiesGradle() {
		def request = createProjectRequest('web')
		request.buildProperties.gradle['name'] = { 'test' }
		request.buildProperties.versions['foo.version'] = { '1.2.3' }
		request.buildProperties.maven['ignore.property'] = { 'yes' }

		generateGradleBuild(request)
				.contains("name = 'test'")
				.contains("ext['foo.version'] = '1.2.3'")
				.doesNotContain('ignore.property')
	}

	@Test
	void versionRangeWithPostProcessor() {
		Dependency foo = new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo')
		foo.mappings << new Dependency.Mapping(versionRange: '[1.2.0.RELEASE,1.3.0.M1)', version: '1.0.0')
		foo.mappings << new Dependency.Mapping(versionRange: '1.3.0.M1', version: '1.2.0')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('foo', foo).build()
		applyMetadata(metadata)

		// First without processor, get the correct version
		def request = createProjectRequest('foo')
		request.bootVersion = '1.2.5.RELEASE'
		generateMavenPom(request).hasDependency(
				new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', version: '1.0.0'))

		// First after processor that flips Spring Boot version
		projectGenerator.requestResolver = new ProjectRequestResolver(Collections.singletonList(
				new ProjectRequestPostProcessorAdapter() {
					@Override
					void postProcessBeforeResolution(ProjectRequest r, InitializrMetadata m) {
						r.bootVersion = '1.3.0.M2'
					}
				}
		))
		generateMavenPom(request).hasDependency(
				new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', version: '1.2.0'))
	}

	@Test
	void gitIgnoreMaven() {
		def request = createProjectRequest()
		request.type = 'maven-project'
		def project = generateProject(request)
		project.sourceCodeAssert(".gitignore")
				.equalsTo(new ClassPathResource("project/maven/gitignore.gen"))
	}

	@Test
	void gitIgnoreGradle() {
		def request = createProjectRequest()
		request.type = 'gradle-project'
		def project = generateProject(request)
		project.sourceCodeAssert(".gitignore")
				.equalsTo(new ClassPathResource("project/gradle/gitignore.gen"))
	}

	@Test
	void invalidProjectTypeMavenPom() {
		def request = createProjectRequest('web')
		request.type = 'gradle-build'
		this.thrown.expect(InvalidProjectRequestException)
		this.thrown.expectMessage('gradle-build')
		projectGenerator.generateMavenPom(request)
	}

	@Test
	void invalidProjectTypeGradleBuild() {
		def request = createProjectRequest('web')
		request.type = 'maven-build'
		this.thrown.expect(InvalidProjectRequestException)
		this.thrown.expectMessage('maven-build')
		projectGenerator.generateGradleBuild(request)
	}

	@Test
	void invalidDependency() {
		def request = createProjectRequest('foo-bar')
		try {
			generateMavenPom(request)
			fail("Should have failed to generate project")
		} catch (InvalidProjectRequestException ex) {
			assertThat ex.message, containsString('foo-bar')
			verifyProjectFailedEventFor(request, ex)
		}
	}

	@Test
	void invalidType() {
		def request = createProjectRequest('web')
		request.type = 'foo-bar'
		try {
			generateProject(request)
			fail("Should have failed to generate project")
		} catch (InvalidProjectRequestException ex) {
			assertThat ex.message, containsString('foo-bar')
			verifyProjectFailedEventFor(request, ex)
		}
	}

	@Test
	void invalidPackaging() {
		def request = createProjectRequest('web')
		request.packaging = 'foo-bar'
		try {
			generateGradleBuild(request)
			fail("Should have failed to generate project")
		} catch (InvalidProjectRequestException ex) {
			assertThat ex.message, containsString('foo-bar')
			verifyProjectFailedEventFor(request, ex)
		}
	}

	@Test
	void invalidLanguage() {
		def request = createProjectRequest('web')
		request.language = 'foo-bar'
		try {
			generateProject(request)
			fail("Should have failed to generate project")
		} catch (InvalidProjectRequestException ex) {
			assertThat ex.message, containsString('foo-bar')
			verifyProjectFailedEventFor(request, ex)
		}
	}

}
