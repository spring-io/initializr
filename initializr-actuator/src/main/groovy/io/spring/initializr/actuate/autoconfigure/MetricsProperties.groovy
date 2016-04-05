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

package io.spring.initializr.actuate.autoconfigure

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Metrics-related configuration.
 *
 * @author Dave Syer
 * @since 1.0
 */
@ConfigurationProperties('initializr.metrics')
class MetricsProperties {

	/**
	 * Prefix for redis keys holding metrics in data store.
	 */
	String prefix = 'spring.metrics.collector.'

	/**
	 * Redis key holding index to metrics keys in data store.
	 */
	String key = 'keys.spring.metrics.collector'

	/**
	 * Identifier for application in metrics keys. Keys will be exported in the form
	 * '[id].[hex].[name]' (where '[id]' is this value, '[hex]' is unique per application
	 * context, and '[name]' is the "natural" name for the metric.
	 */
	@Value('${spring.application.name:${vcap.application.name:application}}')
	String id

	/**
	 * The rate (in milliseconds) at which metrics are exported to Redis. If the value is
	 * <=0 then the export is disabled.
	 */
	@Value('${spring.metrics.export.default.delayMillis:5000}')
	long rateMillis = 5000L

	String getPrefix() {
		if (prefix.endsWith('.')) {
			return prefix
		}
		prefix + '.'
	}

	String getId(String defaultValue) {
		if (id) return id
		defaultValue
	}
}
