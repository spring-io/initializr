/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.project.ProjectDescription;

import org.springframework.core.Ordered;

/**
 * Customize the {@link Build} as early as possible based on the information held in the
 * {@link ProjectDescription}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class SimpleBuildCustomizer implements BuildCustomizer<Build> {

	private final ProjectDescription description;

	public SimpleBuildCustomizer(ProjectDescription description) {
		this.description = description;
	}

	@Override
	public void customize(Build build) {
		build.settings()
			.group(this.description.getGroupId())
			.artifact(this.description.getArtifactId())
			.version(this.description.getVersion());
		this.description.getRequestedDependencies()
			.forEach((id, dependency) -> build.dependencies().add(id, dependency));
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
