/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.condition;

import java.util.function.Consumer;

import io.spring.initializr.generator.packaging.jar.JarPackaging;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationContext;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConditionalOnPackaging}.
 *
 * @author Stephane Nicoll
 */
class ConditionalOnPackagingTests {

	@Test
	void outcomeWithJarPackaging() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPackaging(new JarPackaging());
		assertCondition(description, (context) -> {
			assertThat(context.getBeansOfType(String.class)).hasSize(1);
			assertThat(context.getBean(String.class)).isEqualTo("testJar");
		});
	}

	@Test
	void outcomeWithWarPackaging() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPackaging(new WarPackaging());
		assertCondition(description, (context) -> {
			assertThat(context.getBeansOfType(String.class)).hasSize(1);
			assertThat(context.getBean(String.class)).isEqualTo("testWar");
		});
	}

	@Test
	void outcomeWithNoAvailablePackaging() {
		MutableProjectDescription description = new MutableProjectDescription();
		assertCondition(description, (context) -> assertThat(context.getBeansOfType(String.class)).isEmpty());
	}

	private void assertCondition(MutableProjectDescription description, Consumer<ProjectGenerationContext> context) {
		try (ProjectGenerationContext projectContext = new ProjectGenerationContext()) {
			projectContext.registerBean(ProjectDescription.class, () -> description);
			projectContext.register(PackagingTestConfiguration.class);
			projectContext.refresh();
			context.accept(projectContext);
		}
	}

	@Configuration
	static class PackagingTestConfiguration {

		@Bean
		@ConditionalOnPackaging("jar")
		String jar() {
			return "testJar";
		}

		@Bean
		@ConditionalOnPackaging("war")
		String war() {
			return "testWar";
		}

	}

}
