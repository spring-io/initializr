/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build.maven;

import java.util.List;

import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.util.LambdaSafe;
import io.spring.initializr.metadata.InitializrMetadata;
import org.jspecify.annotations.Nullable;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;

/**
 * Configuration for contributions specific to the generation of a project that will use
 * Maven as its build system.
 *
 * @author Andy Wilkinson
 * @author Moritz Halbritter
 */
@ProjectGenerationConfiguration
@ConditionalOnBuildSystem(MavenBuildSystem.ID)
public class MavenProjectGenerationConfiguration {

	@Bean
	DefaultMavenBuildCustomizer initializrMetadataMavenBuildCustomizer(ProjectDescription description,
			InitializrMetadata metadata) {
		return new DefaultMavenBuildCustomizer(description, metadata);
	}

	@Bean
	MavenWrapperContributor mavenWrapperContributor() {
		return new MavenWrapperContributor("3");
	}

	@Bean
	public MavenBuild mavenBuild(ObjectProvider<BuildItemResolver> buildItemResolver,
			ObjectProvider<BuildCustomizer<?>> buildCustomizers) {
		return createBuild(buildItemResolver.getIfAvailable(), buildCustomizers.orderedStream().toList());
	}

	@SuppressWarnings("unchecked")
	private MavenBuild createBuild(@Nullable BuildItemResolver buildItemResolver,
			List<BuildCustomizer<?>> buildCustomizers) {
		MavenBuild build = (buildItemResolver != null) ? new MavenBuild(buildItemResolver) : new MavenBuild();
		LambdaSafe.callbacks(BuildCustomizer.class, buildCustomizers, build)
			.invoke((customizer) -> customizer.customize(build));
		return build;
	}

	@Bean
	public MavenBuildProjectContributor mavenBuildProjectContributor(MavenBuild build,
			IndentingWriterFactory indentingWriterFactory) {
		return new MavenBuildProjectContributor(build, indentingWriterFactory);
	}

	@Bean
	ParentOverridesHelpDocumentCustomizer parentOverridesHelpDocumentCustomizer(MavenBuild build) {
		return new ParentOverridesHelpDocumentCustomizer(build.getSettings());
	}

	@Bean
	@ConditionalOnPackaging(WarPackaging.ID)
	public BuildCustomizer<MavenBuild> mavenWarPackagingConfigurer() {
		return (build) -> build.settings().packaging("war");
	}

	@Bean
	ConvertAnnotationProcessorsToPluginConfigBuildCustomizer convertAnnotationProcessorsToPluginConfigBuildCustomizer(
			ProjectDescription projectDescription) {
		return new ConvertAnnotationProcessorsToPluginConfigBuildCustomizer(projectDescription);
	}

}
