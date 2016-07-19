package io.spring.initializr.generator

import io.spring.initializr.metadata.InitializrMetadata

import org.springframework.util.Assert

/**
 * Resolve {@link ProjectRequest} instances, honoring callback hook points.
 *
 * @author Stephane Nicoll
 */
class ProjectRequestResolver {

	private final List<ProjectRequestPostProcessor> postProcessors

	ProjectRequestResolver(List<ProjectRequestPostProcessor> postProcessors) {
		this.postProcessors = new ArrayList<>(postProcessors)
	}

	ProjectRequest resolve(ProjectRequest request, InitializrMetadata metadata) {
		Assert.notNull(request, "Request must not be null")
		applyPostProcessBeforeResolution(request, metadata)
		request.resolve(metadata)
		applyPostProcessAfterResolution(request, metadata)
		request
	}

	private void applyPostProcessBeforeResolution(ProjectRequest request, InitializrMetadata metadata) {
		for (ProjectRequestPostProcessor processor : postProcessors) {
			processor.postProcessBeforeResolution(request, metadata)
		}
	}

	private void applyPostProcessAfterResolution(ProjectRequest request, InitializrMetadata metadata) {
		for (ProjectRequestPostProcessor processor : postProcessors) {
			processor.postProcessAfterResolution(request, metadata)
		}
	}

}
