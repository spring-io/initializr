package io.spring.initializr.service.extension

import io.spring.initializr.generator.ProjectRequest
import io.spring.initializr.generator.ProjectRequestPostProcessorAdapter
import io.spring.initializr.metadata.InitializrMetadata

import org.springframework.stereotype.Component

/**
 * The "web-reactive" starter is experimental as Spring Boot hasn't integrated Spring 5
 * yet. This {@link io.spring.initializr.generator.ProjectRequestPostProcessor} forces
 * Java 8 as well as Spring 5 and Reactor 3.
 *
 * @author Stephane Nicoll
 */
@Component
class ReactiveProjectRequestPostProcessor extends ProjectRequestPostProcessorAdapter {

	@Override
	void postProcessAfterResolution(ProjectRequest request, InitializrMetadata metadata) {
		if (request.resolvedDependencies.find { it.id.equals('experimental-web-reactive') }) {
			request.javaVersion = '1.8'

			// No need to override those versions with Gradle
			if ('maven'.equals(request.build)) {
				request.buildProperties.versions['reactor.version'] = { '3.0.3.BUILD-SNAPSHOT' }
			}
		}
	}

}
