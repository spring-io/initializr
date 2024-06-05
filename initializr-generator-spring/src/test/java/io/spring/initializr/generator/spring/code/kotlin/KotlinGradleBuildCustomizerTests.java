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

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinGradleBuildCustomizer}.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 * @author Moritz Halbritter
 */
class KotlinGradleBuildCustomizerTests {

	@Test
	void kotlinPluginsAreConfigured() {
		GradleBuild build = new GradleBuild();
		new KotlinGradleBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70"), '"').customize(build);
		assertThat(build.plugins().values()).extracting("id", "version")
			.containsExactlyInAnyOrder(Tuple.tuple("org.jetbrains.kotlin.jvm", "1.2.70"),
					Tuple.tuple("org.jetbrains.kotlin.plugin.spring", "1.2.70"));
	}

	@Test
	void shouldCustomizeCompilerOptions() {
		GradleBuild build = new GradleBuild();
		new KotlinGradleBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70"), '\'').customize(build);
		assertThat(build.extensions().values()).singleElement().satisfies((kotlin) -> {
			assertThat(kotlin.getName()).isEqualTo("kotlin");
			assertThat(kotlin.getNested().values()).singleElement().satisfies((compilerOptions) -> {
				assertThat(compilerOptions.getName()).isEqualTo("compilerOptions");
				assertThat(compilerOptions.getInvocations()).singleElement().satisfies((freeCompilerArgs) -> {
					assertThat(freeCompilerArgs.getTarget()).isEqualTo("freeCompilerArgs.addAll");
					assertThat(freeCompilerArgs.getArguments()).containsExactly("'-Xjsr305=strict'");
				});
			});
		});
	}

}
