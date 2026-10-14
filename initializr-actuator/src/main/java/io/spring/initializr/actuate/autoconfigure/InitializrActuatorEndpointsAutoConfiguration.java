/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.actuate.autoconfigure;

import io.spring.initializr.actuate.info.BomRangesInfoContributor;
import io.spring.initializr.actuate.info.DependencyRangesInfoContributor;
import io.spring.initializr.metadata.InitializrMetadataProvider;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} to improve actuator endpoints with initializr specific information.
 *
 * @author Stephane Nicoll
 */
@AutoConfiguration
public class InitializrActuatorEndpointsAutoConfiguration {

	@Bean
	public BomRangesInfoContributor bomRangesInfoContributor(InitializrMetadataProvider metadataProvider) {
		return new BomRangesInfoContributor(metadataProvider);
	}

	@Bean
	public DependencyRangesInfoContributor dependencyRangesInfoContributor(
			InitializrMetadataProvider metadataProvider) {
		return new DependencyRangesInfoContributor(metadataProvider);
	}

}
