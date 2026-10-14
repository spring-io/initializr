/*
 * Copyright 2012 - present the original author or authors.
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

	private static final SimpleIndentStrategy SPACE_STRATEGY = new SimpleIndentStrategy("    ");

	private static final SimpleIndentStrategy TAB_STRATEGY = new SimpleIndentStrategy("\t");

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
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory.create(SPACE_STRATEGY, (factory) -> {
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
