/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.spring.initializr.generator.version.InvalidVersionException;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionRange;

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
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	/**
	 * Return the version of the BOM. Can be {@code null} if it is provided via a mapping.
	 * @return the version of the BOM or {@code null}
	 */
	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Return the {@link VersionProperty} to use to externalize the version of the BOM.
	 * When this is set, a version property is automatically added rather than setting the
	 * version in the BOM declaration itself.
	 * @return the version property
	 */
	public VersionProperty getVersionProperty() {
		return this.versionProperty;
	}

	public void setVersionProperty(VersionProperty versionProperty) {
		this.versionProperty = versionProperty;
	}

	public void setVersionProperty(String versionPropertyName) {
		setVersionProperty(VersionProperty.of(versionPropertyName));
	}

	/**
	 * Return the relative order of this BOM where lower values have higher priority. The
	 * default value is {@code Integer.MAX_VALUE}, indicating lowest priority. The Spring
	 * Boot dependencies BOM has an order of 100.
	 * @return the relative order of this BOM
	 */
	public Integer getOrder() {
		return this.order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * Return the BOM(s) that should be automatically included if this BOM is required.
	 * Can be {@code null} if it is provided via a mapping.
	 * @return the additional BOMs
	 */
	public List<String> getAdditionalBoms() {
		return this.additionalBoms;
	}

	public void setAdditionalBoms(List<String> additionalBoms) {
		this.additionalBoms = additionalBoms;
	}

	/**
	 * Return the repositories that are required if this BOM is required. Can be
	 * {@code null} if it is provided via a mapping.
	 * @return the repositories
	 */
	public List<String> getRepositories() {
		return this.repositories;
	}

	public void setRepositories(List<String> repositories) {
		this.repositories = repositories;
	}

	public List<Mapping> getMappings() {
		return this.mappings;
	}

	public void validate() {
		if (this.version == null && this.mappings.isEmpty()) {
			throw new InvalidInitializrMetadataException(
					"No version available for " + this);
		}
		updateVersionRange(VersionParser.DEFAULT);
	}

	public void updateVersionRange(VersionParser versionParser) {
		this.mappings.forEach((it) -> {
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
	 * @param bootVersion the Spring Boot version
	 * @return the bill of materials
	 */
	public BillOfMaterials resolve(Version bootVersion) {
		if (this.mappings.isEmpty()) {
			return this;
		}

		for (Mapping mapping : this.mappings) {
			if (mapping.range.match(bootVersion)) {
				BillOfMaterials resolvedBom = new BillOfMaterials(
						(mapping.groupId != null) ? mapping.groupId : this.groupId,
						(mapping.artifactId != null) ? mapping.artifactId
								: this.artifactId,
						mapping.version);
				resolvedBom.setVersionProperty(this.versionProperty);
				resolvedBom.setOrder(this.order);
				resolvedBom.repositories.addAll(!mapping.repositories.isEmpty()
						? mapping.repositories : this.repositories);
				resolvedBom.additionalBoms.addAll(!mapping.additionalBoms.isEmpty()
						? mapping.additionalBoms : this.additionalBoms);
				return resolvedBom;
			}
		}
		throw new IllegalStateException("No suitable mapping was found for " + this
				+ " and version " + bootVersion);
	}

	@Override
	public String toString() {
		return "BillOfMaterials ["
				+ ((this.groupId != null) ? "groupId=" + this.groupId + ", " : "")
				+ ((this.artifactId != null) ? "artifactId=" + this.artifactId + ", "
						: "")
				+ ((this.version != null) ? "version=" + this.version + ", " : "")
				+ ((this.versionProperty != null)
						? "versionProperty=" + this.versionProperty + ", " : "")
				+ ((this.order != null) ? "order=" + this.order + ", " : "")
				+ ((this.additionalBoms != null)
						? "additionalBoms=" + this.additionalBoms + ", " : "")
				+ ((this.repositories != null) ? "repositories=" + this.repositories : "")
				+ "]";
	}

	public static BillOfMaterials create(String groupId, String artifactId) {
		return new BillOfMaterials(groupId, artifactId);
	}

	public static BillOfMaterials create(String groupId, String artifactId,
			String version) {
		return new BillOfMaterials(groupId, artifactId, version);
	}

	/**
	 * Mapping information.
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class Mapping {

		private String versionRange;

		/**
		 * The groupId to use for this mapping or {@code null} to use the default.
		 */
		private String groupId;

		/**
		 * The artifactId to use for this mapping or {@code null} to use the default.
		 */
		private String artifactId;

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
			return this.range.toString();
		}

		public static Mapping create(String range, String version) {
			return new Mapping(range, version);
		}

		public static Mapping create(String range, String version,
				String... repositories) {
			return new Mapping(range, version, repositories);
		}

		public String getVersionRange() {
			return this.versionRange;
		}

		public void setVersionRange(String versionRange) {
			this.versionRange = versionRange;
		}

		public String getGroupId() {
			return this.groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		public String getArtifactId() {
			return this.artifactId;
		}

		public void setArtifactId(String artifactId) {
			this.artifactId = artifactId;
		}

		public String getVersion() {
			return this.version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public List<String> getRepositories() {
			return this.repositories;
		}

		public List<String> getAdditionalBoms() {
			return this.additionalBoms;
		}

		public VersionRange getRange() {
			return this.range;
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
					+ ((this.versionRange != null)
							? "versionRange=" + this.versionRange + ", " : "")
					+ ((this.groupId != null) ? "groupId=" + this.groupId + ", " : "")
					+ ((this.artifactId != null) ? "artifactId=" + this.artifactId + ", "
							: "")
					+ ((this.version != null) ? "version=" + this.version + ", " : "")
					+ ((this.repositories != null)
							? "repositories=" + this.repositories + ", " : "")
					+ ((this.additionalBoms != null)
							? "additionalBoms=" + this.additionalBoms + ", " : "")
					+ ((this.range != null) ? "range=" + this.range : "") + "]";
		}

	}

}
