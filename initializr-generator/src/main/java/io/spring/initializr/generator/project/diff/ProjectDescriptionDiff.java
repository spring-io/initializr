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

package io.spring.initializr.generator.project.diff;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.BiConsumer;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.version.Version;

import org.springframework.util.ReflectionUtils;

/**
 * Provides a convenient API for determining if certain fields on a
 * {@link ProjectDescription} were modified.
 *
 * @author Chris Bono
 */
public class ProjectDescriptionDiff {

	private final ProjectDescription original;

	/**
	 * Construct a {@link ProjectDescriptionDiff} that creates and uses a copy of the
	 * specified description as its source.
	 * @param original the description to copy as the source
	 */
	public ProjectDescriptionDiff(final ProjectDescription original) {
		this.original = original.createCopy();
	}

	/**
	 * Gets the copy of the originally specified description that is being tracked.
	 * @return copy of the originally specified description
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
	public void ifPlatformVersionChanged(final ProjectDescription current,
			final BiConsumer<Version, Version> consumer) {
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
	public void ifBuildSystemChanged(final ProjectDescription current,
			final BiConsumer<BuildSystem, BuildSystem> consumer) {
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
	public void ifPackagingChanged(final ProjectDescription current, final BiConsumer<Packaging, Packaging> consumer) {
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
	public void ifLanguageChanged(final ProjectDescription current, final BiConsumer<Language, Language> consumer) {
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
	public void ifGroupIdChanged(final ProjectDescription current, final BiConsumer<String, String> consumer) {
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
	public void ifArtifactIdChanged(final ProjectDescription current, final BiConsumer<String, String> consumer) {
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
	public void ifVersionChanged(final ProjectDescription current, final BiConsumer<String, String> consumer) {
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
	public void ifNameChanged(final ProjectDescription current, final BiConsumer<String, String> consumer) {
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
	public void ifDescriptionChanged(final ProjectDescription current, final BiConsumer<String, String> consumer) {
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
	public void ifApplicationNameChanged(final ProjectDescription current, final BiConsumer<String, String> consumer) {
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
	public void ifPackageNameChanged(final ProjectDescription current, final BiConsumer<String, String> consumer) {
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
	public void ifBaseDirectoryChanged(final ProjectDescription current, final BiConsumer<String, String> consumer) {
		if (!Objects.equals(this.original.getBaseDirectory(), current.getBaseDirectory())) {
			consumer.accept(this.original.getBaseDirectory(), current.getBaseDirectory());
		}
	}

	/**
	 * Calls the specified consumer if the value of the specified property is different on
	 * the original source project description than the specified project description.
	 * @param current the description to test against
	 * @param property the name of the property to check
	 * @param propertyClass the class of the property to check
	 * @param consumer to call if the property has changed
	 * @param <V> type of the property
	 */
	public <V> void ifPropertyChanged(final ProjectDescription current, final String property,
			final Class<V> propertyClass, final BiConsumer<V, V> consumer) {
		final V originalValue = getPropertyValueReflectively(this.original, property);
		final V currentValue = getPropertyValueReflectively(current, property);
		if (!Objects.equals(originalValue, currentValue)) {
			consumer.accept(originalValue, currentValue);
		}
	}

	private <V> V getPropertyValueReflectively(final ProjectDescription description, final String property) {
		final Class descriptionClass = description.getClass();
		final Field field = ReflectionUtils.findField(descriptionClass, property);
		if (field == null) {
			throw new IllegalArgumentException(
					String.format("No property named '%s' in '%s'.", property, descriptionClass.getSimpleName()));
		}
		ReflectionUtils.makeAccessible(field);
		return (V) ReflectionUtils.getField(field, description);
	}

}
