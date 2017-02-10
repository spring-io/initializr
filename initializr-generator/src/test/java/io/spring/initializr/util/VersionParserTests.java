/*
 * Copyright 2012-2017 the original author or authors.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertNull;

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
		Version version = parser.parse("1.2.0");
		assertThat(version.toString(), equalTo("1.2.0"));
	}

	@Test
	public void withQualifierString() {
		Version version = parser.parse("1.2.0.RELEASE");
		assertThat(version.toString(), equalTo("1.2.0.RELEASE"));
	}

	@Test
	public void withQualifierAndVersionString() {
		Version version = parser.parse("1.2.0.RC2");
		assertThat(version.toString(), equalTo("1.2.0.RC2"));
	}

	@Test
	public void parseInvalidVersion() {
		thrown.expect(InvalidVersionException.class);
		parser.parse("foo");
	}

	@Test
	public void safeParseInvalidVersion() {
		assertNull(parser.safeParse("foo"));
	}

	@Test
	public void parseVersionWithSpaces() {
		assertThat(parser.parse("    1.2.0.RC3  "),
				lessThan(parser.parse("1.3.0.RELEASE")));
	}

	@Test
	public void parseVariableVersionMatch() {
		List<Version> currentVersions = Arrays.asList(parser.parse("1.3.8.RELEASE"),
				parser.parse("1.3.9.BUILD-SNAPSHOT"));
		parser = new VersionParser(currentVersions);
		assertThat(parser.parse("1.3.x.BUILD-SNAPSHOT").toString(),
				equalTo("1.3.9.BUILD-SNAPSHOT"));
	}

	@Test
	public void parseVariableVersionNoPatchMatch() {
		List<Version> currentVersions = Arrays.asList(parser.parse("1.3.8.RELEASE"),
				parser.parse("1.3.9.BUILD-SNAPSHOT"));
		parser = new VersionParser(currentVersions);
		assertThat(parser.parse("1.x.x.RELEASE").toString(),
				equalTo("1.3.8.RELEASE"));
	}

	@Test
	public void parseVariableVersionNoQualifierMatch() {
		List<Version> currentVersions = Arrays.asList(parser.parse("1.3.8.RELEASE"),
				parser.parse("1.4.0.BUILD-SNAPSHOT"));
		parser = new VersionParser(currentVersions);
		assertThat(parser.parse("1.4.x").toString(),
				equalTo("1.4.0.BUILD-SNAPSHOT"));
	}

	@Test
	public void parseVariableVersionNoMatch() {
		List<Version> currentVersions = Arrays.asList(parser.parse("1.3.8.RELEASE"),
				parser.parse("1.3.9.BUILD-SNAPSHOT"));
		parser = new VersionParser(currentVersions);
		assertThat(parser.parse("1.4.x.BUILD-SNAPSHOT").toString(),
				equalTo("1.4.999.BUILD-SNAPSHOT"));
	}

	@Test
	public void parseVariableVersionNoPatchNoMatch() {
		List<Version> currentVersions = Arrays.asList(parser.parse("1.3.8.RELEASE"),
				parser.parse("1.3.9.BUILD-SNAPSHOT"));
		parser = new VersionParser(currentVersions);
		assertThat(parser.parse("2.x.x.RELEASE").toString(),
				equalTo("2.999.999.RELEASE"));
	}

	@Test
	public void parseVariableVersionNoQualifierNoMatch() {
		List<Version> currentVersions = Arrays.asList(parser.parse("1.3.8.RELEASE"),
				parser.parse("1.4.0.BUILD-SNAPSHOT"));
		parser = new VersionParser(currentVersions);
		assertThat(parser.parse("1.2.x").toString(), equalTo("1.2.999"));
	}

	@Test
	public void invalidRange() {
		thrown.expect(InvalidVersionException.class);
		parser.parseRange("foo-bar");
	}

}
