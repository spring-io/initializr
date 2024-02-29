/*
 * Copyright 2012-2024 the original author or authors.
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A service to be declared in a Docker Compose file.
 *
 * @author Moritz Halbritter
 * @author Stephane Nicoll
 * @author Eddú Meléndez
 */
public final class ComposeService {

	private final String name;

	private final String image;

	private final String imageTag;

	private final String imageWebsite;

	private final Map<String, String> environment;

	private final Set<Integer> ports;

	private final String command;

	private final Map<String, String> labels;

	private ComposeService(Builder builder) {
		this.name = builder.name;
		this.image = builder.image;
		this.imageTag = builder.imageTag;
		this.imageWebsite = builder.imageWebsite;
		this.environment = Collections.unmodifiableMap(new TreeMap<>(builder.environment));
		this.ports = Collections.unmodifiableSet(new TreeSet<>(builder.ports));
		this.command = builder.command;
		this.labels = Collections.unmodifiableMap(new TreeMap<>(builder.labels));
	}

	public String getName() {
		return this.name;
	}

	public String getImage() {
		return this.image;
	}

	public String getImageTag() {
		return this.imageTag;
	}

	public String getImageWebsite() {
		return this.imageWebsite;
	}

	public Map<String, String> getEnvironment() {
		return this.environment;
	}

	public Set<Integer> getPorts() {
		return this.ports;
	}

	public String getCommand() {
		return this.command;
	}

	public Map<String, String> getLabels() {
		return this.labels;
	}

	/**
	 * Builder for {@link ComposeService}.
	 */
	public static class Builder {

		private final String name;

		private String image;

		private String imageTag = "latest";

		private String imageWebsite;

		private final Map<String, String> environment = new TreeMap<>();

		private final Set<Integer> ports = new TreeSet<>();

		private String command;

		private final Map<String, String> labels = new TreeMap<>();

		protected Builder(String name) {
			this.name = name;
		}

		public Builder imageAndTag(String imageAndTag) {
			String[] split = imageAndTag.split(":", 2);
			String tag = (split.length == 1) ? "latest" : split[1];
			return image(split[0]).imageTag(tag);
		}

		public Builder image(String image) {
			this.image = image;
			return this;
		}

		public Builder imageTag(String imageTag) {
			this.imageTag = imageTag;
			return this;
		}

		public Builder imageWebsite(String imageWebsite) {
			this.imageWebsite = imageWebsite;
			return this;
		}

		public Builder environment(String key, String value) {
			this.environment.put(key, value);
			return this;
		}

		public Builder environment(Map<String, String> environment) {
			this.environment.putAll(environment);
			return this;
		}

		public Builder ports(Collection<Integer> ports) {
			this.ports.addAll(ports);
			return this;
		}

		public Builder ports(int... ports) {
			return ports(Arrays.stream(ports).boxed().toList());
		}

		public Builder command(String command) {
			this.command = command;
			return this;
		}

		public Builder label(String key, String value) {
			this.labels.put(key, value);
			return this;
		}

		public Builder labels(Map<String, String> label) {
			this.labels.putAll(label);
			return this;
		}

		/**
		 * Builds the {@link ComposeService} instance.
		 * @return the built instance
		 */
		public ComposeService build() {
			return new ComposeService(this);
		}

	}

}
