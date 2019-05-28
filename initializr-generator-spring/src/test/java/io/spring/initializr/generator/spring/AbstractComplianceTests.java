/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.generator.spring;

import java.nio.file.Path;
import java.util.function.Consumer;

import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationContext;
import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.spring.test.ProjectAssert;
import io.spring.initializr.generator.test.project.ProjectGeneratorTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

/**
 * Abstract base class for compliance tests.
 *
 * @author Madhura Bhave
 */
public abstract class AbstractComplianceTests {

	private Path tempDir;

	protected static final Dependency WEB = Dependency.withId("web",
			"org.springframework.boot", "spring-boot-starter-web");

	@BeforeEach
	void setup(@TempDir Path dir) {
		this.tempDir = dir;
	}

	protected ProjectAssert generateProject(Language language, BuildSystem buildSystem,
			String version) {
		return generateProject(language, buildSystem, version, (description) -> {
		});
	}

	protected ProjectAssert generateProject(Language language, BuildSystem buildSystem,
			String version, Consumer<ProjectDescription> descriptionCustomizer) {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("web", WEB).build();
		return generateProject(language, buildSystem, version, descriptionCustomizer,
				metadata);
	}

	protected ProjectAssert generateProject(Language language, BuildSystem buildSystem,
			String version, Consumer<ProjectDescription> descriptionCustomizer,
			Consumer<ProjectGenerationContext> contextCustomizer) {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("web", WEB).build();
		return generateProject(language, buildSystem, version, descriptionCustomizer,
				metadata, contextCustomizer);
	}

	protected ProjectAssert generateProject(Language language, BuildSystem buildSystem,
			String version, Consumer<ProjectDescription> descriptionCustomizer,
			InitializrMetadata metadata) {
		return generateProject(language, buildSystem, version, descriptionCustomizer,
				metadata, (projectGenerationContext) -> {
				});

	}

	private ProjectAssert generateProject(Language language, BuildSystem buildSystem,
			String version, Consumer<ProjectDescription> descriptionCustomizer,
			InitializrMetadata metadata,
			Consumer<ProjectGenerationContext> contextCustomizer) {
		ProjectGeneratorTester projectTester = new ProjectGeneratorTester()
				.withDirectory(this.tempDir)
				.withDescriptionCustomizer((description) -> setupProjectDescription(
						language, version, buildSystem, description))
				.withDescriptionCustomizer(descriptionCustomizer)
				.withContextInitializer(
						(context) -> setupProjectGenerationContext(metadata, context))
				.withContextInitializer(contextCustomizer);
		ProjectStructure projectStructure = projectTester
				.generate(new ProjectDescription());
		Path resolve = projectStructure.resolve("");
		return new ProjectAssert(resolve);
	}

	private void setupProjectGenerationContext(InitializrMetadata metadata,
			ProjectGenerationContext context) {
		context.registerBean(InitializrMetadata.class, () -> metadata);
		context.registerBean(BuildItemResolver.class, () -> new MetadataBuildItemResolver(
				metadata,
				context.getBean(ResolvedProjectDescription.class).getPlatformVersion()));
		context.registerBean(IndentingWriterFactory.class,
				() -> IndentingWriterFactory.create(new SimpleIndentStrategy("\t")));
	}

	private void setupProjectDescription(Language language, String version,
			BuildSystem buildSystem, ProjectDescription description) {
		description.setLanguage(language);
		description.setBuildSystem(buildSystem);
		description.setPlatformVersion(Version.parse(version));
		description.setVersion("0.0.1-SNAPSHOT");
		description.setName("demo");
		description.setDescription("Demo project for Spring Boot");
	}

}
