/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.documentation;

import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GettingStartedSection}.
 *
 * @author Stephane Nicoll
 */
class GettingStartedSectionTests {

	private final MustacheTemplateRenderer renderer = new MustacheTemplateRenderer("");

	@Test
	void gettingStartedEmpty() {
		GettingStartedSection gettingStarted = newGettingStartedSection();
		assertThat(gettingStarted.isEmpty()).isTrue();
	}

	@Test
	void gettingStartedWithGuideLinkIsNotEmpty() {
		GettingStartedSection gettingStarted = newGettingStartedSection();
		gettingStarted.addGuideLink("https://example.com", "Test");
		assertThat(gettingStarted.isEmpty()).isFalse();
	}

	@Test
	void gettingStartedWithReferenceDocLinkIsNotEmpty() {
		GettingStartedSection gettingStarted = newGettingStartedSection();
		gettingStarted.addReferenceDocLink("https://example.com", "Test");
		assertThat(gettingStarted.isEmpty()).isFalse();
	}

	@Test
	void gettingStartedWithAdditionalLinkIsNotEmpty() {
		GettingStartedSection gettingStarted = newGettingStartedSection();
		gettingStarted.addAdditionalLink("https://example.com", "Test");
		assertThat(gettingStarted.isEmpty()).isFalse();
	}

	@Test
	void gettingStartedWithSubSectionIsNotEmpty() {
		GettingStartedSection gettingStarted = newGettingStartedSection();
		gettingStarted.addSection((writer) -> writer.println("test"));
		assertThat(gettingStarted.isEmpty()).isFalse();
	}

	private GettingStartedSection newGettingStartedSection() {
		return new GettingStartedSection(this.renderer);
	}

}
