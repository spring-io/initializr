/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.project;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * A default {@link DefaultProjectRequestPlatformVersionTransformer} that uses configured
 * ranges to format the version if necessary.
 *
 * @author Stephane Nicoll
 */
public class DefaultProjectRequestPlatformVersionTransformer implements ProjectRequestPlatformVersionTransformer {

	@Override
	public Version transform(Version platformVersion, InitializrMetadata metadata) {
		return metadata.getConfiguration().getEnv().getPlatform().formatPlatformVersion(platformVersion);
	}

}
