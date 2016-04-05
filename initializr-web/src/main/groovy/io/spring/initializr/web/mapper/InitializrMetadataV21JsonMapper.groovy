/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.web.mapper

import org.springframework.hateoas.TemplateVariable
import org.springframework.hateoas.TemplateVariables
import org.springframework.hateoas.UriTemplate

/**
 * A {@link InitializrMetadataJsonMapper} handling the meta-data format for v2.1
 * <p>
 * Version 2.1 brings the 'versionRange' attribute for a dependency to restrict
 * the Spring Boot versions that can be used against it. That version also adds
 * an additional `dependencies` endpoint.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class InitializrMetadataV21JsonMapper extends InitializrMetadataV2JsonMapper {

	private final TemplateVariables dependenciesVariables

	InitializrMetadataV21JsonMapper() {
		this.dependenciesVariables = new TemplateVariables(
				new TemplateVariable('bootVersion', TemplateVariable.VariableType.REQUEST_PARAM)
		)
	}

	@Override
	protected links(parent, types, appUrl) {
		def links = super.links(parent, types, appUrl)
		links['dependencies'] = dependenciesLink(appUrl)
		links
	}

	@Override
	protected mapDependency(dependency) {
		def content = mapValue(dependency)
		if (dependency.versionRange) {
			content['versionRange'] = dependency.versionRange
		}
		content
	}

	private dependenciesLink(appUrl) {
		String uri = appUrl != null ? appUrl + '/dependencies' : '/dependencies'
		UriTemplate uriTemplate = new UriTemplate(uri, this.dependenciesVariables)
		def result = [:]
		result.href = uriTemplate.toString()
		result.templated = true
		result
	}
}
