/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.condition;

import java.util.Map;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationContext;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConditionalOnBuildSystem}.
 *
 * @author Stephane Nicoll
 */
class ConditionalOnBuildSystemTests {

	@Test
	void outcomeWithMavenBuildSystem() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBuildSystem(new MavenBuildSystem());
		assertThat(candidatesFor(description, BuildSystemTestConfiguration.class)).containsOnlyKeys("maven");
	}

	@Test
	void outcomeWithGradleBuildSystem() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBuildSystem(new GradleBuildSystem());
		assertThat(candidatesFor(description, BuildSystemTestConfiguration.class)).containsOnlyKeys("gradle");
	}

	@Test
	void conditionalOnGradleWithKotlinDialectMatchesWhenGradleBuildSystemUsesKotlinDialect() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBuildSystem(new GradleBuildSystem("kotlin"));
		assertThat(candidatesFor(description, BuildSystemTestConfiguration.class)).containsOnlyKeys("gradle",
				"gradleKotlin");
	}

	private Map<String, String> candidatesFor(MutableProjectDescription description, Class<?>... extraConfigurations) {
		try (ProjectGenerationContext context = new ProjectGenerationContext()) {
			context.registerBean(ProjectDescription.class, () -> description);
			context.register(extraConfigurations);
			context.refresh();
			return context.getBeansOfType(String.class);
		}
	}

	@Configuration
	static class BuildSystemTestConfiguration {

		@Bean
		@ConditionalOnBuildSystem("gradle")
		String gradle() {
			return "testGradle";
		}

		@Bean
		@ConditionalOnBuildSystem("maven")
		String maven() {
			return "testMaven";
		}

		@Bean
		@ConditionalOnBuildSystem("not-a-build-system")
		String notABuildSystem() {
			return "testNone";
		}

		@Bean
		@ConditionalOnBuildSystem(id = "gradle", dialect = "kotlin")
		String gradleKotlin() {
			return "testGradleKotlinDialect";
		}

	}

}
