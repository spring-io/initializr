/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

import java.io.IOException;
import java.io.Writer;

/**
 * Strategy that can be implemented by a {@link BuildSystem} that can generate a proper
 * build representation for the project in a single text asset.
 *
 * @author Stephane Nicoll
 */
public interface BuildWriter {

	/**
	 * Write the {@link Build} to the specified {@link Writer}.
	 * @param out the writer to use
	 * @throws IOException if writing the build to {@code out} failed
	 */
	void writeBuild(Writer out) throws IOException;

}
