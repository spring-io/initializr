/*
 * Copyright 2012-2017 the original author or authors.
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.DependenciesCapability;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.DependencyGroup;
import io.spring.initializr.metadata.Describable;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.MetadataElement;
import io.spring.initializr.metadata.SingleSelectCapability;
import io.spring.initializr.metadata.TextCapability;
import io.spring.initializr.metadata.Type;
import io.spring.initializr.metadata.TypeCapability;
import org.json.JSONObject;

import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.util.StringUtils;

/**
 * A {@link InitializrMetadataJsonMapper} handling the metadata format for v2.
 *
 * @author Stephane Nicoll
 */
public class InitializrMetadataV2JsonMapper implements InitializrMetadataJsonMapper {

	private final TemplateVariables templateVariables;

	public InitializrMetadataV2JsonMapper() {
		this.templateVariables = new TemplateVariables(
				new TemplateVariable("dependencies",
						TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("packaging",
						TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("javaVersion",
						TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("language",
						TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("bootVersion",
						TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("groupId",
						TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("artifactId",
						TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("version",
						TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("name", TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("description",
						TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("packageName",
						TemplateVariable.VariableType.REQUEST_PARAM));
	}

	@Override
	public String write(InitializrMetadata metadata, String appUrl) {
		JSONObject delegate = new JSONObject();
		links(delegate, metadata.getTypes().getContent(), appUrl);
		dependencies(delegate, metadata.getDependencies());
		type(delegate, metadata.getTypes());
		singleSelect(delegate, metadata.getPackagings());
		singleSelect(delegate, metadata.getJavaVersions());
		singleSelect(delegate, metadata.getLanguages());
		singleSelect(delegate, metadata.getBootVersions());
		text(delegate, metadata.getGroupId());
		text(delegate, metadata.getArtifactId());
		text(delegate, metadata.getVersion());
		text(delegate, metadata.getName());
		text(delegate, metadata.getDescription());
		text(delegate, metadata.getPackageName());
		return delegate.toString();
	}

	protected Map<String, Object> links(JSONObject parent, List<Type> types, String appUrl) {
		Map<String, Object> content = new LinkedHashMap<>();
		types.forEach(it -> content.put(it.getId(), link(appUrl, it)));
		parent.put("_links", content);
		return content;
	}

	protected Map<String, Object> link(String appUrl, Type type) {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("href", generateTemplatedUri(appUrl, type));
		result.put("templated", true);
		return result;
	}

	private String generateTemplatedUri(String appUrl, Type type) {
		String uri = appUrl != null ? appUrl + type.getAction() : type.getAction();
		uri = uri + "?type=" + type.getId();
		UriTemplate uriTemplate = new UriTemplate(uri, this.templateVariables);
		return uriTemplate.toString();
	}

	protected void dependencies(JSONObject parent, DependenciesCapability capability) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("type", capability.getType().getName());
		map.put("values",
				capability.getContent().stream().map(this::mapDependencyGroup)
						.collect(Collectors.toList()));
		parent.put(capability.getId(), map);
	}

	protected void type(JSONObject parent, TypeCapability capability) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("type", "action");
		Type defaultType = capability.getDefault();
		if (defaultType != null) {
			map.put("default", defaultType.getId());
		}
		map.put("values", capability.getContent().stream().map(this::mapType)
				.collect(Collectors.toList()));
		parent.put("type", map);
	}

	protected void singleSelect(JSONObject parent, SingleSelectCapability capability) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("type", capability.getType().getName());
		DefaultMetadataElement defaultType = capability.getDefault();
		if (defaultType != null) {
			map.put("default", defaultType.getId());
		}
		map.put("values", capability.getContent().stream().map(this::mapValue)
				.collect(Collectors.toList()));
		parent.put(capability.getId(), map);
	}

	protected void text(JSONObject parent, TextCapability capability) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("type", capability.getType().getName());
		String defaultValue = capability.getContent();
		if (StringUtils.hasText(defaultValue)) {
			map.put("default", defaultValue);
		}
		parent.put(capability.getId(), map);
	}

	protected Map<String, Object> mapDependencyGroup(DependencyGroup group) {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("name", group.getName());
		if ((group instanceof Describable)
				&& ((Describable) group).getDescription() != null) {
			result.put("description", ((Describable) group).getDescription());
		}
		List<Object> items = new ArrayList<>();
		group.getContent().forEach(it -> {
			Map<String, Object> dependency = mapDependency(it);
			if (dependency != null) {
				items.add(dependency);
			}
		});
		result.put("values", items);
		return result;
	}

	protected Map<String, Object> mapDependency(Dependency dependency) {
		if (dependency.getVersionRange() == null) {
			// only map the dependency if no versionRange is set
			return mapValue(dependency);
		}
		return null;
	}

	protected Map<String, Object> mapType(Type type) {
		Map<String, Object> result = mapValue(type);
		result.put("action", type.getAction());
		result.put("tags", type.getTags());
		return result;
	}

	protected Map<String, Object> mapValue(MetadataElement value) {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("id", value.getId());
		result.put("name", value.getName());
		if ((value instanceof Describable)
				&& ((Describable) value).getDescription() != null) {
			result.put("description", ((Describable) value).getDescription());
		}
		return result;
	}

}
