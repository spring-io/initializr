/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.generator.spring.container.dockercompose;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * A Docker Compose service.
 *
 * @author Moritz Halbritter
 */
public final class DockerComposeService {

	private static final String INDENT = "  ";

	private final String name;

	private final String image;

	private final String imageTag;

	private final String imageWebsite;

	private final Map<String, String> environment;

	private final Set<Integer> ports;

	private DockerComposeService(Builder builder) {
		this.name = builder.name;
		this.image = builder.image;
		this.imageTag = builder.imageTag;
		this.imageWebsite = builder.imageWebsite;
		this.environment = builder.environment;
		this.ports = builder.ports;
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
		return Collections.unmodifiableMap(this.environment);
	}

	public Set<Integer> getPorts() {
		return Collections.unmodifiableSet(this.ports);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DockerComposeService service = (DockerComposeService) o;
		return Objects.equals(this.name, service.name) && Objects.equals(this.image, service.image)
				&& Objects.equals(this.imageTag, service.imageTag)
				&& Objects.equals(this.imageWebsite, service.imageWebsite)
				&& Objects.equals(this.environment, service.environment) && Objects.equals(this.ports, service.ports);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.image, this.imageTag, this.imageWebsite, this.environment, this.ports);
	}

	@Override
	public String toString() {
		return "DockerComposeService{" + "name='" + this.name + '\'' + ", image='" + this.image + '\'' + ", imageTag='"
				+ this.imageTag + '\'' + ", imageWebsite='" + this.imageWebsite + '\'' + ", environment="
				+ this.environment + ", ports=" + this.ports + '}';
	}

	void write(PrintWriter writer, int indentation) {
		int currentIndent = indentation;
		println(writer, this.name + ":", currentIndent);
		currentIndent++;
		println(writer, "image: '%s:%s'".formatted(this.image, this.imageTag), currentIndent);
		if (!this.environment.isEmpty()) {
			writeEnvironment(writer, currentIndent);
		}
		if (!this.ports.isEmpty()) {
			writePorts(writer, currentIndent);
		}
	}

	private void writePorts(PrintWriter writer, int currentIndent) {
		println(writer, "ports:", currentIndent);
		for (Integer port : this.ports) {
			println(writer, "- '%d'".formatted(port), currentIndent + 1);
		}
	}

	private void writeEnvironment(PrintWriter writer, int currentIndent) {
		println(writer, "environment:", currentIndent);
		for (Map.Entry<String, String> env : this.environment.entrySet()) {
			println(writer, "- '%s=%s'".formatted(env.getKey(), env.getValue()), currentIndent + 1);
		}
	}

	private void println(PrintWriter writer, String value, int indentation) {
		writer.write(INDENT.repeat(indentation));
		writer.println(value);
	}

	/**
	 * Initialize a new {@link Builder} with the given image. The name is automatically
	 * deduced.
	 * @param image the image
	 * @param tag the image tag
	 * @return a new builder
	 */
	public static Builder withImage(String image, String tag) {
		// See https://github.com/docker/compose/pull/1624
		String name = image.replaceAll("[^a-zA-Z0-9._\\-]", "_");
		return new Builder(name, image, tag);
	}

	/**
	 * Initialize a new {@link Builder} with the given image. The name is automatically
	 * deduced.
	 * @param imageAndTag the image and tag in the format {@code image:tag}
	 * @return a new builder
	 */
	public static Builder withImage(String imageAndTag) {
		String[] split = imageAndTag.split(":", 2);
		if (split.length == 1) {
			return withImage(split[0], "latest");
		}
		else {
			return withImage(split[0], split[1]);
		}
	}

	/**
	 * Initialize a {@link Builder} with the given service.
	 * @param service the service to initialize from
	 * @return a new builder
	 */
	public static Builder from(DockerComposeService service) {
		return new Builder(service.name, service.image, service.imageTag).imageWebsite(service.imageWebsite)
			.environment(service.environment)
			.ports(service.ports);
	}

	/**
	 * Builder for {@link DockerComposeService}.
	 */
	public static final class Builder {

		private String name;

		private String image;

		private String imageTag;

		private String imageWebsite;

		private final Map<String, String> environment = new TreeMap<>();

		private final Set<Integer> ports = new HashSet<>();

		private Builder(String name, String image, String imageTag) {
			this.name = name;
			this.image = image;
			this.imageTag = imageTag;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
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

		/**
		 * Builds the {@link DockerComposeService} instance.
		 * @return the built instance
		 */
		public DockerComposeService build() {
			return new DockerComposeService(this);
		}

	}

}
