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

import java.util.List;

import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Link;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RequestedDependenciesHelpDocumentCustomizer}.
 *
 * @author Stephane Nicoll
 */
class RequestedDependenciesHelpDocumentCustomizerTests {

	private final InitializrMetadataTestBuilder metadataBuilder = InitializrMetadataTestBuilder
			.withDefaults();

	@Test
	void dependencyLinkWithNoDescriptionIsIgnored() {
		Dependency dependency = Dependency.withId("example", "com.example", "example");
		dependency.getLinks().add(Link.create("guide", "https://example.com/how-to"));
		this.metadataBuilder.addDependencyGroup("test", dependency);
		HelpDocument document = customizeHelp("example");
		assertThat(document.gettingStarted().isEmpty()).isTrue();
	}

	@Test
	void dependencyWithReferenceDocLink() {
		Dependency dependency = Dependency.withId("example", "com.example", "example");
		dependency.getLinks().add(Link.create("reference", "https://example.com/doc",
				"Reference doc example"));
		this.metadataBuilder.addDependencyGroup("test", dependency);
		HelpDocument document = customizeHelp("example");
		assertThat(document.gettingStarted().isEmpty()).isFalse();
		List<GettingStartedSection.Link> links = document.gettingStarted().referenceDocs()
				.getItems();
		assertThat(links).hasSize(1);
		assertLink(links.get(0), "https://example.com/doc", "Reference doc example");
	}

	@Test
	void dependencyWithGuideLink() {
		Dependency dependency = Dependency.withId("example", "com.example", "example");
		dependency.getLinks().add(
				Link.create("guide", "https://example.com/how-to", "How-to example"));
		this.metadataBuilder.addDependencyGroup("test", dependency);
		HelpDocument document = customizeHelp("example");
		assertThat(document.gettingStarted().isEmpty()).isFalse();
		List<GettingStartedSection.Link> links = document.gettingStarted().guides()
				.getItems();
		assertThat(links).hasSize(1);
		assertLink(links.get(0), "https://example.com/how-to", "How-to example");
	}

	@Test
	void dependencyWithAdditionalLink() {
		Dependency dependency = Dependency.withId("example", "com.example", "example");
		dependency.getLinks()
				.add(Link.create("something", "https://example.com/test", "Test App"));
		this.metadataBuilder.addDependencyGroup("test", dependency);
		HelpDocument document = customizeHelp("example");
		assertThat(document.gettingStarted().isEmpty()).isFalse();
		List<GettingStartedSection.Link> links = document.gettingStarted()
				.additionalLinks().getItems();
		assertThat(links).hasSize(1);
		assertLink(links.get(0), "https://example.com/test", "Test App");
	}

	private void assertLink(GettingStartedSection.Link link, String href,
			String description) {
		assertThat(link.getHref()).isEqualTo(href);
		assertThat(link.getDescription()).isEqualTo(description);
	}

	private HelpDocument customizeHelp(String... requestedDependencies) {
		ProjectDescription description = new ProjectDescription();
		for (String requestedDependency : requestedDependencies) {
			description.addDependency(requestedDependency, null);
		}
		InitializrMetadata metadata = this.metadataBuilder.build();
		HelpDocument document = new HelpDocument(
				new MustacheTemplateRenderer("classpath:/templates"));
		new RequestedDependenciesHelpDocumentCustomizer(
				new ResolvedProjectDescription(description), metadata)
						.customize(document);
		return document;
	}

}
