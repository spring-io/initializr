/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.web.project;

import java.util.List;
import java.util.stream.Collectors;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.MetadataBuildItemMapper;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Type;
import io.spring.initializr.util.Version;
import io.spring.initializr.web.InvalidProjectRequestException;

import org.springframework.util.StringUtils;

/**
 * Validates a {@link ProjectRequest} and creates a {@link ProjectDescription} from it.
 *
 * @author Madhura Bhave
 */
public class ProjectRequestToDescriptionConverter {

	private static final Version VERSION_1_5_0 = Version.parse("1.5.0.RELEASE");

	public ProjectDescription convert(ProjectRequest request,
			InitializrMetadata metadata) {
		validate(request, metadata);
		ProjectDescription description = new ProjectDescription();
		description.setApplicationName(getApplicationName(request, metadata));
		description.setArtifactId(request.getArtifactId());
		description.setBaseDirectory(request.getBaseDir());
		description.setBuildSystem(getBuildSystem(request));
		description.setDescription(request.getDescription());
		description.setGroupId(request.getGroupId());
		description.setLanguage(
				Language.forId(request.getLanguage(), request.getJavaVersion()));
		description.setName(request.getName());
		description.setPackageName(getPackageName(request, metadata));
		description.setPackaging(Packaging.forId(request.getPackaging()));
		String springBootVersion = getSpringBootVersion(request, metadata);
		description
				.setPlatformVersion(MetadataBuildItemMapper.toVersion(springBootVersion));
		getResolvedDependencies(request, springBootVersion, metadata)
				.forEach((dependency) -> description.addDependency(dependency.getId(),
						MetadataBuildItemMapper.toDependency(dependency)));
		return description;
	}

	private void validate(ProjectRequest request, InitializrMetadata metadata) {
		validateSpringBootVersion(request);
		validateType(request.getType(), metadata);
		validateLanguage(request.getLanguage(), metadata);
		validatePackaging(request.getPackaging(), metadata);
		validateDependencies(request, metadata);
	}

	private void validateSpringBootVersion(ProjectRequest request) {
		Version bootVersion = Version.safeParse(request.getBootVersion());
		if (bootVersion != null && bootVersion.compareTo(VERSION_1_5_0) < 0) {
			throw new InvalidProjectRequestException("Invalid Spring Boot version "
					+ bootVersion + " must be 1.5.0 or higher");
		}
	}

	private void validateType(String type, InitializrMetadata metadata) {
		if (type != null) {
			Type typeFromMetadata = metadata.getTypes().get(type);
			if (typeFromMetadata == null) {
				throw new InvalidProjectRequestException(
						"Unknown type '" + type + "' check project metadata");
			}
		}
	}

	private void validateLanguage(String language, InitializrMetadata metadata) {
		if (language != null) {
			DefaultMetadataElement languageFromMetadata = metadata.getLanguages()
					.get(language);
			if (languageFromMetadata == null) {
				throw new InvalidProjectRequestException(
						"Unknown language '" + language + "' check project metadata");
			}
		}
	}

	private void validatePackaging(String packaging, InitializrMetadata metadata) {
		if (packaging != null) {
			DefaultMetadataElement packagingFromMetadata = metadata.getPackagings()
					.get(packaging);
			if (packagingFromMetadata == null) {
				throw new InvalidProjectRequestException(
						"Unknown packaging '" + packaging + "' check project metadata");
			}
		}
	}

	private void validateDependencies(ProjectRequest request,
			InitializrMetadata metadata) {
		List<String> dependencies = (!request.getStyle().isEmpty() ? request.getStyle()
				: request.getDependencies());
		dependencies.forEach((dep) -> {
			Dependency dependency = metadata.getDependencies().get(dep);
			if (dependency == null) {
				throw new InvalidProjectRequestException(
						"Unknown dependency '" + dep + "' check project metadata");
			}
		});
	}

	private BuildSystem getBuildSystem(ProjectRequest request) {
		return (request.getType().startsWith("gradle")) ? new GradleBuildSystem()
				: new MavenBuildSystem();
	}

	private String getPackageName(ProjectRequest request, InitializrMetadata metadata) {
		return metadata.getConfiguration().cleanPackageName(request.getPackageName(),
				metadata.getPackageName().getContent());
	}

	private String getApplicationName(ProjectRequest request,
			InitializrMetadata metadata) {
		if (!StringUtils.hasText(request.getApplicationName())) {
			return metadata.getConfiguration().generateApplicationName(request.getName());
		}
		return request.getApplicationName();
	}

	private String getSpringBootVersion(ProjectRequest request,
			InitializrMetadata metadata) {
		return (request.getBootVersion() != null) ? request.getBootVersion()
				: metadata.getBootVersions().getDefault().getId();
	}

	private List<Dependency> getResolvedDependencies(ProjectRequest request,
			String springBootVersion, InitializrMetadata metadata) {
		List<String> depIds = (!request.getStyle().isEmpty() ? request.getStyle()
				: request.getDependencies());
		Version requestedVersion = Version.parse(springBootVersion);
		return depIds.stream().map((it) -> {
			Dependency dependency = metadata.getDependencies().get(it);
			return dependency.resolve(requestedVersion);
		}).collect(Collectors.toList());
	}

}
