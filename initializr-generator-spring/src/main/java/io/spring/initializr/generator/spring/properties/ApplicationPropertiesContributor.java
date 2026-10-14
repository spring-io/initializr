/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import io.spring.initializr.generator.configuration.format.properties.PropertiesFormat;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

import org.springframework.util.StringUtils;

/**
 * A {@link ProjectContributor} that contributes
 * {@code application[-{profile}].properties} files to a project, one per source set and
 * Spring profile that has properties. The {@code application.properties} of the main
 * source set is contributed even when empty.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public class ApplicationPropertiesContributor extends AbstractApplicationPropertiesContributor {

	/**
	 * Creates a new instance.
	 * @param properties the application properties to contribute
	 * @param description the description of the project, used to resolve the source
	 * structures
	 */
	public ApplicationPropertiesContributor(ApplicationProperties properties, ProjectDescription description) {
		super(properties, description, new PropertiesFormat());
	}

	@Override
	protected void writeProperties(Map<String, Object> properties, PrintWriter writer) {
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			// Collections are written as comma delimited values, for example 'a,b'
			Object value = (entry.getValue() instanceof Collection<?> collection)
					? StringUtils.collectionToCommaDelimitedString(collection) : entry.getValue();
			writer.printf("%s=%s%n", entry.getKey(), value);
		}
	}

}
