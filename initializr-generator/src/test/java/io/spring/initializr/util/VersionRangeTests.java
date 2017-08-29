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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * @author Stephane Nicoll
 */
public class VersionRangeTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void simpleStartingRange() {
		assertThat(new VersionRange(Version.parse("1.3.0.RELEASE")).toString(),
				equalTo(">=1.3.0.RELEASE"));
	}

	@Test
	public void matchSimpleRange() {
		assertThat("1.2.0.RC3", match("[1.2.0.RC1,1.2.0.RC5]"));
	}

	@Test
	public void matchSimpleRangeBefore() {
		assertThat("1.1.9.RC3", not(match("[1.2.0.RC1,1.2.0.RC5]")));
	}

	@Test
	public void matchSimpleRangeAfter() {
		assertThat("1.2.0.RC6", not(match("[1.2.0.RC1,1.2.0.RC5]")));
	}

	@Test
	public void matchInclusiveLowerRange() {
		assertThat("1.2.0.RC1", match("[1.2.0.RC1,1.2.0.RC5]"));
	}

	@Test
	public void matchInclusiveHigherRange() {
		assertThat("1.2.0.RC5", match("[1.2.0.RC1,1.2.0.RC5]"));
	}

	@Test
	public void matchExclusiveLowerRange() {
		assertThat("1.2.0.RC1", not(match("(1.2.0.RC1,1.2.0.RC5)")));
	}

	@Test
	public void matchExclusiveHigherRange() {
		assertThat("1.2.0.RC5", not(match("[1.2.0.RC1,1.2.0.RC5)")));
	}

	@Test
	public void matchUnboundedRangeEqual() {
		assertThat("1.2.0.RELEASE", match("1.2.0.RELEASE"));
	}

	@Test
	public void matchUnboundedRangeAfter() {
		assertThat("2.2.0.RELEASE", match("1.2.0.RELEASE"));
	}

	@Test
	public void matchUnboundedRangeBefore() {
		assertThat("1.1.9.RELEASE", not(match("1.2.0.RELEASE")));
	}

	@Test
	public void rangeWithSpaces() {
		assertThat("1.2.0.RC3", match("[   1.2.0.RC1 ,  1.2.0.RC5]"));
	}

	@Test
	public void matchLatestVersion() {
		assertThat("1.2.8.RELEASE", match("[1.2.0.RELEASE,1.2.x.BUILD-SNAPSHOT]",
				new VersionParser(Collections.singletonList(
						Version.parse("1.2.9.BUILD-SNAPSHOT")))));
	}

	@Test
	public void matchOverLatestVersion() {
		assertThat("1.2.10.RELEASE", not(match("[1.2.0.RELEASE,1.2.x.BUILD-SNAPSHOT]",
				new VersionParser(Collections.singletonList(
						Version.parse("1.2.9.BUILD-SNAPSHOT"))))));
	}

	@Test
	public void matchAsOfCurrentVersion() {
		assertThat("1.3.5.RELEASE", match("[1.3.x.RELEASE,1.3.x.BUILD-SNAPSHOT]",
				new VersionParser(Arrays.asList(Version.parse("1.3.4.RELEASE"),
						Version.parse("1.3.6.BUILD-SNAPSHOT")))));
	}

	@Test
	public void matchOverAsOfCurrentVersion() {
		assertThat("1.3.5.RELEASE", not(match("[1.3.x.RELEASE,1.3.x.BUILD-SNAPSHOT]",
				new VersionParser(Arrays.asList(Version.parse("1.3.7.RELEASE"),
						Version.parse("1.3.6.BUILD-SNAPSHOT"))))));
	}

	private static VersionRangeMatcher match(String range) {
		return new VersionRangeMatcher(range, new VersionParser(Collections.emptyList()));
	}

	private static VersionRangeMatcher match(String range, VersionParser parser) {
		return new VersionRangeMatcher(range, parser);
	}


	static class VersionRangeMatcher extends BaseMatcher<String> {

		private final VersionRange range;
		private final VersionParser parser;

		VersionRangeMatcher(String text, VersionParser parser) {
			this.parser = parser;
			this.range = parser.parseRange(text);
		}

		@Override
		public boolean matches(Object item) {
			if (!(item instanceof String)) {
				return false;
			}
			return this.range.match(this.parser.parse((String) item));
		}

		@Override
		public void describeTo(Description description) {
			description.appendText(range.toString());
		}
	}

}
