package io.spring.initializr

import io.spring.initializr.web.MainController

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} to configure Spring initializr. In a web environment,
 * configures the necessary controller to serve the applications from the
 * root context.
 *
 * <p>Project generation can be customized by defining a custom
 * {@link ProjectGenerator}.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(InitializrMetadata.class)
class InitializrAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(MainController.class)
	MainController initializrMainController() {
		new MainController()
	}

	@Bean
	@ConditionalOnMissingBean(ProjectGenerator.class)
	ProjectGenerator projectGenerator() {
		new ProjectGenerator()
	}
}
