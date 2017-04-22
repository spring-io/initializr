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

package io.spring.initializr.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.spring.initializr.util.InvalidVersionException;
import io.spring.initializr.util.Version;
import io.spring.initializr.util.VersionParser;
import io.spring.initializr.util.VersionProperty;
import io.spring.initializr.util.VersionRange;

/**
 * Define a Bill Of Materials to be represented in the generated project if a dependency
 * refers to it.
 *
 * @author Stephane Nicoll
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BillOfMaterials {

	private String groupId;
	private String artifactId;
	private String version;
	private VersionProperty versionProperty;
	private Integer order = Integer.MAX_VALUE;
	private List<String> additionalBoms = new ArrayList<>();
	private List<String> repositories = new ArrayList<>();
	private final List<Mapping> mappings = new ArrayList<>();

	public BillOfMaterials() {
	}

	private BillOfMaterials(String groupId, String artifactId) {
		this(groupId, artifactId, null);
	}

	private BillOfMaterials(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	/**
	 * Return the version of the BOM. Can be {@code null} if it is provided via a mapping.
	 */
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Return the {@link VersionProperty} to use to externalize the version of the BOM.
	 * When this is set, a version property is automatically added rather than setting
	 * the version in the bom declaration itself.
	 */
	public VersionProperty getVersionProperty() {
		return versionProperty;
	}

	public void setVersionProperty(VersionProperty versionProperty) {
		this.versionProperty = versionProperty;
	}

	public void setVersionProperty(String versionPropertyName) {
		setVersionProperty(new VersionProperty(versionPropertyName));
	}

	/**
	 * Return the relative order of this BOM where lower values have higher priority. The
	 * default value is {@code Integer.MAX_VALUE}, indicating lowest priority. The Spring
	 * Boot dependencies bom has an order of 100.
	 */
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * Return the BOM(s) that should be automatically included if this BOM is required.
	 * Can be {@code null} if it is provided via a mapping.
	 */
	public List<String> getAdditionalBoms() {
		return additionalBoms;
	}

	public void setAdditionalBoms(List<String> additionalBoms) {
		this.additionalBoms = additionalBoms;
	}

	/**
	 * Return the repositories that are required if this BOM is required. Can be
	 * {@code null} if it is provided via a mapping.
	 */
	public List<String> getRepositories() {
		return repositories;
	}

	public void setRepositories(List<String> repositories) {
		this.repositories = repositories;
	}

	public List<Mapping> getMappings() {
		return mappings;
	}

	public void validate() {
		if (version == null && mappings.isEmpty()) {
			throw new InvalidInitializrMetadataException(
					"No version available for " + this);
		}
		updateVersionRange(VersionParser.DEFAULT);
	}

	public void updateVersionRange(VersionParser versionParser) {
		mappings.forEach(it -> {
			try {
				it.range = versionParser.parseRange(it.versionRange);
			}
			catch (InvalidVersionException ex) {
				throw new InvalidInitializrMetadataException(
						"Invalid version range " + it.versionRange + " for " + this, ex);
			}
		});
	}

	/**
	 * Resolve this instance according to the specified Spring Boot {@link Version}.
	 * Return a {@link BillOfMaterials} instance that holds the version, repositories and
	 * additional BOMs to use, if any.
	 */
	public BillOfMaterials resolve(Version bootVersion) {
		if (mappings.isEmpty()) {
			return this;
		}

		for (Mapping mapping : mappings) {
			if (mapping.range.match(bootVersion)) {
				BillOfMaterials resolvedBom = new BillOfMaterials(groupId, artifactId,
						mapping.version);
				resolvedBom.setVersionProperty(versionProperty);
				resolvedBom.setOrder(order);
				resolvedBom.repositories.addAll(!mapping.repositories.isEmpty()
						? mapping.repositories : repositories);
				resolvedBom.additionalBoms.addAll(!mapping.additionalBoms.isEmpty()
						? mapping.additionalBoms : additionalBoms);
				return resolvedBom;
			}
		}
		throw new IllegalStateException(
				"No suitable mapping was found for " + this + " and version " + bootVersion);
	}

	@Override
	public String toString() {
		return "BillOfMaterials [" + (groupId != null ? "groupId=" + groupId + ", " : "")
				+ (artifactId != null ? "artifactId=" + artifactId + ", " : "")
				+ (version != null ? "version=" + version + ", " : "")
				+ (versionProperty != null ? "versionProperty=" + versionProperty + ", "
						: "")
				+ (order != null ? "order=" + order + ", " : "")
				+ (additionalBoms != null ? "additionalBoms=" + additionalBoms + ", "
						: "")
				+ (repositories != null ? "repositories=" + repositories : "") + "]";
	}

	public static class Mapping {

		private String versionRange;

		private String version;

		private List<String> repositories = new ArrayList<>();

		private List<String> additionalBoms = new ArrayList<>();

		@JsonIgnore
		private VersionRange range;

		public Mapping() {
		}

		private Mapping(String range, String version, String... repositories) {
			this.versionRange = range;
			this.version = version;
			this.repositories.addAll(Arrays.asList(repositories));
		}

		public String determineVersionRangeRequirement() {
			return range.toString();
		}

		public static Mapping create(String range, String version) {
			return new Mapping(range, version);
		}

		public static Mapping create(String range, String version,
				String... repositories) {
			return new Mapping(range, version, repositories);
		}

		public String getVersionRange() {
			return versionRange;
		}

		public String getVersion() {
			return version;
		}

		public List<String> getRepositories() {
			return repositories;
		}

		public List<String> getAdditionalBoms() {
			return additionalBoms;
		}

		public VersionRange getRange() {
			return range;
		}

		public void setVersionRange(String versionRange) {
			this.versionRange = versionRange;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public void setRepositories(List<String> repositories) {
			this.repositories = repositories;
		}

		public void setAdditionalBoms(List<String> additionalBoms) {
			this.additionalBoms = additionalBoms;
		}

		public void setRange(VersionRange range) {
			this.range = range;
		}

		@Override
		public String toString() {
			return "Mapping ["
					+ (versionRange != null ? "versionRange=" + versionRange + ", " : "")
					+ (version != null ? "version=" + version + ", " : "")
					+ (repositories != null ? "repositories=" + repositories + ", " : "")
					+ (additionalBoms != null ? "additionalBoms=" + additionalBoms + ", "
							: "")
					+ (range != null ? "range=" + range : "") + "]";
		}

	}

	public static BillOfMaterials create(String groupId, String artifactId) {
		return new BillOfMaterials(groupId, artifactId);
	}

	public static BillOfMaterials create(String groupId, String artifactId,
			String version) {
		return new BillOfMaterials(groupId, artifactId, version);
	}

}
