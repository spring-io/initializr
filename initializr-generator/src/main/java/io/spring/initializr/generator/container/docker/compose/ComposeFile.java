/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.container.docker.compose;

/**
 * Model for a Docker Compose file.
 *
 * @author Moritz Halbritter
 * @author Stephane Nicoll
 */
public class ComposeFile {

	private final ComposeServiceContainer services = new ComposeServiceContainer();

	private final ComposeConfigContainer configs = new ComposeConfigContainer();

	/**
	 * Return the {@linkplain ComposeServiceContainer service container} to use to
	 * configure services.
	 * @return the {@link ComposeServiceContainer}
	 */
	public ComposeServiceContainer services() {
		return this.services;
	}

	/**
	 * Return the {@linkplain ComposeConfigContainer config container} to use to configure
	 * configs.
	 * @return the {@link ComposeConfigContainer}
	 */
	public ComposeConfigContainer configs() {
		return this.configs;
	}

}
