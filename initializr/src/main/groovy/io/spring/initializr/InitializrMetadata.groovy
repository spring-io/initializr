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

package io.spring.initializr

import javax.annotation.PostConstruct

import groovy.transform.ToString
import groovy.util.logging.Slf4j
import io.spring.initializr.mapper.InitializrMetadataJsonMapper
import io.spring.initializr.mapper.InitializrMetadataV21JsonMapper
import io.spring.initializr.mapper.InitializrMetadataV2JsonMapper
import io.spring.initializr.support.InvalidVersionException
import io.spring.initializr.support.VersionRange

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * The metadata using by the initializr, that is:
 *
 * <ul>
 * <li>Known dependencies gathered in group</li>
 * <li>The build types supported by the service</li>
 * <li>Supported Java versions</li>
 * <li>Supported language</li>
 * <li>Supported Spring Boot versions</li>
 * <li>Default settings used to generate the project</li>
 * <li>Environment related settings</li>
 * </ul>
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@ConfigurationProperties(prefix = 'initializr', ignoreUnknownFields = false)
@Slf4j
class InitializrMetadata {

	final List<DependencyGroup> dependencies = []

	final List<Type> types = []

	final List<Packaging> packagings = []

	final List<JavaVersion> javaVersions = []

	final List<Language> languages = []

	final List<BootVersion> bootVersions = []

	final Defaults defaults = new Defaults()

	final Env env = new Env()

	private final Map<String, Dependency> indexedDependencies = [:]
	
	@Autowired
	List<InitializrMetadataCustomizer> customizers = []

	/**
	 * Return the {@link Dependency} with the specified id or {@code null} if
	 * no such dependency exists.
	 */
	Dependency getDependency(String id) {
		indexedDependencies[id]
	}

	/**
	 * Return all dependencies as a flat collection
	 */
	Collection<Dependency> getAllDependencies() {
		indexedDependencies.values()
	}

	/**
	 * Return the {@link Type} with the specified id or {@code null} if no
	 * such type exists.
	 */
	Type getType(String id) {
		for (it in this.types) {
			if (id.equals(it.id) || id.equals(it.stsId)) {
				return it
			}
		}
		return null
	}

	/**
	 * Create an URL suitable to download Spring Boot cli for the specified version and extension.
	 */
	String createCliDistributionURl(String extension) {
		env.artifactRepository + "org/springframework/boot/spring-boot-cli/" +
				"$defaults.bootVersion/spring-boot-cli-$defaults.bootVersion-bin.$extension"
	}

	/**
	 * Generate a suitable application mame based on the specified name. If no suitable
	 * application name can be generated from the specified {@code name}, the
	 * {@link Env#fallbackApplicationName} is used instead.
	 * <p>No suitable application name can be generated if the name is {@code null} or
	 * if it contains an invalid character for a class identifier.
	 * @see Env#fallbackApplicationName
	 * @see Env#invalidApplicationNames
	 */
	String generateApplicationName(String name) {
		if (!name) {
			return env.fallbackApplicationName
		}
		String text = splitCamelCase(name.trim())
		String result = text.replaceAll("(_|-| |:)+([A-Za-z0-9])", { Object[] it ->
			it[2].toUpperCase()
		})
		if (!result.endsWith('Application')) {
			result += 'Application'
		}
		String candidate = result.capitalize();
		if (hasInvalidChar(candidate) || env.invalidApplicationNames.contains(candidate)) {
			return env.fallbackApplicationName
		} else {
			return candidate
		}
	}

	/**
	 * Initializes a {@link ProjectRequest} instance with the defaults
	 * defined in this instance.
	 */
	void initializeProjectRequest(ProjectRequest request) {
		defaults.properties.each { key, value ->
			if (request.hasProperty(key) && !(key in ['class', 'metaClass'])) {
				request[key] = value
			}
		}
		request
	}

