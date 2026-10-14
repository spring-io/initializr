/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
class SingleSelectCapabilityTests {

	@Test
	void defaultEmpty() {
		SingleSelectCapability capability = new SingleSelectCapability("test");
		assertThat(capability.getDefault()).isNull();
	}

	@Test
	void defaultNoDefault() {
		SingleSelectCapability capability = new SingleSelectCapability("test");
		capability.setContent(Arrays.asList(DefaultMetadataElement.create("foo", false),
				DefaultMetadataElement.create("bar", false)));
		assertThat(capability.getDefault()).isNull();
	}

	@Test
	void defaultType() {
		SingleSelectCapability capability = new SingleSelectCapability("test");
		DefaultMetadataElement first = DefaultMetadataElement.create("foo", false);
		DefaultMetadataElement second = DefaultMetadataElement.create("bar", true);
		capability.setContent(Arrays.asList(first, second));
		assertThat(capability.getDefault()).isEqualTo(second);
	}

	@Test
	void mergeAddEntry() {
		SingleSelectCapability capability = new SingleSelectCapability("test");
		DefaultMetadataElement foo = DefaultMetadataElement.create("foo", false);
		capability.setContent(Arrays.asList(foo));

		SingleSelectCapability anotherCapability = new SingleSelectCapability("test");
		DefaultMetadataElement bar = DefaultMetadataElement.create("bar", false);
		anotherCapability.setContent(Arrays.asList(bar));

		capability.merge(anotherCapability);
		assertThat(capability.getContent()).hasSize(2);
		assertThat(capability.get("foo")).isEqualTo(foo);
		assertThat(capability.get("bar")).isEqualTo(bar);
	}

}
