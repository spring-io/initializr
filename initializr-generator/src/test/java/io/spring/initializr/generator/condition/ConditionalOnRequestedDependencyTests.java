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
