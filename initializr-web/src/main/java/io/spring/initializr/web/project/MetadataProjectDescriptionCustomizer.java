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

import java.util.function.Supplier;

import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescriptionCustomizer;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.util.StringUtils;

/**
 * A {@link ProjectDescriptionCustomizer} that uses the {@link InitializrMetadata
 * metadata} to set default values for missing attributes if necessary.
 *
 * @author Stephane Nicoll
 */
public class MetadataProjectDescriptionCustomizer implements ProjectDescriptionCustomizer {

	private static final char[] VALID_MAVEN_SPECIAL_CHARACTERS = new char[] { '_', '-', '.' };

	private final InitializrMetadata metadata;

	public MetadataProjectDescriptionCustomizer(InitializrMetadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public void customize(MutableProjectDescription description) {
		if (!StringUtils.hasText(description.getApplicationName())) {
			description.setApplicationName(
					this.metadata.getConfiguration().generateApplicationName(description.getName()));
		}
		String targetArtifactId = determineValue(description.getArtifactId(),
				() -> this.metadata.getArtifactId().getContent());
		description.setArtifactId(cleanMavenCoordinate(targetArtifactId, "-"));
		if (targetArtifactId.equals(description.getBaseDirectory())) {
			description.setBaseDirectory(cleanMavenCoordinate(targetArtifactId, "-"));
		}
		if (!StringUtils.hasText(description.getDescription())) {
			description.setDescription(this.metadata.getDescription().getContent());
		}
		String targetGroupId = determineValue(description.getGroupId(), () -> this.metadata.getGroupId().getContent());
		description.setGroupId(cleanMavenCoordinate(targetGroupId, "."));
		if (!StringUtils.hasText(description.getName())) {
			description.setName(this.metadata.getName().getContent());
		}
		else if (targetArtifactId.equals(description.getName())) {
			description.setName(cleanMavenCoordinate(targetArtifactId, "-"));
		}
		description.setPackageName(this.metadata.getConfiguration().cleanPackageName(description.getPackageName(),
				this.metadata.getPackageName().getContent()));
		if (description.getPlatformVersion() == null) {
			description.setPlatformVersion(Version.parse(this.metadata.getBootVersions().getDefault().getId()));
		}
		if (!StringUtils.hasText(description.getVersion())) {
			description.setVersion(this.metadata.getVersion().getContent());
		}
	}

	private String cleanMavenCoordinate(String coordinate, String delimiter) {
		String[] elements = coordinate.split("[^\\w\\-.]+");
		if (elements.length == 1) {
			return coordinate;
		}
		StringBuilder builder = new StringBuilder();
		for (String element : elements) {
			if (shouldAppendDelimiter(element, builder)) {
				builder.append(delimiter);
			}
			builder.append(element);
		}
		return builder.toString();
	}

	private boolean shouldAppendDelimiter(String element, StringBuilder builder) {
		if (builder.length() == 0) {
			return false;
		}
		for (char c : VALID_MAVEN_SPECIAL_CHARACTERS) {
			int prevIndex = builder.length() - 1;
			if (element.charAt(0) == c || builder.charAt(prevIndex) == c) {
				return false;
			}
		}
		return true;
	}

	private String determineValue(String candidate, Supplier<String> fallback) {
		return (StringUtils.hasText(candidate)) ? candidate : fallback.get();
	}

}
