/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.actuate.test;

import java.util.Arrays;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.search.Search;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Metrics assertion based on {@link MeterRegistry}.
 *
 * @author Stephane Nicoll
 */
public class MetricsAssert {

	private final MeterRegistry meterRegistry;

	public MetricsAssert(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	public MetricsAssert hasValue(long value, String... metrics) {
		Arrays.asList(metrics)
			.forEach((metric) -> assertThat(this.meterRegistry.get(metric).counter().count()).isEqualTo(value));
		return this;
	}

	public MetricsAssert hasNoValue(String... metrics) {
		Arrays.asList(metrics)
			.forEach((metric) -> assertThat(Search.in(this.meterRegistry).name((n) -> n.startsWith(metric)).counter())
				.isNull());
		return this;
	}

	public MetricsAssert metricsCount(int count) {
		assertThat(Search.in(this.meterRegistry).meters()).hasSize(count);
		return this;
	}

}
