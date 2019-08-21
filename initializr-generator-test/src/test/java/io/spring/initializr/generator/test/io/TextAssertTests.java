/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.generator.test.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.assertj.core.api.AssertProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link TextAssert}.
 *
 * @author Stephane Nicoll
 */
class TextAssertTests {

	@Test
	void sameContentAsWithNonReadableResource() {
		Resource resource = mock(Resource.class);
		given(resource.isReadable()).willReturn(false);
		given(resource.toString()).willReturn("project/does-not-exist");
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forContent("Hello")).hasSameContentAs(resource))
				.withMessageContaining("project/does-not-exist");
	}

	@Test
	void sameContentAsWithNonReliableResource() throws IOException {
		Resource resource = mock(Resource.class);
		given(resource.isReadable()).willReturn(true);
		given(resource.getInputStream()).willThrow(new IOException("Test exception"));
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forContent("Hello")).hasSameContentAs(resource))
				.withMessageContaining("Cannot read expected content");
	}

	@Test
	void sameContentAsWithMatchingResource() throws IOException {
		assertThat(forContent("Hello")).hasSameContentAs(createResource("Hello"));
	}

	@Test
	void sameContentAsWithMatchingResourceAndDifferentNewLinesInTarget() throws IOException {
		assertThat(forContent("Hello\nWorld")).hasSameContentAs(createResource("Hello\r\nWorld"));
	}

	@Test
	void sameContentAsWithMatchingResourceAndDifferentNewLinesInSource() throws IOException {
		assertThat(forContent("Hello\r\nWorld")).hasSameContentAs(createResource("Hello\nWorld"));
	}

	@Test
	void sameContentAsWithNonMatchingResource() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forContent("Test")).hasSameContentAs(createResource("Hello")))
				.withMessageContaining("Test").withMessageContaining("Hello");
	}

	@Test
	void sameContentAsWithFile(@TempDir Path dir) throws IOException {
		Path file = Files.createFile(dir.resolve("hello.txt"));
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file))) {
			writer.println("Test");
		}
		assertThat(forContent(file)).hasSameContentAs(createResource("Test"));
	}

	@Test
	void sameContentAsWithFileAndNonMatchingResource(@TempDir Path dir) throws IOException {
		Path file = Files.createFile(dir.resolve("hello.txt"));
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file))) {
			writer.println("Test");
		}
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forContent(file)).hasSameContentAs(createResource("Hello")))
				.withMessageContaining("Test").withMessageContaining("Hello").withMessageContaining(file.toString());
	}

	private AssertProvider<TextAssert> forContent(String content) {
		return () -> new TextAssert(content);
	}

	private AssertProvider<TextAssert> forContent(Path file) {
		return () -> new TextAssert(file);
	}

	private Resource createResource(String content) throws IOException {
		Resource resource = mock(Resource.class);
		given(resource.isReadable()).willReturn(true);
		given(resource.getInputStream()).willReturn(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
		return resource;
	}

}
