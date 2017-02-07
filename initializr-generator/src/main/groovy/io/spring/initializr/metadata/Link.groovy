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

package io.spring.initializr.metadata

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.ToString

/**
 * Metadata for a link. Each link has a "relation" that potentially attaches a strong
 * semantic to the nature of the link. The URI of the link itself can be templated by
 * including variables in the form `{variableName}`.
 * <p>
 * An actual {@code URI} can be generated using {@code expand}, providing a mapping for
 * those variables.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
@ToString(ignoreNulls = true, includePackage = false)
class Link {

	private static final String VARIABLE_REGEX = "\\{(\\w+)\\}";

	/**
	 * The relation of the link.
	 */
	String rel;

	/**
	 * The URI the link is pointing to.
	 */
	String href

	/**
	 * Specify if the URI is templated.
	 */
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	boolean templated

	@JsonIgnore
	final Set<String> templateVariables = []

	/**
	 * A description of the link.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	String description

	Set<String> getTemplateVariables() {
		Collections.unmodifiableSet(templateVariables)
	}

	void setHref(String href) {
		this.href = href
	}

	void resolve() {
		if (!rel) {
			throw new InvalidInitializrMetadataException(
					"Invalid link $this: rel attribute is mandatory")
		}
		if (!href) {
			throw new InvalidInitializrMetadataException(
					"Invalid link $this: href attribute is mandatory")
		}
		def matcher = (href =~ VARIABLE_REGEX)
		while (matcher.find()) {
			def variable = matcher.group(1)
			this.templateVariables << variable
		}
		this.templated = this.templateVariables
	}

	/**
	 * Expand the link using the specified parameters.
	 * @param parameters the parameters value
	 * @return an URI where all variables have been expanded
	 */
	URI expand(Map<String, String> parameters) {
		String result = href
		templateVariables.forEach { var ->
			Object value = parameters[var]
			if (!value) {
				throw new IllegalArgumentException(
						"Could not explan $href, missing value for '$var'")
			}
			result = result.replace("{$var}", value.toString())
		}
		new URI(result)
	}

	public static Link create(String rel, String href) {
		return new Link(rel: rel, href: href);
	}

	public static Link create(String rel, String href, String description) {
		return new Link(rel: rel, href: href, description: description);
	}

	public static Link create(String rel, String href, boolean templated) {
		return new Link(rel: rel, href: href, templated: templated);
	}

}