	/**
	 * Merge this instance with the specified content.
	 */
	void merge(List<BootVersion> bootVersions) {
		if (bootVersions) {
			synchronized (this.bootVersions) {
				this.bootVersions.clear()
				this.bootVersions.addAll(bootVersions)
			}
		}
		refreshDefaults()
	}

	/**
	 * Generate a JSON representation of the current metadata
	 * @param version the meta-data version
	 * @param appUrl the application url
	 */
	String generateJson(InitializrMetadataVersion version, String appUrl) {
		getJsonMapper(version).write(this, appUrl)
	}

	/**
	 * Initialize and validate the configuration.
	 */
	@PostConstruct
	void validate() {

		customizers.each { customizer ->
			customizer.customize(this)
		}

		dependencies.each { group ->
			group.content.each { dependency ->
				validateDependency(dependency)
				indexDependency(dependency.id, dependency)
				for (String alias : dependency.aliases) {
					indexDependency(alias, dependency)
				}
			}
		}
		env.validate()

		refreshDefaults()
	}

	private void refreshDefaults() {
		defaults.type = getDefault(types)
		defaults.packaging = getDefault(packagings)
		defaults.javaVersion = getDefault(javaVersions)
		defaults.language = getDefault(languages)
		defaults.bootVersion = getDefault(bootVersions)
	}

	private void indexDependency(String id, Dependency dependency) {
		def existing = indexedDependencies[id]
		if (existing) {
			throw new IllegalArgumentException("Could not register $dependency another dependency has also the '$id' id $existing");
		}
		indexedDependencies[id] = dependency
	}

	static void validateDependency(Dependency dependency) {
		def id = dependency.id
		if (id == null) {
			if (!dependency.hasCoordinates()) {
				throw new InvalidInitializrMetadataException(
						'Invalid dependency, should have at least an id or a groupId/artifactId pair.')
			}
			dependency.generateId()
		} else if (!dependency.hasCoordinates()) {
			// Let's build the coordinates from the id
			def st = new StringTokenizer(id, ':')
			if (st.countTokens() == 1) { // assume spring-boot-starter
				dependency.asSpringBootStarter(id)
			} else if (st.countTokens() == 2 || st.countTokens() == 3) {
				dependency.groupId = st.nextToken()
				dependency.artifactId = st.nextToken()
				if (st.hasMoreTokens()) {
					dependency.version = st.nextToken()
				}
			} else {
				throw new InvalidInitializrMetadataException(
						"Invalid dependency, id should have the form groupId:artifactId[:version] but got $id")
			}
		}
		if (dependency.versionRange) {
			try {
				VersionRange.parse(dependency.versionRange)
			} catch (InvalidVersionException ex) {
				throw new InvalidInitializrMetadataException("Invalid version range '$dependency.versionRange' for " +
						"dependency with id '$dependency.id'")
			}
		}
	}

	static def getDefault(List elements) {
		for (DefaultIdentifiableElement element : elements) {
			if (element.default) {
				return element.id
			}
		}
		log.warn("No default found amongst $elements")
		return (elements.isEmpty() ? null : elements.get(0).id)
	}

