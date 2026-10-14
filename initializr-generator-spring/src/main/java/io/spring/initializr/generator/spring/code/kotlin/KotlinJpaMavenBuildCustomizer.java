/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.build.BuildMetadataResolver;
import io.spring.initializr.generator.version.Version;
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
 */
public class KotlinJpaMavenBuildCustomizer implements BuildCustomizer<MavenBuild> {

	private final BuildMetadataResolver buildMetadataResolver;

	public KotlinJpaMavenBuildCustomizer(InitializrMetadata metadata, ProjectDescription projectDescription) {
		Version platformVersion = projectDescription.getPlatformVersion();
		Assert.state(platformVersion != null, "'platformVersion' must not be null");
		this.buildMetadataResolver = new BuildMetadataResolver(metadata, platformVersion);
	}

	@Override
	public void customize(MavenBuild build) {
		if (this.buildMetadataResolver.hasFacet(build, "jpa")) {
			build.plugins().add("org.jetbrains.kotlin", "kotlin-maven-plugin", (kotlinPlugin) -> {
				kotlinPlugin.configuration((configuration) -> {
					configuration.configure("compilerPlugins", (compilerPlugins) -> {
						compilerPlugins.add("plugin", "jpa");
						compilerPlugins.add("plugin", "all-open");
					});
					configuration.configure("pluginOptions", (option) -> {
						option.add("option", "all-open:annotation=jakarta.persistence.Entity");
						option.add("option", "all-open:annotation=jakarta.persistence.MappedSuperclass");
						option.add("option", "all-open:annotation=jakarta.persistence.Embeddable");
					});
				});
				kotlinPlugin.dependency("org.jetbrains.kotlin", "kotlin-maven-noarg",
						VersionReference.ofProperty(KotlinMavenBuildCustomizer.KOTLIN_VERSION_PROPERTY));
			});
		}
	}

}
