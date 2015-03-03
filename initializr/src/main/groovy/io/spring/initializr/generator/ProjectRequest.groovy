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

package io.spring.initializr.generator

import groovy.util.logging.Slf4j
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.metadata.Type
import io.spring.initializr.util.Version
import io.spring.initializr.util.VersionRange

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
	List<Dependency> resolvedDependencies

	def facets = []
	def build

	/**
	 * Initializes this instance with the defaults defined in the specified {@link InitializrMetadata}.
	 */
	void initialize(InitializrMetadata metadata) {
		metadata.defaults().each { key, value ->
			if (owner.hasProperty(key)) {
				owner.setProperty(key, value)
			}
		}
	}

	/**
	 * Resolve this instance against the specified {@link InitializrMetadata}
	 */
	void resolve(InitializrMetadata metadata) {
		List<String> depIds = style ? style : dependencies
		resolvedDependencies = depIds.collect {
			def dependency = metadata.dependencies.get(it)
			if (dependency == null) {
				if (it.contains(':')) {
					throw new InvalidProjectRequestException("Unknown dependency '$it' check project metadata")
				}
				log.warn("No known dependency for style '$it' assuming spring-boot-starter")
				dependency = new Dependency()
				dependency.asSpringBootStarter(it)
			}
			dependency
		}
		String actualBootVersion = bootVersion ?: metadata.bootVersions.default.id
		Version requestedVersion = Version.parse(actualBootVersion)
		resolvedDependencies.each {
			it.facets.each {
				if (!facets.contains(it)) {
					facets.add(it)
				}
			}
			if (it.versionRange) {
				def range = VersionRange.parse(it.versionRange)
				if (!range.match(requestedVersion)) {
					throw new InvalidProjectRequestException("Dependency '$it.id' is not compatible " +
							"with Spring Boot $bootVersion")
				}
			}
		}

		if (this.type) {
			Type type = metadata.types.get(this.type)
			if (!type) {
				throw new InvalidProjectRequestException("Unknown type '${this.type}' check project metadata")
			}
			String buildTag = type.tags['build']
			if (buildTag) {
				this.build = buildTag
			}
		}

		if (!applicationName) {
			this.applicationName = metadata.configuration.generateApplicationName(this.name)
		}

		afterResolution(metadata)
	}

	/**
	 * Update this request once it has been resolved with the specified {@link InitializrMetadata}.
	 */
	protected afterResolution(InitializrMetadata metadata) {
		if (packaging == 'war' && !hasWebFacet()) {
			// Need to be able to bootstrap the web app
			resolvedDependencies << metadata.dependencies.get('web')
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
		def root = new Dependency()
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

}
