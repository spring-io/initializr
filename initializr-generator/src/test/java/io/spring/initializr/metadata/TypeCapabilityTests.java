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
public class TypeCapabilityTests {

	@Test
	public void defaultEmpty() {
		TypeCapability capability = new TypeCapability();
		assertThat(capability.getDefault()).isNull();
	}

	@Test
	public void defaultNoDefault() {
		TypeCapability capability = new TypeCapability();
		Type first = new Type();
		first.setId("foo");
		first.setDefault(false);
		Type second = new Type();
		second.setId("bar");
		second.setDefault(false);
		capability.getContent().add(first);
		capability.getContent().add(second);
		assertThat(capability.getDefault()).isNull();
	}

	@Test
	public void defaultType() {
		TypeCapability capability = new TypeCapability();
		Type first = new Type();
		first.setId("foo");
		first.setDefault(false);
		Type second = new Type();
		second.setId("bar");
		second.setDefault(true);
		capability.getContent().add(first);
		capability.getContent().add(second);
		assertThat(capability.getDefault()).isEqualTo(second);
	}

	@Test
	public void mergeAddEntry() {
		TypeCapability capability = new TypeCapability();
		Type first = new Type();
		first.setId("foo");
		first.setDefault(false);
		capability.getContent().add(first);

		TypeCapability anotherCapability = new TypeCapability();
		Type another = new Type();
		another.setId("foo");
		another.setDefault(false);
		Type second = new Type();
		second.setId("bar");
		second.setDefault(true);
		anotherCapability.getContent().add(another);
		anotherCapability.getContent().add(second);

		capability.merge(anotherCapability);
		assertThat(capability.getContent()).hasSize(2);
		assertThat(capability.get("foo")).isEqualTo(first);
		assertThat(capability.get("bar")).isEqualTo(second);
		assertThat(capability.getDefault()).isEqualTo(second);
	}

}
