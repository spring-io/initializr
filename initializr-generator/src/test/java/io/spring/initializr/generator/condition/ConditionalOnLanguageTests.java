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
