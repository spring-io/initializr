/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import java.util.Arrays;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.test.ResponseFieldSnippet;
import org.junit.jupiter.api.Test;

import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

/**
 * Generates the REST Docs snippets for {@link ProjectMetadataController} used by the
 * reference documentation.
 *
 * @author Moritz Halbritter
 */
@ActiveProfiles("test-default")
@AutoConfigureRestDocs(outputDir = "target/snippets", uriPort = 80, uriHost = "start.example.com")
class ProjectMetadataControllerDocumentationTests extends AbstractInitializrControllerIntegrationTests {

	// Referenced from 'metadata-format.adoc', e.g. 'response-fields/type.values.0.adoc'
	private static final String[] DOCUMENTED_FIELDS = { "_links.maven-project", "dependencies.values[0]",
			"type.values[0]", "javaVersion.values[0]", "packaging.values[0]", "bootVersion.values[0]",
			"language.values[0]", "configurationFileFormat.values[0]" };

	@Test
	void metadataWithCurrentAcceptHeader() {
		MvcTestResult result = invokeHome(null, CURRENT_METADATA_MEDIA_TYPE.toString());
		assertThat(result).hasStatusOk()
			.apply(document("metadataWithCurrentAcceptHeader", preprocessResponse(prettyPrint()), fieldSnippets()));
	}

	private static Snippet[] fieldSnippets() {
		return Arrays.stream(DOCUMENTED_FIELDS).map(ResponseFieldSnippet::new).toArray(Snippet[]::new);
	}

}
