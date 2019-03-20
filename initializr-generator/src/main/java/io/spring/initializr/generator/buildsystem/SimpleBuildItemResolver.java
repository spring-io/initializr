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

import java.util.function.Function;

/**
 * A simple {@link BuildItemResolver} implementation.
 *
 * @author Stephane Nicoll
 */
public class SimpleBuildItemResolver implements BuildItemResolver {

	private final Function<String, Dependency> dependencyResolver;

	private final Function<String, BillOfMaterials> bomResolver;

	private final Function<String, MavenRepository> repositoryResolver;

	public SimpleBuildItemResolver(Function<String, Dependency> dependencyResolver,
			Function<String, BillOfMaterials> bomResolver,
			Function<String, MavenRepository> repositoryResolver) {
		this.dependencyResolver = dependencyResolver;
		this.bomResolver = bomResolver;
		this.repositoryResolver = repositoryResolver;
	}

	@Override
	public Dependency resolveDependency(String id) {
		return this.dependencyResolver.apply(id);
	}

	@Override
	public BillOfMaterials resolveBom(String id) {
		return this.bomResolver.apply(id);
	}

	@Override
	public MavenRepository resolveRepository(String id) {
		return this.repositoryResolver.apply(id);
	}

}
