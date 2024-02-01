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

package io.spring.initializr.generator.spring.scm.git;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link GitIgnore}.
 *
 * @author Moritz Halbritter
 */
class GitIgnoreTests {

	private GitIgnore gitIgnore;

	@BeforeEach
	void setUp() {
		this.gitIgnore = new GitIgnore();
	}

	@Test
	void shouldAddSection() throws IOException {
		this.gitIgnore.addSection(new GitIgnore.GitIgnoreSection("demo"));
		GitIgnore.GitIgnoreSection section = this.gitIgnore.getSection("demo");
		assertThat(section).isNotNull();
		section.add("file.txt");
		String content = write(this.gitIgnore);
		assertThat(content).containsIgnoringNewLines("### demo ###\nfile.txt");
	}

	@Test
	void shouldFailIfSectionAlreadyExists() {
		this.gitIgnore.addSection(new GitIgnore.GitIgnoreSection("test"));
		assertThatIllegalStateException()
			.isThrownBy(() -> this.gitIgnore.addSection(new GitIgnore.GitIgnoreSection("test")))
			.withMessageContaining("Section with name 'test' already exists");
	}

	@Test
	void addSectionIfAbsentShouldNotFail() {
		GitIgnore.GitIgnoreSection section = this.gitIgnore.addSectionIfAbsent("test");
		assertThat(section).isNotNull();
		GitIgnore.GitIgnoreSection section2 = this.gitIgnore.addSectionIfAbsent("test");
		assertThat(section2).isNotNull();
		assertThat(section2).isSameAs(section);
	}

	private String write(GitIgnore gitIgnore) throws IOException {
		StringWriter stringWriter = new StringWriter();
		try (PrintWriter writer = new PrintWriter(stringWriter)) {
			gitIgnore.write(writer);
		}
		return stringWriter.toString();
	}

}
