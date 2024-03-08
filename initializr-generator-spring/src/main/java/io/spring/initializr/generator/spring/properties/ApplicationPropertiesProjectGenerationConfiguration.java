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

import io.spring.initializr.generator.project.ProjectGenerationConfiguration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;

/**
 * Configuration for application properties related contributions to a generated project.
 *
 * @author Moritz Halbritter
 */
@ProjectGenerationConfiguration
class ApplicationPropertiesProjectGenerationConfiguration {

	@Bean
	ApplicationProperties applicationProperties(ObjectProvider<ApplicationPropertiesCustomizer> customizers) {
		ApplicationProperties properties = new ApplicationProperties();
		customizers.orderedStream().forEach((customizer) -> customizer.customize(properties));
		return properties;
	}

	@Bean
	ApplicationPropertiesContributor applicationPropertiesContributor(ApplicationProperties applicationProperties) {
		return new ApplicationPropertiesContributor(applicationProperties);
	}

}
