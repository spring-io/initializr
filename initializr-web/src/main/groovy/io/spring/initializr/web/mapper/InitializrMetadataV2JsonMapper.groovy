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

import groovy.json.JsonBuilder
import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.metadata.SingleSelectCapability

import org.springframework.hateoas.TemplateVariable
import org.springframework.hateoas.TemplateVariables
import org.springframework.hateoas.UriTemplate

/**
 * A {@link InitializrMetadataJsonMapper} handling the meta-data format for v2.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class InitializrMetadataV2JsonMapper implements InitializrMetadataJsonMapper {

	private final TemplateVariables templateVariables

	InitializrMetadataV2JsonMapper() {
		this.templateVariables = new TemplateVariables(
				new TemplateVariable('dependencies', TemplateVariable.VariableType.REQUEST_PARAM),
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

	@Override
	String write(InitializrMetadata metadata, String appUrl) {
		JsonBuilder json = new JsonBuilder()
		json {
			links(delegate, metadata.types.content, appUrl)
			dependencies(delegate, metadata.dependencies)
			type(delegate, metadata.types)
			singleSelect(delegate, metadata.packagings)
			singleSelect(delegate, metadata.javaVersions)
			singleSelect(delegate, metadata.languages)
			singleSelect(delegate, metadata.bootVersions)
			text(delegate, metadata.groupId)
			text(delegate, metadata.artifactId)
			text(delegate, metadata.version)
			text(delegate, metadata.name)
			text(delegate, metadata.description)
			text(delegate, metadata.packageName)
		}
		json.toString()
	}

	protected links(parent, types, appUrl) {
		def content = [:]
		types.each {
			content[it.id] = link(appUrl, it)
		}
		parent._links content
	}

	protected link(appUrl, type) {
		def result = [:]
		result.href = generateTemplatedUri(appUrl, type)
		result.templated = true
		result
	}

	private generateTemplatedUri(appUrl, type) {
		String uri = appUrl != null ? appUrl + type.action : type.action
		uri += "?type=$type.id"
		UriTemplate uriTemplate = new UriTemplate(uri, this.templateVariables)
		uriTemplate.toString()
	}


	protected dependencies(parent, capability) {
		parent."$capability.id" {
			type "$capability.type.name"
			values capability.content.collect {
				mapDependencyGroup(it)
			}
		}
	}

	protected type(parent, capability) {
		parent.type {
			type 'action'
			def defaultType = capability.default
			if (defaultType) {
				'default' defaultType.id
			}
			values capability.content.collect {
				mapType(it)
			}
		}
	}

	protected singleSelect(parent, SingleSelectCapability capability) {
		parent."$capability.id" {
			type "$capability.type.name"
			def defaultValue = capability.default
			if (defaultValue) {
				'default' defaultValue.id
			}
			values capability.content.collect {
				mapValue(it)
			}
		}
	}

	protected text(parent, capability) {
		parent."$capability.id" {
			type "$capability.type.name"
			def defaultValue = capability.content
			if (defaultValue) {
				'default' defaultValue
			}
		}
	}

	protected mapDependencyGroup(group) {
		def result = [:]
		result.name = group.name
		if (group.hasProperty('description') && group.description) {
			result.description = group.description
		}
		def items = []
		group.content.collect {
			def dependency = mapDependency(it)
			if (dependency) {
				items << dependency
			}
		}
		result.values = items
		result
	}

	protected mapDependency(dependency) {
		if (!dependency.versionRange) { // only map the dependency if no versionRange is set
			mapValue(dependency)
		}
	}

	protected mapType(type) {
		def result = mapValue(type)
		result.action = type.action
		result.tags = type.tags
		result
	}

	protected mapValue(value) {
		def result = [:]
		result.id = value.id
		result.name = value.name
		if (value.hasProperty('description') && value.description) {
			result.description = value.description
		}
		result
	}

}
