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

import org.springframework.util.Assert

/**
 * Parser for {@link Version} and {@link VersionRange} that allows to resolve the minor
 * and patch value against a configurable list of "latest versions".
 * <p>
 * For example a parser that is configured with {@code 1.3.7.RELEASE} and
 * {@code 1.4.2.RELEASE} as latest versions can parse {@code 1.3.x.RELEASE} to
 * {@code 1.3.7.RELEASE}. Note that the qualifier is important here:
 * {@code 1.3.8.BUILD-SNAPSHOT} would be parsed as {@code 1.3.999.BUILD-SNAPSHOT} as the
 * parser doesn't know the latest {@code BUILD-SNAPSHOT} in the {@code 1.3.x} release
 * line.
 *
 * @author Stephane Nicoll
 */
class VersionParser {

	public static final VersionParser DEFAULT = new VersionParser(Collections.emptyList())

	private static final String VERSION_REGEX = '^(\\d+)\\.(\\d+|x)\\.(\\d+|x)(?:\\.([^0-9]+)(\\d+)?)?$'

	private static final String RANGE_REGEX = "(\\(|\\[)(.*),(.*)(\\)|\\])"

	private final List<Version> latestVersions;

	VersionParser(List<Version> latestVersions) {
		this.latestVersions = latestVersions
	}

	/**
	 * Parse the string representation of a {@link Version}. Throws an
	 * {@link InvalidVersionException} if the version could not be parsed.
	 * @param text the version text
	 * @return a Version instance for the specified version text
	 * @throws InvalidVersionException if the version text could not be parsed
	 * @see #safeParse(java.lang.String)
	 */
	Version parse(String text) {
		Assert.notNull(text, 'Text must not be null')
		def matcher = (text.trim() =~ VERSION_REGEX)
		if (!matcher.matches()) {
			throw new InvalidVersionException("Could not determine version based on '$text': version format " +
					"is Minor.Major.Patch.Qualifier (e.g. 1.0.5.RELEASE)")
		}
		Integer major = Integer.valueOf(matcher[0][1])
		String minor = matcher[0][2]
		String patch = matcher[0][3]
		def qualifier = null;
		String qualifierId = matcher[0][4]
		if (qualifierId) {
			qualifier = new Version.Qualifier(qualifier: qualifierId)
			String o = matcher[0][5]
			if (o != null) {
				qualifier.version = Integer.valueOf(o)
			}
		}
		if (minor == "x" || patch == "x") {
			Integer minorInt = minor == "x" ? null : Integer.parseInt(minor)
			Version latest = findLatestVersion(major, minorInt, qualifier)
			if (!latest) {
				return new Version(major, (minor == "x" ? 999 : Integer.parseInt(minor)),
						(patch == "x" ? 999 : Integer.parseInt(patch)), qualifier)
			}
			return new Version(major, latest.minor, latest.patch, latest.qualifier)
		} else {
			return new Version(major, Integer.parseInt(minor), Integer.parseInt(patch), qualifier)
		}
	}

	/**
	 * Parse safely the specified string representation of a {@link Version}.
	 * <p>
	 * Return {@code null} if the text represents an invalid version.
	 * @param text the version text
	 * @return a Version instance for the specified version text
	 * @see #parse(java.lang.String)
	 */
	Version safeParse(String text) {
		try {
			return parse(text)
		} catch (InvalidVersionException ex) {
			return null
		}
	}

	/**
	 * Parse the string representation of a {@link VersionRange}. Throws an
	 * {@link InvalidVersionException} if the range could not be parsed.
	 * @param text the range text
	 * @return a VersionRange instance for the specified range text
	 * @throws InvalidVersionException if the range text could not be parsed
	 */
	VersionRange parseRange(String text) {
		Assert.notNull(text, "Text must not be null")
		def matcher = (text.trim() =~ RANGE_REGEX)
		if (!matcher.matches()) {
			// Try to read it as simple string
			Version version = parse(text)
			return new VersionRange(version, true, null, true)
		}
		boolean lowerInclusive = matcher[0][1].equals('[')
		Version lowerVersion = parse(matcher[0][2])
		Version higherVersion = parse(matcher[0][3])
		boolean higherInclusive = matcher[0][4].equals(']')
		new VersionRange(lowerVersion, lowerInclusive, higherVersion, higherInclusive)
	}

	private Version findLatestVersion(Integer major, Integer minor,
									  Version.Qualifier qualifier) {
		def matches = this.latestVersions.findAll {
			if (major && major != it.major) {
				return false;
			}
			if (minor && minor != it.minor) {
				return false;
			}
			if (qualifier && it.qualifier != qualifier) {
				return false;
			}
			return true;
		}
		return (matches.size() == 1 ? matches[0] : null)
	}

}
