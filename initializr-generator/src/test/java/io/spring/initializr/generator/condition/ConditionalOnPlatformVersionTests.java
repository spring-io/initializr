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

import java.util.Map;

import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationContext;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConditionalOnPlatformVersion}.
 *
 * @author Stephane Nicoll
 */
class ConditionalOnPlatformVersionTests {

	@Test
	void outcomeWithMatchingRange() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("1.2.0.RELEASE"));
		assertThat(candidatesFor(description, PlatformVersionTestConfiguration.class)).containsOnlyKeys("first");
	}

	@Test
	void outcomeWithMatchingOpenRange() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.0.1.RELEASE"));
		assertThat(candidatesFor(description, PlatformVersionTestConfiguration.class)).containsOnlyKeys("second");
	}

	@Test
	void outcomeWithMatchingStartOfOpenRange() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.0.0.M1"));
		assertThat(candidatesFor(description, PlatformVersionTestConfiguration.class)).containsOnlyKeys("second");
	}

	@Test
	void outcomeWithNoMatch() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("0.1.0"));
		assertThat(candidatesFor(description, PlatformVersionTestConfiguration.class)).isEmpty();
	}

	@Test
	void outcomeWithNoAvailablePlatformVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		assertThat(candidatesFor(description, PlatformVersionTestConfiguration.class)).isEmpty();
	}

	@Test
	void outcomeWithSeveralRangesAndMatchingVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
		assertThat(candidatesFor(description, PlatformVersionTestConfiguration.class,
				OneOrTwoPlatformVersionTestConfiguration.class)).containsOnlyKeys("second", "firstOrSecond");
	}

	@Test
	void outcomeWithSeveralRangesAndNonMatchingVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.0.0.M2"));
		assertThat(candidatesFor(description, PlatformVersionTestConfiguration.class,
				OneOrTwoPlatformVersionTestConfiguration.class)).containsOnlyKeys("second");
	}

	private Map<String, String> candidatesFor(MutableProjectDescription description, Class<?>... extraConfigurations) {
		try (ProjectGenerationContext context = new ProjectGenerationContext()) {
			context.registerBean(ProjectDescription.class, () -> description);
			context.register(PlatformVersionTestConfiguration.class);
			context.register(extraConfigurations);
			context.refresh();
			return context.getBeansOfType(String.class);
		}
	}

	@Configuration
	static class PlatformVersionTestConfiguration {

		@Bean
		@ConditionalOnPlatformVersion("[1.0.0.RELEASE, 2.0.0.M1)")
		String first() {
			return "one";
		}

		@Bean
		@ConditionalOnPlatformVersion("2.0.0.M1")
		String second() {
			return "two";
		}

	}

	@Configuration
	static class OneOrTwoPlatformVersionTestConfiguration {

		@Bean
		@ConditionalOnPlatformVersion({ "[1.0.0.RELEASE, 2.0.0.M1)", "2.0.0.RELEASE" })
		String firstOrSecond() {
			return "oneOrTwo";
		}

	}

}
