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

package io.spring.initializr.generator.spring.container.docker.compose;

import java.util.ArrayList;
import java.util.List;

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

	/**
	 * A Markdown table.
	 * <p>
	 * The formatted table is pretty-printed, all the columns are padded with spaces to
	 * have a consistent look.
	 *
	 * @author Moritz Halbritter
	 */
	static class MarkdownTable {

		private final List<String> headerCaptions;

		private final List<List<String>> rows;

		/**
		 * Creates a new table with the given header captions.
		 * @param headerCaptions the header captions
		 */
		MarkdownTable(String... headerCaptions) {
			this.headerCaptions = List.of(headerCaptions);
			this.rows = new ArrayList<>();
		}

		/**
		 * Adds a new row with the given cells.
		 * @param cells the cells to add
		 * @throws IllegalArgumentException if the cell size doesn't match the number of
		 * header captions
		 */
		void addRow(String... cells) {
			if (cells.length != this.headerCaptions.size()) {
				throw new IllegalArgumentException(
						"Expected %d cells, got %d".formatted(this.headerCaptions.size(), cells.length));
			}
			this.rows.add(List.of(cells));
		}

		/**
		 * Formats the whole table as Markdown.
		 * @return the table formatted as Markdown.
		 */
		String toMarkdown() {
			int[] columnMaxLengths = calculateMaxColumnLengths();
			StringBuilder result = new StringBuilder();
			writeHeader(result, columnMaxLengths);
			writeHeaderSeparator(result, columnMaxLengths);
			writeRows(result, columnMaxLengths);
			return result.toString();
		}

		private void writeHeader(StringBuilder result, int[] columnMaxLengths) {
			for (int i = 0; i < this.headerCaptions.size(); i++) {
				result.append((i > 0) ? " " : "| ")
					.append(pad(this.headerCaptions.get(i), columnMaxLengths[i]))
					.append(" |");
			}
			result.append(System.lineSeparator());
		}

		private void writeHeaderSeparator(StringBuilder result, int[] columnMaxLengths) {
			for (int i = 0; i < this.headerCaptions.size(); i++) {
				result.append((i > 0) ? " " : "| ").append("-".repeat(columnMaxLengths[i])).append(" |");
			}
			result.append(System.lineSeparator());
		}

		private void writeRows(StringBuilder result, int[] columnMaxLengths) {
			for (List<String> row : this.rows) {
				for (int i = 0; i < row.size(); i++) {
					result.append((i > 0) ? " " : "| ").append(pad(row.get(i), columnMaxLengths[i])).append(" |");
				}
				result.append(System.lineSeparator());
			}
		}

		private int[] calculateMaxColumnLengths() {
			int[] columnMaxLengths = new int[this.headerCaptions.size()];
			for (int i = 0; i < this.headerCaptions.size(); i++) {
				columnMaxLengths[i] = this.headerCaptions.get(i).length();
			}
			for (List<String> row : this.rows) {
				for (int i = 0; i < row.size(); i++) {
					String cell = row.get(i);
					if (cell.length() > columnMaxLengths[i]) {
						columnMaxLengths[i] = cell.length();
					}
				}
			}
			return columnMaxLengths;
		}

		private String pad(String input, int length) {
			return input + " ".repeat(length - input.length());
		}

	}

}
