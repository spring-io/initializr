/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.project;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * Strategy interface to transform the platform version of a {@link ProjectRequest}.
 *
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface ProjectRequestPlatformVersionTransformer {

	/**
	 * Transform the platform version of a {@link ProjectRequest} if necessary.
	 * @param platformVersion the candidate platform version
	 * @param metadata the metadata instance to use
	 * @return the platform version to use
	 */
	Version transform(Version platformVersion, InitializrMetadata metadata);

}
