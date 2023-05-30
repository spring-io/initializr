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
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link DockerComposeService}.
 *
 * @author Moritz Halbritter
 */
class DockerComposeServiceTests {

	@Test
	void shouldWriteToPrinter() {
		StringWriter stringWriter = new StringWriter();
		DockerComposeService service = DockerComposeService.withImage("elasticsearch:8.6.1")
			.imageWebsite("https://www.docker.elastic.co/r/elasticsearch")
			.environment("ELASTIC_PASSWORD", "secret")
			.environment("discovery.type", "single-node")
			.ports(9200, 9300)
			.build();
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

	@Test
	void nameIsDeduced() {
		DockerComposeService service = DockerComposeService.withImage("elasticsearch:8.6.1").build();
		assertThat(service.getName()).isEqualTo("elasticsearch");
	}

	@Test
	void tagIsSetToLatestIfNotGiven() {
		DockerComposeService service = DockerComposeService.withImage("redis").build();
		assertThat(service.getImage()).isEqualTo("redis");
		assertThat(service.getImageTag()).isEqualTo("latest");
	}

	@Test
	void removesIllegalCharsFromDecudedName() {
		DockerComposeService service = DockerComposeService.withImage("SOME._-name<>;|\uD83C\uDF31").build();
		assertThat(service.getName()).isEqualTo("SOME._-name_____");
	}

	@Test
	void portsAreSorted() {
		DockerComposeService service = DockerComposeService.withImage("redis").ports(5, 3, 4, 2, 1).build();
		assertThat(service.getPorts()).containsExactly(1, 2, 3, 4, 5);
	}

	@Test
	void environmentIsSorted() {
		DockerComposeService service = DockerComposeService.withImage("redis")
			.environment("z", "zz")
			.environment("a", "aa")
			.build();
		assertThat(service.getEnvironment()).containsExactly(entry("a", "aa"), entry("z", "zz"));
	}

	@Test
	void builderFrom() {
		DockerComposeService service = DockerComposeService.withImage("elasticsearch", "8.6.1")
			.imageWebsite("https://hub.docker.com/_/redis")
			.environment(Map.of("some", "value"))
			.ports(6379)
			.build();
		DockerComposeService service2 = DockerComposeService.from(service).build();
		assertThat(service).isEqualTo(service2);
	}

	@Test
	void equalsAndHashcode() {
		DockerComposeService service1 = DockerComposeService.withImage("redis").build();
		DockerComposeService service2 = DockerComposeService.withImage("elasticsearch:8.6.1").build();
		DockerComposeService service3 = DockerComposeService.withImage("redis").build();
		assertThat(service1).isEqualTo(service3);
		assertThat(service1).hasSameHashCodeAs(service3);
		assertThat(service3).isEqualTo(service1);
		assertThat(service3).hasSameHashCodeAs(service1);
		assertThat(service1).isNotEqualTo(service2);
		assertThat(service2).isNotEqualTo(service1);
	}

}
