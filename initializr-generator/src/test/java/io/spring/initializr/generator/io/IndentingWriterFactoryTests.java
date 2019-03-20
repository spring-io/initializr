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

package io.spring.initializr.generator.io;

import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link IndentingWriterFactory}.
 *
 * @author Stephane Nicoll
 */
class IndentingWriterFactoryTests {

	private static final SimpleIndentStrategy SPACE_STRATEGY = new SimpleIndentStrategy(
			"    ");

	private static final SimpleIndentStrategy TAB_STRATEGY = new SimpleIndentStrategy(
			"\t");

	private final StringWriter out = new StringWriter();

	@Test
	void createWithSingleIndentStrategy() {
		IndentingWriter writer = IndentingWriterFactory.create((SPACE_STRATEGY))
				.createIndentingWriter("test", this.out);
		assertThat(writer).hasFieldOrPropertyWithValue("indentStrategy", SPACE_STRATEGY);
	}

	@Test
	void createWithSpecializedIndentStrategy() {
		SimpleIndentStrategy twoSpacesStrategy = new SimpleIndentStrategy("  ");
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory
				.create(SPACE_STRATEGY, (factory) -> {
					factory.indentingStrategy("java", TAB_STRATEGY);
					factory.indentingStrategy("pom", twoSpacesStrategy);
				});
		assertThat(indentingWriterFactory.createIndentingWriter("java", this.out))
				.hasFieldOrPropertyWithValue("indentStrategy", TAB_STRATEGY);
		assertThat(indentingWriterFactory.createIndentingWriter("pom", this.out))
				.hasFieldOrPropertyWithValue("indentStrategy", twoSpacesStrategy);
		assertThat(indentingWriterFactory.createIndentingWriter("c", this.out))
				.hasFieldOrPropertyWithValue("indentStrategy", SPACE_STRATEGY);
	}

}
