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

/**
 * A config to be declared in a Docker Compose file.
 *
 * @author Moritz Halbritter
 */
public final class ComposeConfig {

	private final @Nullable String name;

	private final boolean external;

	private final @Nullable String file;

	private final @Nullable String content;

	private final @Nullable String environment;

	private ComposeConfig(Builder builder) {
		this.name = builder.name;
		this.external = builder.external;
		this.file = builder.file;
		this.content = builder.content;
		this.environment = builder.environment;
	}

	public @Nullable String getName() {
		return this.name;
	}

	public boolean isExternal() {
		return this.external;
	}

	public @Nullable String getFile() {
		return this.file;
	}

	public @Nullable String getContent() {
		return this.content;
	}

	public @Nullable String getEnvironment() {
		return this.environment;
	}

	/**
	 * Builder for {@link ComposeConfig}.
	 */
	public static final class Builder {

		private @Nullable String name;

		private final boolean external;

		private final @Nullable String file;

		private final @Nullable String content;

		private final @Nullable String environment;

		private Builder(boolean external, @Nullable String file, @Nullable String content,
				@Nullable String environment) {
			this.external = external;
			this.file = file;
			this.content = content;
			this.environment = environment;
		}

		public Builder name(@Nullable String name) {
			this.name = name;
			return this;
		}

		/**
		 * Builds the {@link ComposeConfig} instance.
		 * @return the built instance
		 */
		public ComposeConfig build() {
			return new ComposeConfig(this);
		}

		public static Builder forFile(String file) {
			return new Builder(false, file, null, null);
		}

		public static Builder forEnvironment(String environment) {
			return new Builder(false, null, null, environment);
		}

		public static Builder forContent(String content) {
			return new Builder(false, null, content, null);
		}

		public static Builder forExternal() {
			return new Builder(true, null, null, null);
		}

	}

}
