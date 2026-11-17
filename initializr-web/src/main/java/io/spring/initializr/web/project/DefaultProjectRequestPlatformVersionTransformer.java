/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
