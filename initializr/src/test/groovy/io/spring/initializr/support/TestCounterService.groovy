/*
 * Copyright 2012-2014 the original author or authors.
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

package io.spring.initializr.support

import org.springframework.boot.actuate.metrics.CounterService

/**
 * A test {@link CounterService} that keeps track of the metric values.
 *
 * @author Stephane Nicoll
 */
class TestCounterService implements CounterService {

	final Map<String, Long> values = new HashMap<>()

	@Override
	void increment(String metricName) {
		Long value = values.get(metricName)
		value != null ? values.put(metricName, ++value) : values.put(metricName, 1)
	}

	@Override
	void decrement(String metricName) {
		Long value = values.get(metricName)
		value != null ? values.put(metricName, --value) : values.put(metricName, -1)
	}

	@Override
	void reset(String metricName) {
		values.put(metricName, 0)
	}
}
