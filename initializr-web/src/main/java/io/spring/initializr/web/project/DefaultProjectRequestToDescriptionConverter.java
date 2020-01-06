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

package io.spring.initializr.web.project;

import java.util.List;
import java.util.stream.Collectors;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrConfiguration.Env;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Type;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;

/**
 * A default {@link ProjectRequestToDescriptionConverter} implementation that uses the
 * {@link InitializrMetadata metadata} to set default values for missing attributes if
 * necessary.
 *
 * @author Madhura Bhave
 * @author HaiTao Zhang
 */
public class DefaultProjectRequestToDescriptionConverter
		implements ProjectRequestToDescriptionConverter<ProjectRequest> {

	@Override
	public ProjectDescription convert(ProjectRequest request, InitializrMetadata metadata) {
		MutableProjectDescription description = new MutableProjectDescription();
		convert(request, description, metadata);
		return description;
	}

	/**
	 * Validate the specified {@link ProjectRequest request} and initialize the specified
	 * {@link ProjectDescription description}. Override any attribute of the description
	 * that are managed by this instance.
	 * @param request the request to validate
	 * @param description the description to initialize
	 * @param metadata the metadata instance to use to apply defaults if necessary
	 */
	public void convert(ProjectRequest request, MutableProjectDescription description, InitializrMetadata metadata) {
		validate(request, metadata);
		String springBootVersion = getSpringBootVersion(request, metadata);
		List<Dependency> resolvedDependencies = getResolvedDependencies(request, springBootVersion, metadata);
		validateDependencyRange(springBootVersion, resolvedDependencies);

		description.setApplicationName(request.getApplicationName());
		description.setArtifactId(request.getArtifactId());
		description.setBaseDirectory(request.getBaseDir());
		description.setBuildSystem(getBuildSystem(request, metadata));
		description.setDescription(request.getDescription());
		description.setGroupId(request.getGroupId());
		description.setLanguage(Language.forId(request.getLanguage(), request.getJavaVersion()));
		description.setName(request.getName());
		description.setPackageName(request.getPackageName());
		description.setPackaging(Packaging.forId(request.getPackaging()));
		description.setPlatformVersion(Version.parse(springBootVersion));
		description.setVersion(request.getVersion());
		resolvedDependencies.forEach((dependency) -> description.addDependency(dependency.getId(),
				MetadataBuildItemMapper.toDependency(dependency)));
	}

	private void validate(ProjectRequest request, InitializrMetadata metadata) {
		validateSpringBootVersion(request, metadata);
		validateType(request.getType(), metadata);
		validateLanguage(request.getLanguage(), metadata);
		validatePackaging(request.getPackaging(), metadata);
		validateDependencies(request, metadata);
	}

	private void validateSpringBootVersion(ProjectRequest request, InitializrMetadata metadata) {
		Version bootVersion = Version.safeParse(request.getBootVersion());
		Env env = metadata.getConfiguration().getEnv();
		if (bootVersion != null && !env.isCompatiblePlatformVersion(bootVersion)) {
			throw new InvalidProjectRequestException("Invalid Spring Boot version '" + bootVersion
					+ "', Spring Boot compatibility range is " + env.determinePlatformCompatibilityRangeRequirement());
		}
	}

	private void validateType(String type, InitializrMetadata metadata) {
		if (type != null) {
			Type typeFromMetadata = metadata.getTypes().get(type);
			if (typeFromMetadata == null) {
				throw new InvalidProjectRequestException("Unknown type '" + type + "' check project metadata");
			}
			if (!typeFromMetadata.getTags().containsKey("build")) {
				throw new InvalidProjectRequestException(
						"Invalid type '" + type + "' (missing build tag) check project metadata");
			}
		}
	}

	private void validateLanguage(String language, InitializrMetadata metadata) {
		if (language != null) {
			DefaultMetadataElement languageFromMetadata = metadata.getLanguages().get(language);
			if (languageFromMetadata == null) {
				throw new InvalidProjectRequestException("Unknown language '" + language + "' check project metadata");
			}
		}
	}

	private void validatePackaging(String packaging, InitializrMetadata metadata) {
		if (packaging != null) {
			DefaultMetadataElement packagingFromMetadata = metadata.getPackagings().get(packaging);
			if (packagingFromMetadata == null) {
				throw new InvalidProjectRequestException(
						"Unknown packaging '" + packaging + "' check project metadata");
			}
		}
	}

	private void validateDependencies(ProjectRequest request, InitializrMetadata metadata) {
		List<String> dependencies = request.getDependencies();
		dependencies.forEach((dep) -> {
			Dependency dependency = metadata.getDependencies().get(dep);
			if (dependency == null) {
				throw new InvalidProjectRequestException("Unknown dependency '" + dep + "' check project metadata");
			}
		});
	}

	private void validateDependencyRange(String springBootVersion, List<Dependency> resolvedDependencies) {
		resolvedDependencies.forEach((dep) -> {
			if (!dep.match(Version.parse(springBootVersion))) {
				throw new InvalidProjectRequestException("Dependency '" + dep.getId() + "' is not compatible "
						+ "with Spring Boot " + springBootVersion);
			}
		});
	}

	private BuildSystem getBuildSystem(ProjectRequest request, InitializrMetadata metadata) {
		Type typeFromMetadata = metadata.getTypes().get(request.getType());
		return BuildSystem.forId(typeFromMetadata.getTags().get("build"));
	}

	private String getSpringBootVersion(ProjectRequest request, InitializrMetadata metadata) {
		return (request.getBootVersion() != null) ? request.getBootVersion()
				: metadata.getBootVersions().getDefault().getId();
	}

	private List<Dependency> getResolvedDependencies(ProjectRequest request, String springBootVersion,
			InitializrMetadata metadata) {
		List<String> depIds = request.getDependencies();
		Version requestedVersion = Version.parse(springBootVersion);
		return depIds.stream().map((it) -> {
			Dependency dependency = metadata.getDependencies().get(it);
			return dependency.resolve(requestedVersion);
		}).collect(Collectors.toList());
	}

}
