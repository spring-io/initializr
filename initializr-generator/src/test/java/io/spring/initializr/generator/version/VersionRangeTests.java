/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.version;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import io.spring.initializr.generator.version.Version.Format;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link VersionRange}.
 *
 * @author Stephane Nicoll
 */
class VersionRangeTests {

	@ParameterizedTest(name = "{0} in {1}")
	@MethodSource("matchInRangeParameters")
	void matchWithVersionInTheRange(String version, String range) {
		assertThat(version).is(match(range));
	}

	static Stream<Arguments> matchInRangeParameters() {
		return Stream.of(Arguments.arguments("1.2.2.RELEASE", "[1.2.0.RELEASE,1.2.5.RELEASE]"),
				Arguments.arguments("1.2.2", "[1.2.0,1.2.5]"), Arguments.arguments("1.2.0.M3", "[1.2.0.M1,1.2.0.RC1]"),
				Arguments.arguments("1.2.0-M3", "[1.2.0-M1,1.2.0-RC1]"),
				Arguments.arguments("1.2.0.RC1", "[1.2.0.M1,1.2.0.RC2]"),
				Arguments.arguments("1.2.0-RC1", "[1.2.0-M1,1.2.0-RC2]"),
				Arguments.arguments("1.2.2.BUILD-SNAPSHOT", "[1.2.0.RELEASE,1.2.5.RELEASE]"),
				Arguments.arguments("1.2.2-SNAPSHOT", "[1.2.0,1.2.5]"),
				Arguments.arguments("1.2.0.RELEASE", "[1.2.0.RELEASE,1.2.5.RELEASE]"),
				Arguments.arguments("1.2.0", "[1.2.0,1.2.5]"), Arguments.arguments("1.2.0.M1", "[1.2.0.M1,1.2.0.RC1]"),
				Arguments.arguments("1.2.0-M1", "[1.2.0-M1,1.2.0-RC1]"),
				Arguments.arguments("1.2.0.RC1", "[1.2.0.RC1,1.2.0.RC2]"),
				Arguments.arguments("1.2.0-RC1", "[1.2.0-RC1,1.2.0-RC2]"),
				Arguments.arguments("1.2.2.BUILD-SNAPSHOT", "[1.2.2.BUILD-SNAPSHOT,1.2.5.RELEASE]"),
				Arguments.arguments("1.2.2-SNAPSHOT", "[1.2.2-SNAPSHOT,1.2.5]"),
				Arguments.arguments("1.2.5.RELEASE", "[1.2.0.RELEASE,1.2.5.RELEASE]"),
				Arguments.arguments("1.2.5", "[1.2.0,1.2.5]"), Arguments.arguments("1.2.0.M3", "[1.2.0.M1,1.2.0.M3]"),
				Arguments.arguments("1.2.0-M3", "[1.2.0-M1,1.2.0-M3]"),
				Arguments.arguments("1.2.0.RC2", "[1.2.0.RC1,1.2.0.RC2]"),
				Arguments.arguments("1.2.0-RC2", "[1.2.0-RC1,1.2.0-RC2]"),
				Arguments.arguments("1.2.2.BUILD-SNAPSHOT", "[1.2.0.RELEASE,1.2.2.BUILD-SNAPSHOT]"),
				Arguments.arguments("1.2.2-SNAPSHOT", "[1.2.0,1.2.2-SNAPSHOT]"),
				Arguments.arguments("1.2.0.RELEASE", "1.2.0.RELEASE"), Arguments.arguments("1.2.0", "1.2.0"),
				Arguments.arguments("1.2.0.M3", "1.2.0.M3"), Arguments.arguments("1.2.0-M3", "1.2.0-M3"),
				Arguments.arguments("1.2.0.RC1", "1.2.0.RC1"), Arguments.arguments("1.2.0-RC1", "1.2.0-RC1"),
				Arguments.arguments("1.2.2.BUILD-SNAPSHOT", "1.2.2.BUILD-SNAPSHOT"),
				Arguments.arguments("1.2.2-SNAPSHOT", "1.2.2-SNAPSHOT"),
				Arguments.arguments("1.2.5.RELEASE", "1.2.0.RELEASE"), Arguments.arguments("1.2.5", "1.2.0"),
				Arguments.arguments("2.2.0.M3", "1.2.0.M3"), Arguments.arguments("2.2.0-M3", "1.2.0-M3"),
				Arguments.arguments("1.4.0.RC1", "1.2.0.RC1"), Arguments.arguments("1.4.0-RC1", "1.2.0-RC1"),
				Arguments.arguments("1.3.2.BUILD-SNAPSHOT", "1.2.2.BUILD-SNAPSHOT"),
				Arguments.arguments("1.3.2-SNAPSHOT", "1.2.2-SNAPSHOT"));
	}

	@ParameterizedTest(name = "{0} in {1}")
	@MethodSource("matchOutRangeParameters")
	void matchWithVersionOutTheRange(String version, String range) {
		assertThat(version).isNot(match(range));
	}

