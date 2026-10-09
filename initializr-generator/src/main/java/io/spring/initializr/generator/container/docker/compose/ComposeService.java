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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;

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

	private final @Nullable String imageWebsite;

	private final Map<String, String> environment;

	private final Set<PortMapping> portMappings;

	private final @Nullable String command;

	private final Map<String, String> labels;

	private final Set<ComposeServiceConfig> configs;

	private final @Nullable ComposeServiceHealthcheck healthcheck;

	private ComposeService(Builder builder) {
		this.name = builder.name;
		Assert.state(builder.image != null, "'builder.image' must not be null");
		this.image = builder.image;
		this.imageTag = builder.imageTag;
		this.imageWebsite = builder.imageWebsite;
		this.environment = Collections.unmodifiableMap(new TreeMap<>(builder.environment));
		this.portMappings = Collections.unmodifiableSet(new TreeSet<>(builder.portMappings));
		this.command = builder.command;
		this.labels = Collections.unmodifiableMap(new TreeMap<>(builder.labels));
		this.configs = Collections.unmodifiableSet(new TreeSet<>(builder.configs));
		this.healthcheck = builder.healthcheck;
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

	public @Nullable String getImageWebsite() {
		return this.imageWebsite;
	}

	public Map<String, String> getEnvironment() {
		return this.environment;
	}

	/**
	 * Returns the container ports.
	 * @return the ports
	 * @deprecated in favor of {@link #getPortMappings()}
	 */
	@Deprecated(forRemoval = true)
	public Set<Integer> getPorts() {
		return this.portMappings.stream()
			.map(PortMapping::getContainerPort)
			.collect(Collectors.toCollection(TreeSet::new));
	}

	public Set<PortMapping> getPortMappings() {
		return this.portMappings;
	}

	public @Nullable String getCommand() {
		return this.command;
	}

	public Map<String, String> getLabels() {
		return this.labels;
	}

	public Set<ComposeServiceConfig> getConfigs() {
		return this.configs;
	}

	public @Nullable ComposeServiceHealthcheck getHealthcheck() {
		return this.healthcheck;
	}

	/**
	 * Builder for {@link ComposeService}.
	 */
	public static class Builder {

		private final String name;

		private @Nullable String image;

		private String imageTag = "latest";

		private @Nullable String imageWebsite;

		private final Map<String, String> environment = new TreeMap<>();

		private final Set<PortMapping> portMappings = new TreeSet<>();

		private @Nullable String command;

		private final Map<String, String> labels = new TreeMap<>();

		private final Set<ComposeServiceConfig> configs = new TreeSet<>();

		private @Nullable ComposeServiceHealthcheck healthcheck;

		protected Builder(String name) {
			this.name = name;
		}

		/**
		 * Set the image and tag. Uses {@code latest} if no tag is given.
		 * @param imageAndTag the image and tag, for example {@code redis:7}
		 * @return this for method chaining
		 */
		public Builder imageAndTag(String imageAndTag) {
			String[] split = imageAndTag.split(":", 2);
			String tag = (split.length == 1) ? "latest" : split[1];
			return image(split[0]).imageTag(tag);
		}

		/**
		 * Set the image.
		 * @param image the image
		 * @return this for method chaining
		 */
		public Builder image(@Nullable String image) {
			this.image = image;
			return this;
		}

		/**
		 * Set the image tag.
		 * @param imageTag the image tag
		 * @return this for method chaining
		 */
		public Builder imageTag(String imageTag) {
			this.imageTag = imageTag;
			return this;
		}

		/**
		 * Set the website of the image.
		 * @param imageWebsite the website of the image
		 * @return this for method chaining
		 */
		public Builder imageWebsite(@Nullable String imageWebsite) {
			this.imageWebsite = imageWebsite;
			return this;
		}

		/**
		 * Add an environment variable.
		 * @param key the key
		 * @param value the value
		 * @return this for method chaining
		 */
		public Builder environment(String key, String value) {
			this.environment.put(key, value);
			return this;
		}

		/**
		 * Add environment variables.
		 * @param environment the environment variables
		 * @return this for method chaining
		 */
		public Builder environment(Map<String, String> environment) {
			this.environment.putAll(environment);
			return this;
		}

		/**
		 * Add ports mapped to random host ports.
		 * @param ports the container ports
		 * @return this for method chaining
		 */
		public Builder ports(Collection<Integer> ports) {
			ports.forEach((port) -> this.portMappings.add(PortMapping.random(port)));
			return this;
		}

		/**
		 * Add ports mapped to random host ports.
		 * @param ports the container ports
		 * @return this for method chaining
		 */
		public Builder ports(int... ports) {
			return ports(Arrays.stream(ports).boxed().toList());
		}

		/**
		 * Add a port mapped to a random host port.
		 * @param containerPort the container port
		 * @return this for method chaining
		 */
		public Builder portMapping(int containerPort) {
			this.portMappings.add(PortMapping.random(containerPort));
			return this;
		}

		/**
		 * Add a port mapping.
		 * @param hostPort the host port
		 * @param containerPort the container port
		 * @return this for method chaining
		 */
		public Builder portMapping(int hostPort, int containerPort) {
			this.portMappings.add(PortMapping.fixed(hostPort, containerPort));
			return this;
		}

		/**
		 * Add port mappings.
		 * @param portMappings the port mappings
		 * @return this for method chaining
		 */
		public Builder portMappings(Collection<PortMapping> portMappings) {
			this.portMappings.addAll(portMappings);
			return this;
		}

		/**
		 * Set the command.
		 * @param command the command
		 * @return this for method chaining
		 */
		public Builder command(@Nullable String command) {
			this.command = command;
			return this;
		}

		/**
		 * Add a label.
		 * @param key the key
		 * @param value the value
		 * @return this for method chaining
		 */
		public Builder label(String key, String value) {
			this.labels.put(key, value);
			return this;
		}

		/**
		 * Add labels.
		 * @param label the labels
		 * @return this for method chaining
		 */
		public Builder labels(Map<String, String> label) {
			this.labels.putAll(label);
			return this;
		}

		/**
		 * Add a config.
		 * @param config the config
		 * @return this for method chaining
		 */
		public Builder config(ComposeServiceConfig config) {
			this.configs.add(config);
			return this;
		}

		/**
		 * Set the healthcheck.
		 * @param healthcheck the healthcheck
		 * @return this for method chaining
		 */
		public Builder healthcheck(ComposeServiceHealthcheck healthcheck) {
			this.healthcheck = healthcheck;
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
