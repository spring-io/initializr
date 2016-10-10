package io.spring.initializr.service.extension

import io.spring.initializr.generator.ProjectGenerator
import io.spring.initializr.generator.ProjectRequest
import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.metadata.InitializrMetadataBuilder
import io.spring.initializr.metadata.InitializrMetadataProvider
import io.spring.initializr.metadata.InitializrProperties
import io.spring.initializr.test.generator.GradleBuildAssert
import io.spring.initializr.test.generator.PomAssert
import io.spring.initializr.web.support.DefaultInitializrMetadataProvider
import org.junit.Test
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.web.client.RestTemplate

/**
 * Tests for {@link ReactiveProjectRequestPostProcessor}.
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringJUnit4ClassRunner)
@SpringBootTest
class ReactiveProjectRequestPostProcessorTests {

	@Autowired
	private ProjectGenerator projectGenerator

	@Autowired
	private InitializrMetadataProvider metadataProvider

	@Test
	void java8IsMandatoryMaven() {
		ProjectRequest request = createProjectRequest('experimental-web-reactive')
		request.bootVersion = '2.0.0.BUILD-SNAPSHOT'
		request.javaVersion = '1.7'
		generateMavenPom(request).hasJavaVersion('1.8')
	}

	@Test
	void java8IsMandatoryGradle() {
		ProjectRequest request = createProjectRequest('experimental-web-reactive')
		request.bootVersion = '2.0.0.BUILD-SNAPSHOT'
		request.javaVersion = '1.7'
		generateGradleBuild(request).hasJavaVersion('1.8')
	}

	@Test
	void versionsAreOverriddenMaven() {
		ProjectRequest request = createProjectRequest('experimental-web-reactive')
		request.bootVersion = '2.0.0.BUILD-SNAPSHOT'
		generateMavenPom(request)
				.hasProperty('reactor.version', '3.0.3.BUILD-SNAPSHOT')
	}

	@Test
	void versionsAreNorOverriddenGradle() {
		ProjectRequest request = createProjectRequest('experimental-web-reactive')
		request.bootVersion = '2.0.0.BUILD-SNAPSHOT'
		generateGradleBuild(request)
				.doesNotContain("ext['spring.version'] = '5.0.0.BUILD-SNAPSHOT'")
				.doesNotContain("ext['reactor.version'] = '3.0.3.BUILD-SNAPSHOT'")
	}

	@Test
	void bomIsAddedMaven() {
		ProjectRequest request = createProjectRequest('experimental-web-reactive')
		request.bootVersion = '2.0.0.RELEASE'
		generateMavenPom(request).hasBom('org.springframework.boot.experimental',
				'spring-boot-dependencies-web-reactive', '0.1.0.BUILD-SNAPSHOT')
	}

	@Test
	void bomIsAddedWithSnapshotMaven() {
		ProjectRequest request = createProjectRequest('experimental-web-reactive')
		request.bootVersion = '2.0.0.BUILD-SNAPSHOT'
		generateMavenPom(request).hasBom('org.springframework.boot.experimental',
				'spring-boot-dependencies-web-reactive', '0.1.0.BUILD-SNAPSHOT')
	}

	@Test
	void bomIsAddedGradle() {
		ProjectRequest request = createProjectRequest('experimental-web-reactive')
		request.bootVersion = '2.0.0.RELEASE'
		generateGradleBuild(request).contains("dependencyManagement {")
				.contains("imports {")
				.contains("mavenBom \"org.springframework.boot.experimental:spring-boot-dependencies-web-reactive:0.1.0.BUILD-SNAPSHOT\"")
	}

	@Test
	void bomIsAddedWithSnapshotGradle() {
		ProjectRequest request = createProjectRequest('experimental-web-reactive')
		request.bootVersion = '2.0.0.BUILD-SNAPSHOT'
		generateGradleBuild(request).contains("dependencyManagement {")
				.contains("imports {")
				.contains("mavenBom \"org.springframework.boot.experimental:spring-boot-dependencies-web-reactive:0.1.0.BUILD-SNAPSHOT\"")
	}

	@Test
	void snapshotRepoIsAddedIfNecessary() {
		ProjectRequest request = createProjectRequest('experimental-web-reactive')
		request.bootVersion = '2.0.0.RELEASE'
		generateMavenPom(request).hasRepository('spring-snapshots', 'Spring Snapshots',
				'https://repo.spring.io/snapshot', true)
	}

	@Test
	void simpleProjectUnaffected() {
		ProjectRequest request = createProjectRequest('web')
		request.javaVersion = '1.7'
		request.buildProperties.versions['spring.version'] = { '3.2.7.RELEASE' }
		generateMavenPom(request).hasJavaVersion('1.7')
				.hasProperty('spring.version', '3.2.7.RELEASE')
				.hasNoProperty('reactor.version')
	}


	private ProjectRequest createProjectRequest(String... styles) {
		def request = new ProjectRequest()
		request.initialize(metadataProvider.get())
		request.style.addAll Arrays.asList(styles)
		request
	}


	private PomAssert generateMavenPom(ProjectRequest request) {
		request.type = 'maven-build'
		def content = new String(projectGenerator.generateMavenPom(request))
		new PomAssert(content)
	}

	private GradleBuildAssert generateGradleBuild(ProjectRequest request) {
		request.type = 'gradle-build'
		def content = new String(projectGenerator.generateGradleBuild(request))
		new GradleBuildAssert(content)
	}

	@TestConfiguration
	static class Config {

		@Bean
		InitializrMetadataProvider initializrMetadataProvider(InitializrProperties properties) {
			new DefaultInitializrMetadataProvider(
					InitializrMetadataBuilder.fromInitializrProperties(properties).build(),
					new RestTemplate()) {
				@Override
				protected void updateInitializrMetadata(InitializrMetadata metadata) {
					// Disable metadata fetching from spring.io
				}

			}
		}

	}

}
