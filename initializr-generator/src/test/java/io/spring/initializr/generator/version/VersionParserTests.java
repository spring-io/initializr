/*
 * Copyright 2012-2019 the original author or authors.
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
import java.util.List;

import org.junit.jupiter.api.Test;

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
	void noQualifierString() {
		Version version = this.parser.parse("1.2.0");
		assertThat(version.toString()).isEqualTo("1.2.0");
	}

	@Test
	void withQualifierString() {
		Version version = this.parser.parse("1.2.0.RELEASE");
		assertThat(version.toString()).isEqualTo("1.2.0.RELEASE");
	}

	@Test
	void withQualifierAndVersionString() {
		Version version = this.parser.parse("1.2.0.RC2");
		assertThat(version.toString()).isEqualTo("1.2.0.RC2");
	}

	@Test
	void parseInvalidVersion() {
		assertThatExceptionOfType(InvalidVersionException.class)
				.isThrownBy(() -> this.parser.parse("foo"));
	}

	@Test
	void safeParseInvalidVersion() {
		assertThat(this.parser.safeParse("foo")).isNull();
	}

	@Test
	void parseVersionWithSpaces() {
		assertThat(this.parser.parse("    1.2.0.RC3  "))
				.isLessThan(this.parser.parse("1.3.0.RELEASE"));
	}

	@Test
	void parseVariableVersionMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.3.9.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.3.x.BUILD-SNAPSHOT").toString())
				.isEqualTo("1.3.9.BUILD-SNAPSHOT");
	}

	@Test
	void parseVariableVersionNoPatchMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.3.9.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.x.x.RELEASE").toString())
				.isEqualTo("1.3.8.RELEASE");
	}

	@Test
	void parseVariableVersionNoQualifierMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.4.0.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.4.x").toString())
				.isEqualTo("1.4.0.BUILD-SNAPSHOT");
	}

	@Test
	void parseVariableVersionNoMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.3.9.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.4.x.BUILD-SNAPSHOT").toString())
				.isEqualTo("1.4.999.BUILD-SNAPSHOT");
	}

	@Test
	void parseVariableVersionNoPatchNoMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.3.9.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("2.x.x.RELEASE").toString())
				.isEqualTo("2.999.999.RELEASE");
	}

	@Test
	void parseVariableVersionNoQualifierNoMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.4.0.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.2.x").toString()).isEqualTo("1.2.999");
	}

	@Test
	void invalidRange() {
		assertThatExceptionOfType(InvalidVersionException.class)
				.isThrownBy(() -> this.parser.parseRange("foo-bar"));
	}

}
