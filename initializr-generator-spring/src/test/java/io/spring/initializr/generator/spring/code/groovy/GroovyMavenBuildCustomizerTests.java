/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code.groovy;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Configuration;
import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Execution;
import io.spring.initializr.generator.version.VersionReference;
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
		assertThat(build.plugins().values()).singleElement().satisfies((groovyPlugin) -> {
			assertThat(groovyPlugin.getGroupId()).isEqualTo("org.codehaus.gmavenplus");
			assertThat(groovyPlugin.getArtifactId()).isEqualTo("gmavenplus-plugin");
			assertThat(groovyPlugin.getVersionReference()).isEqualTo(VersionReference.ofValue("1.13.1"));
			Configuration configuration = groovyPlugin.getConfiguration();
			assertThat(configuration).isNull();
			assertThat(groovyPlugin.getExecutions()).hasSize(1);
			Execution execution = groovyPlugin.getExecutions().get(0);
			assertThat(execution.getId()).isNull();
			assertThat(execution.getGoals()).containsExactly("addSources", "addTestSources", "generateStubs", "compile",
					"generateTestStubs", "compileTests", "removeStubs", "removeTestStubs");
			assertThat(execution.getPhase()).isNull();
			assertThat(execution.getConfiguration()).isNull();
		});
	}

}
