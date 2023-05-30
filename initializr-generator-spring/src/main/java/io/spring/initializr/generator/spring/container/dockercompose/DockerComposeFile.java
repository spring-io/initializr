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
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * A Docker Compose file.
 *
 * @author Moritz Halbritter
 */
public class DockerComposeFile {

	// TreeMap to sort services by name
	private final Map<String, DockerComposeService> services = new TreeMap<>();

	/**
	 * Adds a {@link DockerComposeService service} to the file. If a service with the same
	 * {@link DockerComposeService#getName() name} already exists, it is replaced.
	 * @param service the service to add
	 * @return {@code this}
	 */
	public DockerComposeFile addService(DockerComposeService service) {
		this.services.put(service.getName(), service);
		return this;
	}

	/**
	 * Returns the service with the given name.
	 * @param name the name
	 * @return the service or {@code null} if no service with the given name exists
	 */
	public DockerComposeService getService(String name) {
		return this.services.get(name);
	}

	/**
	 * Returns all services.
	 * @return all services
	 */
	public Collection<DockerComposeService> getServices() {
		return Collections.unmodifiableCollection(this.services.values());
	}

	void write(PrintWriter writer) {
		writer.println("services:");
		for (DockerComposeService service : this.services.values()) {
			service.write(writer, 1);
		}
	}

}
