/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr

import groovy.json.JsonBuilder

import org.springframework.hateoas.TemplateVariable
import org.springframework.hateoas.TemplateVariables
import org.springframework.hateoas.UriTemplate

/**
 * Generate a JSON representation of the metadata.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class InitializrMetadataJsonMapper {

	private final TemplateVariables templateVariables

	InitializrMetadataJsonMapper() {
		this.templateVariables = new TemplateVariables(
				new TemplateVariable('type', TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable('packaging', TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable('javaVersion', TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable('language', TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable('bootVersion', TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable('groupId', TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable('artifactId', TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable('version', TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable('name', TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable('description', TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable('packageName', TemplateVariable.VariableType.REQUEST_PARAM)
		)
	}

	String write(InitializrMetadata metadata, String appUrl) {
		JsonBuilder json = new JsonBuilder()
		json {
			links(delegate, metadata.types, appUrl)
			dependencies(delegate, metadata.dependencies)
			type(delegate, metadata.defaults.type, metadata.types)
			singleSelect(delegate, 'packaging', metadata.defaults.packaging, metadata.packagings)
			singleSelect(delegate, 'javaVersion', metadata.defaults.javaVersion, metadata.javaVersions)
			singleSelect(delegate, 'language', metadata.defaults.language, metadata.languages)
			singleSelect(delegate, 'bootVersion', metadata.defaults.bootVersion, metadata.bootVersions)
			text(delegate, 'groupId', metadata.defaults.groupId)
			text(delegate, 'artifactId', metadata.defaults.artifactId)
			text(delegate, 'version', metadata.defaults.version)
			text(delegate, 'name', metadata.defaults.name)
			text(delegate, 'description', metadata.defaults.description)
			text(delegate, 'packageName', metadata.defaults.packageName)
		}
		json.toString()
	}

	private links(parent, types, appUrl) {
		def content = [:]
		types.each {
			content[it.id] = link(appUrl, it)
		}
		parent._links content
	}

	private link(appUrl, type) {
		def result = [:]
		result.href = generateTemplatedUri(appUrl, type.action)
		result.templated = true
		result
	}

	private generateTemplatedUri(appUrl, action) {
		String uri = appUrl != null ? appUrl + action : action
		UriTemplate uriTemplate = new UriTemplate(uri + '?style={dependencies}', this.templateVariables)
		uriTemplate.toString()
	}


	private static dependencies(parent, groups) {
		parent.dependencies {
			type 'hierarchical-multi-select'
			values groups.collect {
				processDependencyGroup(it)
			}
		}

	}

	private static type(parent, defaultValue, dependencies) {
		parent.type {
			type 'action'
			if (defaultValue) {
				'default' defaultValue
			}
			values dependencies.collect {
				processType(it)
			}
		}
	}

	private static singleSelect(parent, name, defaultValue, itemValues) {
		parent."$name" {
			type 'single-select'
			if (defaultValue) {
				'default' defaultValue
			}
			values itemValues.collect {
				processValue(it)
			}
		}
	}

	private static text(parent, name, value) {
		parent."$name" {
			type 'text'
			if (value) {
				'default' value
			}
		}
	}


	private static processDependencyGroup(group) {
		def result = [:]
		result.name = group.name
		if (group.hasProperty('description') && group.description) {
			result.description = group.description
		}
		def items = []
		group.content.collect {
			items << processValue(it)
		}
		result.values = items
		result
	}

	private static processType(type) {
		def result = processValue(type)
		result.action = type.action
		result.tags = type.tags
		result
	}

	private static processValue(value) {
		def result = [:]
		result.id = value.id
		result.name = value.name
		if (value.hasProperty('description') && value.description) {
			result.description = value.description
		}
		result
	}

}
