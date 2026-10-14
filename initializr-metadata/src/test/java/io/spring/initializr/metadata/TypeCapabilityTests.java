/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
class TypeCapabilityTests {

	@Test
	void defaultEmpty() {
		TypeCapability capability = new TypeCapability();
		assertThat(capability.getDefault()).isNull();
	}

	@Test
	void defaultNoDefault() {
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
	void defaultType() {
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
	void mergeAddEntry() {
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
