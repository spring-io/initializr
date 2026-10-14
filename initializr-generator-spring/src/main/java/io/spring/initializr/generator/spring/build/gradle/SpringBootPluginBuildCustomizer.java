/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build.gradle;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.version.Version;

import org.springframework.util.Assert;

/**
 * A {@link BuildCustomizer} to configure the Spring Boot plugin and dependency management
 * feature for a {@link GradleBuild}.
 *
 * @author Stephane Nicoll
 */
public final class SpringBootPluginBuildCustomizer implements BuildCustomizer<GradleBuild> {

	/**
	 * Order of this customizer. Runs before default customizers so that these plugins are
	 * added at the beginning of the {@code plugins} block.
	 */
	public static final int ORDER = -100;

	private final ProjectDescription description;

	private final DependencyManagementPluginVersionResolver versionResolver;

	public SpringBootPluginBuildCustomizer(ProjectDescription description,
			DependencyManagementPluginVersionResolver versionResolver) {
		this.description = description;
		this.versionResolver = versionResolver;
	}

	@Override
	public void customize(GradleBuild build) {
		Version platformVersion = this.description.getPlatformVersion();
		Assert.state(platformVersion != null, "'platformVersion' must not be null");
		build.plugins().add("org.springframework.boot", (plugin) -> plugin.setVersion(platformVersion.toString()));
		build.plugins()
			.add("io.spring.dependency-management", (plugin) -> plugin
				.setVersion(this.versionResolver.resolveDependencyManagementPluginVersion(this.description)));
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

}
