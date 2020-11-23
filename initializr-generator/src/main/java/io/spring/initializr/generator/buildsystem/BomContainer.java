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

package io.spring.initializr.generator.buildsystem;

import java.util.LinkedHashMap;
import java.util.function.Function;

/**
 * A {@link BuildItemContainer} implementation for boms.
 *
 * @author Stephane Nicoll
 */
public class BomContainer extends BuildItemContainer<String, BillOfMaterials> {

	/**
	 * Create an instance with the specified {@code itemResolver}.
	 * @param itemResolver the function that returns a {@link BillOfMaterials} based on an
	 * identifier.
	 */
	public BomContainer(Function<String, BillOfMaterials> itemResolver) {
		super(new LinkedHashMap<>(), itemResolver);
	}

	/**
	 * Register a {@link BillOfMaterials} with the specified {@code id} and a
	 * {@linkplain BillOfMaterials.Builder state}.
	 * @param id the id of the bom
	 * @param builder the state of the bom
	 */
	public void add(String id, BillOfMaterials.Builder builder) {
		add(id, builder.build());
	}

}
