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

package io.spring.initializr.web.mapper;

import java.util.List;
import java.util.function.Function;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.Version.Format;
import io.spring.initializr.generator.version.VersionParser;
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
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A {@link InitializrMetadataJsonMapper} handling the metadata format for v2.
 *
 * @author Stephane Nicoll
 * @author Guillaume Gerbaud
 * @author Moritz Halbritter
 */
public class InitializrMetadataV2JsonMapper implements InitializrMetadataJsonMapper {

	private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

	private final TemplateVariables templateVariables;

	/**
	 * Create a new instance.
	 */
	public InitializrMetadataV2JsonMapper() {
		this.templateVariables = new TemplateVariables(
				new TemplateVariable("dependencies", TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("packaging", TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("javaVersion", TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("language", TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("bootVersion", TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("groupId", TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("artifactId", TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("version", TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("name", TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("description", TemplateVariable.VariableType.REQUEST_PARAM),
				new TemplateVariable("packageName", TemplateVariable.VariableType.REQUEST_PARAM));
	}

	protected JsonNodeFactory nodeFactory() {
		return nodeFactory;
	}

	@Override
	public String write(InitializrMetadata metadata, @Nullable String appUrl) {
		ObjectNode parent = nodeFactory.objectNode();
		links(parent, metadata.getTypes().getContent(), appUrl);
		dependencies(parent, metadata.getDependencies());
		type(parent, metadata.getTypes());
		singleSelect(parent, metadata.getPackagings());
		singleSelect(parent, metadata.getJavaVersions());
		singleSelect(parent, metadata.getLanguages());
		singleSelect(parent, metadata.getBootVersions(), this::mapVersionMetadata, this::formatVersion);
		text(parent, metadata.getGroupId());
		text(parent, metadata.getArtifactId());
		text(parent, metadata.getVersion());
		text(parent, metadata.getName());
		text(parent, metadata.getDescription());
		text(parent, metadata.getPackageName());
		customizeParent(parent, metadata);
		return parent.toString();
	}

	/**
	 * Customizes the parent.
	 * @param parent the parent
	 * @param metadata the metadata
	 */
	protected void customizeParent(ObjectNode parent, InitializrMetadata metadata) {
	}

	protected ObjectNode links(ObjectNode parent, List<Type> types, @Nullable String appUrl) {
		ObjectNode content = nodeFactory.objectNode();
		types.forEach((it) -> content.set(it.getId(), link(appUrl, it)));
		parent.set("_links", content);
		return content;
	}

	protected ObjectNode link(@Nullable String appUrl, Type type) {
		ObjectNode result = nodeFactory.objectNode();
		result.put("href", generateTemplatedUri(appUrl, type));
		result.put("templated", true);
		return result;
	}

	protected String generateTemplatedUri(@Nullable String appUrl, Type type) {
		String uri = (appUrl != null) ? appUrl + type.getAction() : type.getAction();
		uri = uri + "?type=" + type.getId();
		UriTemplate uriTemplate = UriTemplate.of(uri, getTemplateVariables(type));
		return uriTemplate.toString();
	}

	protected TemplateVariables getTemplateVariables(Type type) {
		return this.templateVariables;
	}

	protected void dependencies(ObjectNode parent, DependenciesCapability capability) {
		ObjectNode dependencies = nodeFactory.objectNode();
		dependencies.put("type", capability.getType().getName());
		ArrayNode values = nodeFactory.arrayNode();
		values.addAll(capability.getContent().stream().map(this::mapDependencyGroup).toList());
		dependencies.set("values", values);
		parent.set(capability.getId(), dependencies);
	}

	protected void type(ObjectNode parent, TypeCapability capability) {
		ObjectNode type = nodeFactory.objectNode();
		type.put("type", capability.getType().getName());
		Type defaultType = capability.getDefault();
		if (defaultType != null) {
			type.put("default", defaultType.getId());
		}
		ArrayNode values = nodeFactory.arrayNode();
		values.addAll(capability.getContent().stream().map(this::mapType).toList());
		type.set("values", values);
		parent.set("type", type);
	}

	protected void singleSelect(ObjectNode parent, SingleSelectCapability capability) {
		singleSelect(parent, capability, this::mapValue, (id) -> id);
	}

	/**
	 * Map a {@link SingleSelectCapability} invoking the specified {@code valueMapper}.
	 * @param parent the parent node
	 * @param capability the capability to map
	 * @param valueMapper the function to invoke to transform one value of the capability
	 * @deprecated in favor of
	 * {@link #singleSelect(ObjectNode, SingleSelectCapability, Function, Function)}
	 */
	@Deprecated
	protected void singleSelect(ObjectNode parent, SingleSelectCapability capability,
			Function<MetadataElement, ObjectNode> valueMapper) {
		singleSelect(parent, capability, valueMapper, (id) -> id);
	}

	protected void singleSelect(ObjectNode parent, SingleSelectCapability capability,
			Function<MetadataElement, ObjectNode> valueMapper, Function<String, String> defaultMapper) {
		ObjectNode single = nodeFactory.objectNode();
		single.put("type", capability.getType().getName());
		DefaultMetadataElement defaultType = capability.getDefault();
		if (defaultType != null) {
			Assert.state(defaultType.getId() != null, "'defaultType.getId()' must not be null");
			single.put("default", defaultMapper.apply(defaultType.getId()));
		}
		ArrayNode values = nodeFactory.arrayNode();
		values.addAll(capability.getContent().stream().map(valueMapper).toList());
		single.set("values", values);
		parent.set(capability.getId(), single);
	}

	protected void text(ObjectNode parent, TextCapability capability) {
		ObjectNode text = nodeFactory.objectNode();
		text.put("type", capability.getType().getName());
		String defaultValue = capability.getContent();
		if (StringUtils.hasText(defaultValue)) {
			text.put("default", defaultValue);
		}
		parent.set(capability.getId(), text);
	}

	protected ObjectNode mapDependencyGroup(DependencyGroup group) {
		ObjectNode result = nodeFactory.objectNode();
		result.put("name", group.getName());
		if ((group instanceof Describable) && ((Describable) group).getDescription() != null) {
			result.put("description", ((Describable) group).getDescription());
		}
		ArrayNode items = nodeFactory.arrayNode();
		group.getContent().forEach((it) -> {
			JsonNode dependency = mapDependency(it);
			if (dependency != null) {
				items.add(dependency);
			}
		});
		result.set("values", items);
		return result;
	}

	protected @Nullable ObjectNode mapDependency(Dependency dependency) {
		if (dependency.getCompatibilityRange() == null) {
			// only map the dependency if no compatibilityRange is set
			return mapValue(dependency);
		}
		return null;
	}

	protected ObjectNode mapType(Type type) {
		ObjectNode result = mapValue(type);
		result.put("action", type.getAction());
		ObjectNode tags = nodeFactory.objectNode();
		type.getTags().forEach(tags::put);
		result.set("tags", tags);
		return result;
	}

	private ObjectNode mapVersionMetadata(MetadataElement value) {
		ObjectNode result = nodeFactory.objectNode();
		String id = value.getId();
		Assert.state(id != null, "'id' must not be null");
		result.put("id", formatVersion(id));
		result.put("name", value.getName());
		return result;
	}

	protected String formatVersion(String versionId) {
		Version version = VersionParser.DEFAULT.safeParse(versionId);
		return (version != null) ? version.format(Format.V1).toString() : versionId;
	}

	protected ObjectNode mapValue(MetadataElement value) {
		ObjectNode result = nodeFactory.objectNode();
		String id = value.getId();
		Assert.state(id != null, "'id' must not be null");
		result.put("id", id);
		result.put("name", value.getName());
		if ((value instanceof Describable) && ((Describable) value).getDescription() != null) {
			result.put("description", ((Describable) value).getDescription());
		}
		return result;
	}

}
