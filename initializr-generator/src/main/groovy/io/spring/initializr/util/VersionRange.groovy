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

import groovy.transform.EqualsAndHashCode

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
@EqualsAndHashCode
class VersionRange {

	final Version lowerVersion
	final boolean lowerInclusive
	final Version higherVersion
	final boolean higherInclusive

	protected VersionRange(Version lowerVersion, boolean lowerInclusive,
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

	@Override
	String toString() {
		StringBuffer sb = new StringBuffer()
		if (lowerVersion) {
			sb.append("${lowerInclusive ? '>=' : '>'}${lowerVersion}")
		}
		if (higherVersion) {
			sb.append(" and ${higherInclusive ? '<=' : '<'}${higherVersion}")
		}
		return sb.toString()
	}

}
