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
