/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.scm.git;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Project's {@code .gitattributes}.
 *
 * @author Moritz Halbritter
 */
public class GitAttributes {

	private final List<Line> lines = new ArrayList<>();

	/**
	 * Adds a new pattern with attributes.
	 * @param pattern the pattern
	 * @param attribute the first attribute
	 * @param remainingAttributes the remaining attributes
	 */
	public void add(String pattern, String attribute, String... remainingAttributes) {
		List<String> attributes = new ArrayList<>();
		attributes.add(attribute);
		attributes.addAll(Arrays.asList(remainingAttributes));
		this.lines.add(new Line(pattern, attributes));
	}

	void write(PrintWriter writer) {
		for (Line line : this.lines) {
			line.write(writer);
		}
	}

	boolean isEmpty() {
		return this.lines.isEmpty();
	}

	private record Line(String pattern, List<String> attributes) {
		void write(PrintWriter writer) {
			writer.print(this.pattern);
			writer.print(' ');
			writer.print(String.join(" ", this.attributes));
			writer.println();
		}
	}

}
