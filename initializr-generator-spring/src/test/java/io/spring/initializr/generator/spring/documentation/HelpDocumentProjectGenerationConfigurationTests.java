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

package io.spring.initializr.generator.spring.documentation;

import java.nio.file.Path;

import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.scm.git.GitIgnoreCustomizer;
import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link HelpDocumentProjectGenerationConfiguration}.
 *
 * @author Stephane Nicoll
 */
class HelpDocumentProjectGenerationConfigurationTests {

	private ProjectAssetTester projectTester;

	private InitializrMetadataTestBuilder metadataBuilder = InitializrMetadataTestBuilder
			.withDefaults();

	@BeforeEach
	void setup(@TempDir Path directory) {
		this.projectTester = new ProjectAssetTester()
				.withConfiguration(HelpDocumentProjectGenerationConfiguration.class)
				.withBean(MustacheTemplateRenderer.class,
						() -> new MustacheTemplateRenderer("classpath:/templates"))
				.withBean(InitializrMetadata.class, () -> this.metadataBuilder.build())
				.withDirectory(directory);
	}

	@Test
	void helpDocumentIsNotContributedWithoutLinks() {
		assertThat(this.projectTester.generate(new ProjectDescription())
				.getRelativePathsOfProjectFiles()).isEmpty();
	}

	@Test
	void helpDocumentIsContributedWithLinks() {
		Dependency dependency = Dependency.withId("example", "com.example", "example");
		dependency.getLinks().add(
				Link.create("guide", "https://example.com/how-to", "How-to example"));
		dependency.getLinks().add(Link.create("reference", "https://example.com/doc",
				"Reference doc example"));
		this.metadataBuilder.addDependencyGroup("test", dependency);
		ProjectDescription description = new ProjectDescription();
		description.addDependency("example", null);
		ProjectStructure projectStructure = this.projectTester.generate(description);
		assertThat(projectStructure.getRelativePathsOfProjectFiles())
				.containsOnly("HELP.md");
	}

	@Test
	void helpDocumentIsAddedToGitIgnore() {
		ProjectDescription description = new ProjectDescription();
		GitIgnoreCustomizer gitIgnoreCustomizer = this.projectTester.generate(description,
				(context) -> context.getBean(GitIgnoreCustomizer.class));
		assertThat(gitIgnoreCustomizer)
				.isInstanceOf(HelpDocumentGitIgnoreCustomizer.class);
	}

}
