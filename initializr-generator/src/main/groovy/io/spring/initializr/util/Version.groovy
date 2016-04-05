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
 * Define the version number of a module. A typical version is represented
 * as {@code MAJOR.MINOR.PATCH.QUALIFIER} where the qualifier can have an
 * extra version.
 * <p>
 * For example: {@code 1.2.0.RC1} is the first release candidate of 1.2.0
 * and {@code 1.5.0.M4} is the fourth milestone of 1.5.0. The special
 * {@code RELEASE} qualifier indicates a final release (a.k.a. GA)
 * <p>
 * The main purpose of parsing a version is to compare it with another
 * version, see {@link Comparable}.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@EqualsAndHashCode
@SuppressWarnings("serial")
final class Version implements Serializable, Comparable<Version> {

	private static final String VERSION_REGEX = '^(\\d+)\\.(\\d+)\\.(\\d+)(?:\\.([^0-9]+)(\\d+)?)?$'

	private static final VersionQualifierComparator qualifierComparator = new VersionQualifierComparator()

	Integer major
	Integer minor
	Integer patch
	Qualifier qualifier
	
	@Override
	public String toString() {
		"${major}.${minor}.${patch}" + (qualifier?".${qualifier.qualifier}${qualifier.version?:''}" : '')
	}

	/**
	 * Parse the string representation of a {@link Version}. Throws an
	 * {@link InvalidVersionException} if the version could not be parsed.
	 * @param text the version text
	 * @return a Version instance for the specified version text
	 * @throws InvalidVersionException if the version text could not be parsed
	 * @see #safeParse(java.lang.String)
	 */
	static Version parse(String text) {
		Assert.notNull(text, 'Text must not be null')
		def matcher = (text.trim() =~ VERSION_REGEX)
		if (!matcher.matches()) {
			throw new InvalidVersionException("Could not determine version based on '$text': version format " +
					"is Minor.Major.Patch.Qualifier (i.e. 1.0.5.RELEASE)")
		}
		Version version = new Version()
		version.major = Integer.valueOf(matcher[0][1])
		version.minor = Integer.valueOf(matcher[0][2])
		version.patch = Integer.valueOf(matcher[0][3])
		String qualifierId = matcher[0][4]
		if (qualifierId) {
			Qualifier qualifier = new Qualifier(qualifier: qualifierId)
			String o = matcher[0][5]
			if (o != null) {
				qualifier.version = Integer.valueOf(o)
			}
			version.qualifier = qualifier
		}
		version
	}

	/**
	 * Parse safely the specified string representation of a {@link Version}.
	 * <p>
	 * Return {@code null} if the text represents an invalid version.
	 * @param text the version text
	 * @return a Version instance for the specified version text
	 * @see #parse(java.lang.String)
	 */
	static safeParse(String text) {
		try {
			return parse(text)
		} catch (InvalidVersionException e) {
			return null
		}
	}

	@Override
	int compareTo(Version other) {
		if (other == null) {
			return 1
		}
		int majorDiff = safeCompare(this.major, other.major)
		if (majorDiff != 0) {
			return majorDiff
		}
		int minorDiff = safeCompare(this.minor, other.minor)
		if (minorDiff != 0) {
			return minorDiff
		}
		int patch = safeCompare(this.patch, other.patch)
		if (patch != 0) {
			return patch
		}
		qualifierComparator.compare(this.qualifier, other.qualifier)
	}

	private static int safeCompare(Integer first, Integer second) {
		int firstIndex = first ?: 0
		int secondIndex = second ?: 0
		return firstIndex.compareTo(secondIndex)
	}

	@ToString
	@EqualsAndHashCode
	public static class Qualifier {
		String qualifier
		Integer version
	}


	private static class VersionQualifierComparator implements Comparator<Qualifier> {

		static final String RELEASE = 'RELEASE'
		static final String SNAPSHOT = 'BUILD-SNAPSHOT'
		static final String MILESTONE = 'M'
		static final String RC = 'RC'

		static final List<String> KNOWN_QUALIFIERS = Arrays.asList(MILESTONE, RC, SNAPSHOT, RELEASE)

		@Override
		int compare(Qualifier o1, Qualifier o2) {
			Qualifier first = o1 ?: new Qualifier(qualifier: RELEASE)
			Qualifier second = o2 ?: new Qualifier(qualifier: RELEASE)

			int qualifier = compareQualifier(first, second)
			qualifier ? qualifier : compareQualifierVersion(first, second)
		}

		private static int compareQualifierVersion(Qualifier first, Qualifier second) {
			int firstVersion = first.getVersion() ?: 0
			int secondVersion = second.getVersion() ?: 0
			firstVersion.compareTo(secondVersion)
		}

		private static int compareQualifier(Qualifier first, Qualifier second) {
			int firstIndex = getQualifierIndex(first.qualifier)
			int secondIndex = getQualifierIndex(second.qualifier)

			if (firstIndex == -1 && secondIndex == -1) { // Unknown qualifier, alphabetic ordering
				return first.qualifier.compareTo(second.qualifier)
			} else {
				return firstIndex.compareTo(secondIndex)
			}
		}

		private static int getQualifierIndex(String qualifier) {
			qualifier ? KNOWN_QUALIFIERS.indexOf(qualifier) : RELEASE
		}
	}

}
