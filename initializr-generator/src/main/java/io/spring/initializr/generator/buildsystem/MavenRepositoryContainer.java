/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

import java.util.LinkedHashMap;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

/**
 * A {@link BuildItemContainer} implementation for {@link MavenRepository maven
 * repositories}.
 *
 * @author Stephane Nicoll
 */
public class MavenRepositoryContainer extends BuildItemContainer<String, MavenRepository> {

	/**
	 * Create an instance with the specified {@code itemResolver}.
	 * @param itemResolver the function that returns a {@link MavenRepository} based on an
	 * identifier.
	 */
	public MavenRepositoryContainer(Function<String, @Nullable MavenRepository> itemResolver) {
		super(new LinkedHashMap<>(), itemResolver);
	}

	/**
	 * Register the specified {@link MavenRepository repository}.
	 * @param repository the repository to register
	 */
	public void add(MavenRepository repository) {
		add(repository.getId(), repository);
	}

	/**
	 * Register a {@link MavenRepository repository} with the specified
	 * {@linkplain MavenRepository.Builder state}.
	 * @param builder the state of the repository
	 */
	public void add(MavenRepository.Builder builder) {
		add(builder.build());
	}

}
