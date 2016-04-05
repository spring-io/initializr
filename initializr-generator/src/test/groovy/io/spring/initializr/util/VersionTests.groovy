/*
 * Copyright 2012-2015 the original author or authors.
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

import static io.spring.initializr.util.Version.parse
import static io.spring.initializr.util.Version.safeParse
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertNull

/**
 * @author Stephane Nicoll
 */
class VersionTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none()

	@Test
	void noQualifierString() {
		def version = parse('1.2.0')
		assertThat(version.toString(), equalTo('1.2.0'))
	}

	@Test
	void withQualifierString() {
		def version = parse('1.2.0.RELEASE')
		assertThat(version.toString(), equalTo('1.2.0.RELEASE'))
	}

	@Test
	void withQualifierAndVersionString() {
		def version = parse('1.2.0.RC2')
		assertThat(version.toString(), equalTo('1.2.0.RC2'))
	}

	@Test
	void equalNoQualifier() {
		def first = parse('1.2.0')
		def second = parse('1.2.0')
		assertThat(first, comparesEqualTo(second))
		assertThat(first, equalTo(second))
	}

	@Test
	void equalQualifierNoVersion() {
		def first = parse('1.2.0.RELEASE')
		def second = parse('1.2.0.RELEASE')
		assertThat(first, comparesEqualTo(second))
		assertThat(first, equalTo(second))
	}

	@Test
	void equalQualifierVersion() {
		def first = parse('1.2.0.RC1')
		def second = parse('1.2.0.RC1')
		assertThat(first, comparesEqualTo(second))
		assertThat(first, equalTo(second))
	}

	@Test
	void compareMajorOnly() {
		assertThat(parse('2.2.0'), greaterThan(parse('1.8.0')))
	}

	@Test
	void compareMinorOnly() {
		assertThat(parse('2.2.0'), greaterThan(parse('2.1.9')))
	}

	@Test
	void comparePatchOnly() {
		assertThat(parse('2.2.4'), greaterThan(parse('2.2.3')))
	}

	@Test
	void compareHigherVersion() {
		assertThat(parse('1.2.0.RELEASE'), greaterThan(parse('1.1.9.RELEASE')))
	}

	@Test
	void compareHigherQualifier() {
		assertThat(parse('1.2.0.RC1'), greaterThan(parse('1.2.0.M1')))
	}

	@Test
	void compareHigherQualifierVersion() {
		assertThat(parse('1.2.0.RC2'), greaterThan(parse('1.2.0.RC1')))
	}

	@Test
	void compareLowerVersion() {
		assertThat(parse('1.0.5.RELEASE'), lessThan(parse('1.1.9.RELEASE')))
	}

	@Test
	void compareLowerQualifier() {
		assertThat(parse('1.2.0.RC1'), lessThan(parse('1.2.0.RELEASE')))
	}

	@Test
	void compareLessQualifierVersion() {
		assertThat(parse('1.2.0.RC2'), lessThan(parse('1.2.0.RC3')))
	}

	@Test
	void compareWithNull() {
		assertThat(parse('1.2.0.RC2'), greaterThan(null))
	}

	@Test
	void compareUnknownQualifier() {
		assertThat(parse('1.2.0.Beta'), lessThan(parse('1.2.0.CR')))
	}

	@Test
	void compareUnknownQualifierVersion() {
		assertThat(parse('1.2.0.Beta1'), lessThan(parse('1.2.0.Beta2')))
	}

	@Test
	void snapshotGreaterThanRC() {
		assertThat(parse('1.2.0.BUILD-SNAPSHOT'), greaterThan(parse('1.2.0.RC1')))
	}

	@Test
	void snapshotLowerThanRelease() {
		assertThat(parse('1.2.0.BUILD-SNAPSHOT'), lessThan(parse('1.2.0.RELEASE')))
	}

	@Test
	void parseInvalidVersion() {
		thrown.expect(InvalidVersionException)
		parse('foo')
	}

	@Test
	void safeParseInvalidVersion() {
		assertNull safeParse('foo')
	}

	@Test
	void parseVersionWithSpaces() {
		assertThat(parse('    1.2.0.RC3  '), lessThan(parse('1.3.0.RELEASE')))
	}

}
