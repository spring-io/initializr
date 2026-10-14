/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.packaging;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * Application packaging, such as a jar file or a war file.
 *
 * @author Andy Wilkinson
 */
public interface Packaging {

	/**
	 * Return the id of the packaging.
	 * @return the id
	 */
	String id();

	/**
	 * Creates the packaging for the given id.
	 * @param id the id
	 * @return the packaging
	 * @throws IllegalStateException if the packaging with the given id can't be found
	 */
	static Packaging forId(@Nullable String id) {
		return SpringFactoriesLoader.loadFactories(PackagingFactory.class, Packaging.class.getClassLoader())
			.stream()
			.map((factory) -> factory.createPackaging(id))
			.filter(Objects::nonNull)
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Unrecognized packaging id '" + id + "'"));
	}

}
