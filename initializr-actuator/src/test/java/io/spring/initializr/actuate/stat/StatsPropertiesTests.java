/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.actuate.stat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StatsProperties}.
 *
 * @author Stephane Nicoll
 */
class StatsPropertiesTests {

	private final StatsProperties properties = new StatsProperties();

	@Test
	void cleanTrailingSlash() {
		this.properties.getElastic().setUri("https://example.com/");
		assertThat(this.properties.getElastic().getUri()).isEqualTo("https://example.com");
	}

}
