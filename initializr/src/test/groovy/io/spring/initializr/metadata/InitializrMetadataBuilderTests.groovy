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

import org.junit.Test

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.boot.bind.PropertiesConfigurationFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

/**
 * @author Stephane Nicoll
 */
class InitializrMetadataBuilderTests {

	@Test
	void loadDefaultConfig() {
		def bean = load(new ClassPathResource("application-test-default.yml"))
		def metadata = InitializrMetadataBuilder.fromInitializrProperties(bean).build()
		assertDefaultConfig(metadata)
	}

	@Test
	void mergeIdenticalConfig() {
		def bean = load(new ClassPathResource("application-test-default.yml"))
		def metadata = InitializrMetadataBuilder
				.fromInitializrProperties(bean)
				.withInitializrProperties(bean, true).build()
		assertDefaultConfig(metadata)
	}

	@Test
	void mergeConfig() {
		def config = load(new ClassPathResource("application-test-default.yml"))
		def customDefaultsConfig = load(new ClassPathResource("application-test-custom-defaults.yml"))
		def metadata = InitializrMetadataBuilder
				.fromInitializrProperties(config)
				.withInitializrProperties(customDefaultsConfig).build()
		assertDefaultConfig(metadata)
		assertEquals 'org.foo', metadata.groupId.content
		assertEquals 'foo-bar', metadata.artifactId.content
		assertEquals '1.2.4-SNAPSHOT', metadata.version.content
		assertEquals 'FooBar', metadata.name.content
		assertEquals 'FooBar Project', metadata.description.content
		assertEquals 'org.foo.demo', metadata.packageName.content
	}

	@Test
	void mergeMetadata() {
		def metadata = InitializrMetadataBuilder.create().withInitializrMetadata(
				new ClassPathResource('metadata/config/test-min.json')).build()
		assertEquals false, metadata.configuration.env.forceSsl
		assertEquals 1, metadata.dependencies.content.size()
		Dependency dependency = metadata.dependencies.get('test')
		assertNotNull dependency
		assertEquals 'org.springframework.boot', dependency.groupId
		assertEquals 1, metadata.types.content.size()
		assertEquals 2, metadata.bootVersions.content.size()
		assertEquals 2, metadata.packagings.content.size()
		assertEquals 1, metadata.javaVersions.content.size()
		assertEquals 3, metadata.languages.content.size()
		assertEquals 'meta-data-merge', metadata.name.content
		assertEquals 'Demo project for meta-data merge', metadata.description.content
		assertEquals 'org.acme', metadata.groupId.content
		assertEquals 'meta-data', metadata.artifactId.content
		assertEquals '1.0.0-SNAPSHOT', metadata.version.content
		assertEquals 'org.acme.demo', metadata.packageName.content
	}

	@Test
	void mergeMetadataWithBom() {
		def metadata = InitializrMetadataBuilder.create().withInitializrMetadata(
				new ClassPathResource('metadata/config/test-bom.json')).build()

		def boms = metadata.configuration.env.boms
		assertEquals 2, boms.size()
		BillOfMaterials myBom = boms['my-bom']
		assertNotNull myBom
		assertEquals 'org.acme', myBom.groupId
		assertEquals 'my-bom', myBom.artifactId
		assertEquals '1.2.3.RELEASE', myBom.version

		BillOfMaterials anotherBom = boms['another-bom']
		assertNotNull anotherBom
		assertEquals 'org.acme', anotherBom.groupId
		assertEquals 'another-bom', anotherBom.artifactId
		assertEquals '4.5.6.RELEASE', anotherBom.version
	}

	@Test
	void mergeMetadataWithRepository() {
		def metadata = InitializrMetadataBuilder.create().withInitializrMetadata(
				new ClassPathResource('metadata/config/test-repository.json')).build()

		def repositories = metadata.configuration.env.repositories
		assertEquals 4, repositories.size() // 2 standard repos
		Repository myRepo = repositories['my-repo']
		assertNotNull myRepo
		assertEquals 'my repo', myRepo.name
		assertEquals new URL('http://example.com/my'), myRepo.url
		assertEquals true, myRepo.snapshotsEnabled

		Repository anotherRepo = repositories['another-repo']
		assertNotNull anotherRepo
		assertEquals 'another repo', anotherRepo.name
		assertEquals new URL('http://example.com/another'), anotherRepo.url
		assertEquals false, anotherRepo.snapshotsEnabled
	}

	@Test
	void mergeConfigurationDisabledByDefault() {
		def config = load(new ClassPathResource("application-test-default.yml"))
		def customDefaultsConfig = load(new ClassPathResource("application-test-custom-env.yml"))
		def metadata = InitializrMetadataBuilder
				.fromInitializrProperties(config)
				.withInitializrProperties(customDefaultsConfig).build()
		InitializrConfiguration.Env defaultEnv = new InitializrConfiguration().env
		InitializrConfiguration.Env actualEnv = metadata.configuration.env
		assertEquals defaultEnv.artifactRepository, actualEnv.artifactRepository
		assertEquals defaultEnv.springBootMetadataUrl, actualEnv.springBootMetadataUrl
		assertEquals defaultEnv.fallbackApplicationName, actualEnv.fallbackApplicationName
		assertEquals defaultEnv.forceSsl, actualEnv.forceSsl
		assertEquals defaultEnv.kotlin.version, actualEnv.kotlin.version
	}

	@Test
	void mergeConfiguration() {
		def config = load(new ClassPathResource("application-test-default.yml"))
		def customDefaultsConfig = load(new ClassPathResource("application-test-custom-env.yml"))
		def metadata = InitializrMetadataBuilder
				.fromInitializrProperties(config)
				.withInitializrProperties(customDefaultsConfig, true).build()
		InitializrConfiguration.Env defaultEnv = new InitializrConfiguration().env
		InitializrConfiguration.Env actualEnv = metadata.configuration.env
		assertEquals 'https://repo.spring.io/lib-release/', actualEnv.artifactRepository
		assertEquals defaultEnv.springBootMetadataUrl, actualEnv.springBootMetadataUrl
		assertEquals 'FooBarApplication', actualEnv.fallbackApplicationName
		assertEquals false, actualEnv.forceSsl
		assertEquals '1.0.0-beta-2423', actualEnv.kotlin.version
	}

	@Test
	void addDependencyInCustomizer() {
		def group = new DependencyGroup(name: 'Extra')
		def dependency = new Dependency(id: 'com.foo:foo:1.0.0')
		group.content << dependency
		def metadata = InitializrMetadataBuilder.create().withCustomizer(new InitializrMetadataCustomizer() {
			@Override
			void customize(InitializrMetadata metadata) {
				metadata.dependencies.content << group
			}
		}).build()
		assertEquals 1, metadata.dependencies.content.size()
		assertEquals group, metadata.dependencies.content[0]
	}

	private static assertDefaultConfig(InitializrMetadata metadata) {
		assertNotNull metadata
		assertEquals "Wrong number of dependencies", 9, metadata.dependencies.all.size()
		assertEquals "Wrong number of dependency group", 2, metadata.dependencies.content.size()
		assertEquals "Wrong number of types", 4, metadata.types.content.size()
	}

	private static InitializrProperties load(Resource resource) {
		PropertiesConfigurationFactory<InitializrProperties> factory =
				new PropertiesConfigurationFactory<>(InitializrProperties)
		factory.setTargetName("initializr")
		factory.setProperties(loadProperties(resource))
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	private static Properties loadProperties(Resource resource) {
		YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean()
		yamlFactory.setResources(resource)
		yamlFactory.afterPropertiesSet()
		return yamlFactory.getObject()
	}

}
