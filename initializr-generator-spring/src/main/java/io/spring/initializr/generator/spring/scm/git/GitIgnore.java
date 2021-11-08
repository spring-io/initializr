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

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Stream;

import io.spring.initializr.generator.io.text.Section;

/**
 * Project's {@code .gitignore}. Contain a general section and pre-defined section for
 * popular IDEs. Empty sections are not rendered.
 *
 * @author Stephane Nicoll
 * @author Yves Galvão
 */
public class GitIgnore {

	private final Map<GitEnum, GitIgnoreSection> ignoreSections;

	public GitIgnore() {
		this.ignoreSections = initializeGitIgnoreSections();
	}

	private Map<GitEnum, GitIgnoreSection> initializeGitIgnoreSections() {
		Map<GitEnum, GitIgnoreSection> ignoreSections = new HashMap<>();
		Stream.of(GitEnum.values()).forEach((gitEnum) -> {
			GitIgnoreSection gitIgnoreSection = new GitIgnoreSection(gitEnum.getName());
			gitIgnoreSection.add(gitEnum.getIgnores());
			ignoreSections.put(gitEnum, gitIgnoreSection);
		});

		return ignoreSections;
	}

	public void write(PrintWriter writer) {
		this.ignoreSections.values().stream().forEach((it) -> it.write(writer));
	}

	public boolean isEmpty() {
		return this.ignoreSections.values().stream().allMatch((it) -> it.getItems().isEmpty());
	}

	public GitIgnoreSection getGitIgnoreSection(GitEnum gitEnum) {
		return this.ignoreSections.get(gitEnum);
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
			if (items != null) {
				this.items.addAll(Arrays.asList(items));
			}
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
