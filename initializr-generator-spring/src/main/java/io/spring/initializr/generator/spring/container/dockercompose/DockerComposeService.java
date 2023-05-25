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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * A Docker Compose service.
 *
 * @author Moritz Halbritter
 */
public class DockerComposeService {

	private static final String INDENT = "  ";

	private final String name;

	private final String image;

	private final String imageTag;

	private final String imageWebsite;

	private final Map<String, String> environment;

	private final List<Integer> ports;

	/**
	 * Creates a new docker compose service.
	 * @param name the name of the service
	 * @param image the image of the service
	 * @param imageTag the image tag of the service
	 * @param imageWebsite the image website of the service
	 * @param environment the environment of the service
	 * @param ports the ports of the service
	 */
	public DockerComposeService(String name, String image, String imageTag, String imageWebsite,
			Map<String, String> environment, List<Integer> ports) {
		this.name = name;
		this.image = image;
		this.imageTag = imageTag;
		this.imageWebsite = imageWebsite;
		// Sort the environment alphabetically
		this.environment = Collections.unmodifiableMap(new TreeMap<>(environment));
		this.ports = ports;
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

}
