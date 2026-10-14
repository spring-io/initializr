/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.project;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * Convert a {@link ProjectRequest} to a {@link ProjectDescription}.
 *
 * @param <R> the concrete {@link ProjectRequest} type
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface ProjectRequestToDescriptionConverter<R extends ProjectRequest> {

	/**
	 * Validate and convert the specified {@link ProjectRequest} to a
	 * {@link ProjectDescription} used as the source of project generation.
	 * @param request the request to convert
	 * @param metadata the metadata instance to use
	 * @return a validated {@link ProjectDescription} to use to generate a project that
	 * matches the specified {@code request}
	 */
	ProjectDescription convert(R request, InitializrMetadata metadata);

}
