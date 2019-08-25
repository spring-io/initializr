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

package io.spring.initializr.generator.spring.build.gradle;

import java.util.Map;

import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConditionalOnGradleVersion}.
 *
 * @author Stephane Nicoll
 */
public class ConditionalOnGradleVersionTests {

	private final ProjectAssetTester projectTester = new ProjectAssetTester()
			.withConfiguration(GradleVersionTestConfiguration.class);

	@Test
	void outcomeWithSpringBoot15() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("1.5.18.RELEASE"));
		String bean = outcomeFor(description);
		assertThat(bean).isEqualTo("testGradle3");
	}

	@Test
	void outcomeWithSpringBoot20() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.0.9.RELEASE"));
		String bean = outcomeFor(description);
		assertThat(bean).isEqualTo("testGradle4");
	}

	@Test
	void outcomeWithSpringBoot21() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.3.RELEASE"));
		String bean = outcomeFor(description);
		assertThat(bean).isEqualTo("testGradle5");
	}

	@Test
	void outcomeWithNoMatch() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("1.0.0.RELEASE"));
		this.projectTester.generate(description, (projectGenerationContext) -> {
			assertThat(projectGenerationContext.getBeansOfType(String.class)).isEmpty();
			return null;
		});
	}

	@Test
	void outcomeWithNoAvailableSpringBootVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		this.projectTester.generate(description, (projectGenerationContext) -> {
			assertThat(projectGenerationContext.getBeansOfType(String.class)).isEmpty();
			return null;
		});
	}

	@Test
	void outcomeWithSpringBoot15AndMultipleGenerations() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("1.5.18.RELEASE"));
		Map<String, String> candidates = candidatesFor(description, Gradle3Or4TestConfiguration.class);
		assertThat(candidates).containsOnlyKeys("gradle3", "gradle3AndLater");
	}

	@Test
	void outcomeWithSpringBoot20AndMultipleGenerations() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.0.9.RELEASE"));
		Map<String, String> candidates = candidatesFor(description, Gradle3Or4TestConfiguration.class);
		assertThat(candidates).containsOnlyKeys("gradle4", "gradle3AndLater");
	}

	@Test
	void outcomeWithSpringBoot21AndMultipleNonMatchingGenerations() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.3.RELEASE"));
		Map<String, String> candidates = candidatesFor(description, Gradle3Or4TestConfiguration.class);
		assertThat(candidates).containsOnlyKeys("gradle5");
	}

	private String outcomeFor(MutableProjectDescription description) {
		return this.projectTester.generate(description, (projectGenerationContext) -> {
			assertThat(projectGenerationContext.getBeansOfType(String.class)).hasSize(1);
			return projectGenerationContext.getBean(String.class);
		});
	}

	private Map<String, String> candidatesFor(MutableProjectDescription description, Class<?>... extraConfigurations) {
		return this.projectTester.withConfiguration(extraConfigurations).generate(description,
				(context) -> context.getBeansOfType(String.class));
	}

	@Configuration
	static class GradleVersionTestConfiguration {

		@Bean
		@ConditionalOnGradleVersion("3")
		String gradle3() {
			return "testGradle3";
		}

		@Bean
		@ConditionalOnGradleVersion("4")
		String gradle4() {
			return "testGradle4";
		}

		@Bean
		@ConditionalOnGradleVersion("5")
		String gradle5() {
			return "testGradle5";
		}

	}

	@Configuration
	static class Gradle3Or4TestConfiguration {

		@Bean
		@ConditionalOnGradleVersion({ "3", "4" })
		String gradle3AndLater() {
			return "testGradle3AndLater";
		}

	}

}
