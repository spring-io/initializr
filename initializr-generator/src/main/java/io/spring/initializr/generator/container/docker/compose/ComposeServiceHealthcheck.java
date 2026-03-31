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

package io.spring.initializr.generator.container.docker.compose;

import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;

/**
 * A healthcheck configuration for a Docker Compose service.
 *
 * @author Eddú Meléndez
 * @author Moritz Halbritter
 */
public final class ComposeServiceHealthcheck {

	private final boolean disable;

	private final @Nullable String test;

	private final @Nullable String interval;

	private final @Nullable String timeout;

	private final @Nullable Integer retries;

	private final @Nullable String startPeriod;

	private final @Nullable String startInterval;

	private ComposeServiceHealthcheck(Builder builder) {
		this.disable = builder.disable;
		if (!this.disable) {
			Assert.hasText(builder.test, "'test' must not be empty");
		}
		this.test = builder.test;
		this.interval = builder.interval;
		this.timeout = builder.timeout;
		this.retries = builder.retries;
		this.startPeriod = builder.startPeriod;
		this.startInterval = builder.startInterval;
	}

	public boolean isDisable() {
		return this.disable;
	}

	public @Nullable String getTest() {
		return this.test;
	}

	public @Nullable String getInterval() {
		return this.interval;
	}

	public @Nullable String getTimeout() {
		return this.timeout;
	}

	public @Nullable Integer getRetries() {
		return this.retries;
	}

	public @Nullable String getStartPeriod() {
		return this.startPeriod;
	}

	public @Nullable String getStartInterval() {
		return this.startInterval;
	}

	/**
	 * Builder for {@link ComposeServiceHealthcheck}.
	 */
	public static class Builder {

		private boolean disable;

		private @Nullable String test;

		private @Nullable String interval;

		private @Nullable String timeout;

		private @Nullable Integer retries;

		private @Nullable String startPeriod;

		private @Nullable String startInterval;

		public Builder disable(boolean disable) {
			this.disable = disable;
			return this;
		}

		public Builder test(String test) {
			this.test = test;
			return this;
		}

		public Builder interval(String interval) {
			this.interval = interval;
			return this;
		}

		public Builder timeout(String timeout) {
			this.timeout = timeout;
			return this;
		}

		public Builder retries(int retries) {
			this.retries = retries;
			return this;
		}

		public Builder startPeriod(String startPeriod) {
			this.startPeriod = startPeriod;
			return this;
		}

		public Builder startInterval(String startInterval) {
			this.startInterval = startInterval;
			return this;
		}

		/**
		 * Builds the {@link ComposeServiceHealthcheck} instance.
		 * @return the built instance
		 */
		public ComposeServiceHealthcheck build() {
			return new ComposeServiceHealthcheck(this);
		}

	}

}
