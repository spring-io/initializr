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

import java.util.LinkedHashMap;
import java.util.function.Function;

import io.spring.initializr.generator.version.VersionReference;

/**
 * A {@link BuildItemContainer} implementation for boms.
 *
 * @author Stephane Nicoll
 */
public class BomContainer extends BuildItemContainer<String, BillOfMaterials> {

	BomContainer(Function<String, BillOfMaterials> itemResolver) {
		super(new LinkedHashMap<>(), itemResolver);
	}

	/**
	 * Register a {@link BillOfMaterials} with the specified {@code id} and a
	 * {@link Integer#MAX_VALUE} order.
	 * @param id the id of the bom
	 * @param groupId the groupId
	 * @param artifactId the artifactId
	 * @param version the {@link VersionReference}
	 */
	public void add(String id, String groupId, String artifactId,
			VersionReference version) {
		add(id, new BillOfMaterials(groupId, artifactId, version));
	}

	/**
	 * Register a {@link BillOfMaterials} with the specified {@code id} and a custom
	 * order.
	 * @param id the id of the bom
	 * @param groupId the groupId
	 * @param artifactId the artifactId
	 * @param version the {@link VersionReference}
	 * @param order the order of the bom
	 */
	public void add(String id, String groupId, String artifactId,
			VersionReference version, int order) {
		add(id, new BillOfMaterials(groupId, artifactId, version, order));
	}

}
