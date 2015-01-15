/*
 * Copyright 2012-2014 the original author or authors.
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

package io.spring.initializr

import groovy.util.logging.Slf4j

/**
 * A request to generate a project.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @since 1.0
 */
@Slf4j
class ProjectRequest {

	/**
	 * The id of the starter to use if no dependency is defined.
	 */
	static final DEFAULT_STARTER = 'root_starter'

	/**
	 * The default name of the application class.
	 */
	static final String DEFAULT_APPLICATION_NAME = 'Application'

	List<String> style = []
	List<String> dependencies = []
	String name
	String type
	String description
	String groupId
	String artifactId
	String version
	String bootVersion
	String packaging
	String applicationName
	String language
	String packageName
	String javaVersion

	// The base directory to create in the archive - no baseDir by default
	String baseDir

	// Resolved dependencies based on the ids provided by either "style" or "dependencies"
	List<InitializrMetadata.Dependency> resolvedDependencies

	def facets = []
	def build

	/**
	 * Resolve this instance against the specified {@link InitializrMetadata}
	 */
	void resolve(InitializrMetadata metadata) {
		List<String> depIds = style ? style : dependencies
		resolvedDependencies = depIds.collect {
			def dependency = metadata.getDependency(it)
			if (dependency == null) {
				if (it.contains(':')) {
					throw new InvalidProjectRequestException("Unknown dependency '$it' check project metadata")
				}
				log.warn("No known dependency for style '$it' assuming spring-boot-starter")
				dependency = new InitializrMetadata.Dependency()
				dependency.asSpringBootStarter(it)
			}
			dependency
		}
		resolvedDependencies.each {
			it.facets.each {
				if (!facets.contains(it)) {
					facets.add(it)
				}
			}
		}

		if (this.type) {
			InitializrMetadata.Type type = metadata.getType(this.type)
			if (!type) {
				throw new InvalidProjectRequestException("Unknown type '${this.type}' check project metadata")
			}
			String buildTag = type.tags['build']
			if (buildTag) {
				this.build = buildTag
			}
		}

		if (!applicationName) {
			this.applicationName = generateApplicationName(this.name, DEFAULT_APPLICATION_NAME)
		}

		afterResolution(metadata)
	}

	/**
	 * Update this request once it has been resolved with the specified {@link InitializrMetadata}.
	 */
	protected afterResolution(InitializrMetadata metadata) {
		if (packaging == 'war' && !hasWebFacet()) {
			// Need to be able to bootstrap the web app
			resolvedDependencies << metadata.getDependency('web')
			facets << 'web'
		}
		if (resolvedDependencies.isEmpty()) {
			addDefaultDependency()
		}
	}

	/**
	 * Add a default dependency if the project does not define any
	 * dependency
	 */
	protected addDefaultDependency() {
		def root = new InitializrMetadata.Dependency()
		root.id = DEFAULT_STARTER
		root.asSpringBootStarter('')
		resolvedDependencies << root
	}

	/**
	 * Specify if this request has the web facet enabled.
	 */
	boolean hasWebFacet() {
		hasFacet('web')
	}

	/**
	 * Specify if this request has the specified facet enabled
	 */
	boolean hasFacet(String facet) {
		facets.contains(facet)
	}

	/**
	 * Generate a suitable application mame based on the specified name. If no suitable
	 * application name can be generated from the specified {@code name}, the
	 * {@code defaultApplicationName} is used instead.
	 * <p>No suitable application name can be generated if the name is {@code null} or
	 * if it contains an invalid character for a class identifier.
	 */
	static String generateApplicationName(String name, String defaultApplicationName) {
		if (!name) {
			return defaultApplicationName
		}
		String text = splitCamelCase(name.trim())
		String result = text.replaceAll("(_|-| |:)+([A-Za-z0-9])", { Object[] it ->
			it[2].toUpperCase()
		})
		if (!result.endsWith('Application')) {
			result += 'Application'
		}
		String candidate = result.capitalize();
		if (hasInvalidChar(candidate)) {
			return defaultApplicationName
		} else {
			return candidate
		}
	}

	private static String splitCamelCase(String text) {
		text.split('(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])').collect {
			String s = it.toLowerCase()
			s.capitalize()
		}.join("")
	}

	private static boolean hasInvalidChar(String text) {
		if (!Character.isJavaIdentifierStart(text.charAt(0))) {
			return true
		}
		if (text.length() > 1) {
			for (int i = 1; i < text.length(); i++) {
				if (!Character.isJavaIdentifierPart(text.charAt(i))) {
					return true
				}
			}
		}
		return false
	}

}
