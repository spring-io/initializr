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

package io.spring.initializr.service.extension;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.util.Version;

/**
 * Base {@link ProjectRequestPostProcessor} with reusable utilities.
 *
 * @author Stephane Nicoll
 */
public class AbstractProjectRequestPostProcessor implements ProjectRequestPostProcessor {

	/**
	 * Determine if the {@link ProjectRequest request} defines the dependency with the
	 * specified {@code dependencyId}.
	 * @param request the request to handle
	 * @param dependencyId the id of a dependency
	 * @return {@code true} if the project defines that dependency
	 */
	protected boolean hasDependency(ProjectRequest request, String dependencyId) {
		return hasDependencies(request, dependencyId);
	}

	/**
	 * Determine if the {@link ProjectRequest request} defines the dependencies with the
	 * specified {@code dependenciesId}.
	 * @param request the request to handle
	 * @param dependenciesId the dependency ids
	 * @return {@code true} if the project defines all dependencies
	 */
	protected boolean hasDependencies(ProjectRequest request, String... dependenciesId) {
		for (String id : dependenciesId) {
			if (getDependency(request, id) == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return the {@link Dependency} with the specified {@code id} or {@code null} if the
	 * project does not define it.
	 * @param request the request to handle
	 * @param id the id of a dependency
	 * @return the {@link Dependency} with that id or {@code null} if the project does not
	 * define such dependency
	 */
	protected Dependency getDependency(ProjectRequest request, String id) {
		return request.getResolvedDependencies().stream()
				.filter((d) -> id.equals(d.getId())).findFirst().orElse(null);
	}

	/**
	 * Specify if the Spring Boot version of the {@link ProjectRequest request} is higher
	 * or equal to the specified {@link Version}.
	 * @param request the request to handle
	 * @param version the minimum version
	 * @return {@code true} if the requested version is equal or higher than the specified
	 * {@code version}
	 */
	protected boolean isSpringBootVersionAtLeastAfter(ProjectRequest request,
			Version version) {
		Version requestVersion = Version.safeParse(request.getBootVersion());
		return version.compareTo(requestVersion) <= 0;
	}

}
