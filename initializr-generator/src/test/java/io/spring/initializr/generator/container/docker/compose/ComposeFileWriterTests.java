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

import java.io.StringWriter;
import java.util.function.Consumer;

import io.spring.initializr.generator.container.docker.compose.ComposeService.Builder;
import io.spring.initializr.generator.io.IndentingWriter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ComposeFile}.
 *
 * @author Moritz Halbritter
 * @author Stephane Nicoll
 * @author Eddú Meléndez
 */
class ComposeFileWriterTests {

	@Test
	void writeBasicServices() {
		ComposeFile file = new ComposeFile();
		file.services().add("first", withSuffix(1));
		file.services().add("second", withSuffix(2));
		assertThat(write(file)).isEqualToIgnoringNewLines("""
				services:
					first:
						image: 'image-1:image-tag-1'
					second:
						image: 'image-2:image-tag-2'
				""");
	}

	@Test
	void writeDetailedService() {
		ComposeFile file = new ComposeFile();
		file.services()
			.add("elasticsearch",
					(builder) -> builder.image("elasticsearch")
						.imageTag("8.6.1")
						.imageWebsite("https://www.docker.elastic.co/r/elasticsearch")
						.environment("ELASTIC_PASSWORD", "secret")
						.environment("discovery.type", "single-node")
						.ports(9200, 9300)
						.command("bin/run thing")
						.label("foo", "bar"));
		assertThat(write(file)).isEqualToIgnoringNewLines("""
				services:
					elasticsearch:
						image: 'elasticsearch:8.6.1'
						environment:
							- 'ELASTIC_PASSWORD=secret'
							- 'discovery.type=single-node'
						labels:
							- "foo=bar"
						ports:
							- '9200'
							- '9300'
						command: 'bin/run thing'
				""");
	}

	@Test
	void writeServiceWithFixedPort() {
		ComposeFile file = new ComposeFile();
		file.services()
			.add("grafana", (builder) -> builder.imageAndTag("grafana/grafana:latest").portMapping(3000, 3000));
		assertThat(write(file)).isEqualToIgnoringNewLines("""
				services:
					grafana:
						image: 'grafana/grafana:latest'
						ports:
							- '3000:3000'
				""");
	}

	@Test
	void writeServiceWithMixedPortMappings() {
		ComposeFile file = new ComposeFile();
		file.services()
			.add("grafana",
					(builder) -> builder.imageAndTag("grafana/grafana:latest")
						.portMapping(3000, 3000)
						.portMapping(9090));
		assertThat(write(file)).isEqualToIgnoringNewLines("""
				services:
					grafana:
						image: 'grafana/grafana:latest'
						ports:
							- '3000:3000'
							- '9090'
				""");
	}

	@Test
	void servicesAreOrderedByName() {
		ComposeFile file = new ComposeFile();
		file.services().add("b", withSuffix(2));
		file.services().add("a", withSuffix(1));
		assertThat(write(file)).isEqualToIgnoringNewLines("""
				services:
					a:
						image: 'image-1:image-tag-1'
					b:
						image: 'image-2:image-tag-2'
				""");
	}

	@Test
	void servicesWithNoEntriesWrittenAsEmptyMap() {
		ComposeFile file = new ComposeFile();
		assertThat(write(file)).isEqualToIgnoringNewLines("""
				services: {}
				""");
	}

	@Test
	void writeConfigs() {
		ComposeFile file = new ComposeFile();
		file.configs().add("config-0", ComposeConfig.Builder.forExternal().build());
		file.configs().add("config-1", ComposeConfig.Builder.forExternal().name("external").build());
		file.configs().add("config-2", ComposeConfig.Builder.forFile("./config").name("file").build());
		file.configs()
			.add("config-3", ComposeConfig.Builder.forEnvironment("CONFIG_CONTENT").name("environment").build());
		file.configs()
			.add("config-4", ComposeConfig.Builder.forContent("single-line").name("single-line-content").build());
		file.configs()
			.add("config-5", ComposeConfig.Builder.forContent("multi\nline").name("multi-line-content").build());
		assertThat(write(file)).isEqualToIgnoringNewLines("""
				services: {}

				configs:
					config-0:
						external: true
					config-1:
						name: "external"
						external: true
					config-2:
						name: "file"
						file: "./config"
					config-3:
						name: "environment"
						environment: "CONFIG_CONTENT"
					config-4:
						name: "single-line-content"
						content: "single-line"
					config-5:
						name: "multi-line-content"
						content: |
							multi
							line
				""");
	}

