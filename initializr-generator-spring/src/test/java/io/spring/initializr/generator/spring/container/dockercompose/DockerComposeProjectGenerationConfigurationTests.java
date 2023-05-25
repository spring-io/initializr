/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.generator.spring.container.dockercompose;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DockerComposeProjectGenerationConfiguration}.
 *
 * @author Moritz Halbritter
 */
class DockerComposeProjectGenerationConfigurationTests {

	private final ApplicationContextRunner runner = new ApplicationContextRunner()
		.withUserConfiguration(DockerComposeProjectGenerationConfiguration.class);

	@Test
	void providesBeans() {
		this.runner.run((context) -> {
			assertThat(context).hasSingleBean(DockerComposeFile.class);
			assertThat(context).hasSingleBean(DockerComposeProjectContributor.class);
			assertThat(context).hasSingleBean(DockerComposeHelpDocumentCustomizer.class);
		});
	}

	@Test
	void collectsServices() {
		DockerComposeService service1 = DockerComposeServiceFixtures.service(1);
		DockerComposeService service2 = DockerComposeServiceFixtures.service(2);
		this.runner.withUserConfiguration(Services.class).run((context) -> {
			DockerComposeFile composeFile = context.getBean(DockerComposeFile.class);
			assertThat(composeFile.getServices()).containsExactlyInAnyOrder(service1, service2);
		});
	}

	@Test
	void callsCustomizers() {
		DockerComposeService service = DockerComposeServiceFixtures.service(3);
		DockerComposeFileCustomizer customizer = (composeFile) -> composeFile.addService(service);
		this.runner.withBean(DockerComposeFileCustomizer.class, () -> customizer).run((context) -> {
			DockerComposeFile composeFile = context.getBean(DockerComposeFile.class);
			assertThat(composeFile.getServices()).containsExactly(service);
		});
	}

	@Configuration
	static class Services {

		@Bean
		DockerComposeService service1() {
			return DockerComposeServiceFixtures.service(1);
		}

		@Bean
		DockerComposeService service2() {
			return DockerComposeServiceFixtures.service(2);
		}

	}

}
