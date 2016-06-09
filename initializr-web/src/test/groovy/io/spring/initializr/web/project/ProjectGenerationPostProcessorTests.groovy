package io.spring.initializr.web.project

import io.spring.initializr.generator.ProjectRequest
import io.spring.initializr.generator.ProjectRequestPostProcessor
import io.spring.initializr.generator.ProjectRequestPostProcessorAdapter
import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests
import org.junit.Test

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.Order
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles('test-default')
@Import(ProjectRequestPostProcessorConfiguration)
class ProjectGenerationPostProcessorTests extends AbstractInitializrControllerIntegrationTests {


	@Test
	void postProcessorsInvoked() {
		downloadZip('/starter.zip?bootVersion=1.2.4.RELEASE&javaVersion=1.6')
				.isJavaProject()
				.isMavenProject().pomAssert()
				.hasSpringBootParent('1.2.3.RELEASE')
				.hasProperty('java.version', '1.7')
	}


	@Configuration
	static class ProjectRequestPostProcessorConfiguration {

		@Bean
		@Order(2)
		ProjectRequestPostProcessor secondPostProcessor() {
			new ProjectRequestPostProcessorAdapter() {
				@Override
				void postProcessBeforeResolution(ProjectRequest request, InitializrMetadata metadata) {
					request.javaVersion = '1.7'
				}
			}
		}

		@Bean
		@Order(1)
		ProjectRequestPostProcessor firstPostProcessor() {
			new ProjectRequestPostProcessorAdapter() {
				@Override
				void postProcessBeforeResolution(ProjectRequest request, InitializrMetadata metadata) {
					request.javaVersion = '1.2'
					request.bootVersion = '1.2.3.RELEASE'
				}
			}
		}

	}

}
