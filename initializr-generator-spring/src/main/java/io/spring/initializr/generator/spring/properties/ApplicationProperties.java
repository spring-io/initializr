/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import io.spring.initializr.generator.buildsystem.SourceSet;
import org.jspecify.annotations.Nullable;

/**
 * The application properties of a generated project, one
 * {@link ApplicationPropertiesFile} per source set and Spring profile.
 * <p>
 * Properties added directly belong to the main source set and the default profile, use
 * {@link #profile(String)} or {@link #file(SourceSet)} for the others.
 * <p>
 * Every file holding properties is written to
 * {@code src/{sourceSet}/resources/application[-{profile}]}, resolved against the
 * project's build system, with the extension of the requested
 * {@link io.spring.initializr.generator.configuration.format.ConfigurationFileFormat}.
 * The default file of the main source set is written even when empty.
 *
 * @author Moritz Halbritter
 * @author Rodrigo Mibielli Peixoto
 * @author Denis A. Altoé Falqueto
 */
public class ApplicationProperties {

	private final Map<Key, ApplicationPropertiesFile> files = createFiles();

	/**
	 * Adds a property to the main source set and the default profile.
	 * @param key the property key
	 * @param value the property value
	 * @throws IllegalStateException if the key already exists
	 */
	public void add(String key, long value) {
		main().add(key, value);
	}

	/**
	 * Adds a property to the main source set and the default profile.
	 * @param key the property key
	 * @param value the property value
	 * @throws IllegalStateException if the key already exists
	 */
	public void add(String key, boolean value) {
		main().add(key, value);
	}

	/**
	 * Adds a property to the main source set and the default profile.
	 * @param key the property key
	 * @param value the property value
	 * @throws IllegalStateException if the key already exists
	 */
	public void add(String key, double value) {
		main().add(key, value);
	}

	/**
	 * Adds a property to the main source set and the default profile.
	 * @param key the property key
	 * @param value the property value
	 * @throws IllegalStateException if the key already exists
	 */
	public void add(String key, String value) {
		main().add(key, value);
	}

	/**
	 * Adds a property to the main source set and the default profile.
	 * @param key the property key
	 * @param value the property value
	 * @throws IllegalStateException if the key already exists
	 */
	public void add(String key, Collection<?> value) {
		main().add(key, value);
	}

	/**
	 * Whether the given key exists in the main source set and the default profile.
	 * @param key the property key
	 * @return true if the key exists
	 */
	public boolean contains(String key) {
		return main().contains(key);
	}

	/**
	 * Returns the value of the given key in the main source set and the default profile,
	 * cast to the given type.
	 * @param <T> the type of the value
	 * @param key the property key
	 * @param clazz the type of the value
	 * @return the value, or null if the key does not exist
	 * @throws ClassCastException if the value is not assignable to the type T
	 */
	public <T> @Nullable T get(String key, Class<T> clazz) {
		return main().get(key, clazz);
	}

	/**
	 * Returns the value of the given key in the main source set and the default profile.
	 * @param key the property key
	 * @return the value, or null if the key does not exist
	 */
	public @Nullable Object get(String key) {
		return main().get(key);
	}

	/**
	 * Removes the given key and its value from the main source set and the default
	 * profile.
	 * @param key the property key
	 * @return true if the key has been removed
	 */
	public boolean remove(String key) {
		return main().remove(key);
	}

	/**
	 * Returns the file of the default profile of the given source set, creating it if
	 * necessary.
	 * @param sourceSet the source set
	 * @return the file
	 */
	public ApplicationPropertiesFile file(SourceSet sourceSet) {
		return file(sourceSet, ProfileName.DEFAULT);
	}

	/**
	 * Returns the file of the given profile of the given source set, creating it if
	 * necessary.
	 * @param sourceSet the source set
	 * @param profile the Spring profile name
	 * @return the file
	 * @throws IllegalStateException if the profile name is invalid
	 */
	public ApplicationPropertiesFile file(SourceSet sourceSet, String profile) {
		return file(sourceSet, new ProfileName(profile));
	}

	/**
	 * Returns the file of the given profile of the main source set, creating it if
	 * necessary. Shortcut for {@code file(SourceSet.MAIN, profile)}.
	 * @param profile the Spring profile name
	 * @return the file
	 * @throws IllegalStateException if the profile name is invalid
	 */
	public ApplicationPropertiesFile profile(String profile) {
		return file(SourceSet.MAIN, profile);
	}

	Map<ProfileName, ApplicationPropertiesFile> files(SourceSet sourceSet) {
		Map<ProfileName, ApplicationPropertiesFile> sourceSetFiles = new LinkedHashMap<>();
		this.files.forEach((key, file) -> {
			if (key.sourceSet() != sourceSet) {
				return;
			}
			sourceSetFiles.put(key.profile(), file);
		});
		return sourceSetFiles;
	}

	private ApplicationPropertiesFile main() {
		return file(SourceSet.MAIN);
	}

	private ApplicationPropertiesFile file(SourceSet sourceSet, ProfileName profile) {
		return this.files.computeIfAbsent(new Key(sourceSet, profile), (ignored) -> new ApplicationPropertiesFile());
	}

	private static Map<Key, ApplicationPropertiesFile> createFiles() {
		Map<Key, ApplicationPropertiesFile> files = new LinkedHashMap<>();
		files.put(new Key(SourceSet.MAIN, ProfileName.DEFAULT), new ApplicationPropertiesFile());
		return files;
	}

	/**
	 * Identifies the file of a source set and profile combination.
	 *
	 * @param sourceSet the source set
	 * @param profile the profile
	 */
	private record Key(SourceSet sourceSet, ProfileName profile) {
	}

}
