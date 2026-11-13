/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.doc.generator.project;

import io.spring.initializr.generator.buildsystem.SourceSet;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescriptionCustomizer;
import io.spring.initializr.generator.project.ProjectDescriptionField;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.code.kotlin.KotlinCompilerArgsCustomizer;
import io.spring.initializr.generator.spring.properties.ApplicationPropertiesCustomizer;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionRange;

import org.springframework.context.annotation.Bean;

/**
 * Examples of customizers.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 * @author Denis A. Altoé Falqueto
 */
public class ProjectCustomizationExamples {

	// tag::war-plugin-contributor[]
	@Bean
	@ConditionalOnBuildSystem(GradleBuildSystem.ID)
	@ConditionalOnPackaging(WarPackaging.ID)
	public BuildCustomizer<GradleBuild> warPluginContributor() {
		return (build) -> build.plugins().add("war");
	}
	// end::war-plugin-contributor[]

	// tag::jvm-version-change-reason[]
	@Bean
	public ProjectDescriptionCustomizer jvmVersionProjectDescriptionCustomizer() {
		return (description) -> {
			Language language = description.getLanguage();
			if (language == null || !isBelowJava17(language.jvmVersion())) {
				return;
			}
			description.setLanguage(Language.forId(language.id(), "17"));
			description.getChanges()
				.add(ProjectDescriptionField.JVM_VERSION,
						"The JVM level was changed to '17' as it is the oldest supported version.");
		};
	}

	private boolean isBelowJava17(String jvmVersion) {
		try {
			return Integer.parseInt(jvmVersion) < 17;
		}
		catch (NumberFormatException ex) {
			return false;
		}
	}
	// end::jvm-version-change-reason[]

	// tag::application-properties-customizer[]
	@Bean
	public ApplicationPropertiesCustomizer applicationPropertiesCustomizer() {
		return (properties) -> {
			// src/main/resources/application.properties (or .yaml)
			properties.add("spring.application.name", "acme");
			// src/test/resources/application.properties (or .yaml)
			properties.file(SourceSet.TEST).add("spring.datasource.url", "jdbc:h2:mem:acme");
			// src/main/resources/application-dev.properties (or .yaml)
			properties.profile("dev").add("logging.level.root", "DEBUG");
		};
	}
	// end::application-properties-customizer[]

	// tag::kotlin-compiler-args-customizer[]
	private static final VersionRange KOTLIN_2_2_OR_LATER = VersionParser.DEFAULT.parseRange("2.2.0");

	@Bean
	public KotlinCompilerArgsCustomizer kotlinCompilerArgsCustomizer() {
		return (kotlinVersion, compilerArgs) -> {
			compilerArgs.remove("-Xjsr305=strict");
			if (KOTLIN_2_2_OR_LATER.match(Version.parse(kotlinVersion))) {
				compilerArgs.add("-Xcontext-parameters");
			}
		};
	}
	// end::kotlin-compiler-args-customizer[]

}
