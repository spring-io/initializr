/*
 * Copyright 2012-2022 the original author or authors.
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

package io.spring.initializr.generator.buildsystem.gradle;

import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Common tests for {@link GradleBuildWriter} implementations.
 *
 * @author Stephane Nicoll
 */
public abstract class GradleBuildWriterTests {

	@Test
	void gradleBuildWithSnippet() {
		GradleBuild build = new GradleBuild();
		build.snippets().add((writer) -> {
			writer.println("custom {");
			writer.indented(() -> {
				writer.println("first = 1");
				writer.println("second = 2");
			});
			writer.println("}");
		});
		assertThat(write(build)).contains("""
				custom {
					first = 1
					second = 2
				}
				""");
	}

	@Test
	void gradleBuildWithSnippetsAreSeparated() {
		GradleBuild build = new GradleBuild();
		build.snippets().add((writer) -> {
			writer.println("custom {");
			writer.indented(() -> {
				writer.println("first = 1");
				writer.println("second = 2");
			});
			writer.println("}");
		});
		build.snippets().add((writer) -> {
			writer.println("another {");
			writer.indented(() -> {
				writer.println("third = 3");
				writer.println("fourth = 4");
			});
			writer.println("}");
		});

		assertThat(write(build)).contains("""
				custom {
					first = 1
					second = 2
				}

				another {
					third = 3
					fourth = 4
				}
				""");
	}

	@Test
	void gradleBuildWithSnippetAndImports() {
		GradleBuild build = new GradleBuild();
		build.snippets().add(Set.of("com.example.CustomTask"), (writer) -> writer.println("custom { }"));
		assertThat(write(build)).containsOnlyOnce("import com.example.CustomTask");
	}

	protected abstract String write(GradleBuild build);

}
