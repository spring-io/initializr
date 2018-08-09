/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.web.mapper;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.Type;

import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;

/**
 * A {@link InitializrMetadataJsonMapper} handling the metadata format for v2.1
 * <p>
 * Version 2.1 brings the "versionRange" attribute for a dependency to restrict the Spring
 * Boot versions that can be used against it. That version also adds an additional
 * `dependencies` endpoint.
 *
 * @author Stephane Nicoll
 */
public class InitializrMetadataV21JsonMapper extends InitializrMetadataV2JsonMapper {

	private final TemplateVariables dependenciesVariables;

	public InitializrMetadataV21JsonMapper() {
		this.dependenciesVariables = new TemplateVariables(new TemplateVariable(
				"bootVersion", TemplateVariable.VariableType.REQUEST_PARAM));
	}

	@Override
	protected ObjectNode links(ObjectNode parent, List<Type> types, String appUrl) {
		ObjectNode links = super.links(parent, types, appUrl);
		links.set("dependencies", dependenciesLink(appUrl));
		parent.set("_links", links);
		return links;
	}

	@Override
	protected ObjectNode mapDependency(Dependency dependency) {
		ObjectNode content = mapValue(dependency);
		if (dependency.getVersionRange() != null) {
			content.put("versionRange", dependency.getVersionRange());
		}
		if (dependency.getLinks() != null && !dependency.getLinks().isEmpty()) {
			content.set("_links", LinkMapper.mapLinks(dependency.getLinks()));
		}
		return content;
	}

	private ObjectNode dependenciesLink(String appUrl) {
		String uri = (appUrl != null ? appUrl + "/dependencies" : "/dependencies");
		UriTemplate uriTemplate = new UriTemplate(uri, this.dependenciesVariables);
		ObjectNode result = nodeFactory().objectNode();
		result.put("href", uriTemplate.toString());
		result.put("templated", true);
		return result;
	}

}
