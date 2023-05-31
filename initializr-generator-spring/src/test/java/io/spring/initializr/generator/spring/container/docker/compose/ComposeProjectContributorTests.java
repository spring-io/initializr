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

package io.spring.initializr.generator.spring.container.docker.compose;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;

import io.spring.initializr.generator.container.docker.compose.ComposeFile;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ComposeProjectContributor}.
 *
 * @author Moritz Halbritter
 * @author Stephane Nicoll
 */
class ComposeProjectContributorTests {

	@Test
	void composeFileIsContributedInProjectStructure(@TempDir Path projectDir) throws IOException {
		ComposeFile compose = new ComposeFile();
		compose.services().add("test", (service) -> service.image("my-image:1.2.3"));
		new ComposeProjectContributor(compose, IndentingWriterFactory.withDefaultSettings()).contribute(projectDir);
		Path composeFile = projectDir.resolve("compose.yaml");
		assertThat(composeFile).isRegularFile();
	}

	@Test
	void composeFileIsContributedUsingYamlContentId() throws IOException {
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory.create(new SimpleIndentStrategy("    "),
				(factory) -> factory.indentingStrategy("yaml", new SimpleIndentStrategy("\t")));
		ComposeFile composeFile = new ComposeFile();
		composeFile.services()
			.add("test", (service) -> service.imageAndTag("image:1.3.3").environment("a", "aa").environment("b", "bb"));
		assertThat(generateComposeFile(composeFile, indentingWriterFactory)).isEqualToIgnoringNewLines("""
				services:
					test:
						image: 'image:1.3.3'
						environment:
							- 'a=aa'
							- 'b=bb'
				""");

	}

	private String generateComposeFile(ComposeFile composeFile, IndentingWriterFactory indentingWriterFactory)
			throws IOException {
		StringWriter writer = new StringWriter();
		new ComposeProjectContributor(composeFile, indentingWriterFactory).writeComposeFile(writer);
		return writer.toString();
	}

}
