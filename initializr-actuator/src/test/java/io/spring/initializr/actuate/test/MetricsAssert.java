/*
 * Copyright 2012-2016 the original author or authors.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

/**
 * Metrics assertion based on {@link TestCounterService}.
 *
 * @author Stephane Nicoll
 */
public class MetricsAssert {

	private final TestCounterService counterService;

	public MetricsAssert(TestCounterService counterService) {
		this.counterService = counterService;
	}

	public MetricsAssert hasValue(long value, String... metrics) {
		Arrays.asList(metrics).forEach(it -> {
			Long actual = counterService.values.get(it);
			if (actual == null) {
				fail("Metric '" + it + "' not found, got '"
						+ counterService.values.keySet() + "'");
			}
			assertEquals("Wrong value for metric " + it, value, actual.longValue());
		});
		return this;
	}

	public MetricsAssert hasNoValue(String... metrics) {
		Arrays.asList(metrics).forEach(it -> {
			assertEquals("Metric '" + it + "' should not be registered", null,
					counterService.values.get(it));
		});
		return this;
	}

	public MetricsAssert metricsCount(int count) {
		assertEquals(
				"Wrong number of metrics, got '" + counterService.values.keySet() + "'",
				count, counterService.values.size());
		return this;
	}
}
