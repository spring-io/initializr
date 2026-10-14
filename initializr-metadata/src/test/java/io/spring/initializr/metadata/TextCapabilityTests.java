/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
class TextCapabilityTests {

	@Test
	void mergeValue() {
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
	void mergeTitle() {
		TextCapability capability = new TextCapability("foo", "Foo", "my desc");
		capability.merge(new TextCapability("foo", "AnotherFoo", ""));
		assertThat(capability.getId()).isEqualTo("foo");
		assertThat(capability.getType()).isEqualTo(ServiceCapabilityType.TEXT);
		assertThat(capability.getTitle()).isEqualTo("AnotherFoo");
		assertThat(capability.getDescription()).isEqualTo("my desc");
	}

	@Test
	void mergeDescription() {
		TextCapability capability = new TextCapability("foo", "Foo", "my desc");
		capability.merge(new TextCapability("foo", "", "another desc"));
		assertThat(capability.getId()).isEqualTo("foo");
		assertThat(capability.getType()).isEqualTo(ServiceCapabilityType.TEXT);
		assertThat(capability.getTitle()).isEqualTo("Foo");
		assertThat(capability.getDescription()).isEqualTo("another desc");
	}

}
