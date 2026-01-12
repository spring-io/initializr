/*
 * Copyright 2012 - present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.actuate.stat;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.lang.Contract;
import org.springframework.util.StringUtils;

/**
 * Statistics-related properties.
 *
 * @author Stephane Nicoll
 */
@ConfigurationProperties("initializr.stats")
public class StatsProperties {

	@NestedConfigurationProperty
	private final Elastic elastic = new Elastic();

	public Elastic getElastic() {
		return this.elastic;
	}

	/**
	 * Elasticsearch configuration.
	 */
	public static final class Elastic {

		/**
		 * Elastic service uri. Overrides username and password when UserInfo is set.
		 */
		private @Nullable String uri;

		/**
		 * Elastic service username.
		 */
		private @Nullable String username;

		/**
		 * Elastic service password.
		 */
		private @Nullable String password;

		/**
		 * Name of the index.
		 */
		private String indexName = "initializr";

		/**
		 * Number of attempts before giving up.
		 */
		private int maxAttempts = 3;

		public @Nullable String getUsername() {
			return this.username;
		}

		public void setUsername(@Nullable String username) {
			this.username = username;
		}

		public @Nullable String getPassword() {
			return this.password;
		}

		public void setPassword(@Nullable String password) {
			this.password = password;
		}

		public String getIndexName() {
			return this.indexName;
		}

		public void setIndexName(String indexName) {
			this.indexName = indexName;
		}

		public int getMaxAttempts() {
			return this.maxAttempts;
		}

		public void setMaxAttempts(int maxAttempts) {
			this.maxAttempts = maxAttempts;
		}

		public @Nullable String getUri() {
			return this.uri;
		}

		public void setUri(@Nullable String uri) {
			this.uri = cleanUri(uri);
		}

		@Contract("!null -> !null")
		private static @Nullable String cleanUri(@Nullable String contextPath) {
			if (StringUtils.hasText(contextPath) && contextPath.endsWith("/")) {
				return contextPath.substring(0, contextPath.length() - 1);
			}
			return contextPath;
		}

	}

}
