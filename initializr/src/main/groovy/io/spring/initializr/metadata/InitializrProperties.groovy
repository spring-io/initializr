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

	@JsonIgnore
	final List<DependencyGroup> dependencies = []

	@JsonIgnore
	final List<Type> types = []

	@JsonIgnore
	final List<DefaultMetadataElement> packagings = []

	@JsonIgnore
	final List<DefaultMetadataElement> javaVersions = []

	@JsonIgnore
	final List<DefaultMetadataElement> languages = []

	@JsonIgnore
	final List<DefaultMetadataElement> bootVersions = []

	@JsonIgnore
	final SimpleElement groupId = new SimpleElement(value: 'com.example')

	@JsonIgnore
	final SimpleElement artifactId = new SimpleElement()

	@JsonIgnore
	final SimpleElement version = new SimpleElement(value: '0.0.1-SNAPSHOT')

	@JsonIgnore
	final SimpleElement name = new SimpleElement(value: 'demo')

	@JsonIgnore
	final SimpleElement description = new SimpleElement(value: 'Demo project for Spring Boot')

	@JsonIgnore
	final SimpleElement packageName = new SimpleElement()


	static class SimpleElement {
		String title
		String description
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
