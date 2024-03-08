/*
 * Copyright 2012-2024 the original author or authors.
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

package io.spring.initializr.generator.spring.properties;

import java.io.IOException;
import java.nio.file.Path;

import io.spring.initializr.generator.test.project.ProjectStructure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ApplicationPropertiesContributor}.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
class ApplicationPropertiesContributorTests {

	@TempDir
	Path directory;

	@Test
	void applicationConfigurationWithDefaultSettings() throws IOException {
		new ApplicationPropertiesContributor(new ApplicationProperties()).contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).textFile("src/main/resources/application.properties")
			.isEmpty();
	}

	@Test
	void shouldAddStringProperty() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("spring.application.name", "test");
		ApplicationPropertiesContributor contributor = new ApplicationPropertiesContributor(properties);
		contributor.contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).textFile("src/main/resources/application.properties")
			.lines()
			.contains("spring.application.name=test");
	}

}
