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

import com.fasterxml.jackson.annotation.JsonIgnore

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration of the initializr service.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@ConfigurationProperties(prefix = 'initializr')
class InitializrProperties extends InitializrConfiguration {

	/**
	 * Dependencies, organized in groups (i.e. themes).
	 */
	@JsonIgnore
	final List<DependencyGroup> dependencies = []

	/**
	 * Available project types.
	 */
	@JsonIgnore
	final List<Type> types = []

	/**
	 * Available packaging types.
	 */
	@JsonIgnore
	final List<DefaultMetadataElement> packagings = []

	/**
	 * Available java versions.
	 */
	@JsonIgnore
	final List<DefaultMetadataElement> javaVersions = []

	/**
	 * Available programming languages.
	 */
	@JsonIgnore
	final List<DefaultMetadataElement> languages = []

	/**
	 * Available Spring Boot versions.
	 */
	@JsonIgnore
	final List<DefaultMetadataElement> bootVersions = []

	/**
	 * GroupId meta-data.
	 */
	@JsonIgnore
	final SimpleElement groupId = new SimpleElement(value: 'com.example')

	/**
	 * ArtifactId meta-data.
	 */
	@JsonIgnore
	final SimpleElement artifactId = new SimpleElement()

	/**
	 * Version meta-data.
	 */
	@JsonIgnore
	final SimpleElement version = new SimpleElement(value: '0.0.1-SNAPSHOT')

	/**
	 * Name meta-data.
	 */
	@JsonIgnore
	final SimpleElement name = new SimpleElement(value: 'demo')

	/**
	 * Description meta-data.
	 */
	@JsonIgnore
	final SimpleElement description = new SimpleElement(value: 'Demo project for Spring Boot')

	/**
	 * Package name meta-data.
	 */
	@JsonIgnore
	final SimpleElement packageName = new SimpleElement()


	static class SimpleElement {
		/**
		 * Element title.
		 */
		String title

		/**
		 * Element description.
		 */
		String description

		/**
		 * Element default value.
		 */
		String value

		void apply(TextCapability capability) {
			if (title) {
				capability.title = title
			}
			if (description) {
				capability.description = description
			}
			if (value) {
				capability.content = value
			}
		}
	}

}
