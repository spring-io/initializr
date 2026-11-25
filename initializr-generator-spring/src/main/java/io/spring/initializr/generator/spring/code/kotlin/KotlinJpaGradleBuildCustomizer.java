/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.build.BuildMetadataResolver;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.util.Assert;

/**
 *
 * {@link BuildCustomizer} for Gradle that configures the JPA Kotlin plugin if a JPA
 * related dependency is present.
 *
 * @author Madhura Bhave
 * @author Sijun Yang
 */
public class KotlinJpaGradleBuildCustomizer implements BuildCustomizer<GradleBuild> {

	private final BuildMetadataResolver buildMetadataResolver;

	private final KotlinProjectSettings settings;

	private final char quote;

	public KotlinJpaGradleBuildCustomizer(InitializrMetadata metadata, KotlinProjectSettings settings,
			ProjectDescription projectDescription) {
		this(metadata, settings, projectDescription, determineQuoteStyle(projectDescription));
	}

	@Deprecated(forRemoval = true)
	public KotlinJpaGradleBuildCustomizer(InitializrMetadata metadata, KotlinProjectSettings settings,
			ProjectDescription projectDescription, char quote) {
		Version platformVersion = projectDescription.getPlatformVersion();
		Assert.state(platformVersion != null, "'platformVersion' must not be null");
		this.buildMetadataResolver = new BuildMetadataResolver(metadata, platformVersion);
		this.settings = settings;
		this.quote = quote;
	}

	private static char determineQuoteStyle(ProjectDescription projectDescription) {
		BuildSystem buildSystem = projectDescription.getBuildSystem();
		return (buildSystem != null && GradleBuildSystem.DIALECT_KOTLIN.equals(buildSystem.dialect())) ? '"' : '\'';
	}

	@Override
	public void customize(GradleBuild build) {
		if (this.buildMetadataResolver.hasFacet(build, "jpa")) {
			build.plugins()
				.add("org.jetbrains.kotlin.plugin.jpa", (plugin) -> plugin.setVersion(this.settings.getVersion()));
			build.extensions().customize("allOpen", (allOpen) -> {
				allOpen.invoke("annotation", quote("jakarta.persistence.Entity"));
				allOpen.invoke("annotation", quote("jakarta.persistence.MappedSuperclass"));
				allOpen.invoke("annotation", quote("jakarta.persistence.Embeddable"));
			});
		}
	}

	private String quote(String element) {
		return this.quote + element + this.quote;
	}

}