	private static InitializrMetadataJsonMapper getJsonMapper(InitializrMetadataVersion version) {
		switch(version) {
			case InitializrMetadataVersion.V2: return new InitializrMetadataV2JsonMapper();
			default: return new InitializrMetadataV21JsonMapper();
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

	static class DependencyGroup {

		String name

		final List<Dependency> content = []

	}

	@ToString(ignoreNulls = true, includePackage = false)
	static class Dependency extends IdentifiableElement {

		static final String SCOPE_COMPILE = 'compile'
		static final String SCOPE_RUNTIME = 'runtime'
		static final String SCOPE_PROVIDED = 'provided'
		static final String SCOPE_TEST = 'test'
		static final List<String> SCOPE_ALL = [SCOPE_COMPILE, SCOPE_RUNTIME, SCOPE_PROVIDED, SCOPE_TEST]

		List<String> aliases = []

		List<String> facets = []

		String groupId

		String artifactId

		String version

		String scope = SCOPE_COMPILE

		String description

		String versionRange

		void setScope(String scope) {
			if (!SCOPE_ALL.contains(scope)) {
				throw new InvalidInitializrMetadataException("Invalid scope $scope must be one of $SCOPE_ALL")
			}
			this.scope = scope
		}

		void setVersionRange(String versionRange) {
			this.versionRange = versionRange ? versionRange.trim() : null
		}

		/**
		 * Specify if the dependency has its coordinates set, i.e. {@code groupId}
		 * and {@code artifactId}.
		 */
		boolean hasCoordinates() {
			groupId && artifactId
		}

		/**
		 * Define this dependency as a standard spring boot starter with the specified name
		 * <p>If no name is specified, the root 'spring-boot-starter' is assumed.
		 */
		Dependency asSpringBootStarter(String name) {
			groupId = 'org.springframework.boot'
			artifactId = name ? 'spring-boot-starter-' + name : 'spring-boot-starter'
			if (name) {
				id = name
			}
			this
		}

		/**
		 * Generate an id using the groupId and artifactId
		 */
		def generateId() {
			if (groupId == null || artifactId == null) {
				throw new IllegalArgumentException(
						"Could not generate id for $this: at least groupId and artifactId must be set.")
			}
			StringBuilder sb = new StringBuilder()
			sb.append(groupId).append(':').append(artifactId)
			id = sb.toString()
		}
	}

	static class Type extends DefaultIdentifiableElement {

		String description

		@Deprecated
		String stsId

		String action

		void setAction(String action) {
			String actionToUse = action
			if (!actionToUse.startsWith("/")) {
				actionToUse =  "/" +  actionToUse
			}
			this.action = actionToUse
		}

		final Map<String, String> tags = [:]
	}

	static class Packaging extends DefaultIdentifiableElement {
	}

	static class JavaVersion extends DefaultIdentifiableElement {
	}

	static class Language extends DefaultIdentifiableElement {
	}

	static class BootVersion extends DefaultIdentifiableElement {
	}

	static class Defaults {

		static final String DEFAULT_NAME = 'demo'

		String groupId = 'org.test'
		String artifactId
		String version = '0.0.1-SNAPSHOT'
		String name = DEFAULT_NAME
		String description = 'Demo project for Spring Boot'
		String packageName
		String type
		String packaging
		String javaVersion
		String language
		String bootVersion

		/**
		 * Return the artifactId or the name of the project if none is set.
		 */
		String getArtifactId() {
			artifactId == null ? name : artifactId
		}

		/**
		 * Return the package name or the name of the project if none is set
		 */
		String getPackageName() {
			packageName == null ? name.replace('-', '.') : packageName
		}

	}

	/**
	 * Defines additional environment settings
	 */
	static class Env {

		String artifactRepository = 'https://repo.spring.io/release/'

		String springBootMetadataUrl = 'https://spring.io/project_metadata/spring-boot'

		/**
		 * The application name to use if none could be generated.
		 */
		String fallbackApplicationName = 'Application'

		/**
		 * The list of invalid application names. If such name is chosen or generated,
		 * the {@link #fallbackApplicationName} should be used instead.
		 */
		List<String> invalidApplicationNames = ['SpringApplication', 'SpringBootApplication']

		boolean forceSsl = true

		void validate() {
			if (!artifactRepository.endsWith('/')) {
				artifactRepository = artifactRepository + '/'
			}
		}

	}

	static class DefaultIdentifiableElement extends IdentifiableElement {

		private boolean defaultValue

		void setDefault(boolean defaultValue) {
			this.defaultValue = defaultValue
		}

		boolean isDefault() {
			this.defaultValue
		}
	}

	static class IdentifiableElement {

		String name

		String id

		String getName() {
			(name ?: id)
		}
	}
}
