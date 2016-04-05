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

import java.nio.charset.Charset

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Log

import org.springframework.core.io.Resource
import org.springframework.util.StreamUtils

/**
 * Builder for {@link InitializrMetadata}. Allows to read meta-data from any arbitrary resource,
 * including remote URLs.
 *
 * @author Stephane Nicoll
 * @since 1.0
 * @see InitializrMetadataCustomizer
 */
class InitializrMetadataBuilder {

	private final List<InitializrMetadataCustomizer> customizers = []
	private final InitializrConfiguration configuration

	private InitializrMetadataBuilder(InitializrConfiguration configuration) {
		this.configuration = configuration
	}

	/**
	 * Create a builder instance from the specified {@link InitializrProperties}. Initialize
	 * the configuration to use.
	 * @see #withInitializrProperties(InitializrProperties)
	 */
	public static InitializrMetadataBuilder fromInitializrProperties(InitializrProperties configuration) {
		new InitializrMetadataBuilder(configuration).withInitializrProperties(configuration)
	}

	/**
	 * Create an empty builder instance with a default {@link InitializrConfiguration}
	 */
	public static InitializrMetadataBuilder create() {
		new InitializrMetadataBuilder(new InitializrConfiguration())
	}


	/**
	 * Add a {@link InitializrProperties} to be merged with other content. Merges the settings only
	 * and not the configuration.
	 * @see #withInitializrProperties(InitializrProperties, boolean)
	 */
	InitializrMetadataBuilder withInitializrProperties(InitializrProperties properties) {
		withInitializrProperties(properties, false)
	}

	/**
	 * Add a {@link InitializrProperties} to be merged with other content.
	 * @param properties the settings to merge onto this instance
	 * @param mergeConfiguration specify if service configuration should be merged as well
	 */
	InitializrMetadataBuilder withInitializrProperties(InitializrProperties properties, boolean mergeConfiguration) {
		if (mergeConfiguration) {
			this.configuration.merge(properties)
		}
		withCustomizer(new InitializerPropertiesCustomizer(properties))
	}

	/**
	 * Add a {@link InitializrMetadata} to be merged with other content.
	 * @param resource a resource to a json document describing the meta-data to include
	 */
	InitializrMetadataBuilder withInitializrMetadata(Resource resource) {
		withCustomizer(new ResourceInitializrMetadataCustomizer(resource))
	}

	/**
	 * Add a {@link InitializrMetadataCustomizer}. customizers are invoked in their
	 * order of addition.
	 * @see InitializrMetadataCustomizer
	 */
	InitializrMetadataBuilder withCustomizer(InitializrMetadataCustomizer customizer) {
		customizers << customizer
		this
	}

	/**
	 * Build a {@link InitializrMetadata} based on the state of this builder.
	 */
	InitializrMetadata build() {
		InitializrConfiguration config = this.configuration ?: new InitializrConfiguration()
		InitializrMetadata metadata = createInstance(config)
		for (InitializrMetadataCustomizer customizer : customizers) {
			customizer.customize(metadata)
		}
		applyDefaults(metadata)
		metadata.validate()
		metadata
	}

	/**
	 * Creates an empty instance based on the specified {@link InitializrConfiguration}
	 */
	protected InitializrMetadata createInstance(InitializrConfiguration configuration) {
		new InitializrMetadata(configuration)
	}

	/**
	 * Apply defaults to capabilities that have no value.
	 */
	protected applyDefaults(InitializrMetadata metadata) {
		if (!metadata.name.content) {
			metadata.name.content = 'demo'
		}
		if (!metadata.description.content) {
			metadata.description.content = 'Demo project for Spring Boot'
		}
		if (!metadata.groupId.content) {
			metadata.groupId.content = 'com.example'
		}
		if (!metadata.version.content) {
			metadata.version.content = '0.0.1-SNAPSHOT'
		}
	}

	private static class InitializerPropertiesCustomizer implements InitializrMetadataCustomizer {

		private final InitializrProperties properties

		InitializerPropertiesCustomizer(InitializrProperties properties) {
			this.properties = properties
		}

		@Override
		void customize(InitializrMetadata metadata) {
			metadata.dependencies.merge(properties.dependencies)
			metadata.types.merge(properties.types)
			metadata.bootVersions.merge(properties.bootVersions)
			metadata.packagings.merge(properties.packagings)
			metadata.javaVersions.merge(properties.javaVersions)
			metadata.languages.merge(properties.languages)
			properties.groupId.apply(metadata.groupId)
			properties.artifactId.apply(metadata.artifactId)
			properties.version.apply(metadata.version)
			properties.name.apply(metadata.name)
			properties.description.apply(metadata.description)
			properties.packageName.apply(metadata.packageName)
		}
	}

	@Log
	private static class ResourceInitializrMetadataCustomizer implements InitializrMetadataCustomizer {

		private static final Charset UTF_8 = Charset.forName('UTF-8')

		private final Resource resource

		ResourceInitializrMetadataCustomizer(Resource resource) {
			this.resource = resource
		}

		@Override
		void customize(InitializrMetadata metadata) {
			log.info("Loading initializr meta-data from $resource")
			def content = StreamUtils.copyToString(resource.getInputStream(), UTF_8)
			ObjectMapper objectMapper = new ObjectMapper()
			def anotherMetadata = objectMapper.readValue(content, InitializrMetadata)
			metadata.merge(anotherMetadata)
		}

	}

}
