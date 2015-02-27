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
		def metadata = new InitializrMetadataBuilder().fromConfiguration(bean).build()
		assertNotNull metadata
		assertEquals("Wrong number of dependencies", 9, metadata.dependencies.all.size())
		assertEquals("Wrong number of dependency group", 2, metadata.dependencies.content.size())
		assertEquals("Wrong number of types", 4, metadata.types.content.size())
	}

	@Test
	void addDependencyInCustomizer() {
		def group = new DependencyGroup(name: 'Extra')
		def dependency = new Dependency(id: 'com.foo:foo:1.0.0')
		group.content << dependency
		def metadata = new InitializrMetadataBuilder().withCustomizer(new InitializrMetadataCustomizer() {
			@Override
			void customize(InitializrMetadata metadata) {
				metadata.dependencies.content << group
			}
		}).build()
		assertEquals 1, metadata.dependencies.content.size()
		assertEquals group, metadata.dependencies.content[0]
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
