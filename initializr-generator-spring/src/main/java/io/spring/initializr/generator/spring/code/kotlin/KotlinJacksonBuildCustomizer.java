/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.build.BuildMetadataResolver;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionRange;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * A {@link BuildCustomizer} that automatically adds "jackson-module-kotlin" when Kotlin
 * is used and a dependency has the "json" facet.
 *
 * @author Sebastien Deleuze
 * @author Madhura Bhave
 */
public class KotlinJacksonBuildCustomizer implements BuildCustomizer<Build> {

	private static final VersionRange SPRING_BOOT_4_OR_LATER = VersionParser.DEFAULT.parseRange("4.0.0");

	private final BuildMetadataResolver buildMetadataResolver;

	private final ProjectDescription description;

	public KotlinJacksonBuildCustomizer(InitializrMetadata metadata, ProjectDescription description) {
		Version platformVersion = description.getPlatformVersion();
		Assert.state(platformVersion != null, "'platformVersion' must not be null");
		this.buildMetadataResolver = new BuildMetadataResolver(metadata, platformVersion);
		this.description = description;
	}

	@Override
	public void customize(Build build) {
		boolean isKotlin = ClassUtils.isAssignableValue(KotlinLanguage.class, this.description.getLanguage());
		if (this.buildMetadataResolver.hasFacet(build, "json") && isKotlin) {
			String groupId = isBoot4OrLater() ? "tools.jackson.module" : "com.fasterxml.jackson.module";
			build.dependencies()
				.add("jackson-module-kotlin", groupId, "jackson-module-kotlin", DependencyScope.COMPILE);
		}
	}

	private boolean isBoot4OrLater() {
		Version platformVersion = this.description.getPlatformVersion();
		Assert.state(platformVersion != null, "'platformVersion' must not be null");
		return SPRING_BOOT_4_OR_LATER.match(platformVersion);
	}

}
