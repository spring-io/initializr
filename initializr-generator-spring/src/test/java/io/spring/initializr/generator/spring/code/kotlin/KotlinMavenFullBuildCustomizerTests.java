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

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Configuration;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Dependency;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Execution;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Setting;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinMavenFullBuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
class KotlinMavenFullBuildCustomizerTests {

	@Test
	void kotlinVersionPropertyIsConfigured() {
		MavenBuild build = new MavenBuild();
		new KotlinMavenFullBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70"))
				.customize(build);
		assertThat(build.getProperties()).hasSize(1);
		assertThat(build.getProperties()).containsEntry("kotlin.version", "1.2.70");
	}

	@Test
	void buildSourceDirectoriesAreConfigured() {
		MavenBuild build = new MavenBuild();
		new KotlinMavenFullBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70"))
				.customize(build);
		assertThat(build.getSourceDirectory())
				.isEqualTo("${project.basedir}/src/main/kotlin");
		assertThat(build.getTestSourceDirectory())
				.isEqualTo("${project.basedir}/src/test/kotlin");
	}

	@Test
	void kotlinMavenPluginIsConfigured() {
		MavenBuild build = new MavenBuild();
		new KotlinMavenFullBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70"))
				.customize(build);
		assertThat(build.getPlugins()).hasSize(1);
		MavenPlugin kotlinPlugin = build.getPlugins().get(0);
		assertThat(kotlinPlugin.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinPlugin.getArtifactId()).isEqualTo("kotlin-maven-plugin");
		assertThat(kotlinPlugin.getVersion()).isEqualTo("${kotlin.version}");
		Configuration configuration = kotlinPlugin.getConfiguration();
		assertThat(configuration).isNotNull();
		assertThat(configuration.getSettings()).hasSize(3);
		Setting args = configuration.getSettings().get(0);
		assertThat(args.getName()).isEqualTo("args");
		assertThat(args.getValue()).asList().hasSize(1);
		assertThat(args.getValue()).asList().element(0)
				.hasFieldOrPropertyWithValue("name", "arg")
				.hasFieldOrPropertyWithValue("value", "-Xjsr305=strict");
		Setting compilerPlugins = configuration.getSettings().get(1);
		assertThat(compilerPlugins.getName()).isEqualTo("compilerPlugins");
		assertThat(compilerPlugins.getValue()).asList().hasSize(1);
		assertThat(compilerPlugins.getValue()).asList().element(0)
				.hasFieldOrPropertyWithValue("name", "plugin")
				.hasFieldOrPropertyWithValue("value", "spring");
		Setting jvmTarget = configuration.getSettings().get(2);
		assertThat(jvmTarget.getName()).isEqualTo("jvmTarget");
		assertThat(jvmTarget.getValue()).isEqualTo("1.8");
		assertThat(kotlinPlugin.getExecutions()).hasSize(2);
		Execution compile = kotlinPlugin.getExecutions().get(0);
		assertThat(compile.getId()).isEqualTo("compile");
		assertThat(compile.getGoals()).containsExactly("compile");
		assertThat(compile.getPhase()).isEqualTo("compile");
		assertThat(compile.getConfiguration()).isNull();
		Execution testCompile = kotlinPlugin.getExecutions().get(1);
		assertThat(testCompile.getId()).isEqualTo("test-compile");
		assertThat(testCompile.getGoals()).containsExactly("test-compile");
		assertThat(testCompile.getPhase()).isEqualTo("test-compile");
		assertThat(testCompile.getConfiguration()).isNull();
		assertThat(kotlinPlugin.getDependencies()).hasSize(1);
		Dependency allOpen = kotlinPlugin.getDependencies().get(0);
		assertThat(allOpen.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(allOpen.getArtifactId()).isEqualTo("kotlin-maven-allopen");
		assertThat(allOpen.getVersion()).isEqualTo("${kotlin.version}");
	}

}
