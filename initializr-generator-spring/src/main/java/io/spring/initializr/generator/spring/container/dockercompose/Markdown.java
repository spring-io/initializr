/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.generator.spring.container.dockercompose;

/**
 * Helper class for Markdown.
 *
 * @author Moritz Halbritter
 */
final class Markdown {

	private Markdown() {
		// Static class
	}

	/**
	 * Formats the given string as code.
	 * @param code the input string
	 * @return string formatted as code
	 */
	static String code(String code) {
		return "`%s`".formatted(code);
	}

	/**
	 * Creates a Markdown link.
	 * @param text text of the link
	 * @param url url of the link
	 * @return the formatted link in Markdown
	 */
	static String link(String text, String url) {
		return "[%s](%s)".formatted(text, url);
	}

	/**
	 * Creates a Markdown table.
	 * @param headerCaptions captions of the header
	 * @return the Markdown table
	 */
	static MarkdownTable table(String... headerCaptions) {
		return new MarkdownTable(headerCaptions);
	}

}
