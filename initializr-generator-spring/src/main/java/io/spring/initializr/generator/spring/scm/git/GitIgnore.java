/*
 * Copyright 2012-2019 the original author or authors.
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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;

import io.spring.initializr.generator.io.text.Section;

/**
 * Project's {@code .gitignore}. Contain a general section and pre-defined section for
 * popular IDEs. Empty sections are not rendered.
 *
 * @author Stephane Nicoll
 */
public class GitIgnore {

	private final GitIgnoreSection general = new GitIgnoreSection(null);

	private final GitIgnoreSection sts = new GitIgnoreSection("STS");

	private final GitIgnoreSection intellijIdea = new GitIgnoreSection("IntelliJ IDEA");

	private final GitIgnoreSection netBeans = new GitIgnoreSection("NetBeans");

	private final GitIgnoreSection vscode = new GitIgnoreSection("VS Code");

	public void write(PrintWriter writer) throws IOException {
		this.general.write(writer);
		this.sts.write(writer);
		this.intellijIdea.write(writer);
		this.netBeans.write(writer);
		this.vscode.write(writer);
	}

	public boolean isEmpty() {
		return this.general.getItems().isEmpty() && this.sts.getItems().isEmpty()
				&& this.intellijIdea.getItems().isEmpty()
				&& this.netBeans.getItems().isEmpty() && this.vscode.getItems().isEmpty();
	}

	public GitIgnoreSection getGeneral() {
		return this.general;
	}

	public GitIgnoreSection getSts() {
		return this.sts;
	}

	public GitIgnoreSection getIntellijIdea() {
		return this.intellijIdea;
	}

	public GitIgnoreSection getNetBeans() {
		return this.netBeans;
	}

	public GitIgnoreSection getVscode() {
		return this.vscode;
	}

	/**
	 * Representation of a section of a {@code .gitignore} file.
	 */
	public static class GitIgnoreSection implements Section {

		private final String name;

		private final LinkedList<String> items;

		public GitIgnoreSection(String name) {
			this.name = name;
			this.items = new LinkedList<>();
		}

		public void add(String... items) {
			this.items.addAll(Arrays.asList(items));
		}

		public LinkedList<String> getItems() {
			return this.items;
		}

		@Override
		public void write(PrintWriter writer) {
			if (!this.items.isEmpty()) {
				if (this.name != null) {
					writer.println();
					writer.println(String.format("### %s ###", this.name));
				}
				this.items.forEach(writer::println);
			}
		}

	}

}
