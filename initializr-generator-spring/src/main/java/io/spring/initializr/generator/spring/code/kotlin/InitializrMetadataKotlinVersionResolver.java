/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.util.Assert;

/**
 * {@link KotlinVersionResolver} that resolves the version from the
 * {@link InitializrMetadata}.
 *
 * @author Andy Wilkinson
 */
public class InitializrMetadataKotlinVersionResolver implements KotlinVersionResolver {

	private final InitializrMetadata metadata;

	public InitializrMetadataKotlinVersionResolver(InitializrMetadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public String resolveKotlinVersion(ProjectDescription description) {
		Version platformVersion = description.getPlatformVersion();
		Assert.state(platformVersion != null, "'platformVersion' must not be null");
		return this.metadata.getConfiguration().getEnv().getKotlin().resolveKotlinVersion(platformVersion);
	}

}
