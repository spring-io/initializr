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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * Define the version number of a module. A typical version is represented as
 * {@code MAJOR.MINOR.PATCH.QUALIFIER} where the qualifier can have an extra version.
 * <p>
 * For example: {@code 1.2.0.RC1} is the first release candidate of 1.2.0 and
 * {@code 1.5.0.M4} is the fourth milestone of 1.5.0. The special {@code RELEASE}
 * qualifier indicates a final release (a.k.a. GA)
 * <p>
 * The main purpose of parsing a version is to compare it with another version, see
 * {@link Comparable}.
 *
 * @author Stephane Nicoll
 */
@SuppressWarnings("serial")
public final class Version implements Serializable, Comparable<Version> {

	private static final VersionQualifierComparator qualifierComparator = new VersionQualifierComparator();

	private static final VersionParser parser = new VersionParser(
			Collections.emptyList());

	private final Integer major;

	private final Integer minor;

	private final Integer patch;

	private final Qualifier qualifier;

	// For Jackson
	@SuppressWarnings("unused")
	private Version() {
		this(null, null, null, null);
	}

	public Version(Integer major, Integer minor, Integer patch, Qualifier qualifier) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.qualifier = qualifier;
	}

	public Integer getMajor() {
		return this.major;
	}

	public Integer getMinor() {
		return this.minor;
	}

	public Integer getPatch() {
		return this.patch;
	}

	public Qualifier getQualifier() {
		return this.qualifier;
	}

	@Override
	public String toString() {
		return this.major + "." + this.minor + "." + this.patch
				+ (this.qualifier != null ? "." + this.qualifier.qualifier
						+ (this.qualifier.version != null ? this.qualifier.version : "")
						: "");
	}

	/**
	 * Parse the string representation of a {@link Version}. Throws an
	 * {@link InvalidVersionException} if the version could not be parsed.
	 * @param text the version text
	 * @return a Version instance for the specified version text
	 * @throws InvalidVersionException if the version text could not be parsed
	 * @see VersionParser
	 */
	public static Version parse(String text) {
		return parser.parse(text);
	}

	/**
	 * Parse safely the specified string representation of a {@link Version}.
	 * <p>
	 * Return {@code null} if the text represents an invalid version.
	 * @param text the version text
	 * @return a Version instance for the specified version text
	 * @see VersionParser
	 */
	public static Version safeParse(String text) {
		try {
			return parse(text);
		}
		catch (InvalidVersionException ex) {
			return null;
		}
	}

	@Override
	public int compareTo(Version other) {
		if (other == null) {
			return 1;
		}
		int majorDiff = safeCompare(this.major, other.major);
		if (majorDiff != 0) {
			return majorDiff;
		}
		int minorDiff = safeCompare(this.minor, other.minor);
		if (minorDiff != 0) {
			return minorDiff;
		}
		int patch = safeCompare(this.patch, other.patch);
		if (patch != 0) {
			return patch;
		}
		return qualifierComparator.compare(this.qualifier, other.qualifier);
	}

	private static int safeCompare(Integer first, Integer second) {
		Integer firstIndex = (first != null ? first : 0);
		Integer secondIndex = (second != null ? second : 0);
		return firstIndex.compareTo(secondIndex);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.major == null) ? 0 : this.major.hashCode());
		result = prime * result + ((this.minor == null) ? 0 : this.minor.hashCode());
		result = prime * result + ((this.patch == null) ? 0 : this.patch.hashCode());
		result = prime * result
				+ ((this.qualifier == null) ? 0 : this.qualifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Version other = (Version) obj;
		if (this.major == null) {
			if (other.major != null) {
				return false;
			}
		}
		else if (!this.major.equals(other.major)) {
			return false;
		}
		if (this.minor == null) {
			if (other.minor != null) {
				return false;
			}
		}
		else if (!this.minor.equals(other.minor)) {
			return false;
		}
		if (this.patch == null) {
			if (other.patch != null) {
				return false;
			}
		}
		else if (!this.patch.equals(other.patch)) {
			return false;
		}
		if (this.qualifier == null) {
			if (other.qualifier != null) {
				return false;
			}
		}
		else if (!this.qualifier.equals(other.qualifier)) {
			return false;
		}
		return true;
	}

	/**
	 * A version qualifier.
	 */
	public static class Qualifier implements Serializable {

		public Qualifier(String qualifier) {
			this.qualifier = qualifier;
		}

		private String qualifier;

		private Integer version;

		public String getQualifier() {
			return this.qualifier;
		}

		public void setQualifier(String qualifier) {
			this.qualifier = qualifier;
		}

		public Integer getVersion() {
			return this.version;
		}

		public void setVersion(Integer version) {
			this.version = version;
		}

		@Override
		public String toString() {
			return "Qualifier ["
					+ (this.qualifier != null ? "qualifier=" + this.qualifier + ", " : "")
					+ (this.version != null ? "version=" + this.version : "") + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((this.qualifier == null) ? 0 : this.qualifier.hashCode());
			result = prime * result
					+ ((this.version == null) ? 0 : this.version.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Qualifier other = (Qualifier) obj;
			if (this.qualifier == null) {
				if (other.qualifier != null) {
					return false;
				}
			}
			else if (!this.qualifier.equals(other.qualifier)) {
				return false;
			}
			if (this.version == null) {
				if (other.version != null) {
					return false;
				}
			}
			else if (!this.version.equals(other.version)) {
				return false;
			}
			return true;
		}

	}

	private static class VersionQualifierComparator implements Comparator<Qualifier> {

		static final String RELEASE = "RELEASE";
		static final String SNAPSHOT = "BUILD-SNAPSHOT";
		static final String MILESTONE = "M";
		static final String RC = "RC";

		static final List<String> KNOWN_QUALIFIERS = Arrays.asList(MILESTONE, RC,
				SNAPSHOT, RELEASE);

		@Override
		public int compare(Qualifier o1, Qualifier o2) {
			Qualifier first = (o1 != null ? o1 : new Qualifier(RELEASE));
			Qualifier second = (o2 != null ? o2 : new Qualifier(RELEASE));

			int qualifier = compareQualifier(first, second);
			return (qualifier != 0 ? qualifier : compareQualifierVersion(first, second));
		}

		private static int compareQualifierVersion(Qualifier first, Qualifier second) {
			Integer firstVersion = (first.getVersion() != null ? first.getVersion() : 0);
			Integer secondVersion = (second.getVersion() != null ? second.getVersion()
					: 0);
			return firstVersion.compareTo(secondVersion);
		}

		private static int compareQualifier(Qualifier first, Qualifier second) {
			Integer firstIndex = getQualifierIndex(first.qualifier);
			Integer secondIndex = getQualifierIndex(second.qualifier);

			// Unknown qualifier, alphabetic ordering
			if (firstIndex == -1 && secondIndex == -1) {
				return first.qualifier.compareTo(second.qualifier);
			}
			else {
				return firstIndex.compareTo(secondIndex);
			}
		}

		private static int getQualifierIndex(String qualifier) {
			return (StringUtils.hasText(qualifier) ? KNOWN_QUALIFIERS.indexOf(qualifier)
					: 0);
		}

	}

}
