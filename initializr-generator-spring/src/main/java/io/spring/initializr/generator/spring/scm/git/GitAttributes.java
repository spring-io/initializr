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
