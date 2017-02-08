package io.spring.initializr.generator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import io.spring.initializr.metadata.InitializrMetadata;

/**
 * Resolve {@link ProjectRequest} instances, honouring callback hook points.
 *
 * @author Stephane Nicoll
 */
public class ProjectRequestResolver {

	private final List<ProjectRequestPostProcessor> postProcessors;

	public ProjectRequestResolver(List<ProjectRequestPostProcessor> postProcessors) {
		this.postProcessors = new ArrayList<>(postProcessors);
	}

	public ProjectRequest resolve(ProjectRequest request, InitializrMetadata metadata) {
		Assert.notNull(request, "Request must not be null");
		applyPostProcessBeforeResolution(request, metadata);
		request.resolve(metadata);
		applyPostProcessAfterResolution(request, metadata);
		return request;
	}

	private void applyPostProcessBeforeResolution(ProjectRequest request, InitializrMetadata metadata) {
		for (ProjectRequestPostProcessor processor : postProcessors) {
			processor.postProcessBeforeResolution(request, metadata);
		}
	}

	private void applyPostProcessAfterResolution(ProjectRequest request, InitializrMetadata metadata) {
		for (ProjectRequestPostProcessor processor : postProcessors) {
			processor.postProcessAfterResolution(request, metadata);
		}
	}

}
