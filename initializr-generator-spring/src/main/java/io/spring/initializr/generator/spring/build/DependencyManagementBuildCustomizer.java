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

package io.spring.initializr.generator.spring.build;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Repository;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;

import org.springframework.core.Ordered;

/**
 * A {@link BuildCustomizer} that configures the {@link Build} based on the metadata.
 *
 * @author Stephane Nicoll
 */
public class DependencyManagementBuildCustomizer implements BuildCustomizer<Build> {

	private final ResolvedProjectDescription projectDescription;

	private final InitializrMetadata metadata;

	public DependencyManagementBuildCustomizer(
			ResolvedProjectDescription projectDescription, InitializrMetadata metadata) {
		this.projectDescription = projectDescription;
		this.metadata = metadata;
	}

	@Override
	public void customize(Build build) {
		contributeDependencyManagement(build);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 5;
	}

	protected void contributeDependencyManagement(Build build) {
		Map<String, BillOfMaterials> resolvedBoms = new LinkedHashMap<>();
		Map<String, Repository> repositories = new LinkedHashMap<>();
		mapDependencies(build).forEach((dependency) -> {
			if (dependency.getBom() != null) {
				resolveBom(resolvedBoms, dependency.getBom(),
						this.projectDescription.getPlatformVersion());
			}
			if (dependency.getRepository() != null) {
				String repositoryId = dependency.getRepository();
				repositories.computeIfAbsent(repositoryId, (key) -> this.metadata
						.getConfiguration().getEnv().getRepositories().get(key));
			}
		});
		resolvedBoms.values()
				.forEach((bom) -> bom.getRepositories()
						.forEach((repositoryId) -> repositories.computeIfAbsent(
								repositoryId, (key) -> this.metadata.getConfiguration()
										.getEnv().getRepositories().get(key))));
		resolvedBoms.forEach((key, bom) -> {
			build.boms().add(key, MetadataBuildItemMapper.toBom(bom));
			if (bom.getVersionProperty() != null) {
				build.addVersionProperty(bom.getVersionProperty(), bom.getVersion());
			}
		});
		repositories.keySet().forEach((id) -> build.repositories().add(id));
	}

	private Stream<Dependency> mapDependencies(Build build) {
		return build.dependencies().ids()
				.map((id) -> this.metadata.getDependencies().get(id))
				.filter(Objects::nonNull);
	}

	private void resolveBom(Map<String, BillOfMaterials> boms, String bomId,
			Version requestedVersion) {
		if (!boms.containsKey(bomId)) {
			BillOfMaterials bom = this.metadata.getConfiguration().getEnv().getBoms()
					.get(bomId).resolve(requestedVersion);
			bom.getAdditionalBoms()
					.forEach((id) -> resolveBom(boms, id, requestedVersion));
			boms.put(bomId, bom);
		}
	}

}
