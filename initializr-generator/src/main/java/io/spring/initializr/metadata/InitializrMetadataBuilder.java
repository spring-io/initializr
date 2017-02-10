/*
 * Copyright 2012-2017 the original author or authors.
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

package io.spring.initializr.metadata;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

/**
 * Builder for {@link InitializrMetadata}. Allows to read metadata from any arbitrary
 * resource, including remote URLs.
 *
 * @author Stephane Nicoll
 * @see InitializrMetadataCustomizer
 */
public class InitializrMetadataBuilder {

	private final List<InitializrMetadataCustomizer> customizers = new ArrayList<>();
	private final InitializrConfiguration configuration;

	private InitializrMetadataBuilder(InitializrConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Create a builder instance from the specified {@link InitializrProperties}.
	 * Initialize the configuration to use.
	 * @see #withInitializrProperties(InitializrProperties)
	 */
	public static InitializrMetadataBuilder fromInitializrProperties(
			InitializrProperties configuration) {
		return new InitializrMetadataBuilder(configuration)
				.withInitializrProperties(configuration);
	}

	/**
	 * Create an empty builder instance with a default {@link InitializrConfiguration}
	 */
	public static InitializrMetadataBuilder create() {
		return new InitializrMetadataBuilder(new InitializrConfiguration());
	}

	/**
	 * Add a {@link InitializrProperties} to be merged with other content. Merges the
	 * settings only and not the configuration.
	 * @see #withInitializrProperties(InitializrProperties, boolean)
	 */
	public InitializrMetadataBuilder withInitializrProperties(
			InitializrProperties properties) {
		return withInitializrProperties(properties, false);
	}

	/**
	 * Add a {@link InitializrProperties} to be merged with other content.
	 * @param properties the settings to merge onto this instance
	 * @param mergeConfiguration specify if service configuration should be merged as well
	 */
	public InitializrMetadataBuilder withInitializrProperties(
			InitializrProperties properties, boolean mergeConfiguration) {
		if (mergeConfiguration) {
			this.configuration.merge(properties);
		}
		return withCustomizer(new InitializerPropertiesCustomizer(properties));
	}

	/**
	 * Add a {@link InitializrMetadata} to be merged with other content.
	 * @param resource a resource to a json document describing the metadata to include
	 */
	public InitializrMetadataBuilder withInitializrMetadata(Resource resource) {
		return withCustomizer(new ResourceInitializrMetadataCustomizer(resource));
	}

	/**
	 * Add a {@link InitializrMetadataCustomizer}. customizers are invoked in their order
	 * of addition.
	 * @see InitializrMetadataCustomizer
	 */
	public InitializrMetadataBuilder withCustomizer(
			InitializrMetadataCustomizer customizer) {
		customizers.add(customizer);
		return this;
	}

	/**
	 * Build a {@link InitializrMetadata} based on the state of this builder.
	 */
	public InitializrMetadata build() {
		InitializrConfiguration config = this.configuration != null ? this.configuration
				: new InitializrConfiguration();
		InitializrMetadata metadata = createInstance(config);
		for (InitializrMetadataCustomizer customizer : customizers) {
			customizer.customize(metadata);
		}
		applyDefaults(metadata);
		metadata.validate();
		return metadata;
	}

	/**
	 * Creates an empty instance based on the specified {@link InitializrConfiguration}
	 */
	protected InitializrMetadata createInstance(InitializrConfiguration configuration) {
		return new InitializrMetadata(configuration);
	}

	/**
	 * Apply defaults to capabilities that have no value.
	 */
	protected void applyDefaults(InitializrMetadata metadata) {
		if (!StringUtils.hasText(metadata.getName().getContent())) {
			metadata.getName().setContent("demo");
		}
		if (!StringUtils.hasText(metadata.getDescription().getContent())) {
			metadata.getDescription().setContent("Demo project for Spring Boot");
		}
		if (!StringUtils.hasText(metadata.getGroupId().getContent())) {
			metadata.getGroupId().setContent("com.example");
		}
		if (!StringUtils.hasText(metadata.getVersion().getContent())) {
			metadata.getVersion().setContent("0.0.1-SNAPSHOT");
		}
	}

	private static class InitializerPropertiesCustomizer
			implements InitializrMetadataCustomizer {

		private final InitializrProperties properties;

		InitializerPropertiesCustomizer(InitializrProperties properties) {
			this.properties = properties;
		}

		@Override
		public void customize(InitializrMetadata metadata) {
			metadata.getDependencies().merge(properties.getDependencies());
			metadata.getTypes().merge(properties.getTypes());
			metadata.getBootVersions().merge(properties.getBootVersions());
			metadata.getPackagings().merge(properties.getPackagings());
			metadata.getJavaVersions().merge(properties.getJavaVersions());
			metadata.getLanguages().merge(properties.getLanguages());
			properties.getGroupId().apply(metadata.getGroupId());
			properties.getArtifactId().apply(metadata.getArtifactId());
			properties.getVersion().apply(metadata.getVersion());
			properties.getName().apply(metadata.getName());
			properties.getDescription().apply(metadata.getDescription());
			properties.getPackageName().apply(metadata.getPackageName());
		}
	}

	private static class ResourceInitializrMetadataCustomizer
			implements InitializrMetadataCustomizer {

		private static final Logger log = LoggerFactory.getLogger(
				InitializrMetadataBuilder.ResourceInitializrMetadataCustomizer.class);

		private static final Charset UTF_8 = Charset.forName("UTF-8");

		private final Resource resource;

		ResourceInitializrMetadataCustomizer(Resource resource) {
			this.resource = resource;
		}

		@Override
		public void customize(InitializrMetadata metadata) {
			log.info("Loading initializr metadata from " + resource);
			try {
				String content = StreamUtils.copyToString(resource.getInputStream(),
						UTF_8);
				ObjectMapper objectMapper = new ObjectMapper();
				InitializrMetadata anotherMetadata = objectMapper.readValue(content,
						InitializrMetadata.class);
				metadata.merge(anotherMetadata);
			}
			catch (Exception e) {
				throw new IllegalStateException("Cannot merge", e);
			}
		}

	}

}
