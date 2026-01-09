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

package io.spring.initializr.generator.spring.documentation;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import io.spring.initializr.generator.io.text.BulletedSection;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Link;
import org.jspecify.annotations.Nullable;

import org.springframework.core.Ordered;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

/**
 * A {@link HelpDocumentCustomizer} that register links for selected dependencies.
 *
 * @author Stephane Nicoll
 */
public class RequestedDependenciesHelpDocumentCustomizer implements HelpDocumentCustomizer {

	private final ProjectDescription description;

	private final InitializrMetadata metadata;

	private final @Nullable String platformVersion;

	public RequestedDependenciesHelpDocumentCustomizer(ProjectDescription description, InitializrMetadata metadata) {
		this.description = description;
		this.metadata = metadata;
		this.platformVersion = (description.getPlatformVersion() != null) ? description.getPlatformVersion().toString()
				: getDefaultBootVersion(metadata);
	}

	private static @Nullable String getDefaultBootVersion(InitializrMetadata metadata) {
		DefaultMetadataElement aDefault = metadata.getBootVersions().getDefault();
		if (aDefault == null) {
			return null;
		}
		return aDefault.getId();
	}

	@Override
	public void customize(HelpDocument document) {
		this.description.getRequestedDependencies().forEach((id, dependency) -> {
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
		MultiValueMap<GuideType, Link> indexedLinks = indexLinks(dependency);
		registerLinks(indexedLinks.get(GuideType.REFERENCE), defaultLinkDescription(dependency),
				gettingStartedSection::referenceDocs);
		registerLinks(indexedLinks.get(GuideType.GUIDE), defaultLinkDescription(dependency),
				gettingStartedSection::guides);
		registerLinks(indexedLinks.get(GuideType.OTHER), (links) -> null, gettingStartedSection::additionalLinks);
	}

	private void registerLinks(@Nullable List<Link> links, Function<List<Link>, @Nullable String> defaultDescription,
			Supplier<BulletedSection<GettingStartedSection.Link>> section) {
		if (ObjectUtils.isEmpty(links)) {
			return;
		}
		links.forEach((link) -> {
			if (link.getHref() != null) {
				String description = (link.getDescription() != null) ? link.getDescription()
						: defaultDescription.apply(links);
				if (description != null) {
					String url = (this.platformVersion != null)
							? link.getHref().replace("{bootVersion}", this.platformVersion) : link.getHref();
					section.get().addItem(new GettingStartedSection.Link(url, description));
				}
			}
		});
	}

	private Function<List<Link>, @Nullable String> defaultLinkDescription(Dependency dependency) {
		return (links) -> (links.size() == 1) ? dependency.getName() : null;
	}

	private MultiValueMap<GuideType, Link> indexLinks(Dependency dependency) {
		MultiValueMap<GuideType, Link> links = new LinkedMultiValueMap<>();
		dependency.getLinks().forEach((link) -> {
			if ("reference".equals(link.getRel())) {
				links.add(GuideType.REFERENCE, link);
			}
			else if ("guide".equals(link.getRel())) {
				links.add(GuideType.GUIDE, link);
			}
			else {
				links.add(GuideType.OTHER, link);
			}
		});
		return links;
	}

	private enum GuideType {

		REFERENCE, GUIDE, OTHER

	}

}
