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

/**
 * Resolve an item of the {@link Build} against an identifier.
 *
 * @author Stephane Nicoll
 */
public interface BuildItemResolver {

	/**
	 * Resolve the {@link Dependency} with the specified {@code id}.
	 * @param id the id of the dependency
	 * @return the matching {@link Dependency} or {@code null} if none is found
	 */
	Dependency resolveDependency(String id);

	/**
	 * Resolve the {@link BillOfMaterials} with the specified {@code id}.
	 * @param id the id of the bom
	 * @return the matching {@link BillOfMaterials} or {@code null} if none is found
	 */
	BillOfMaterials resolveBom(String id);

	/**
	 * Resolve the {@link MavenRepository repository} with the specified {@code id}.
	 * @param id the id of the bom
	 * @return the matching {@link MavenRepository} or {@code null} if none is found
	 */
	MavenRepository resolveRepository(String id);

}
