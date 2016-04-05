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

package io.spring.initializr.web.mapper

import groovy.json.JsonSlurper
import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * @author Stephane Nicoll
 */
class InitializrMetadataJsonMapperTests {

	private final InitializrMetadataJsonMapper jsonMapper = new InitializrMetadataV21JsonMapper()
	private final JsonSlurper slurper = new JsonSlurper()

	@Test
	void withNoAppUrl() {
		InitializrMetadata metadata = new InitializrMetadataTestBuilder().addType('foo', true, '/foo.zip', 'none', 'test')
				.addDependencyGroup('foo', 'one', 'two').build()
		def json = jsonMapper.write(metadata, null)
		def result = slurper.parseText(json)
		assertEquals '/foo.zip?type=foo{&dependencies,packaging,javaVersion,language,bootVersion,' +
				'groupId,artifactId,version,name,description,packageName}', result._links.foo.href
	}

	@Test
	void withAppUrl() {
		InitializrMetadata metadata = new InitializrMetadataTestBuilder().addType('foo', true, '/foo.zip', 'none', 'test')
				.addDependencyGroup('foo', 'one', 'two').build()
		def json = jsonMapper.write(metadata, 'http://server:8080/my-app')
		def result = slurper.parseText(json)
		assertEquals 'http://server:8080/my-app/foo.zip?type=foo{&dependencies,packaging,javaVersion,' +
				'language,bootVersion,groupId,artifactId,version,name,description,packageName}',
				result._links.foo.href
	}

}
