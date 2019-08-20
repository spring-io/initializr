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

import io.spring.initializr.generator.packaging.jar.JarPackaging;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationContext;
import io.spring.initializr.generator.project.ResolvedProjectDescription;
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
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setPackaging(new JarPackaging());
		assertCondition(projectDescription, (context) -> {
			assertThat(context.getBeansOfType(String.class)).hasSize(1);
			assertThat(context.getBean(String.class)).isEqualTo("testJar");
		});
	}

	@Test
	void outcomeWithWarPackaging() {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setPackaging(new WarPackaging());
		assertCondition(projectDescription, (context) -> {
			assertThat(context.getBeansOfType(String.class)).hasSize(1);
			assertThat(context.getBean(String.class)).isEqualTo("testWar");
		});
	}

	@Test
	void outcomeWithNoAvailablePackaging() {
		ProjectDescription projectDescription = new ProjectDescription();
		assertCondition(projectDescription, (context) -> assertThat(context.getBeansOfType(String.class)).isEmpty());
	}

	private void assertCondition(ProjectDescription projectDescription, Consumer<ProjectGenerationContext> context) {
		try (ProjectGenerationContext projectContext = new ProjectGenerationContext()) {
			projectContext.registerBean(ResolvedProjectDescription.class, projectDescription::resolve);
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
