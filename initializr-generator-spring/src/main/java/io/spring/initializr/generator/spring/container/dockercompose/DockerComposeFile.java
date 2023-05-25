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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A Docker Compose file.
 *
 * @author Moritz Halbritter
 */
public class DockerComposeFile {

	private final List<DockerComposeService> services = new ArrayList<>();

	/**
	 * Adds a {@link DockerComposeService service} to the file.
	 * @param service the service to add
	 * @return {@code this}
	 */
	public DockerComposeFile addService(DockerComposeService service) {
		this.services.add(service);
		return this;
	}

	/**
	 * Returns the services contained in the file.
	 * @return the services contained in the file
	 */
	public List<DockerComposeService> getServices() {
		return Collections.unmodifiableList(this.services);
	}

	/**
	 * Returns whether this file is empty.
	 * @return whether this file is empty
	 */
	public boolean isEmpty() {
		return this.services.isEmpty();
	}

	void write(PrintWriter writer) {
		writer.println("services:");
		this.services.stream()
			.sorted(Comparator.comparing(DockerComposeService::getName))
			.forEach((service) -> service.write(writer, 1));
	}

}
