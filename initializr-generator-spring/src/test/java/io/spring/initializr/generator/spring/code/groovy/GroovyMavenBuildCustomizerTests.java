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

package io.spring.initializr.generator.spring.code.groovy;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Configuration;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Execution;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GroovyMavenBuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
class GroovyMavenBuildCustomizerTests {

	@Test
	void groovyMavenPluginIsConfigured() {
		MavenBuild build = new MavenBuild();
		new GroovyMavenBuildCustomizer().customize(build);
		assertThat(build.getPlugins()).hasSize(1);
		MavenPlugin groovyPlugin = build.getPlugins().get(0);
		assertThat(groovyPlugin.getGroupId()).isEqualTo("org.codehaus.gmavenplus");
		assertThat(groovyPlugin.getArtifactId()).isEqualTo("gmavenplus-plugin");
		assertThat(groovyPlugin.getVersion()).isEqualTo("1.6.3");
		Configuration configuration = groovyPlugin.getConfiguration();
		assertThat(configuration).isNull();
		assertThat(groovyPlugin.getExecutions()).hasSize(1);
		Execution execution = groovyPlugin.getExecutions().get(0);
		assertThat(execution.getId()).isNull();
		assertThat(execution.getGoals()).containsExactly("addSources", "addTestSources",
				"generateStubs", "compile", "generateTestStubs", "compileTests",
				"removeStubs", "removeTestStubs");
		assertThat(execution.getPhase()).isNull();
		assertThat(execution.getConfiguration()).isNull();
	}

}
