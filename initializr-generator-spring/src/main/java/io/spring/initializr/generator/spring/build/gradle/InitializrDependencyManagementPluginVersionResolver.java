/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.spring.build.gradle;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * {@link DependencyManagementPluginVersionResolver} that resolves the version from the
 * {@link InitializrMetadata}.
 *
 * @author Stephane Nicoll
 */
public class InitializrDependencyManagementPluginVersionResolver implements DependencyManagementPluginVersionResolver {

	private final InitializrMetadata metadata;

	public InitializrDependencyManagementPluginVersionResolver(InitializrMetadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public String resolveDependencyManagementPluginVersion(ProjectDescription description) {
		return this.metadata.getConfiguration().getEnv().getGradle().getDependencyManagementPluginVersion();
	}

}
