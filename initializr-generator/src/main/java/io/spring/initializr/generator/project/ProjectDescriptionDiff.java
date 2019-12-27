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

package io.spring.initializr.generator.project;

import java.util.Objects;
import java.util.function.BiConsumer;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.version.Version;

/**
 * Provides a convenient API for determining if certain fields on a
 * {@link ProjectDescription} were modified.
 *
 * @author Chris Bono
 * @author Stephane Nicoll
 */
public class ProjectDescriptionDiff {

	private final ProjectDescription original;

	/**
	 * Create a {@link ProjectDescriptionDiff} that uses a copy of the specified
	 * description as its source.
	 * @param original the description to copy as the source
	 */
	public ProjectDescriptionDiff(ProjectDescription original) {
		this.original = original.createCopy();
	}

	/**
	 * Return the original {@link ProjectDescription} that is being tracked.
	 * @return the original description
	 */
	public ProjectDescription getOriginal() {
		return this.original;
	}

	/**
	 * Calls the specified consumer if the {@code platformVersion} is different on the
	 * original source project description than the specified project description.
	 * @param current the project description to test against
	 * @param consumer to call if the property has changed
	 */
	public void ifPlatformVersionChanged(ProjectDescription current, BiConsumer<Version, Version> consumer) {
		if (!Objects.equals(this.original.getPlatformVersion(), current.getPlatformVersion())) {
			consumer.accept(this.original.getPlatformVersion(), current.getPlatformVersion());
		}
	}

	/**
	 * Calls the specified consumer if the {@code buildSystem} is different on the
	 * original source project description than the specified project description.
	 * @param current the description to test against
	 * @param consumer to call if the property has changed
	 */
	public void ifBuildSystemChanged(ProjectDescription current, BiConsumer<BuildSystem, BuildSystem> consumer) {
		if (!Objects.equals(this.original.getBuildSystem(), current.getBuildSystem())) {
			consumer.accept(this.original.getBuildSystem(), current.getBuildSystem());
		}
	}

	/**
	 * Calls the specified consumer if the {@code packaging} is different on the original
	 * source project description than the specified project description.
	 * @param current the description to test against
	 * @param consumer to call if the property has changed
	 */
	public void ifPackagingChanged(ProjectDescription current, BiConsumer<Packaging, Packaging> consumer) {
		if (!Objects.equals(this.original.getPackaging(), current.getPackaging())) {
			consumer.accept(this.original.getPackaging(), current.getPackaging());
		}
	}

	/**
	 * Calls the specified consumer if the {@code language} is different on the original
	 * source project description than the specified project description.
	 * @param current the description to test against
	 * @param consumer to call if the property has changed
	 */
	public void ifLanguageChanged(ProjectDescription current, BiConsumer<Language, Language> consumer) {
		if (!Objects.equals(this.original.getLanguage(), current.getLanguage())) {
			consumer.accept(this.original.getLanguage(), current.getLanguage());
		}
	}

	/**
	 * Calls the specified consumer if the {@code groupId} is different on the original
	 * source project description than the specified project description.
	 * @param current the description to test against
	 * @param consumer to call if the property has changed
	 */
	public void ifGroupIdChanged(ProjectDescription current, BiConsumer<String, String> consumer) {
		if (!Objects.equals(this.original.getGroupId(), current.getGroupId())) {
			consumer.accept(this.original.getGroupId(), current.getGroupId());
		}
	}

	/**
	 * Calls the specified consumer if the {@code artifactId} is different on the original
	 * source project description than the specified project description.
	 * @param current the description to test against
	 * @param consumer to call if the property has changed
	 */
	public void ifArtifactIdChanged(ProjectDescription current, BiConsumer<String, String> consumer) {
		if (!Objects.equals(this.original.getArtifactId(), current.getArtifactId())) {
			consumer.accept(this.original.getArtifactId(), current.getArtifactId());
		}
	}

	/**
	 * Calls the specified consumer if the {@code version} is different on the original
	 * source project description than the specified project description.
	 * @param current the description to test against
	 * @param consumer to call if the property has changed
	 */
	public void ifVersionChanged(ProjectDescription current, BiConsumer<String, String> consumer) {
		if (!Objects.equals(this.original.getVersion(), current.getVersion())) {
			consumer.accept(this.original.getVersion(), current.getVersion());
		}
	}

	/**
	 * Calls the specified consumer if the {@code name} is different on the original
	 * source project description than the specified project description.
	 * @param current the description to test against
	 * @param consumer to call if the property has changed
	 */
	public void ifNameChanged(ProjectDescription current, BiConsumer<String, String> consumer) {
		if (!Objects.equals(this.original.getName(), current.getName())) {
			consumer.accept(this.original.getName(), current.getName());
		}
	}

	/**
	 * Calls the specified consumer if the {@code description} is different on the
	 * original source project description than the specified project description.
	 * @param current the description to test against
	 * @param consumer to call if the property has changed
	 */
	public void ifDescriptionChanged(ProjectDescription current, BiConsumer<String, String> consumer) {
		if (!Objects.equals(this.original.getDescription(), current.getDescription())) {
			consumer.accept(this.original.getDescription(), current.getDescription());
		}
	}

	/**
	 * Calls the specified consumer if the {@code applicationName} is different on the
	 * original source project description than the specified project description.
	 * @param current the description to test against
	 * @param consumer to call if the property has changed
	 */
	public void ifApplicationNameChanged(ProjectDescription current, BiConsumer<String, String> consumer) {
		if (!Objects.equals(this.original.getApplicationName(), current.getApplicationName())) {
			consumer.accept(this.original.getApplicationName(), current.getApplicationName());
		}
	}

	/**
	 * Calls the specified consumer if the {@code packageName} is different on the
	 * original source project description than the specified project description.
	 * @param current the description to test against
	 * @param consumer to call if the property has changed
	 */
	public void ifPackageNameChanged(ProjectDescription current, BiConsumer<String, String> consumer) {
		if (!Objects.equals(this.original.getPackageName(), current.getPackageName())) {
			consumer.accept(this.original.getPackageName(), current.getPackageName());
		}
	}

	/**
	 * Calls the specified consumer if the {@code baseDirectory} is different on the
	 * original source project description than the specified project description.
	 * @param current the description to test against
	 * @param consumer to call if the property has changed
	 */
	public void ifBaseDirectoryChanged(ProjectDescription current, BiConsumer<String, String> consumer) {
		if (!Objects.equals(this.original.getBaseDirectory(), current.getBaseDirectory())) {
			consumer.accept(this.original.getBaseDirectory(), current.getBaseDirectory());
		}
	}

}
