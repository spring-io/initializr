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
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DockerComposeFile}.
 *
 * @author Moritz Halbritter
 */
class DockerComposeFileTests {

	@Test
	void write() {
		DockerComposeFile file = new DockerComposeFile();
		file.addService(DockerComposeServiceFixtures.service(1));
		file.addService(DockerComposeServiceFixtures.service(2));
		StringWriter writer = new StringWriter();
		file.write(new PrintWriter(writer));
		assertThat(writer.toString()).isEqualToIgnoringNewLines("""
				services:
				  service-1:
				    image: 'image-1:image-tag-1'
				  service-2:
				    image: 'image-2:image-tag-2'
				""");
	}

	@Test
	void servicesAreOrderedByName() {
		DockerComposeFile file = new DockerComposeFile();
		file.addService(DockerComposeServiceFixtures.service(2));
		file.addService(DockerComposeServiceFixtures.service(1));
		assertThat(file.getServices()).containsExactly(DockerComposeServiceFixtures.service(1),
				DockerComposeServiceFixtures.service(2));
	}

}
