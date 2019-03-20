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

import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.core.Ordered;

/**
 * A {@link HelpDocumentCustomizer} that register links for selected dependencies.
 *
 * @author Stephane Nicoll
 */
public class RequestedDependenciesHelpDocumentCustomizer
		implements HelpDocumentCustomizer {

	private final ResolvedProjectDescription projectDescription;

	private final InitializrMetadata metadata;

	public RequestedDependenciesHelpDocumentCustomizer(
			ResolvedProjectDescription projectDescription, InitializrMetadata metadata) {
		this.projectDescription = projectDescription;
		this.metadata = metadata;
	}

	@Override
	public void customize(HelpDocument document) {
		this.projectDescription.getRequestedDependencies().forEach((id, dependency) -> {
			Dependency dependencyMetadata = this.metadata.getDependencies().get(id);
			if (dependencyMetadata != null) {
				handleDependency(document, dependencyMetadata);
			}
		});
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	private void handleDependency(HelpDocument document, Dependency dependency) {
		GettingStartedSection gettingStartedSection = document.gettingStarted();
		dependency.getLinks().forEach((link) -> {
			if (link.getDescription() != null && link.getRel() != null) {
				if ("reference".equals(link.getRel())) {
					gettingStartedSection.addReferenceDocLink(link.getHref(),
							link.getDescription());
				}
				else if ("guide".equals(link.getRel())) {
					gettingStartedSection.addGuideLink(link.getHref(),
							link.getDescription());
				}
				else {
					gettingStartedSection.addAdditionalLink(link.getHref(),
							link.getDescription());
				}
			}
		});
	}

}
