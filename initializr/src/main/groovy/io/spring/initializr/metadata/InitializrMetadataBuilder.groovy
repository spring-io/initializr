/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.metadata

import io.spring.initializr.InitializrConfiguration

/**
 * Builder for {@link InitializrMetadata}.
 *
 * @author Stephane Nicoll
 * @since 1.0
 * @see InitializrMetadataCustomizer
 */
class InitializrMetadataBuilder {

	private final List<InitializrMetadataCustomizer> customizers = []
	private InitializrConfiguration configuration

	/**
	 * Adds the specified configuration.
	 * @see InitializrProperties
	 */
	InitializrMetadataBuilder fromConfiguration(InitializrProperties configuration) {
		this.configuration = configuration
		withCustomizer(new InitializerPropertiesCustomizer(configuration))
	}

	/**
	 * Adds a {@link InitializrMetadataCustomizer}. customizers are invoked in their
	 * order of addition.
	 * @see InitializrMetadataCustomizer
	 */
	InitializrMetadataBuilder withCustomizer(InitializrMetadataCustomizer customizer) {
		customizers << customizer
		this
	}

	/**
	 * Build a {@link InitializrMetadata} baed on the state of this builder.
	 */
	InitializrMetadata build() {
		InitializrConfiguration config = this.configuration ?: new InitializrConfiguration()
		InitializrMetadata instance = createInstance(config)
		for (InitializrMetadataCustomizer customizer : customizers) {
			customizer.customize(instance)
		}
		instance.validate()
		instance
	}

	/**
	 * Creates an empty instance based on the specified {@link InitializrConfiguration}
	 */
	protected InitializrMetadata createInstance(InitializrConfiguration configuration) {
		new InitializrMetadata(configuration)
	}

	static class InitializerPropertiesCustomizer implements InitializrMetadataCustomizer {

		private final InitializrProperties properties

		InitializerPropertiesCustomizer(InitializrProperties properties) {
			this.properties = properties
		}

		@Override
		void customize(InitializrMetadata metadata) { // NICE: merge
			metadata.dependencies.content.addAll(properties.dependencies)
			metadata.types.content.addAll(properties.types)
			metadata.bootVersions.content.addAll(properties.bootVersions)
			metadata.packagings.content.addAll(properties.packagings)
			metadata.javaVersions.content.addAll(properties.javaVersions)
			metadata.languages.content.addAll(properties.languages)
			metadata.groupId.content = properties.defaults.groupId
			metadata.artifactId.content = properties.defaults.artifactId
			metadata.version.content = properties.defaults.version
			metadata.name.content = properties.defaults.name
			metadata.description.content = properties.defaults.description
			metadata.packageName.content = properties.defaults.packageName
		}
	}


}
