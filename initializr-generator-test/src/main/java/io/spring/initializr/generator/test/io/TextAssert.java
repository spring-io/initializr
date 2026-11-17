/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
