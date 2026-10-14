/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.version;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link VersionReference}.
 *
 * @author Stephane Nicoll
 */
class VersionReferenceTests {

	@Test
	void ofPropertyWithVersionProperty() {
		VersionProperty property = VersionProperty.of("test.version", true);
		VersionReference reference = VersionReference.ofProperty(property);
		assertThat(reference.isProperty()).isTrue();
		assertThat(reference.getProperty()).isEqualTo(property);
		assertThat(reference.getValue()).isNull();
		assertThat(reference).hasToString("${test.version}");
	}

	@Test
	void ofPropertyWithInternalProperty() {
		VersionReference reference = VersionReference.ofProperty("test.version");
		assertThat(reference.isProperty()).isTrue();
		VersionProperty property = reference.getProperty();
		assertThat(property).isNotNull();
		assertThat(property.toStandardFormat()).isEqualTo("test.version");
		assertThat(reference.getValue()).isNull();
		assertThat(reference).hasToString("${test.version}");
	}

	@Test
	void ofPropertyWithValue() {
		VersionReference reference = VersionReference.ofValue("1.2.3.RELEASE");
		assertThat(reference.isProperty()).isFalse();
		assertThat(reference.getProperty()).isNull();
		assertThat(reference.getValue()).isEqualTo("1.2.3.RELEASE");
		assertThat(reference).hasToString("1.2.3.RELEASE");
	}

	@Test
	void equalsWithSameValue() {
		assertThat(VersionReference.ofValue("1")).isEqualTo(VersionReference.ofValue("1"));
	}

	@Test
	void equalsWithDifferentValue() {
		assertThat(VersionReference.ofValue("1")).isNotEqualTo(VersionReference.ofValue("2"));
	}

	@Test
	void equalsWithSameProperty() {
		assertThat(VersionReference.ofProperty("test.version")).isEqualTo(VersionReference.ofProperty("test.version"));
	}

	@Test
	void equalsWithDifferentProperty() {
		assertThat(VersionReference.ofProperty("test.version"))
			.isNotEqualTo(VersionReference.ofProperty("another.version"));
	}

	@Test
	void equalsWithDifferentPropertyScope() {
		assertThat(VersionReference.ofProperty(VersionProperty.of("test.version", false)))
			.isNotEqualTo(VersionReference.ofProperty(VersionProperty.of("test.version", true)));
	}

}
