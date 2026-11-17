/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.spring.documentation;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.scm.git.GitIgnoreCustomizer;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Default {@link HelpDocument} contributors.
 *
 * @author Stephane Nicoll
 */
@Configuration
public class HelpDocumentProjectGenerationDefaultContributorsConfiguration {

	@Bean
	public RequestedDependenciesHelpDocumentCustomizer dependenciesHelpDocumentCustomizer(
			ProjectDescription description, InitializrMetadata metadata) {
		return new RequestedDependenciesHelpDocumentCustomizer(description, metadata);
	}

	@Bean
	public HelpDocumentCustomizer projectDescriptionChangesHelpDocumentCustomizer(ProjectDescription description) {
		return new ProjectDescriptionChangesHelpDocumentCustomizer(description);
	}

	@Bean
	public GitIgnoreCustomizer helpDocumentGitIgnoreCustomizer(HelpDocument document) {
		return new HelpDocumentGitIgnoreCustomizer(document);
	}

}
