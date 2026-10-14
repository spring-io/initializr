/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.version;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import io.spring.initializr.generator.version.Version.Format;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Version}.
 *
 * @author Stephane Nicoll
 */
class VersionTests {

	private final VersionParser parser = new VersionParser(Collections.emptyList());

	@Test
	void equalNoQualifier() {
		Version first = parse("1.2.0");
		Version second = parse("1.2.0");
		assertThat(first).isEqualByComparingTo(second);
		assertThat(first).isEqualTo(second);
	}

	@Test
	void equalQualifierNoVersion() {
		Version first = parse("1.2.0.RELEASE");
		Version second = parse("1.2.0.RELEASE");
		assertThat(first).isEqualByComparingTo(second);
		assertThat(first).isEqualTo(second);
	}

	@Test
	void equalQualifierVersion() {
		Version first = parse("1.2.0.RC1");
		Version second = parse("1.2.0.RC1");
		assertThat(first).isEqualByComparingTo(second);
		assertThat(first).isEqualTo(second);
	}

	@Test
	void compareMajorOnly() {
		assertThat(parse("2.2.0")).isGreaterThan(parse("1.8.0"));
	}

	@Test
	void compareMinorOnly() {
		assertThat(parse("2.2.0")).isGreaterThan(parse("2.1.9"));
	}

	@Test
	void comparePatchOnly() {
		assertThat(parse("2.2.4")).isGreaterThan(parse("2.2.3"));
	}

	@Test
	void compareHigherVersion() {
		assertThat(parse("1.2.0.RELEASE")).isGreaterThan(parse("1.1.9.RELEASE"));
	}

	@Test
	void compareHigherQualifier() {
		assertThat(parse("1.2.0.RC1")).isGreaterThan(parse("1.2.0.M1"));
	}

	@Test
	void compareHigherQualifierVersion() {
		assertThat(parse("1.2.0.RC2")).isGreaterThan(parse("1.2.0.RC1"));
	}

	@Test
	void compareLowerVersion() {
		assertThat(parse("1.0.5.RELEASE")).isLessThan(parse("1.1.9.RELEASE"));
	}

	@Test
	void compareLowerQualifier() {
		assertThat(parse("1.2.0.RC1")).isLessThan(parse("1.2.0.RELEASE"));
	}

	@Test
	void compareLessQualifierVersion() {
		assertThat(parse("1.2.0.RC2")).isLessThan(parse("1.2.0.RC3"));
	}

	@Test
	void compareWithNull() {
		assertThat(parse("1.2.0.RC2")).isGreaterThan(null);
	}

	@Test
	void compareUnknownQualifier() {
		assertThat(parse("1.2.0.Beta")).isLessThan(parse("1.2.0.CR"));
	}

	@Test
	void compareUnknownQualifierVersion() {
		assertThat(parse("1.2.0.Beta1")).isLessThan(parse("1.2.0.Beta2"));
	}

	@Test
	void snapshotGreaterThanRC() {
		assertThat(parse("1.2.0.BUILD-SNAPSHOT")).isGreaterThan(parse("1.2.0.RC1"));
	}

	@Test
	void snapshotLowerThanRelease() {
		assertThat(parse("1.2.0.BUILD-SNAPSHOT")).isLessThan(parse("1.2.0.RELEASE"));
	}

	@Test
	void orderVersionSchemeWithQualifiedVersions() {
		List<String> sortedVersions = Stream
			.of("2.3.0.BUILD-SNAPSHOT", "2.3.0.RC1", "2.3.0.M2", "2.3.0.M1", "2.3.0.RELEASE", "2.3.0.RC2")
			.map(this::parse)
			.sorted()
			.map(Version::toString)
			.toList();
		assertThat(sortedVersions).containsExactly("2.3.0.M1", "2.3.0.M2", "2.3.0.RC1", "2.3.0.RC2",
				"2.3.0.BUILD-SNAPSHOT", "2.3.0.RELEASE");
	}

	@Test
	void orderVersionSchemeWithSemVer() {
		List<String> sortedVersions = Stream
			.of("2.3.0-SNAPSHOT", "2.3.0-RC1", "2.3.0-M2", "2.3.0-M1", "2.3.0", "2.3.0-RC2")
			.map(this::parse)
			.sorted()
			.map(Version::toString)
			.toList();
		assertThat(sortedVersions).containsExactly("2.3.0-M1", "2.3.0-M2", "2.3.0-RC1", "2.3.0-RC2", "2.3.0-SNAPSHOT",
				"2.3.0");
	}

	@Test
	void orderVersionSchemeWithCalVer() {
		List<String> sortedVersions = Stream
			.of("2020.0.0-SNAPSHOT", "2020.0.0-RC1", "2020.0.0-M2", "2020.0.0-M1", "2020.0.0", "2020.0.0-RC2")
			.map(this::parse)
			.sorted()
			.map(Version::toString)
			.toList();
		assertThat(sortedVersions).containsExactly("2020.0.0-M1", "2020.0.0-M2", "2020.0.0-RC1", "2020.0.0-RC2",
				"2020.0.0-SNAPSHOT", "2020.0.0");
	}

	@Test
	void formatV1toV1() {
		Version version = Version.parse("1.2.0.RELEASE");
		assertThat(version.format(Format.V1)).isSameAs(version);
	}

	@Test
	void formatV1SnapshotToV2() {
		Version version = Version.parse("1.2.0.BUILD-SNAPSHOT");
		assertThat(version.format(Format.V2)).hasToString("1.2.0-SNAPSHOT");
	}

	@Test
	void formatV1GAToV2() {
		Version version = Version.parse("1.2.0.RELEASE");
		assertThat(version.format(Format.V2)).hasToString("1.2.0");
	}

	@Test
	void formatNoQualifierToV1() {
		Version version = Version.parse("1.2.0");
		assertThat(version.format(Format.V1)).hasToString("1.2.0.RELEASE");
	}

	@Test
	void formatV2toV2() {
		Version version = Version.parse("1.2.0-RC1");
		assertThat(version.format(Format.V2)).isSameAs(version);
	}

	@Test
	void formatV2SnapshotToV1() {
		Version version = Version.parse("1.2.0-SNAPSHOT");
		assertThat(version.format(Format.V1)).hasToString("1.2.0.BUILD-SNAPSHOT");
	}

	@Test
	void formatV2GAToV1() {
		Version version = Version.parse("1.2.0");
		assertThat(version.format(Format.V1)).hasToString("1.2.0.RELEASE");
	}

	@Test
	void formatNoQualifierToV2() {
		Version version = Version.parse("1.2.0");
		assertThat(version.format(Format.V2)).hasToString("1.2.0");
	}

	@ParameterizedTest
	@ValueSource(strings = { "1.2.0", "1.2.0.RELEASE" })
	void isGeneralAvailabilityWithGaVersion(String text) {
		assertThat(Version.parse(text).isGeneralAvailability()).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = { "1.2.0-M1", "1.2.0.RC1", "1.2.0-SNAPSHOT", "1.2.0.BUILD-SNAPSHOT" })
	void isGeneralAvailabilityWithPreReleaseVersion(String text) {
		assertThat(Version.parse(text).isGeneralAvailability()).isFalse();
	}

	private Version parse(String text) {
		return this.parser.parse(text);
	}

}
