/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.actuate.autoconfigure;

import io.spring.initializr.actuate.info.BomRangesInfoContributor;
import io.spring.initializr.actuate.info.DependencyRangesInfoContributor;
import io.spring.initializr.web.autoconfigure.InitializrAutoConfiguration;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.restclient.autoconfigure.RestTemplateAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InitializrActuatorEndpointsAutoConfiguration}.
 *
 * @author Madhura Bhave
 */
class InitializrActuatorEndpointsAutoConfigurationTests {

	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, InitializrAutoConfiguration.class,
				RestTemplateAutoConfiguration.class, InitializrActuatorEndpointsAutoConfiguration.class));

	@Test
	void autoConfigRegistersBomRangesInfoContributor() {
		this.contextRunner.run((context) -> assertThat(context).hasSingleBean(BomRangesInfoContributor.class));
	}

	@Test
	void autoConfigRegistersDependencyRangesInfoContributor() {
		this.contextRunner.run((context) -> assertThat(context).hasSingleBean(DependencyRangesInfoContributor.class));
	}

}