	@Test
	void writeServiceConfigs() {
		ComposeFile file = new ComposeFile();
		file.configs().add("config-1", ComposeConfig.Builder.forContent("config-content-1").build());
		file.configs().add("config-2", ComposeConfig.Builder.forContent("config-content-2").build());
		file.services()
			.add("service-short",
					(service) -> service.image("service1").config(ComposeServiceConfig.ofShort("config-1")));
		file.services()
			.add("service-long", (service) -> service.image("service2")
				.config(ComposeServiceConfig.ofLong("config-2", "/config", 0440, 1000, 2000)));
		assertThat(write(file)).isEqualToIgnoringNewLines("""
				services:
					service-long:
						image: 'service2:latest'
						configs:
							- source: "config-2"
							  target: "/config"
							  uid: "1000"
							  gid: "2000"
							  mode: 0440
					service-short:
						image: 'service1:latest'
						configs:
							- "config-1"

				configs:
					config-1:
						content: "config-content-1"
					config-2:
						content: "config-content-2"
				""");
	}

	@Test
	void writeServiceWithFullHealthcheck() {
		ComposeFile file = new ComposeFile();
		ComposeServiceHealthcheck healthcheck = new ComposeServiceHealthcheck.Builder().test("curl -f http://localhost")
			.interval("1m30s")
			.timeout("10s")
			.retries(3)
			.startPeriod("40s")
			.startInterval("5s")
			.build();
		file.services().add("web", (builder) -> builder.imageAndTag("nginx:latest").healthcheck(healthcheck));
		assertThat(write(file)).isEqualToIgnoringNewLines("""
				services:
					web:
						image: 'nginx:latest'
						healthcheck:
							test: 'curl -f http://localhost'
							interval: '1m30s'
							timeout: '10s'
							retries: 3
							start_period: '40s'
							start_interval: '5s'
				""");
	}

	@Test
	void writeServiceWithPartialHealthcheck() {
		ComposeFile file = new ComposeFile();
		ComposeServiceHealthcheck healthcheck = new ComposeServiceHealthcheck.Builder().test("curl -f http://localhost")
			.build();
		file.services().add("web", (builder) -> builder.imageAndTag("nginx:latest").healthcheck(healthcheck));
		assertThat(write(file)).isEqualToIgnoringNewLines("""
				services:
					web:
						image: 'nginx:latest'
						healthcheck:
							test: 'curl -f http://localhost'
				""");
	}

	@Test
	void writeServiceWithHealthcheckTestContainingSingleQuote() {
		ComposeFile file = new ComposeFile();
		ComposeServiceHealthcheck healthcheck = new ComposeServiceHealthcheck.Builder()
			.test("[ \"$(redis-cli ping)\" = 'PONG' ]")
			.build();
		file.services().add("cache", (builder) -> builder.imageAndTag("redis:latest").healthcheck(healthcheck));
		assertThat(write(file)).isEqualToIgnoringNewLines("""
				services:
					cache:
						image: 'redis:latest'
						healthcheck:
							test: '[ "$(redis-cli ping)" = ''PONG'' ]'
				""");
	}

	@Test
	void writeServiceWithDisabledHealthcheck() {
		ComposeFile file = new ComposeFile();
		ComposeServiceHealthcheck healthcheck = new ComposeServiceHealthcheck.Builder().disable(true).build();
		file.services().add("web", (builder) -> builder.imageAndTag("nginx:latest").healthcheck(healthcheck));
		assertThat(write(file)).isEqualToIgnoringNewLines("""
				services:
					web:
						image: 'nginx:latest'
						healthcheck:
							disable: true
				""");
	}

	private Consumer<Builder> withSuffix(int suffix) {
		return (builder) -> builder.image("image-" + suffix).imageTag("image-tag-" + suffix);
	}

	private String write(ComposeFile file) {
		StringWriter out = new StringWriter();
		IndentingWriter writer = new IndentingWriter(out, "\t"::repeat);
		new ComposeFileWriter().writeTo(writer, file);
		return out.toString();
	}

}
