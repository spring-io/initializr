/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.mapper;

import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Type;
import tools.jackson.databind.node.ObjectNode;

import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;

/**
 * A {@link InitializrMetadataJsonMapper} handling the metadata format for v2.3.
 * <p>
 * Version 2.3 adds support for configuration file formats, allowing users to specify
 * their preferred configuration file format (e.g., properties, YAML).
 *
 * @author Sijun Yang
 * @see InitializrMetadataVersion#V2_3
 */
public class InitializrMetadataV23JsonMapper extends InitializrMetadataV22JsonMapper {

	@Override
	protected TemplateVariables getTemplateVariables(Type type) {
		return super.getTemplateVariables(type)
			.concat(new TemplateVariable("configurationFileFormat", TemplateVariable.VariableType.REQUEST_PARAM));
	}

	@Override
	protected void customizeParent(ObjectNode parent, InitializrMetadata metadata) {
		super.customizeParent(parent, metadata);
		singleSelect(parent, metadata.getConfigurationFileFormats());
	}

}
