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

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import org.springframework.util.Assert

/**
 * Define a {@link Version} range.  A square bracket "[" or "]" denotes an inclusive
 * end of the range and a round bracket "(" or ")" denotes an exclusive end of the
 * range. A range can also be unbounded by defining a a single {@link Version}. The
 * examples below make this clear.
 *
 * <ul>
 * <li>"[1.2.0.RELEASE,1.3.0.RELEASE)" version 1.2.0 and any version after
 * this, up to, but not including, version 1.3.0.</li>
 * <li>"(2.0.0.RELEASE,3.2.0.RELEASE]" any version after 2.0.0 up to and
 * including version 3.2.0.</li>
 * <li>"1.4.5.RELEASE", version 1.4.5 and all later versions.</li>
 * </ul>
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@ToString
@EqualsAndHashCode
class VersionRange {

	private static final String RANGE_REGEX = "(\\(|\\[)(.*),(.*)(\\)|\\])"

	final Version lowerVersion
	final boolean lowerInclusive
	final Version higherVersion
	final boolean higherInclusive

	private VersionRange(Version lowerVersion, boolean lowerInclusive,
						 Version higherVersion, boolean higherInclusive) {
		this.lowerVersion = lowerVersion
		this.lowerInclusive = lowerInclusive
		this.higherVersion = higherVersion
		this.higherInclusive = higherInclusive
	}

	/**
	 * Specify if the {@link Version} matches this range. Returns {@code true}
	 * if the version is contained within this range, {@code false} otherwise.
	 */
	boolean match(Version version) {
		Assert.notNull(version, "Version must not be null")
		def lower = lowerVersion.compareTo(version)
		if (lower > 0) {
			return false;
		} else if (!lowerInclusive && lower == 0) {
			return false;
		}
		if (higherVersion) {
			def higher = higherVersion.compareTo(version)
			if (higher < 0) {
				return false
			} else if (!higherInclusive && higher == 0) {
				return false
			}
		}
		return true
	}

	/**
	 * Parse the string representation of a {@link VersionRange}. Throws an
	 * {@link InvalidVersionException} if the range could not be parsed.
	 * @param text the range text
	 * @return a VersionRange instance for the specified range text
	 * @throws InvalidVersionException if the range text could not be parsed
	 */
	static VersionRange parse(String text) {
		Assert.notNull(text, "Text must not be null")
		def matcher = (text.trim() =~ RANGE_REGEX)
		if (!matcher.matches()) {
			// Try to read it as simple string
			Version version = Version.parse(text)
			return new VersionRange(version, true, null, true)
		}
		boolean lowerInclusive = matcher[0][1].equals('[')
		Version lowerVersion = Version.parse(matcher[0][2])
		Version higherVersion = Version.parse(matcher[0][3])
		boolean higherInclusive = matcher[0][4].equals(']')
		new VersionRange(lowerVersion, lowerInclusive, higherVersion, higherInclusive)
	}

}
