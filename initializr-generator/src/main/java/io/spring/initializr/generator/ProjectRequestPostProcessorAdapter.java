package io.spring.initializr.generator;

import io.spring.initializr.metadata.InitializrMetadata;

/**
 * An implementation of {@link ProjectRequestPostProcessor} with empty methods allowing
 * sub-classes to override only the methods they're interested in.
 *
 * @author Stephane Nicoll
 */
public class ProjectRequestPostProcessorAdapter implements ProjectRequestPostProcessor {

	@Override
	public void postProcessBeforeResolution(ProjectRequest request,
			InitializrMetadata metadata) {
	}

	@Override
	public void postProcessAfterResolution(ProjectRequest request,
			InitializrMetadata metadata) {
	}

}
