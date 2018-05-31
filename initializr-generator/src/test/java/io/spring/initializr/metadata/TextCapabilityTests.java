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
public class TextCapabilityTests {

	@Test
	public void mergeValue() {
		TextCapability capability = new TextCapability("foo");
		capability.setContent("1234");
		TextCapability another = new TextCapability("foo");
		another.setContent("4567");
		capability.merge(another);
		assertThat(capability.getId()).isEqualTo("foo");
		assertThat(capability.getType()).isEqualTo(ServiceCapabilityType.TEXT);
		assertThat(capability.getContent()).isEqualTo("4567");
	}

	@Test
	public void mergeTitle() {
		TextCapability capability = new TextCapability("foo", "Foo", "my desc");
		capability.merge(new TextCapability("foo", "AnotherFoo", ""));
		assertThat(capability.getId()).isEqualTo("foo");
		assertThat(capability.getType()).isEqualTo(ServiceCapabilityType.TEXT);
		assertThat(capability.getTitle()).isEqualTo("AnotherFoo");
		assertThat(capability.getDescription()).isEqualTo("my desc");
	}

	@Test
	public void mergeDescription() {
		TextCapability capability = new TextCapability("foo", "Foo", "my desc");
		capability.merge(new TextCapability("foo", "", "another desc"));
		assertThat(capability.getId()).isEqualTo("foo");
		assertThat(capability.getType()).isEqualTo(ServiceCapabilityType.TEXT);
		assertThat(capability.getTitle()).isEqualTo("Foo");
		assertThat(capability.getDescription()).isEqualTo("another desc");
	}

}
