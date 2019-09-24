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
package io.spring.initializr.generator.spring.docker;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;

import io.spring.initializr.generator.io.text.Section;

/**
 * Project's dockerfile intended to give additional references to the users. Contains a
 * getting from section, additional sections and a entrypoint section.
 *
 * @author Taekhyun Kim
 */
public class DockerFile {

	private final DockerFileSection from = new DockerFile.DockerFileSection("FROM");

	private final DockerFileSection add = new DockerFile.DockerFileSection("ADD");

	private final DockerFileSection entryPoint = new DockerFile.DockerFileSection("ENTRYPOINT");

	public void write(PrintWriter writer) throws IOException {
		this.from.write(writer);
		this.add.write(writer);
		this.entryPoint.write(writer);
	}

	public DockerFileSection getFrom() {
		return this.from;
	}

	public DockerFileSection getAdd() {
		return this.add;
	}

	public DockerFileSection getEntryPoint() {
		return this.entryPoint;
	}

	public static class DockerFileSection implements Section {

		private final String name;

		private final LinkedList<String> items;

		public DockerFileSection(String name) {
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
				writer.print(this.name);
				this.items.stream().forEach((item) -> {
					writer.print(" ");
					writer.print(item);
				});
			}
			writer.println();
			writer.println();
		}

	}

}
