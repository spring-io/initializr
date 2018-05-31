/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link VersionParser}.
 *
 * @author Stephane Nicoll
 */
public class VersionParserTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	private VersionParser parser = new VersionParser(Collections.emptyList());

	@Test
	public void noQualifierString() {
		Version version = this.parser.parse("1.2.0");
		assertThat(version.toString()).isEqualTo("1.2.0");
	}

	@Test
	public void withQualifierString() {
		Version version = this.parser.parse("1.2.0.RELEASE");
		assertThat(version.toString()).isEqualTo("1.2.0.RELEASE");
	}

	@Test
	public void withQualifierAndVersionString() {
		Version version = this.parser.parse("1.2.0.RC2");
		assertThat(version.toString()).isEqualTo("1.2.0.RC2");
	}

	@Test
	public void parseInvalidVersion() {
		this.thrown.expect(InvalidVersionException.class);
		this.parser.parse("foo");
	}

	@Test
	public void safeParseInvalidVersion() {
		assertThat(this.parser.safeParse("foo")).isNull();
	}

	@Test
	public void parseVersionWithSpaces() {
		assertThat(this.parser.parse("    1.2.0.RC3  "))
				.isLessThan(this.parser.parse("1.3.0.RELEASE"));
	}

	@Test
	public void parseVariableVersionMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.3.9.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.3.x.BUILD-SNAPSHOT").toString())
				.isEqualTo("1.3.9.BUILD-SNAPSHOT");
	}

	@Test
	public void parseVariableVersionNoPatchMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.3.9.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.x.x.RELEASE").toString())
				.isEqualTo("1.3.8.RELEASE");
	}

	@Test
	public void parseVariableVersionNoQualifierMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.4.0.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.4.x").toString())
				.isEqualTo("1.4.0.BUILD-SNAPSHOT");
	}

	@Test
	public void parseVariableVersionNoMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.3.9.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.4.x.BUILD-SNAPSHOT").toString())
				.isEqualTo("1.4.999.BUILD-SNAPSHOT");
	}

	@Test
	public void parseVariableVersionNoPatchNoMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.3.9.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("2.x.x.RELEASE").toString())
				.isEqualTo("2.999.999.RELEASE");
	}

	@Test
	public void parseVariableVersionNoQualifierNoMatch() {
		List<Version> currentVersions = Arrays.asList(this.parser.parse("1.3.8.RELEASE"),
				this.parser.parse("1.4.0.BUILD-SNAPSHOT"));
		this.parser = new VersionParser(currentVersions);
		assertThat(this.parser.parse("1.2.x").toString()).isEqualTo("1.2.999");
	}

	@Test
	public void invalidRange() {
		this.thrown.expect(InvalidVersionException.class);
		this.parser.parseRange("foo-bar");
	}

}
