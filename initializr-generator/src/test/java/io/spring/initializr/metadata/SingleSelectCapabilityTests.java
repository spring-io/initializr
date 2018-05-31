/*
 * Copyright 2012-2018 the original author or authors.
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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
public class SingleSelectCapabilityTests {

	@Test
	public void defaultEmpty() {
		SingleSelectCapability capability = new SingleSelectCapability("test");
		assertThat(capability.getDefault()).isNull();
	}

	@Test
	public void defaultNoDefault() {
		SingleSelectCapability capability = new SingleSelectCapability("test");
		capability.getContent().add(DefaultMetadataElement.create("foo", false));
		capability.getContent().add(DefaultMetadataElement.create("bar", false));
		assertThat(capability.getDefault()).isNull();
	}

	@Test
	public void defaultType() {
		SingleSelectCapability capability = new SingleSelectCapability("test");
		capability.getContent().add(DefaultMetadataElement.create("foo", false));
		DefaultMetadataElement second = DefaultMetadataElement.create("bar", true);
		capability.getContent().add(second);
		assertThat(capability.getDefault()).isEqualTo(second);
	}

	@Test
	public void mergeAddEntry() {
		SingleSelectCapability capability = new SingleSelectCapability("test");
		DefaultMetadataElement foo = DefaultMetadataElement.create("foo", false);
		capability.getContent().add(foo);

		SingleSelectCapability anotherCapability = new SingleSelectCapability("test");
		DefaultMetadataElement bar = DefaultMetadataElement.create("bar", false);
		anotherCapability.getContent().add(bar);

		capability.merge(anotherCapability);
		assertThat(capability.getContent()).hasSize(2);
		assertThat(capability.get("foo")).isEqualTo(foo);
		assertThat(capability.get("bar")).isEqualTo(bar);
	}

}
