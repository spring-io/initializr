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

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A default {@link ProjectRequestToDescriptionConverter} implementation that uses the
 * {@link InitializrMetadata metadata} to set default values for missing attributes if
 * necessary. Transparently transform the platform version if necessary using a
 * {@link ProjectRequestPlatformVersionTransformer}.
 *
 * @author Madhura Bhave
 * @author HaiTao Zhang
 * @author Stephane Nicoll
 * @author Nirbhay Mishra
 */
public class DefaultProjectRequestToDescriptionConverter
		implements ProjectRequestToDescriptionConverter<ProjectRequest> {

	private final ProjectRequestPlatformVersionTransformer platformVersionTransformer;

	public DefaultProjectRequestToDescriptionConverter() {
		this((version, metadata) -> version);
	}

	public DefaultProjectRequestToDescriptionConverter(
			ProjectRequestPlatformVersionTransformer platformVersionTransformer) {
		Assert.notNull(platformVersionTransformer, "PlatformVersionTransformer must not be null");
		this.platformVersionTransformer = platformVersionTransformer;
	}

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
		ProjectRequestValidator validator = new ProjectRequestValidator(metadata);
		validator.validate(request);
		Version platformVersion = getPlatformVersion(request, metadata);
		List<Dependency> resolvedDependencies = getResolvedDependencies(request, platformVersion, metadata);
		validateDependencyRange(platformVersion, resolvedDependencies);

		description.setApplicationName(request.getApplicationName());
		description.setArtifactId(cleanInputValue(request.getArtifactId()));
		description.setBaseDirectory(request.getBaseDir());
		description.setBuildSystem(getBuildSystem(request, metadata));
		description.setDescription(request.getDescription());
		description.setGroupId(cleanInputValue(request.getGroupId()));
		description.setLanguage(Language.forId(request.getLanguage(), request.getJavaVersion()));
		description.setName(cleanInputValue(request.getName()));
		description.setPackageName(cleanInputValue(request.getPackageName()));
		description.setPackaging(Packaging.forId(request.getPackaging()));
		description.setPlatformVersion(platformVersion);
		description.setVersion(request.getVersion());
		resolvedDependencies.forEach((dependency) -> description.addDependency(dependency.getId(),
				MetadataBuildItemMapper.toDependency(dependency)));
	}

	/**
	 * Clean input value to rely on US-ascii character as much as possible.
	 * @param value the input value to clean
	 * @return a value that can be used as part of an identifier
	 */
	protected String cleanInputValue(String value) {
		return StringUtils.hasText(value) ? Normalizer.normalize(value, Normalizer.Form.NFKD).replaceAll("\\p{M}", "")
				: value;
	}

	private void validateDependencyRange(Version platformVersion, List<Dependency> resolvedDependencies) {
		resolvedDependencies.forEach((dep) -> {
			if (!dep.match(platformVersion)) {
				throw new InvalidProjectRequestException(
						"Dependency '" + dep.getId() + "' is not compatible " + "with Spring Boot " + platformVersion);
			}
		});
	}

	private BuildSystem getBuildSystem(ProjectRequest request, InitializrMetadata metadata) {
		Map<String, String> typeTags = metadata.getTypes().get(request.getType()).getTags();
		String id = typeTags.get("build");
		String dialect = typeTags.get("dialect");
		return BuildSystem.forIdAndDialect(id, dialect);
	}

	private Version getPlatformVersion(ProjectRequest request, InitializrMetadata metadata) {
		String versionText = (request.getBootVersion() != null) ? request.getBootVersion()
				: metadata.getBootVersions().getDefault().getId();
		Version version = Version.parse(versionText);
		return this.platformVersionTransformer.transform(version, metadata);
	}

	private List<Dependency> getResolvedDependencies(ProjectRequest request, Version platformVersion,
			InitializrMetadata metadata) {
		List<String> depIds = request.getDependencies();
		return depIds.stream().map((it) -> {
			Dependency dependency = metadata.getDependencies().get(it);
			return dependency.resolve(platformVersion);
		}).collect(Collectors.toList());
	}

}
