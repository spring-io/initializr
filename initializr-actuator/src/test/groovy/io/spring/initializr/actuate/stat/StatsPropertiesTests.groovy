/*
 * Copyright 2012-2016 the original author or authors.
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

package io.spring.initializr.actuate.stat

import io.spring.initializr.actuate.stat.StatsProperties
import org.junit.Test

import static org.junit.Assert.assertThat
import static org.hamcrest.CoreMatchers.is

/**
 * @author Stephane Nicoll
 */
class StatsPropertiesTests {

	private final StatsProperties properties = new StatsProperties()

	@Test
	void cleanTrailingSlash() {
		properties.elastic.uri = 'http://example.com/'
		assertThat(properties.elastic.uri, is('http://example.com'))
	}

	@Test
	void provideEntityUrl() {
		properties.elastic.uri = 'http://example.com/'
		properties.elastic.indexName = 'my-index'
		properties.elastic.entityName = 'foo'
		assertThat(properties.elastic.entityUrl.toString(),
				is('http://example.com/my-index/foo'))
	}

}
