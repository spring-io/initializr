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

package io.spring.initializr.actuate.stat;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Stephane Nicoll
 */
public class StatsPropertiesTests {

	private final StatsProperties properties = new StatsProperties();

	@Test
	public void cleanTrailingSlash() {
		properties.getElastic().setUri("http://example.com/");
		assertThat(properties.getElastic().getUri(), is("http://example.com"));
	}

	@Test
	public void provideEntityUrl() {
		properties.getElastic().setUri("http://example.com/");
		properties.getElastic().setIndexName("my-index");
		properties.getElastic().setEntityName("foo");
		assertThat(properties.getElastic().getEntityUrl().toString(),
				is("http://example.com/my-index/foo"));
	}

}
