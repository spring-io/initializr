/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.test.io;

import java.nio.file.Path;

/**
 * Assertions for text content.
 *
 * @author Stephane Nicoll
 */
public class TextAssert extends AbstractTextAssert<TextAssert> {

	public TextAssert(String actual) {
		super(actual, TextAssert.class);
	}

	public TextAssert(Path textFile) {
		super(textFile, TextAssert.class);
	}

}
