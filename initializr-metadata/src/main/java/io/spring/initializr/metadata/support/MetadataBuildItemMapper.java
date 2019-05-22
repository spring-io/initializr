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

package io.spring.initializr.metadata.support;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.version.VersionReference;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.Repository;

/**
 * An internal class used to easily translate metadata information to {@link Build} items.
 *
 * @author Stephane Nicoll
 */
public final class MetadataBuildItemMapper {

	private MetadataBuildItemMapper() {
	}

	/**
	 * Return an {@link Build} dependency from a {@link Dependency dependency metadata}.
	 * @param dependency a dependency metadata
	 * @return an equivalent build dependency
	 */
	public static io.spring.initializr.generator.buildsystem.Dependency toDependency(
			Dependency dependency) {
		if (dependency == null) {
			return null;
		}
		VersionReference versionReference = (dependency.getVersion() != null)
				? VersionReference.ofValue(dependency.getVersion()) : null;
		return io.spring.initializr.generator.buildsystem.Dependency
				.withCoordinates(dependency.getGroupId(), dependency.getArtifactId())
				.version(versionReference).scope(toDependencyScope(dependency.getScope()))
				.type(dependency.getType()).build();
	}

	private static DependencyScope toDependencyScope(String scope) {
		switch (scope) {
		case Dependency.SCOPE_ANNOTATION_PROCESSOR:
			return DependencyScope.ANNOTATION_PROCESSOR;
		case Dependency.SCOPE_COMPILE:
			return DependencyScope.COMPILE;
		case Dependency.SCOPE_RUNTIME:
			return DependencyScope.RUNTIME;
		case Dependency.SCOPE_COMPILE_ONLY:
			return DependencyScope.COMPILE_ONLY;
		case Dependency.SCOPE_PROVIDED:
			return DependencyScope.PROVIDED_RUNTIME;
		case Dependency.SCOPE_TEST:
			return DependencyScope.TEST_COMPILE;
		}
		return null;
	}

	/**
	 * Return a {@link Build} bom from a {@link BillOfMaterials bom metadata}.
	 * @param bom a metadata bom
	 * @return an equivalent build bom
	 */
	public static io.spring.initializr.generator.buildsystem.BillOfMaterials toBom(
			BillOfMaterials bom) {
		if (bom == null) {
			return null;
		}
		VersionReference version = (bom.getVersionProperty() != null)
				? VersionReference.ofProperty(bom.getVersionProperty())
				: VersionReference.ofValue(bom.getVersion());
		return new io.spring.initializr.generator.buildsystem.BillOfMaterials(
				bom.getGroupId(), bom.getArtifactId(), version, bom.getOrder());
	}

	/**
	 * Return a {@link Build} repository from a {@link Repository repository metadata}.
	 * @param id the repository id
	 * @param repository a repository metadata
	 * @return an equivalent build repository
	 */
	public static io.spring.initializr.generator.buildsystem.MavenRepository toRepository(
			String id, Repository repository) {
		if (repository == null) {
			return null;
		}
		return new io.spring.initializr.generator.buildsystem.MavenRepository(id,
				repository.getName(), repository.getUrl().toExternalForm(),
				repository.isSnapshotsEnabled());
	}

}
