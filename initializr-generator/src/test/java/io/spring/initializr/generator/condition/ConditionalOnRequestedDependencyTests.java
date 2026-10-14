/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.condition;

import java.util.function.Consumer;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationContext;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link ConditionalOnRequestedDependency}.
 *
 * @author Stephane Nicoll
 */
class ConditionalOnRequestedDependencyTests {

	@Test
	void outcomeWithMatchingDependency() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.addDependency("web", mock(Dependency.class));
		assertCondition(description, (context) -> {
			assertThat(context.getBeansOfType(String.class)).hasSize(1);
			assertThat(context.getBean(String.class)).isEqualTo("webDependency");
		});
	}

	@Test
	void outcomeWithNoMatch() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.addDependency("another", mock(Dependency.class));
		assertCondition(description, (context) -> assertThat(context.getBeansOfType(String.class)).isEmpty());
	}

	private void assertCondition(MutableProjectDescription description, Consumer<ProjectGenerationContext> context) {
		try (ProjectGenerationContext projectContext = new ProjectGenerationContext()) {
			projectContext.registerBean(ProjectDescription.class, () -> description);
			projectContext.register(RequestedDependencyTestConfiguration.class);
			projectContext.refresh();
			context.accept(projectContext);
		}
	}

	@Configuration
	static class RequestedDependencyTestConfiguration {

		@Bean
		@ConditionalOnRequestedDependency("web")
		String webActive() {
			return "webDependency";
		}

	}

}
