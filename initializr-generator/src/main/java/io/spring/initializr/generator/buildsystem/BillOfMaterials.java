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

	public BillOfMaterials(String groupId, String artifactId, VersionReference version) {
		this(groupId, artifactId, version, Integer.MAX_VALUE);
	}

	public BillOfMaterials(String groupId, String artifactId, VersionReference version,
			int order) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.order = order;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

	public VersionReference getVersion() {
		return this.version;
	}

	public int getOrder() {
		return this.order;
	}

}
