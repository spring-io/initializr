/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A Spring profile name, validated with the same rules as Spring Boot's
 * {@code ProfilesValidator}: letters, digits and {@value #ALLOWED_CHARS}, starting and
 * ending with a letter or digit.
 *
 * @author Moritz Halbritter
 * @see <a href=
 * "https://github.com/spring-projects/spring-boot/blob/main/core/spring-boot/src/main/java/org/springframework/boot/context/config/ProfilesValidator.java">ProfilesValidator</a>
 */
final class ProfileName {

	/**
	 * Characters allowed in a profile name in addition to letters and digits.
	 */
	private static final String ALLOWED_CHARS = "-_.+@";

	/**
	 * {@link #ALLOWED_CHARS} as used in error messages, for example {@code '-', '_'}.
	 */
	private static final String ALLOWED_CHARS_MESSAGE = "'-', '_', '.', '+', '@'";

	private static final String FILE_SUFFIX_SEPARATOR = "-";

	/**
	 * Name of the profile which is active when no profile is set, used when printing
	 * {@link #DEFAULT}.
	 */
	private static final String DEFAULT_NAME = "default";

	/**
	 * The default profile, which has no name and therefore no file name suffix.
	 */
	static final ProfileName DEFAULT = new ProfileName();

	private final @Nullable String name;

	private ProfileName() {
		this.name = null;
	}

	ProfileName(String name) {
		Assert.state(StringUtils.hasText(name), "'name' must not be empty");
		int[] codePoints = name.codePoints().toArray();
		for (int i = 0; i < codePoints.length; i++) {
			boolean letterOrDigit = Character.isLetterOrDigit(codePoints[i]);
			Assert.state(letterOrDigit || ALLOWED_CHARS.indexOf(codePoints[i]) != -1,
					() -> "Profile '%s' must only contain letters, digits or allowed chars (%s)".formatted(name,
							ALLOWED_CHARS_MESSAGE));
			// Only inner characters may be one of the allowed chars, which keeps the
			// profile usable as a file name suffix (for example 'application-..' is
			// rejected)
			boolean innerChar = (i > 0) && (i < codePoints.length - 1);
			Assert.state(letterOrDigit || innerChar,
					() -> "Profile '%s' must start and end with a letter or digit".formatted(name));
		}
		this.name = name;
	}

	/**
	 * Returns the suffix this profile adds to the configuration file name, for example
	 * {@code -dev} for the {@code dev} profile and {@code ""} for {@link #DEFAULT}.
	 * @return the file name suffix
	 */
	String fileSuffix() {
		return (this.name != null) ? FILE_SUFFIX_SEPARATOR + this.name : "";
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ProfileName other)) {
			return false;
		}
		return Objects.equals(this.name, other.name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.name);
	}

	@Override
	public String toString() {
		return (this.name != null) ? this.name : DEFAULT_NAME;
	}

}
