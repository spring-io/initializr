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

package io.spring.initializr.metadata
/**
 * Meta-data used to generate a project.
 *
 * @author Stephane Nicoll
 * @since 1.0
 * @see ServiceCapability
 */
class InitializrMetadata {

	final InitializrConfiguration configuration

	final DependenciesCapability dependencies = new DependenciesCapability()

	final TypeCapability types = new TypeCapability()

	final SingleSelectCapability bootVersions =
			new SingleSelectCapability('bootVersion', 'Spring Boot Version', 'spring boot version')

	final SingleSelectCapability packagings =
			new SingleSelectCapability('packaging', 'Packaging', 'project packaging')

	final SingleSelectCapability javaVersions =
			new SingleSelectCapability('javaVersion', 'Java Version', 'language level')

	final SingleSelectCapability languages =
			new SingleSelectCapability('language', 'Language', 'programming language')

	final TextCapability name = new TextCapability('name', 'Name', 'project name (infer application name)')

	final TextCapability description = new TextCapability('description', 'Description', 'project description' )

	final TextCapability groupId = new TextCapability('groupId', 'Group', 'project coordinates')

	final TextCapability artifactId = new ArtifactIdCapability(name)

	final TextCapability version = new TextCapability('version', 'Version', 'project version')

	final TextCapability packageName = new PackageCapability(groupId)

	InitializrMetadata() {
		this(new InitializrConfiguration())
	}

	protected InitializrMetadata(InitializrConfiguration configuration) {
		this.configuration = configuration
	}

	/**
	 * Merge this instance with the specified argument
	 * @param other
	 */
	void merge(InitializrMetadata other) {
		this.configuration.merge(other.configuration)
		this.dependencies.merge(other.dependencies)
		this.types.merge(other.types)
		this.bootVersions.merge(other.bootVersions)
		this.packagings.merge(other.packagings)
		this.javaVersions.merge(other.javaVersions)
		this.languages.merge(other.languages)
		this.name.merge(other.name)
		this.description.merge(other.description)
		this.groupId.merge(other.groupId)
		this.artifactId.merge(other.artifactId)
		this.version.merge(other.version)
		this.packageName.merge(other.packageName)
	}

	/**
	 * Validate the meta-data.
	 */
	void validate() {
		this.configuration.validate()
		dependencies.validate()

		for (Dependency dependency : dependencies.all) {
			def boms = configuration.env.boms
			if (dependency.bom && !boms[dependency.bom]) {
				throw new InvalidInitializrMetadataException("Dependency $dependency " +
						"defines an invalid BOM id $dependency.bom, available boms $boms")
			}
			def repositories = configuration.env.repositories
			if (dependency.repository && !repositories[dependency.repository]) {
				throw new InvalidInitializrMetadataException("Dependency $dependency " +
						"defines an invalid repository id $dependency.repository, available repositores $repositories")
			}
		}
	}

	/**
	 * Create an URL suitable to download Spring Boot cli for the specified version and extension.
	 */
	String createCliDistributionURl(String extension) {
		String bootVersion = defaultId(bootVersions)
		configuration.env.artifactRepository + "org/springframework/boot/spring-boot-cli/" +
				"$bootVersion/spring-boot-cli-$bootVersion-bin.$extension"
	}

	/**
	 * Return the defaults for the capabilities defined on this instance.
	 */
	Map<String, ?> defaults() {
		def defaults = [:]
		defaults['type'] = defaultId(types)
		defaults['bootVersion'] = defaultId(bootVersions)
		defaults['packaging'] = defaultId(packagings)
		defaults['javaVersion'] = defaultId(javaVersions)
		defaults['language'] = defaultId(languages)
		defaults['groupId'] = groupId.content
		defaults['artifactId'] = artifactId.content
		defaults['version'] = version.content
		defaults['name'] = name.content
		defaults['description'] = description.content
		defaults['packageName'] = packageName.content
		defaults
	}

	private static String defaultId(def element) {
		def defaultValue = element.default
		defaultValue ? defaultValue.id : null
	}

	private static class ArtifactIdCapability extends TextCapability {
		private final TextCapability nameCapability

		ArtifactIdCapability(TextCapability nameCapability) {
			super('artifactId', 'Artifact', 'project coordinates (infer archive name)')
			this.nameCapability = nameCapability
		}

		@Override
		String getContent() {
			String value = super.getContent()
			value == null ? nameCapability.content : value
		}
	}

	private static class PackageCapability extends TextCapability {
		private final TextCapability nameCapability

		PackageCapability(TextCapability nameCapability) {
			super('packageName', 'Package Name', 'root package')
			this.nameCapability = nameCapability
		}

		@Override
		String getContent() {
			String value = super.getContent()
			value ?: (nameCapability.content != null ? nameCapability.content.replace('-', '.') : null)
		}
	}

}
