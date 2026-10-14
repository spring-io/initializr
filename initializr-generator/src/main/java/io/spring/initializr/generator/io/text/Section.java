/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.io.text;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Define the section of a document that knows how to write itself.
 *
 * @author Stephane Nicoll
 * @author Madhura Bhave
 */
public interface Section {

	/**
	 * Write the content of the section to the specified {@link PrintWriter writer}.
	 * @param writer the writer to use
	 * @throws IOException if writing the section failed
	 */
	void write(PrintWriter writer) throws IOException;

}
