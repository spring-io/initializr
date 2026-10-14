/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.condition;

import java.util.function.Consumer;

import io.spring.initializr.generator.language.groovy.GroovyLanguage;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationContext;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConditionalOnLanguage}.
 *
 * @author Stephane Nicoll
 */
class ConditionalOnLanguageTests {

	@Test
	void outcomeWithJavaLanguage() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setLanguage(new JavaLanguage());
		assertCondition(description, (context) -> {
			assertThat(context.getBeansOfType(String.class)).hasSize(1);
			assertThat(context.getBean(String.class)).isEqualTo("testJava");
		});
	}

	@Test
	void outcomeWithGroovyBuildSystem() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setLanguage(new GroovyLanguage());
		assertCondition(description, (context) -> {
			assertThat(context.getBeansOfType(String.class)).hasSize(1);
			assertThat(context.getBean(String.class)).isEqualTo("testGroovy");
		});
	}

	@Test
	void outcomeWithNoMatch() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setLanguage(new KotlinLanguage());
		assertCondition(description, (context) -> assertThat(context.getBeansOfType(String.class)).isEmpty());
	}

	@Test
	void outcomeWithNoAvailableLanguage() {
		MutableProjectDescription description = new MutableProjectDescription();
		assertCondition(description, (context) -> assertThat(context.getBeansOfType(String.class)).isEmpty());
	}

	private void assertCondition(MutableProjectDescription description, Consumer<ProjectGenerationContext> context) {
		try (ProjectGenerationContext projectContext = new ProjectGenerationContext()) {
			projectContext.registerBean(ProjectDescription.class, () -> description);
			projectContext.register(LanguageTestConfiguration.class);
			projectContext.refresh();
			context.accept(projectContext);
		}
	}

	@Configuration
	static class LanguageTestConfiguration {

		@Bean
		@ConditionalOnLanguage("java")
		String java() {
			return "testJava";
		}

		@Bean
		@ConditionalOnLanguage("groovy")
		String groovy() {
			return "testGroovy";
		}

	}

}
