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

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild.TaskCustomization;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinGradleBuildCustomizer}.
 *
 * @author Andy Wilkinson
 */
class KotlinGradleBuildCustomizerTests {

	@Test
	void kotlinPluginsAreConfigured() {
		GradleBuild build = new GradleBuild();
		new KotlinGradleBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70"))
				.customize(build);
		assertThat(build.getPlugins()).hasSize(2);
		assertThat(build.getPlugins().get(0).getId())
				.isEqualTo("org.jetbrains.kotlin.jvm");
		assertThat(build.getPlugins().get(0).getVersion()).isEqualTo("1.2.70");
		assertThat(build.getPlugins().get(1).getId())
				.isEqualTo("org.jetbrains.kotlin.plugin.spring");
		assertThat(build.getPlugins().get(1).getVersion()).isEqualTo("1.2.70");
	}

	@Test
	void kotlinCompilationTasksAreCustomized() {
		GradleBuild build = new GradleBuild();
		new KotlinGradleBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70"))
				.customize(build);
		assertThat(build.getTaskCustomizations()).hasSize(2);
		assertThat(build.getTaskCustomizations()).containsKeys("compileKotlin",
				"compileTestKotlin");
		assertKotlinOptions(build.getTaskCustomizations().get("compileKotlin"));
		assertKotlinOptions(build.getTaskCustomizations().get("compileTestKotlin"));
	}

	private void assertKotlinOptions(TaskCustomization compileTask) {
		assertThat(compileTask.getAssignments()).isEmpty();
		assertThat(compileTask.getInvocations()).isEmpty();
		assertThat(compileTask.getNested()).hasSize(1);
		TaskCustomization kotlinOptions = compileTask.getNested().get("kotlinOptions");
		assertThat(kotlinOptions.getInvocations()).hasSize(0);
		assertThat(kotlinOptions.getNested()).hasSize(0);
		assertThat(kotlinOptions.getAssignments()).hasSize(2);
		assertThat(kotlinOptions.getAssignments())
				.containsEntry("freeCompilerArgs", "['-Xjsr305=strict']")
				.containsEntry("jvmTarget", "'1.8'");
	}

}
