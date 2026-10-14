/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.version;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link VersionParser}.
 *
 * @author Stephane Nicoll
 */
class VersionParserTests {

	private VersionParser parser = new VersionParser(Collections.emptyList());

	@Test
	void versionWithNoQualifier() {
		Version version = this.parser.parse("1.2.0");
		assertThat(version.toString()).isEqualTo("1.2.0");
	}

	@Test
	void versionWithQualifierAndDotSeparator() {
		Version version = this.parser.parse("1.2.0.RELEASE");
		assertThat(version.toString()).isEqualTo("1.2.0.RELEASE");
	}

	@Test
	void versionWithQualifierAndDashSeparator() {
		Version version = this.parser.parse("1.2.0-SNAPSHOT");
		assertThat(version.toString()).isEqualTo("1.2.0-SNAPSHOT");
	}

	@Test
	void versionWithQualifierVersionAndDotSeparator() {
		Version version = this.parser.parse("1.2.0.RC2");
		assertThat(version.toString()).isEqualTo("1.2.0.RC2");
	}

	@Test
	void versionWithQualifierVersionAndDashSeparator() {
		Version version = this.parser.parse("1.2.0-M3");
		assertThat(version.toString()).isEqualTo("1.2.0-M3");
	}

	@Test
	void parseInvalidVersion() {
		assertThatExceptionOfType(InvalidVersionException.class).isThrownBy(() -> this.parser.parse("foo"));
	}

	@Test
	void safeParseInvalidVersion() {
		assertThat(this.parser.safeParse("foo")).isNull();
	}

	@Test
	void parseVersionWithSpaces() {
		assertThat(this.parser.parse("    1.2.0.RC3  ")).isLessThan(this.parser.parse("1.3.0.RELEASE"));
	}

	@Test
	void parseVariableVersionMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.3.9.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.3.x.BUILD-SNAPSHOT").toString()).isEqualTo("1.3.9.BUILD-SNAPSHOT");
	}

	@Test
	void parseVariableVersionNoPatchMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.3.9.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.x.x.RELEASE").toString()).isEqualTo("1.3.8.RELEASE");
	}

	@Test
	void parseVariableVersionNoQualifierMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.4.0.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.4.x").toString()).isEqualTo("1.4.0.BUILD-SNAPSHOT");
	}

	@Test
	void parseVariableVersionNoMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.3.9.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.4.x.BUILD-SNAPSHOT").toString()).isEqualTo("1.4.999.BUILD-SNAPSHOT");
	}

	@Test
	void parseVariableVersionNoPatchNoMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.3.9.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("2.x.x.RELEASE").toString()).isEqualTo("2.999.999.RELEASE");
	}

	@Test
	void parseVariableVersionNoQualifierNoMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.4.0.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.2.x").toString()).isEqualTo("1.2.999");
	}

	@Test
	void parseVersionWithLargestNumbersThatFitInAnInteger() {
		assertThat(this.parser.parse("999999999.999999999.999999999.M999999999"))
			.hasToString("999999999.999999999.999999999.M999999999");
	}

	@ParameterizedTest
	@ValueSource(
			strings = { "99999999999999.0.0", "1.99999999999999.0", "1.0.99999999999999", "1.0.0.M99999999999999" })
	void parseVersionWithNumberThatDoesNotFitInAnIntegerIsInvalid(String text) {
		assertThatExceptionOfType(InvalidVersionException.class).isThrownBy(() -> this.parser.parse(text));
	}

	@Test
	void resolveLatestWithExactVersionParsesIt() {
		this.parser = createParser("4.0.1", "4.1.2");
		assertThat(this.parser.resolveLatest("4.0.1")).hasToString("4.0.1");
	}

	@Test
	void resolveLatestWithExactVersionThatIsNotConfiguredParsesIt() {
		this.parser = createParser("4.0.1", "4.1.2");
		assertThat(this.parser.resolveLatest("3.5.0")).hasToString("3.5.0");
	}

	@ParameterizedTest
	@ValueSource(strings = { "4", "4.x", "4.x.x" })
	void resolveLatestWithMajorWildcardUsesLatestVersionOfThatMajor(String text) {
		this.parser = createParser("3.5.9", "4.0.1", "4.1.2");
		assertThat(this.parser.resolveLatest(text)).hasToString("4.1.2");
	}

	@ParameterizedTest
	@ValueSource(strings = { "4.0", "4.0.x" })
	void resolveLatestWithMinorWildcardUsesLatestVersionOfThatMinor(String text) {
		this.parser = createParser("4.0.1", "4.0.2", "4.1.2");
		assertThat(this.parser.resolveLatest(text)).hasToString("4.0.2");
	}

	@Test
	void resolveLatestWithWildcardIgnoresPreReleaseVersions() {
		this.parser = createParser("4.0.1", "4.1.0-M2", "4.1.0-SNAPSHOT");
		assertThat(this.parser.resolveLatest("4.x.x")).hasToString("4.0.1");
	}

	@Test
	void resolveLatestWithWildcardConsidersReleaseQualifier() {
		this.parser = createParser("1.5.21.RELEASE", "1.5.22.RELEASE");
		assertThat(this.parser.resolveLatest("1.x.x")).hasToString("1.5.22.RELEASE");
	}

	@Test
	void resolveLatestWithWildcardComparesPatchNumerically() {
		this.parser = createParser("4.0.2", "4.0.10");
		assertThat(this.parser.resolveLatest("4.0.x")).hasToString("4.0.10");
	}

	@ParameterizedTest
	@ValueSource(strings = { "5.x.x", "4.2.x" })
	void resolveLatestWithWildcardWhenNoVersionMatchesReturnsNull(String text) {
		this.parser = createParser("4.0.1", "4.1.2");
		assertThat(this.parser.resolveLatest(text)).isNull();
	}

	@ParameterizedTest
	@ValueSource(strings = { "4.x.3", "4.0.x-M1", "4.x.x-SNAPSHOT", "4.x.3.RELEASE" })
	void resolveLatestWithWildcardThatCannotBeResolvedReturnsNull(String text) {
		this.parser = createParser("4.0.1", "4.0.2-M1");
		assertThat(this.parser.resolveLatest(text)).isNull();
	}

	@Test
	void resolveLatestWithWildcardAndSpaces() {
		this.parser = createParser("4.0.1", "4.0.2");
		assertThat(this.parser.resolveLatest("   4.0.x  ")).hasToString("4.0.2");
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "   ", "nope", "4.", "4.0.0.", "99999999999999", "4.99999999999999",
			"99999999999999.0.0", "4.0.0.M99999999999999" })
	void resolveLatestWithInvalidVersionReturnsNull(String text) {
		this.parser = createParser("4.0.1");
		assertThat(this.parser.resolveLatest(text)).isNull();
	}

	@Test
	void invalidRange() {
		assertThatExceptionOfType(InvalidVersionException.class).isThrownBy(() -> this.parser.parseRange("foo-bar"));
	}

	private VersionParser createParser(String... versions) {
		return new VersionParser(Arrays.stream(versions).map(Version::parse).toList());
	}

}
