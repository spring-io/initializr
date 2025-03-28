/*
 * Copyright 2012-2022 the original author or authors.
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

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrConfiguration;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Type;

/**
 * A validator for {@link ProjectRequest} instances, ensuring they meet the requirements
 * defined in the {@link InitializrMetadata}. This class performs validation on various
 * aspects of the project request, including the platform version, type, language,
 * packaging, and dependencies. Each validation step checks against the provided metadata
 * to ensure the request is valid and compatible.
 *
 * @author Akshat Gulati
 */
public class ProjectRequestValidator {

	private final InitializrMetadata metadata;

	/**
	 * Constructs a new {@link ProjectRequestValidator} with the specified metadata.
	 * @param metadata the metadata to use for validation
	 */
	public ProjectRequestValidator(InitializrMetadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * Validates the specified {@link ProjectRequest}. This method orchestrates the
	 * validation process by calling individual validation methods for each aspect of the
	 * project request. If any validation fails, an {@link InvalidProjectRequestException}
	 * is thrown.
	 * @param request the project request to validate
	 * @throws InvalidProjectRequestException if the request is invalid
	 */
	public void validate(ProjectRequest request) {
		validatePlatformVersion(request);
		validateType(request.getType());
		validateLanguage(request.getLanguage());
		validatePackaging(request.getPackaging());
		validateDependencies(request);
	}

	/**
	 * Validates the platform version specified in the request. Checks if the provided
	 * Spring Boot version is compatible with the platform's compatibility range defined
	 * in the metadata.
	 * @param request the project request containing the boot version
	 * @throws InvalidProjectRequestException if the version is invalid or incompatible
	 */
	private void validatePlatformVersion(ProjectRequest request) {
		Version platformVersion = Version.safeParse(request.getBootVersion());
		InitializrConfiguration.Platform platform = this.metadata.getConfiguration().getEnv().getPlatform();
		if (platformVersion != null && !platform.isCompatibleVersion(platformVersion)) {
			throw new InvalidProjectRequestException("Invalid Spring Boot version '" + platformVersion
					+ "', Spring Boot compatibility range is " + platform.determineCompatibilityRangeRequirement());
		}
	}

	/**
	 * Validates the type specified in the request. Ensures that the type exists in the
	 * metadata and has the required 'build' tag.
	 * @param type the type to validate
	 * @throws InvalidProjectRequestException if the type is unknown or missing the build
	 * tag
	 */
	private void validateType(String type) {
		if (type != null) {
			Type typeFromMetadata = this.metadata.getTypes().get(type);
			if (typeFromMetadata == null) {
				throw new InvalidProjectRequestException("Unknown type '" + type + "' check project metadata");
			}
			if (!typeFromMetadata.getTags().containsKey("build")) {
				throw new InvalidProjectRequestException(
						"Invalid type '" + type + "' (missing build tag) check project metadata");
			}
		}
	}

	/**
	 * Validates the language specified in the request.
	 *
	 * <p>
	 * Checks if the language is defined in the metadata.
	 * @param language the language to validate
	 * @throws InvalidProjectRequestException if the language is unknown
	 */
	private void validateLanguage(String language) {
		if (language != null) {
			DefaultMetadataElement languageFromMetadata = this.metadata.getLanguages().get(language);
			if (languageFromMetadata == null) {
				throw new InvalidProjectRequestException("Unknown language '" + language + "' check project metadata");
			}
		}
	}

	/**
	 * Validates the packaging specified in the request. Ensures that the packaging type
	 * is defined in the metadata.
	 * @param packaging the packaging to validate
	 * @throws InvalidProjectRequestException if the packaging is unknown
	 */
	private void validatePackaging(String packaging) {
		if (packaging != null) {
			DefaultMetadataElement packagingFromMetadata = this.metadata.getPackagings().get(packaging);
			if (packagingFromMetadata == null) {
				throw new InvalidProjectRequestException(
						"Unknown packaging '" + packaging + "' check project metadata");
			}
		}
	}

	/**
	 * Validates the dependencies specified in the request. Checks that each dependency
	 * exists in the metadata.
	 * @param request the project request containing the dependencies
	 * @throws InvalidProjectRequestException if any dependency is unknown
	 */
	private void validateDependencies(ProjectRequest request) {
		List<String> dependencies = request.getDependencies();
		dependencies.forEach((dep) -> {
			Dependency dependency = this.metadata.getDependencies().get(dep);
			if (dependency == null) {
				throw new InvalidProjectRequestException("Unknown dependency '" + dep + "' check project metadata");
			}
		});
	}

}
