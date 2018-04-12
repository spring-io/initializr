/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
		Arrays.asList(metrics).forEach(
				(metric) -> assertThat(this.meterRegistry.get(metric).counter().count())
						.isEqualTo(value));
		return this;
	}

	public MetricsAssert hasNoValue(String... metrics) {
		Arrays.asList(metrics).forEach((metric) -> assertThat(
				Search.in(this.meterRegistry).name((n) -> n.startsWith(metric)).counter())
						.isNull());
		return this;
	}

	public MetricsAssert metricsCount(int count) {
		assertThat(Search.in(this.meterRegistry).meters()).hasSize(count);
		return this;
	}

}