	static Stream<Arguments> matchOutRangeParameters() {
		return Stream.of(Arguments.arguments("1.1.9.RELEASE", "[1.2.0.RELEASE,1.2.5.RELEASE]"),
				Arguments.arguments("1.1.9", "[1.2.0,1.2.5]"),
				Arguments.arguments("1.2.6.RELEASE", "[1.2.0.RELEASE,1.2.5.RELEASE]"),
				Arguments.arguments("1.2.6", "[1.2.0,1.2.5]"), Arguments.arguments("1.2.0.M2", "[1.2.0.M3,1.2.0.RC1]"),
				Arguments.arguments("1.2.0-M2", "[1.2.0-M3,1.2.0-RC1]"),
				Arguments.arguments("1.2.0.M4", "[1.2.0.M1,1.2.0.M3]"),
				Arguments.arguments("1.2.0-M4", "[1.2.0-M1,1.2.0-M3]"),
				Arguments.arguments("1.2.0.RC1", "[1.2.0.RC2,1.2.0.RC3]"),
				Arguments.arguments("1.2.0-RC1", "[1.2.0-RC2,1.2.0-RC3]"),
				Arguments.arguments("1.2.0.RC4", "[1.2.0.RC2,1.2.0.RC3]"),
				Arguments.arguments("1.2.0-RC4", "[1.2.0-RC2,1.2.0-RC3]"),
				Arguments.arguments("1.1.9.BUILD-SNAPSHOT", "[1.2.0.RELEASE,1.2.5.RELEASE]"),
				Arguments.arguments("1.1.9-SNAPSHOT", "[1.2.0,1.2.5]"),
				Arguments.arguments("1.2.6.BUILD-SNAPSHOT", "[1.2.0.RELEASE,1.2.5.RELEASE]"),
				Arguments.arguments("1.2.6-SNAPSHOT", "[1.2.0,1.2.5]"),
				Arguments.arguments("1.2.0.RELEASE", "(1.2.0.RELEASE,1.2.5.RELEASE]"),
				Arguments.arguments("1.2.0", "(1.2.0,1.2.5]"), Arguments.arguments("1.2.0.M1", "(1.2.0.M1,1.2.0.RC1]"),
				Arguments.arguments("1.2.0-M1", "(1.2.0-M1,1.2.0-RC1]"),
				Arguments.arguments("1.2.0.RC1", "(1.2.0.RC1,1.2.0.RC2]"),
				Arguments.arguments("1.2.0-RC1", "(1.2.0-RC1,1.2.0-RC2]"),
				Arguments.arguments("1.2.2.BUILD-SNAPSHOT", "(1.2.2.BUILD-SNAPSHOT,1.2.5.RELEASE]"),
				Arguments.arguments("1.2.2-SNAPSHOT", "(1.2.2-SNAPSHOT,1.2.5]"),
				Arguments.arguments("1.2.5.RELEASE", "[1.2.0.RELEASE,1.2.5.RELEASE)"),
				Arguments.arguments("1.2.5", "[1.2.0,1.2.5)"), Arguments.arguments("1.2.0.M3", "[1.2.0.M1,1.2.0.M3)"),
				Arguments.arguments("1.2.0-M3", "[1.2.0-M1,1.2.0-M3)"),
				Arguments.arguments("1.2.0.RC2", "[1.2.0.RC1,1.2.0.RC2)"),
				Arguments.arguments("1.2.0-RC2", "[1.2.0-RC1,1.2.0-RC2)"),
				Arguments.arguments("1.2.2.BUILD-SNAPSHOT", "[1.2.0.RELEASE,1.2.2.BUILD-SNAPSHOT)"),
				Arguments.arguments("1.2.2-SNAPSHOT", "[1.2.0,1.2.2-SNAPSHOT)"),
				Arguments.arguments("1.2.0.RELEASE", "1.2.1.RELEASE"), Arguments.arguments("1.2.0", "1.2.1"),
				Arguments.arguments("1.2.0.M2", "1.2.0.M3"), Arguments.arguments("1.2.0-M2", "1.2.0-M3"),
				Arguments.arguments("1.2.0.RC1", "1.2.0.RC2"), Arguments.arguments("1.2.0-RC1", "1.2.0-RC2"),
				Arguments.arguments("1.2.1.BUILD-SNAPSHOT", "1.2.2.BUILD-SNAPSHOT"),
				Arguments.arguments("1.2.1-SNAPSHOT", "1.2.2-SNAPSHOT"));
	}

	@Test
	void rangeWithSpaces() {
		assertThat("1.2.0.RC3").is(match("[   1.2.0.RC1 ,  1.2.0.RC5]"));
	}

	@Test
	void matchLatestVersion() {
		assertThat("1.2.8.RELEASE").is(match("[1.2.0.RELEASE,1.2.x.BUILD-SNAPSHOT]",
				new VersionParser(Collections.singletonList(Version.parse("1.2.9.BUILD-SNAPSHOT")))));
	}

