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

package io.spring.initializr.generator.buildsystem;

import io.spring.initializr.generator.version.VersionReference;

/**
 * A Bill of Materials (BOM) definition to be declared in a project's build configuration.
 *
 * @author Stephane Nicoll
 */
public class BillOfMaterials {

	private final String groupId;

	private final String artifactId;

	private final VersionReference version;

	private final int order;

	protected BillOfMaterials(Builder builder) {
		this.groupId = builder.groupId;
		this.artifactId = builder.artifactId;
		this.version = builder.version;
		this.order = builder.order;
	}

	/**
	 * Initialize a new BOM {@link Builder} with the specified coordinates.
	 * @param groupId the group ID of the bom
	 * @param artifactId the artifact ID of the bom
	 * @return a new builder
	 */
	public static Builder withCoordinates(String groupId, String artifactId) {
		return new Builder(groupId, artifactId);
	}

	/**
	 * Return the group ID of the bom.
	 * @return the group ID
	 */
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * Return the artifact ID of the bom.
	 * @return the artifact ID
	 */
	public String getArtifactId() {
		return this.artifactId;
	}

	/**
	 * Return the {@linkplain VersionReference version reference} of the bom. Can be a
	 * fixed value or refer to a property.
	 * @return the version reference
	 */
	public VersionReference getVersion() {
		return this.version;
	}

	/**
	 * Return the order of this bom relative to other boms.
	 * @return the bom order
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * Builder for a Bill of Materials.
	 */
	public static class Builder {

		private String groupId;

		private String artifactId;

		private VersionReference version;

		private int order = Integer.MAX_VALUE;

		protected Builder(String groupId, String artifactId) {
			this.groupId = groupId;
			this.artifactId = artifactId;
		}

		/**
		 * Set the group ID of the bom.
		 * @param groupId the group ID
		 * @return this for method chaining
		 */
		public Builder groupId(String groupId) {
			this.groupId = groupId;
			return this;
		}

		/**
		 * Set the artifact ID of the bom.
		 * @param artifactId the artifact ID
		 * @return this for method chaining
		 */
		public Builder artifactId(String artifactId) {
			this.artifactId = artifactId;
			return this;
		}

		/**
		 * Set the {@linkplain VersionReference version} of the bom.
		 * @param version the version
		 * @return this for method chaining
		 * @see VersionReference#ofProperty(String)
		 * @see VersionReference#ofValue(String)
		 */
		public Builder version(VersionReference version) {
			this.version = version;
			return this;
		}

		/**
		 * Set the order of the bom relative to other boms. By default the order is
		 * {@code Integer.MAX_VALUE} which means the bom has the lowest priority.
		 * @param order the order of the bom
		 * @return this for method chaining
		 */
		public Builder order(int order) {
			this.order = order;
			return this;
		}

		/**
		 * Build a {@link BillOfMaterials} with the current state of this builder.
		 * @return a {@link BillOfMaterials}
		 */
		public BillOfMaterials build() {
			return new BillOfMaterials(this);
		}

	}

}
