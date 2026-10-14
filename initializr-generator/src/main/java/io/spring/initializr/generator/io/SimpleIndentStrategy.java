/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.io;

import java.util.function.Function;

import org.springframework.util.Assert;

/**
 * A simple indenting strategy that uses a configurable {@code indent} value.
 *
 * @author Stephane Nicoll
 */
public class SimpleIndentStrategy implements Function<Integer, String> {

	private final String indent;

	/**
	 * Create a new instance with the indent style to apply.
	 * @param indent the indent to apply for each indent level
	 */
	public SimpleIndentStrategy(String indent) {
		Assert.notNull(indent, "Indent must be provided");
		this.indent = indent;
	}

	@Override
	public String apply(Integer level) {
		if (level < 0) {
			throw new IllegalArgumentException("Indent level must not be negative, got" + level);
		}
		return String.valueOf(this.indent).repeat(level);
	}

}
