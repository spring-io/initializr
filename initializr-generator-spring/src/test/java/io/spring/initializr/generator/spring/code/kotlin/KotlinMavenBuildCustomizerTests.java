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

import java.util.Arrays;
import java.util.List;

import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Configuration;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Dependency;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Setting;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link KotlinMavenBuildCustomizer}.
 *
 * @author Andy Wilkinson
 */
class KotlinMavenBuildCustomizerTests {

	@Test
	void kotlinVersionPropertyIsConfigured() {
		MavenBuild build = new MavenBuild();
		new KotlinMavenBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70")).customize(build);
		assertThat(build.properties().versions(VersionProperty::toStandardFormat))
			.containsOnly(entry("kotlin.version", "1.2.70"));
	}

	@Test
	void buildSourceDirectoriesAreConfigured() {
		MavenBuild build = new MavenBuild();
		new KotlinMavenBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70")).customize(build);
		assertThat(build.getSettings().getSourceDirectory()).isEqualTo("${project.basedir}/src/main/kotlin");
		assertThat(build.getSettings().getTestSourceDirectory()).isEqualTo("${project.basedir}/src/test/kotlin");
	}

	@Test
	void kotlinMavenPluginIsConfigured() {
		MavenBuild build = new MavenBuild();
		new KotlinMavenBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70")).customize(build);
		assertThat(build.plugins().values()).singleElement().satisfies((kotlinPlugin) -> {
			assertThat(kotlinPlugin.getGroupId()).isEqualTo("org.jetbrains.kotlin");
			assertThat(kotlinPlugin.getArtifactId()).isEqualTo("kotlin-maven-plugin");
			assertThat(kotlinPlugin.getVersionReference()).isNull();
			Configuration configuration = kotlinPlugin.getConfiguration();
			assertThat(configuration).isNotNull();
			assertThat(configuration.getSettings()).hasSize(2);
			Setting args = configuration.getSettings().get(0);
			assertThat(args.getName()).isEqualTo("args");
			assertThat(args.getValue()).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1);
			assertThat(args.getValue()).asInstanceOf(InstanceOfAssertFactories.LIST)
				.element(0)
				.hasFieldOrPropertyWithValue("name", "arg")
				.hasFieldOrPropertyWithValue("value", "-Xjsr305=strict");
			Setting compilerPlugins = configuration.getSettings().get(1);
			assertThat(compilerPlugins.getName()).isEqualTo("compilerPlugins");
			assertThat(compilerPlugins.getValue()).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(1);
			assertThat(compilerPlugins.getValue()).asInstanceOf(InstanceOfAssertFactories.LIST)
				.element(0)
				.hasFieldOrPropertyWithValue("name", "plugin")
				.hasFieldOrPropertyWithValue("value", "spring");
			assertThat(kotlinPlugin.getExecutions()).isEmpty();
			assertThat(kotlinPlugin.getDependencies()).hasSize(1);
			Dependency allOpen = kotlinPlugin.getDependencies().get(0);
			assertThat(allOpen.getGroupId()).isEqualTo("org.jetbrains.kotlin");
			assertThat(allOpen.getArtifactId()).isEqualTo("kotlin-maven-allopen");
			assertThat(allOpen.getVersionReference()).isEqualTo(VersionReference.ofProperty("kotlin.version"));
		});
	}

	@Test
	void kotlinMavenPluginWithSeveralArgs() {
		MavenBuild build = new MavenBuild();
		new KotlinMavenBuildCustomizer(new KotlinOneEightProjectSettings()).customize(build);
		assertThat(build.plugins().values()).singleElement().satisfies((kotlinPlugin) -> {
			Configuration configuration = kotlinPlugin.getConfiguration();
			Setting args = configuration.getSettings().get(0);
			assertThat(args.getName()).isEqualTo("args");
			assertThat(args.getValue()).asInstanceOf(InstanceOfAssertFactories.LIST).hasSize(2);
			assertThat(args.getValue()).asInstanceOf(InstanceOfAssertFactories.LIST)
				.element(0)
				.hasFieldOrPropertyWithValue("name", "arg")
				.hasFieldOrPropertyWithValue("value", "-Done=1");
			assertThat(args.getValue()).asInstanceOf(InstanceOfAssertFactories.LIST)
				.element(1)
				.hasFieldOrPropertyWithValue("name", "arg")
				.hasFieldOrPropertyWithValue("value", "-Dtwo=2");
		});
	}

	@Test
	void kotlinMavenKotlinStdlibIsConfiguredWithKotlinOneEight() {
		MavenBuild build = new MavenBuild();
		new KotlinMavenBuildCustomizer(new KotlinOneEightProjectSettings()).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("kotlin-stdlib");
		io.spring.initializr.generator.buildsystem.Dependency kotlinStdlib = build.dependencies().get("kotlin-stdlib");
		assertThat(kotlinStdlib.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinStdlib.getArtifactId()).isEqualTo("kotlin-stdlib");
		assertThat(kotlinStdlib.getVersion()).isNull();
		assertThat(kotlinStdlib.getScope()).isEqualTo(DependencyScope.COMPILE);
	}

	@Test
	void kotlinMavenKotlinStdlibJdk8IsConfiguredWithKotlinOneSeven() {
		MavenBuild build = new MavenBuild();
		new KotlinMavenBuildCustomizer(new KotlinOneSevenProjectSettings()).customize(build);
		assertThat(build.dependencies().ids()).containsOnly("kotlin-stdlib");
		io.spring.initializr.generator.buildsystem.Dependency kotlinStdlib = build.dependencies().get("kotlin-stdlib");
		assertThat(kotlinStdlib.getGroupId()).isEqualTo("org.jetbrains.kotlin");
		assertThat(kotlinStdlib.getArtifactId()).isEqualTo("kotlin-stdlib-jdk8");
		assertThat(kotlinStdlib.getVersion()).isNull();
		assertThat(kotlinStdlib.getScope()).isEqualTo(DependencyScope.COMPILE);
	}

	private static class KotlinOneEightProjectSettings extends SimpleKotlinProjectSettings {

		KotlinOneEightProjectSettings() {
			super("1.8.0");
		}

		@Override
		public List<String> getCompilerArgs() {
			return Arrays.asList("-Done=1", "-Dtwo=2");
		}

	}

	private static class KotlinOneSevenProjectSettings extends SimpleKotlinProjectSettings {

		KotlinOneSevenProjectSettings() {
			super("1.7.22");
		}

		@Override
		public List<String> getCompilerArgs() {
			return Arrays.asList("-Done=1", "-Dtwo=2");
		}

	}

}
