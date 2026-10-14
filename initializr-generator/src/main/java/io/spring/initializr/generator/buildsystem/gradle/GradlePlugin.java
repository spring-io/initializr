/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

/**
 * A plugin in a {@link GradleBuild}.
 *
 * @author Andy Wilkinson
 */
public class GradlePlugin {

	private final String id;

	private final boolean apply;

	/**
	 * Create a new instance.
	 * @param id the id of the plugin
	 * @param apply whether the plugin should be applied or not
	 */
	public GradlePlugin(String id, boolean apply) {
		this.id = id;
		this.apply = apply;
	}

	/**
	 * Return the plugin identifier.
	 * @return the plugin id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Return whether the plugin should be applied.
	 * @return {@code true} to use {@code apply}, {@code false} to register it
	 */
	public boolean isApply() {
		return this.apply;
	}

}
