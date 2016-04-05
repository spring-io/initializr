/*
 * Copyright 2012-2016 the original author or authors.
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
 * Various configuration options used by the service.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class InitializrConfiguration {

	/**
	 * Environment options.
	 */
	final Env env = new Env()

	void validate() {
		env.validate()
	}

	void merge(InitializrConfiguration other) {
		env.merge(other.env)
	}

	/**
	 * Generate a suitable application name based on the specified name. If no suitable
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
	 * Clean the specified package name if necessary. If the package name cannot
	 * be transformed to a valid package name, the {@code defaultPackageName}
	 * is used instead.
	 * <p>The package name cannot be cleaned if the  specified {@code packageName}
	 * is {@code null} or if it contains an invalid character for a class identifier.
	 * @see Env#invalidPackageNames
	 */
	String cleanPackageName(String packageName, String defaultPackageName) {
		if (!packageName) {
			return defaultPackageName
		}
		String candidate = packageName.trim().split('\\W+').join('.')
		if (hasInvalidChar(candidate.replace('.', '')) || env.invalidPackageNames.contains(candidate)) {
			return defaultPackageName
		}
		else {
			candidate
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

	/**
	 * Defines additional environment settings.
	 */
	static class Env {

		/**
		 * The url of the repository servicing distribution bundle.
		 */
		String artifactRepository = 'https://repo.spring.io/release/'

		/**
		 * The meta-data url of the Spring Boot project.
		 */
		String springBootMetadataUrl = 'https://spring.io/project_metadata/spring-boot'

		/**
		 * Tracking code for Google Analytics. Only enabled if a value is explicitly provided.
		 */
		String googleAnalyticsTrackingCode

		/**
		 * The application name to use if none could be generated.
		 */
		String fallbackApplicationName = 'Application'

		/**
		 * The list of invalid application names. If such name is chosen or generated,
		 * the "fallbackApplicationName" should be used instead.
		 */
		List<String> invalidApplicationNames = [
				'SpringApplication',
				'SpringBootApplication'
		]

		/**
		 * The list of invalid package names. If such name is chosen or generated,
		 * the the default package name should be used instead.
		 */
		List<String> invalidPackageNames = [
				'org.springframework'
		]

		/**
		 * Force SSL support. When enabled, any access using http generate https links.
		 */
		boolean forceSsl = true

		/**
		 * The "BillOfMaterials" that are referenced in this instance, identified by an
		 * arbitrary identifier that can be used in the dependencies definition.
		 */
		final Map<String, BillOfMaterials> boms = [:]

		/**
		 * The "Repository" instances that are referenced in this instance, identified by
		 * an arbitrary identifier that can be used in the dependencies definition.
		 */
		final Map<String, Repository> repositories = [:]

		/**
		 * Kotlin-specific settings.
		 */
		final Kotlin kotlin = new Kotlin()

		Env() {
			repositories['spring-snapshots'] = new Repository(name: 'Spring Snapshots',
					url: new URL('https://repo.spring.io/snapshot'), snapshotsEnabled: true)
			repositories['spring-milestones'] = new Repository(name: 'Spring Milestones',
					url: new URL('https://repo.spring.io/milestone'), snapshotsEnabled: false)
		}

		void setArtifactRepository(String artifactRepository) {
			if (!artifactRepository.endsWith('/')) {
				artifactRepository = artifactRepository + '/'
			}
			this.artifactRepository = artifactRepository
		}

		void validate() {
			boms.each {
				it.value.validate()
			}
		}

		void merge(Env other) {
			artifactRepository = other.artifactRepository
			springBootMetadataUrl = other.springBootMetadataUrl
			googleAnalyticsTrackingCode = other.googleAnalyticsTrackingCode
			fallbackApplicationName = other.fallbackApplicationName
			invalidApplicationNames = other.invalidApplicationNames
			forceSsl = other.forceSsl
			kotlin.version = other.kotlin.version
			other.boms.each { id, bom ->
				if (!boms[id]) {
					boms[id] = bom
				}
			}
			other.repositories.each { id, repo ->
				if (!repositories[id]) {
					repositories[id] = repo
				}
			}
		}

		static class Kotlin {

			/**
			 * Kotlin version to use.
			 */
			String version
		}

	}

}
