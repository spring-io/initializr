/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.generator.buildsystem.maven;

import io.spring.initializr.generator.buildsystem.Build;

import java.util.function.Consumer;

/**
 * Maven-specific {@linkplain Build build configuration}.
 *
 * @author Andy Wilkinson
 */
public class MavenProfile{

	private final String id;

	private final MavenProfileActivation activation;

	protected MavenProfile(Builder builder) {
		this.id = builder.id;
		this.activation = (builder.activationBuilder == null) ? null : builder.activationBuilder.build();
	}

	public String getId() {
		return id;
	}

	public static class Builder {

		private final String id;

		private MavenProfileActivation.Builder activationBuilder;

		protected Builder(String id) {
			this.id = id;
		}

		public MavenProfile.Builder activation(Consumer<MavenProfileActivation.Builder> activation) {
			if (this.activationBuilder == null) {
				this.activationBuilder = new MavenProfileActivation.Builder();
			}
			activation.accept(this.activationBuilder);
			return this;
		}

		public MavenProfile build() {
			return new MavenProfile(this);
		}
	}
}
