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
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Moritz Halbritter
 */
class DockerComposeServiceTests {

	@Test
	void shouldWriteToPrinter() {
		StringWriter stringWriter = new StringWriter();
		DockerComposeService service = new DockerComposeService("elasticsearch", "elasticsearch", "8.6.1",
				"https://www.docker.elastic.co/r/elasticsearch",
				Map.of("ELASTIC_PASSWORD", "secret", "discovery.type", "single-node"), List.of(9200, 9300));
		service.write(new PrintWriter(stringWriter), 0);
		String content = stringWriter.toString();
		assertThat(content).isEqualToIgnoringNewLines("""
				elasticsearch:
				  image: 'elasticsearch:8.6.1'
				  environment:
				    - 'ELASTIC_PASSWORD=secret'
				    - 'discovery.type=single-node'
				  ports:
				    - '9200'
				    - '9300'
				""");
	}

}
