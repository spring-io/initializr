/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link ProfileName}.
 *
 * @author Moritz Halbritter
 */
class ProfileNameTests {

	@ParameterizedTest
	@ValueSource(strings = { "dev", "integration-test_1.2+a@b", "a", "default" })
	void shouldAcceptValidProfile(String name) {
		assertThat(new ProfileName(name)).hasToString(name);
	}

	@ParameterizedTest
	@ValueSource(strings = { "  ", "" })
	void shouldRejectEmptyProfile(String name) {
		assertThatIllegalStateException().isThrownBy(() -> new ProfileName(name))
			.withMessage("'name' must not be empty");
	}

	@ParameterizedTest
	@ValueSource(strings = { "a/b", "a\\b", " dev ", "a b", "a\uD83D\uDE00b" })
	void shouldRejectDisallowedChars(String name) {
		assertThatIllegalStateException().isThrownBy(() -> new ProfileName(name))
			.withMessage("Profile '%s' must only contain letters, digits or allowed chars ('-', '_', '.', '+', '@')"
				.formatted(name));
	}

	@ParameterizedTest
	@ValueSource(strings = { "..", "-dev", "dev-", "." })
	void shouldRejectProfileNotStartingOrEndingWithLetterOrDigit(String name) {
		assertThatIllegalStateException().isThrownBy(() -> new ProfileName(name))
			.withMessage("Profile '%s' must start and end with a letter or digit".formatted(name));
	}

	@Test
	void shouldUseDashPrefixedNameAsFileSuffix() {
		assertThat(new ProfileName("dev").fileSuffix()).isEqualTo("-dev");
	}

	@Test
	void shouldHaveNoFileSuffixForDefaultProfile() {
		assertThat(ProfileName.DEFAULT.fileSuffix()).isEmpty();
	}

	@Test
	void shouldBeEqualForSameName() {
		assertThat(new ProfileName("dev")).isEqualTo(new ProfileName("dev"))
			.hasSameHashCodeAs(new ProfileName("dev"))
			.isNotEqualTo(new ProfileName("prod"))
			.isNotEqualTo(ProfileName.DEFAULT);
	}

	@Test
	void shouldUseDefaultAsToStringForDefaultProfile() {
		assertThat(ProfileName.DEFAULT).hasToString("default");
	}

}
