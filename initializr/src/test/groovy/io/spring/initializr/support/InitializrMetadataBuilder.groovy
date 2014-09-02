/*
 * Copyright 2012-2014 the original author or authors.
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

package io.spring.initializr.support

import io.spring.initializr.InitializrMetadata

/**
 * Easily create a {@link InitializrMetadata} instance for testing purposes.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class InitializrMetadataBuilder {

	private final InitializrMetadata metadata = new InitializrMetadata()


	static InitializrMetadataBuilder withDefaults() {
		new InitializrMetadataBuilder().addDefaults()
	}

	InitializrMetadata validateAndGet() {
		metadata.validate()
		instance()
	}

	InitializrMetadata instance() {
		metadata
	}

	InitializrMetadataBuilder addDependencyGroup(String name, String... ids) {
		def group = new InitializrMetadata.DependencyGroup()
		group.name = name
		for (String id : ids) {
			def dependency = new InitializrMetadata.Dependency()
			dependency.id = id
			group.content.add(dependency)
		}
		metadata.dependencies.add(group)
		this
	}

	InitializrMetadataBuilder addDependencyGroup(String name, InitializrMetadata.Dependency... dependencies) {
		def group = new InitializrMetadata.DependencyGroup()
		group.name = name
		group.content.addAll(dependencies)
		metadata.dependencies.add(group)
		this
	}

	InitializrMetadataBuilder addDefaults() {
		addDefaultTypes().addDefaultPackagings().addDefaultJavaVersions()
				.addDefaultLanguages().addDefaultBootVersions()
	}

	InitializrMetadataBuilder addDefaultTypes() {
		addType('pom.xml', false, '/pom.xml').addType('starter.zip', true, '/starter.zip')
				.addType('build.gradle', false, '/build.gradle').addType('gradle.zip', false, '/starter.zip')
	}

	InitializrMetadataBuilder addType(String id, boolean defaultValue, String action) {
		def type = new InitializrMetadata.Type()
		type.id = id
		type.name = id
		type.default = defaultValue
		type.action = action
		metadata.types.add(type)
		this
	}

	InitializrMetadataBuilder addDefaultPackagings() {
		addPackaging('jar', true).addPackaging('war', false)
	}

	InitializrMetadataBuilder addPackaging(String id, boolean defaultValue) {
		def packaging = new InitializrMetadata.Packaging()
		packaging.id = id
		packaging.name = id
		packaging.default = defaultValue
		metadata.packagings.add(packaging)
		this
	}

	InitializrMetadataBuilder addDefaultJavaVersions() {
		addJavaVersion('1.6', false).addJavaVersion('1.7', true).addJavaVersion('1.8', false)
	}

	InitializrMetadataBuilder addJavaVersion(String version, boolean defaultValue) {
		def javaVersion = new InitializrMetadata.JavaVersion()
		javaVersion.id = version
		javaVersion.name = version
		javaVersion.default = defaultValue
		metadata.javaVersions.add(javaVersion)
		this
	}

	InitializrMetadataBuilder addDefaultLanguages() {
		addLanguage('java', true).addPackaging('groovy', false)
	}

	InitializrMetadataBuilder addLanguage(String id, boolean defaultValue) {
		def language = new InitializrMetadata.Language()
		language.id = id
		language.name = id
		language.default = defaultValue
		metadata.languages.add(language)
		this
	}

	InitializrMetadataBuilder addDefaultBootVersions() {
		addBootVersion('1.0.2.RELEASE', false).addBootVersion('1.1.5.RELEASE', true)
				.addBootVersion('1.2.0.BUILD-SNAPSHOT', false)
	}

	InitializrMetadataBuilder addBootVersion(String id, boolean defaultValue) {
		def bootVersion = new InitializrMetadata.BootVersion()
		bootVersion.id = id
		bootVersion.name = id
		bootVersion.default = defaultValue
		metadata.bootVersions.add(bootVersion)
		this
	}

}