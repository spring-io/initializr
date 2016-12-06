/*
 * Copyright 2012-2016 the original author or authors.
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

package io.spring.initializr.util

import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.lessThan
import static org.junit.Assert.assertNull

/**
 * Tests for {@link VersionParser}.
 *
 * @author Stephane Nicoll
 */
class VersionParserTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none()

	private VersionParser parser = new VersionParser(Collections.EMPTY_LIST)

	@Test
	void noQualifierString() {
		def version = parser.parse('1.2.0')
		assertThat(version.toString(), equalTo('1.2.0'))
	}

	@Test
	void withQualifierString() {
		def version = parser.parse('1.2.0.RELEASE')
		assertThat(version.toString(), equalTo('1.2.0.RELEASE'))
	}

	@Test
	void withQualifierAndVersionString() {
		def version = parser.parse('1.2.0.RC2')
		assertThat(version.toString(), equalTo('1.2.0.RC2'))
	}

	@Test
	void parseInvalidVersion() {
		thrown.expect(InvalidVersionException)
		parser.parse('foo')
	}

	@Test
	void safeParseInvalidVersion() {
		assertNull parser.safeParse('foo')
	}

	@Test
	void parseVersionWithSpaces() {
		assertThat(parser.parse('    1.2.0.RC3  '),
				lessThan(parser.parse('1.3.0.RELEASE')))
	}

	@Test
	void parseVariableVersionMatch() {
		List<Version> currentVersions = Arrays.asList(parser.parse('1.3.8.RELEASE'),
				parser.parse('1.3.9.BUILD-SNAPSHOT'))
		parser = new VersionParser(currentVersions)
		assertThat(parser.parse('1.3.x.BUILD-SNAPSHOT').toString(),
				equalTo('1.3.9.BUILD-SNAPSHOT'))
	}

	@Test
	void parseVariableVersionNoPatchMatch() {
		List<Version> currentVersions = Arrays.asList(parser.parse('1.3.8.RELEASE'),
				parser.parse('1.3.9.BUILD-SNAPSHOT'))
		parser = new VersionParser(currentVersions)
		assertThat(parser.parse('1.x.x.RELEASE').toString(),
				equalTo('1.3.8.RELEASE'))
	}

	@Test
	void parseVariableVersionNoQualifierMatch() {
		List<Version> currentVersions = Arrays.asList(parser.parse('1.3.8.RELEASE'),
				parser.parse('1.4.0.BUILD-SNAPSHOT'))
		parser = new VersionParser(currentVersions)
		assertThat(parser.parse('1.4.x').toString(),
				equalTo('1.4.0.BUILD-SNAPSHOT'))
	}

	@Test
	void parseVariableVersionNoMatch() {
		List<Version> currentVersions = Arrays.asList(parser.parse('1.3.8.RELEASE'),
				parser.parse('1.3.9.BUILD-SNAPSHOT'))
		parser = new VersionParser(currentVersions)
		assertThat(parser.parse('1.4.x.BUILD-SNAPSHOT').toString(),
				equalTo("1.4.999.BUILD-SNAPSHOT"))
	}

	@Test
	void parseVariableVersionNoPatchNoMatch() {
		List<Version> currentVersions = Arrays.asList(parser.parse('1.3.8.RELEASE'),
				parser.parse('1.3.9.BUILD-SNAPSHOT'))
		parser = new VersionParser(currentVersions)
		assertThat(parser.parse('2.x.x.RELEASE').toString(),
				equalTo("2.999.999.RELEASE"))
	}

	@Test
	void parseVariableVersionNoQualifierNoMatch() {
		List<Version> currentVersions = Arrays.asList(parser.parse('1.3.8.RELEASE'),
				parser.parse('1.4.0.BUILD-SNAPSHOT'))
		parser = new VersionParser(currentVersions)
		assertThat(parser.parse('1.2.x').toString(), equalTo("1.2.999"))
	}

	@Test
	void invalidRange() {
		thrown.expect(InvalidVersionException)
		parser.parseRange("foo-bar")
	}

}
