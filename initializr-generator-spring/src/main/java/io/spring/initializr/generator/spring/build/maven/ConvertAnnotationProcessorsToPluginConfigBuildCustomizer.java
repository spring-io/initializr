/*
 * Copyright 2012 - present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.spring.build.maven;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenPluginContainer;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;
import org.jspecify.annotations.Nullable;

import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/**
 * A {@link BuildCustomizer} that converts annotation processor dependencies to
 * {@code maven-compiler-plugin} {@code annotationProcessorPaths} configuration.
 *
 * @author Moritz Halbritter
 */
class ConvertAnnotationProcessorsToPluginConfigBuildCustomizer implements BuildCustomizer<MavenBuild> {

	private final ProjectDescription projectDescription;

	ConvertAnnotationProcessorsToPluginConfigBuildCustomizer(ProjectDescription projectDescription) {
		this.projectDescription = projectDescription;
	}

	@Override
	public void customize(MavenBuild build) {
		if (!isJava()) {
			return;
		}
		Set<String> ids = build.dependencies().ids().collect(Collectors.toSet());
		List<Dependency> annotationProcessors = new ArrayList<>();
		List<Dependency> testAnnotationProcessors = new ArrayList<>();
		for (String id : ids) {
			Dependency dependency = build.dependencies().get(id);
			Assert.state(dependency != null, "'dependency' must not be null");
			if (dependency.getScope() == null) {
				continue;
			}
			switch (dependency.getScope()) {
				case ANNOTATION_PROCESSOR -> {
					build.dependencies().remove(id);
					annotationProcessors.add(dependency);
				}
				case TEST_ANNOTATION_PROCESSOR -> {
					build.dependencies().remove(id);
					testAnnotationProcessors.add(dependency);
				}
			}
		}
		configureCompilerPlugin(build.plugins(), annotationProcessors, "default-compile", "compile", "compile");
		configureCompilerPlugin(build.plugins(), testAnnotationProcessors, "default-testCompile", "test-compile",
				"testCompile");
	}

	private void configureCompilerPlugin(MavenPluginContainer plugins, List<Dependency> annotationProcessors,
			String executionId, String phase, String goal) {
		if (annotationProcessors.isEmpty()) {
			return;
		}
		plugins.add("org.apache.maven.plugins", "maven-compiler-plugin",
				(plugin) -> plugin.execution(executionId, (execution) -> {
					execution.phase(phase);
					execution.goal(goal);
					execution.configuration((config) -> {
						config.add("annotationProcessorPaths", (paths) -> {
							for (Dependency annotationProcessor : annotationProcessors) {
								paths.add("path", (path) -> {
									path.add("groupId", annotationProcessor.getGroupId());
									path.add("artifactId", annotationProcessor.getArtifactId());
									if (annotationProcessor.getClassifier() != null) {
										path.add("classifier", annotationProcessor.getClassifier());
									}
									String version = determineVersion(annotationProcessor.getVersion());
									if (version != null) {
										path.add("version", version);
									}
									if (annotationProcessor.getType() != null) {
										path.add("type", annotationProcessor.getType());
									}
									if (!annotationProcessor.getExclusions().isEmpty()) {
										path.add("exclusions", (exclusions) -> {
											for (Dependency.Exclusion exclusion : annotationProcessor.getExclusions()) {
												exclusions.add("exclusion", (exclusionElement) -> {
													exclusionElement.add("groupId", exclusion.getGroupId());
													exclusionElement.add("artifactId", exclusion.getArtifactId());
												});
											}
										});
									}
								});
							}
						});
					});
				}));
	}

	private @Nullable String determineVersion(@Nullable VersionReference versionReference) {
		if (versionReference == null) {
			return null;
		}
		VersionProperty property = versionReference.getProperty();
		if (property != null) {
			return "${" + property.toStandardFormat() + "}";
		}
		return versionReference.getValue();
	}

	private boolean isJava() {
		Language language = this.projectDescription.getLanguage();
		if (language == null) {
			return false;
		}
		return language.id().equals(JavaLanguage.ID);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
