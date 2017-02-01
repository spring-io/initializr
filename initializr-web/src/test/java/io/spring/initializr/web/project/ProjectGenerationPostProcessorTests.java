package io.spring.initializr.web.project;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ActiveProfiles;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.generator.ProjectRequestPostProcessorAdapter;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.project.ProjectGenerationPostProcessorTests.ProjectRequestPostProcessorConfiguration;

@ActiveProfiles("test-default")
@Import(ProjectRequestPostProcessorConfiguration.class)
public class ProjectGenerationPostProcessorTests extends AbstractInitializrControllerIntegrationTests {


	@Test
	public void postProcessorsInvoked() {
		downloadZip("/starter.zip?bootVersion=1.2.4.RELEASE&javaVersion=1.6")
				.isJavaProject()
				.isMavenProject().pomAssert()
				.hasSpringBootParent("1.2.3.RELEASE")
				.hasProperty("java.version", "1.7");
	}


	@Configuration
	static class ProjectRequestPostProcessorConfiguration {

		@Bean
		@Order(2)
		ProjectRequestPostProcessor secondPostProcessor() {
			return new ProjectRequestPostProcessorAdapter() {
				@Override
				public void postProcessBeforeResolution(ProjectRequest request, InitializrMetadata metadata) {
					request.setJavaVersion("1.7");
				}
			};
		}

		@Bean
		@Order(1)
		ProjectRequestPostProcessor firstPostProcessor() {
			return new ProjectRequestPostProcessorAdapter() {
				@Override
				public void postProcessBeforeResolution(ProjectRequest request, InitializrMetadata metadata) {
					request.setJavaVersion("1.2");
					request.setBootVersion("1.2.3.RELEASE");
				}
			};
		}

	}

}
