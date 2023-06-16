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

package io.spring.initializr.generator.spring.code.kotlin;

import java.util.List;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleTask;
import io.spring.initializr.generator.buildsystem.gradle.GradleTask.Attribute;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinDslKotlinGradleBuildCustomizer}.
 *
 * @author Jean-Baptiste Nizet
 */
class KotlinDslKotlinGradleBuildCustomizerTests {

	@Test
	void kotlinPluginsAreConfigured() {
		GradleBuild build = new GradleBuild();
		new KotlinDslKotlinGradleBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70")).customize(build);
		assertThat(build.plugins().values()).extracting("id", "version")
			.containsExactlyInAnyOrder(Tuple.tuple("org.jetbrains.kotlin.jvm", "1.2.70"),
					Tuple.tuple("org.jetbrains.kotlin.plugin.spring", "1.2.70"));
	}

	@Test
	void kotlinCompilationTasksAreCustomized() {
		GradleBuild build = new GradleBuild();
		new KotlinDslKotlinGradleBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70", "11")).customize(build);
		assertThat(build.tasks().importedTypes()).contains("org.jetbrains.kotlin.gradle.tasks.KotlinCompile");
		assertThat(build.tasks().values()).singleElement().satisfies((task) -> {
			assertThat(task.getName()).isEqualTo("KotlinCompile");
			assertKotlinOptions(task, "11");
		});
	}

	@Test
	void kotlinCompilationTasksWithListOfCompilerArgsAreCustomized() {
		GradleBuild build = new GradleBuild();
		KotlinProjectSettings kotlinProjectSettings = new SimpleKotlinProjectSettings("1.2.70", "11") {
			@Override
			public List<String> getCompilerArgs() {
				return List.of("-Xjsr305=strict", "-Xmx=128M");
			}
		};
		new KotlinDslKotlinGradleBuildCustomizer(kotlinProjectSettings).customize(build);
		assertThat(build.tasks().values()).singleElement().satisfies((task) -> {
			GradleTask kotlinOptions = task.getNested().get("kotlinOptions");
			assertThat(kotlinOptions.getAttributes())
				.contains(Attribute.append("freeCompilerArgs", "listOf(\"-Xjsr305=strict\", \"-Xmx=128M\")"));
		});
	}

	private void assertKotlinOptions(GradleTask compileTask, String jvmTarget) {
		assertThat(compileTask.getAttributes()).isEmpty();
		assertThat(compileTask.getInvocations()).isEmpty();
		assertThat(compileTask.getNested()).hasSize(1);
		GradleTask kotlinOptions = compileTask.getNested().get("kotlinOptions");
		assertThat(kotlinOptions.getInvocations()).hasSize(0);
		assertThat(kotlinOptions.getNested()).hasSize(0);
		assertThat(kotlinOptions.getAttributes()).hasSize(2);
		assertThat(kotlinOptions.getAttributes()).contains(Attribute.append("freeCompilerArgs", "\"-Xjsr305=strict\""),
				Attribute.set("jvmTarget", String.format("\"%s\"", jvmTarget)));
	}

}
