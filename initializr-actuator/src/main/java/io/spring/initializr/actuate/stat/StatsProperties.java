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

package io.spring.initializr.actuate.stat

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.util.StringUtils

/**
 * Statistics-related properties.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@ConfigurationProperties("initializr.stats")
class StatsProperties {

	final Elastic elastic = new Elastic()

	static final class Elastic {

		/**
		 * Elastic service uri.
		 */
		String uri

		/**
		 * Elastic service username.
		 */
		String username

		/**
		 * Elastic service password
		 */
		String password

		/**
		 * Name of the index.
		 */
		String indexName = 'initializr'

		/**
		 * Name of the entity to use to publish stats.
		 */
		String entityName = 'request'

		/**
		 * Number of attempts before giving up.
		 */
		int maxAttempts = 3

		void setUri(String uri) {
			this.uri = cleanUri(uri)
		}

		URI getEntityUrl() {
			def string = "$uri/$indexName/$entityName"
			new URI(string)
		}

		private static String cleanUri(String contextPath) {
			if (StringUtils.hasText(contextPath) && contextPath.endsWith("/")) {
				return contextPath.substring(0, contextPath.length() - 1)
			}
			return contextPath
		}

	}

}
