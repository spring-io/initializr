/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.build.BuildMetadataResolver;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionRange;
import io.spring.initializr.generator.version.VersionReference;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.util.Assert;

/**
 * A {@link BuildCustomizer} for Maven that configures the JPA Kotlin plugin if a JPA
 * related dependency is present.
 *
 * @author Madhura Bhave
 * @author Sebastien Deleuze
 * @author Sijun Yang
 * @author Moritz Halbritter
 */
public class KotlinJpaMavenBuildCustomizer implements BuildCustomizer<MavenBuild> {

	// Kotlin 2.3.20+ jpa plugin applies all-open for JPA annotations itself
	private static final VersionRange KOTLIN_JPA_ALL_OPEN_PRESET = VersionParser.DEFAULT.parseRange("2.3.20");

	private final BuildMetadataResolver buildMetadataResolver;

	private final boolean configureAllOpen;

	public KotlinJpaMavenBuildCustomizer(InitializrMetadata metadata, KotlinProjectSettings settings,
			ProjectDescription projectDescription) {
		this.buildMetadataResolver = createBuildMetadataResolver(metadata, projectDescription);
		this.configureAllOpen = !KOTLIN_JPA_ALL_OPEN_PRESET.match(Version.parse(settings.getVersion()));
	}

	/**
	 * Create a new instance.
	 * @param metadata the metadata
	 * @param projectDescription the project description
	 * @deprecated for removal in favor of
	 * {@link #KotlinJpaMavenBuildCustomizer(InitializrMetadata, KotlinProjectSettings, ProjectDescription)}
	 */
	@Deprecated(forRemoval = true)
	public KotlinJpaMavenBuildCustomizer(InitializrMetadata metadata, ProjectDescription projectDescription) {
		this.buildMetadataResolver = createBuildMetadataResolver(metadata, projectDescription);
		this.configureAllOpen = true;
	}

	private static BuildMetadataResolver createBuildMetadataResolver(InitializrMetadata metadata,
			ProjectDescription projectDescription) {
		Version platformVersion = projectDescription.getPlatformVersion();
		Assert.state(platformVersion != null, "'platformVersion' must not be null");
		return new BuildMetadataResolver(metadata, platformVersion);
	}

	@Override
	public void customize(MavenBuild build) {
		if (!this.buildMetadataResolver.hasFacet(build, "jpa")) {
			return;
		}

		build.plugins().add("org.jetbrains.kotlin", "kotlin-maven-plugin", (kotlinPlugin) -> {
			kotlinPlugin.configuration((configuration) -> {
				configuration.configure("compilerPlugins", (compilerPlugins) -> {
					compilerPlugins.add("plugin", "jpa");
					if (this.configureAllOpen) {
						compilerPlugins.add("plugin", "all-open");
					}
				});
				if (this.configureAllOpen) {
					configuration.configure("pluginOptions", (option) -> {
						option.add("option", "all-open:annotation=jakarta.persistence.Entity");
						option.add("option", "all-open:annotation=jakarta.persistence.MappedSuperclass");
						option.add("option", "all-open:annotation=jakarta.persistence.Embeddable");
					});
				}
			});
			kotlinPlugin.dependency("org.jetbrains.kotlin", "kotlin-maven-noarg",
					VersionReference.ofProperty(KotlinMavenBuildCustomizer.KOTLIN_VERSION_PROPERTY));
		});
	}

}
