/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.version;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Define a version. A typical version is represented as
 * {@code MAJOR.MINOR.PATCH[QUALIFIER]} where the qualifier is optional and can have an
 * extra version.
 * <p>
 * For example: {@code 1.2.0.RC1} is the first release candidate of 1.2.0 and
 * {@code 1.5.0-M4} is the fourth milestone of 1.5.0. The special {@code RELEASE}
 * qualifier indicates a final release (a.k.a. GA).
 * <p>
 * Two formats are currently supported, {@link Format#V1} that uses a dot to separate the
 * qualifier from the version itself and {@link Format#V2} that is SemVer compliant (and
 * therefore uses a dash to separate the qualifier).
 * <p>
 * The main purpose of parsing a version is to compare it with another version.
 *
 * @author Stephane Nicoll
 */
@SuppressWarnings("serial")
public final class Version implements Serializable, Comparable<Version> {

	private static final VersionQualifierComparator qualifierComparator = new VersionQualifierComparator();

	private static final VersionParser parser = new VersionParser(Collections.emptyList());

	private final Integer major;

	private final Integer minor;

	private final Integer patch;

	private final Qualifier qualifier;

	private final Format format;

	public Version(Integer major, Integer minor, Integer patch, Qualifier qualifier) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.qualifier = qualifier;
		this.format = determineFormat(qualifier);
	}

	private static Format determineFormat(Qualifier qualifier) {
		if (qualifier == null) {
			return Format.V2;
		}
		return (qualifier.getSeparator().equals(".")) ? Format.V1 : Format.V2;
	}

	/**
	 * Format this version to the specified {@link Format}.
	 * @param format the format to use
	 * @return a version compliant with the specified format.
	 */
	public Version format(Format format) {
		Assert.notNull(format, () -> "Format must not be null");
		if (this.format == format) {
			return this;
		}
		if (format == Format.V1) {
			Qualifier qualifier = formatQualifier(".", this::toV1Qualifier);
			return new Version(this.major, this.minor, this.patch, qualifier);
		}
		Qualifier qualifier = formatQualifier("-", this::toV2Qualifier);
		return new Version(this.major, this.minor, this.patch, qualifier);
	}

	private Qualifier formatQualifier(String newSeparator, Function<String, String> idTransformer) {
		String originalQualifier = (this.qualifier != null) ? this.qualifier.getId() : null;
		String newId = idTransformer.apply(originalQualifier);
		if (newId != null) {
			return new Qualifier(newId, (this.qualifier != null) ? this.qualifier.getVersion() : null, newSeparator);
		}
		return null;
	}

	private String toV1Qualifier(String id) {
		if ("SNAPSHOT".equals(id)) {
			return "BUILD-SNAPSHOT";
		}
		if (id == null) {
			return "RELEASE";
		}
		return id;
	}

	private String toV2Qualifier(String id) {
		if ("BUILD-SNAPSHOT".equals(id)) {
			return "SNAPSHOT";
		}
		if ("RELEASE".equals(id)) {
			return null;
		}
		return id;
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

	public Format getFormat() {
		return this.format;
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
		Integer firstIndex = (first != null) ? first : 0;
		Integer secondIndex = (second != null) ? second : 0;
		return firstIndex.compareTo(secondIndex);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.major == null) ? 0 : this.major.hashCode());
		result = prime * result + ((this.minor == null) ? 0 : this.minor.hashCode());
		result = prime * result + ((this.patch == null) ? 0 : this.patch.hashCode());
		result = prime * result + ((this.qualifier == null) ? 0 : this.qualifier.hashCode());
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder().append(this.major).append(".").append(this.minor).append(".")
				.append(this.patch);
		if (this.qualifier != null) {
			sb.append(this.qualifier.getSeparator()).append(this.qualifier.getId());
			if (this.qualifier.getVersion() != null) {
				sb.append(this.qualifier.getVersion());
			}
		}
		return sb.toString();
	}

	/**
	 * A version qualifier.
	 */
	public static class Qualifier implements Serializable {

		private final String id;

		private final Integer version;

		private final String separator;

		public Qualifier(String id) {
			this(id, null, ".");
		}

		public Qualifier(String id, Integer version, String separator) {
			this.id = id;
			this.version = version;
			this.separator = separator;
		}

		public String getId() {
			return this.id;
		}

		public Integer getVersion() {
			return this.version;
		}

		public String getSeparator() {
			return this.separator;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Qualifier qualifier = (Qualifier) o;
			return this.id.equals(qualifier.id) && Objects.equals(this.version, qualifier.version)
					&& Objects.equals(this.separator, qualifier.separator);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.id, this.version, this.separator);
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Qualifier.class.getSimpleName() + "[", "]").add("id='" + this.id + "'")
					.add("version=" + this.version).add("separator='" + this.separator + "'").toString();
		}

	}

	/**
	 * Define the supported version format.
	 */
	public enum Format {

		/**
		 * Original version format, i.e. {@code Major.Minor.Patch.Qualifier} using
		 * {@code BUILD-SNAPSHOT} as the qualifier for snapshots and {@code RELEASE} for
		 * GAs.
		 */
		V1,

		/**
		 * SemVer-compliant format, i.e. {@code Major.Minor.Patch-Qualifier} using
		 * {@code SNAPSHOT} as the qualifier for snapshots and no qualifier for GAs.
		 */
		V2;

	}

	private static class VersionQualifierComparator implements Comparator<Qualifier> {

		static final String RELEASE = "RELEASE";
		static final String BUILD_SNAPSHOT = "BUILD-SNAPSHOT";
		static final String SNAPSHOT = "SNAPSHOT";
		static final String MILESTONE = "M";
		static final String RC = "RC";

		static final List<String> KNOWN_QUALIFIERS = Arrays.asList(MILESTONE, RC, BUILD_SNAPSHOT, SNAPSHOT, RELEASE);

		@Override
		public int compare(Qualifier o1, Qualifier o2) {
			Qualifier first = (o1 != null) ? o1 : new Qualifier(RELEASE);
			Qualifier second = (o2 != null) ? o2 : new Qualifier(RELEASE);

			int qualifier = compareQualifier(first, second);
			return (qualifier != 0) ? qualifier : compareQualifierVersion(first, second);
		}

		private static int compareQualifierVersion(Qualifier first, Qualifier second) {
			Integer firstVersion = (first.getVersion() != null) ? first.getVersion() : 0;
			Integer secondVersion = (second.getVersion() != null) ? second.getVersion() : 0;
			return firstVersion.compareTo(secondVersion);
		}

		private static int compareQualifier(Qualifier first, Qualifier second) {
			int firstIndex = getQualifierIndex(first.getId());
			int secondIndex = getQualifierIndex(second.getId());

			// Unknown qualifier, alphabetic ordering
			if (firstIndex == -1 && secondIndex == -1) {
				return first.getId().compareTo(second.getId());
			}
			else {
				return Integer.compare(firstIndex, secondIndex);
			}
		}

		private static int getQualifierIndex(String qualifier) {
			return (StringUtils.hasText(qualifier) ? KNOWN_QUALIFIERS.indexOf(qualifier) : 0);
		}

	}

}
