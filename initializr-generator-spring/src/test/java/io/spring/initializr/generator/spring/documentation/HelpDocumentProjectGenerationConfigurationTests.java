/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.documentation;

import java.nio.file.Path;

import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescriptionField;
import io.spring.initializr.generator.spring.scm.git.GitIgnoreCustomizer;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link HelpDocumentProjectGenerationConfiguration}.
 *
 * @author Stephane Nicoll
 */
class HelpDocumentProjectGenerationConfigurationTests {

	private ProjectAssetTester projectTester;

	private InitializrMetadataTestBuilder metadataBuilder = InitializrMetadataTestBuilder.withDefaults();

	@BeforeEach
	void setup(@TempDir Path directory) {
		this.projectTester = new ProjectAssetTester()
			.withConfiguration(HelpDocumentProjectGenerationConfiguration.class)
			.withBean(MustacheTemplateRenderer.class, () -> new MustacheTemplateRenderer("classpath:/templates"))
			.withBean(InitializrMetadata.class, () -> this.metadataBuilder.build())
			.withDirectory(directory);
	}

	@Test
	void helpDocumentIsNotContributedWithoutLinks() {
		ProjectStructure project = this.projectTester.generate(new MutableProjectDescription());
		assertThat(project).filePaths().isEmpty();
	}

	@Test
	void helpDocumentIsContributedWithLinks() {
		Dependency dependency = Dependency.withId("example", "com.example", "example");
		dependency.getLinks().add(Link.create("guide", "https://example.com/how-to", "How-to example"));
		dependency.getLinks().add(Link.create("reference", "https://example.com/doc", "Reference doc example"));
		this.metadataBuilder.addDependencyGroup("test", dependency);
		MutableProjectDescription description = new MutableProjectDescription();
		description.addDependency("example", mock(io.spring.initializr.generator.buildsystem.Dependency.class));
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).filePaths().containsOnly("HELP.md");
	}

	@Test
	void helpDocumentIsContributedWithRecordedProjectDescriptionChange() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.getChanges()
			.add(ProjectDescriptionField.JVM_VERSION, "The JVM level was changed to '21' as this is a test.");
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).filePaths().containsOnly("HELP.md");
		assertThat(project).textFile("HELP.md")
			.contains("# Read Me First", "* The JVM level was changed to '21' as this is a test.");
	}

	@Test
	void helpDocumentIsAddedToGitIgnore() {
		MutableProjectDescription description = new MutableProjectDescription();
		this.projectTester.configure(description,
				(context) -> assertThat(context).hasSingleBean(GitIgnoreCustomizer.class)
					.getBean(GitIgnoreCustomizer.class)
					.isInstanceOf(HelpDocumentGitIgnoreCustomizer.class));
	}

}