	@Test
	void matchOverLatestVersion() {
		assertThat("1.2.10.RELEASE").isNot(match("[1.2.0.RELEASE,1.2.x.BUILD-SNAPSHOT]",
				new VersionParser(Collections.singletonList(Version.parse("1.2.9.BUILD-SNAPSHOT")))));
	}

	@Test
	void matchAsOfCurrentVersion() {
		assertThat("1.3.5.RELEASE").is(match("[1.3.x.RELEASE,1.3.x.BUILD-SNAPSHOT]", new VersionParser(
				Arrays.asList(Version.parse("1.3.4.RELEASE"), Version.parse("1.3.6.BUILD-SNAPSHOT")))));
	}

	@Test
	void matchOverAsOfCurrentVersion() {
		assertThat("1.3.5.RELEASE").isNot(match("[1.3.x.RELEASE,1.3.x.BUILD-SNAPSHOT]", new VersionParser(
				Arrays.asList(Version.parse("1.3.7.RELEASE"), Version.parse("1.3.6.BUILD-SNAPSHOT")))));
	}

	@Test
	void simpleStartingRange() {
		assertThat(new VersionRange(Version.parse("1.3.0.RELEASE")).toString()).isEqualTo(">=1.3.0.RELEASE");
	}

	@Test
	void toVersionRangeWithSimpleVersion() {
		VersionRange range = new VersionParser(Collections.singletonList(Version.parse("1.5.6.RELEASE")))
				.parseRange("1.3.5.RELEASE");
		assertThat(range.toRangeString()).isEqualTo("1.3.5.RELEASE");
	}

	@Test
	void toVersionRangeWithVersionsIncluded() {
		VersionRange range = new VersionParser(Collections.singletonList(Version.parse("1.5.6.RELEASE")))
				.parseRange("[1.3.5.RELEASE,1.5.5.RELEASE]");
		assertThat(range.toRangeString()).isEqualTo("[1.3.5.RELEASE,1.5.5.RELEASE]");
	}

	@Test
	void toVersionRangeWithLowerVersionExcluded() {
		VersionRange range = new VersionParser(Collections.singletonList(Version.parse("1.5.6.RELEASE")))
				.parseRange("(1.3.5.RELEASE,1.5.5.RELEASE]");
		assertThat(range.toRangeString()).isEqualTo("(1.3.5.RELEASE,1.5.5.RELEASE]");
	}

	@Test
	void toVersionRangeWithHigherVersionExcluded() {
		VersionRange range = new VersionParser(Collections.singletonList(Version.parse("1.5.6.RELEASE")))
				.parseRange("[1.3.5.RELEASE,1.5.5.RELEASE)");
		assertThat(range.toRangeString()).isEqualTo("[1.3.5.RELEASE,1.5.5.RELEASE)");
	}

	@Test
	void toVersionRangeWithVersionsExcluded() {
		VersionRange range = new VersionParser(Collections.singletonList(Version.parse("1.5.6.RELEASE")))
				.parseRange("(1.3.5.RELEASE,1.5.5.RELEASE)");
		assertThat(range.toRangeString()).isEqualTo("(1.3.5.RELEASE,1.5.5.RELEASE)");
	}

	@Test
	void formatLowerOnlyV1toV2() {
		VersionRange range = parse("1.2.0.RELEASE").format(Format.V2);
		assertThat(range.toRangeString()).isEqualTo("1.2.0");
	}

	@Test
	void formatV1toV2() {
		VersionRange range = parse("[1.2.0.RELEASE,1.3.0.M1)").format(Format.V2);
		assertThat(range.toRangeString()).isEqualTo("[1.2.0,1.3.0-M1)");
	}

	@Test
	void formatLowerOnlyV2toV1() {
		VersionRange range = parse("1.2.0").format(Format.V1);
		assertThat(range.toRangeString()).isEqualTo("1.2.0.RELEASE");
	}

	@Test
	void formatV2toV1() {
		VersionRange range = parse("[1.2.0,1.3.0-M1)").format(Format.V1);
		assertThat(range.toRangeString()).isEqualTo("[1.2.0.RELEASE,1.3.0.M1)");
	}

	private static VersionRange parse(String text) {
		return new VersionParser(Collections.emptyList()).parseRange(text);
	}

	private static Condition<String> match(String range) {
		return match(range, new VersionParser(Collections.emptyList()));
	}

	private static Condition<String> match(String range, VersionParser parser) {
		return new VersionRangeCondition(range, parser);
	}

	static class VersionRangeCondition extends Condition<String> {

		private final VersionRange range;

		private final VersionParser parser;

		VersionRangeCondition(String text, VersionParser parser) {
			this.parser = parser;
			this.range = parser.parseRange(text);
			as(this.range.toString());
		}

		@Override
		public boolean matches(String value) {
			return this.range.match(this.parser.parse(value));
		}

	}

}
