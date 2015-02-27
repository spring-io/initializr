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

/**
 * Various configuration options used by the service.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class InitializrConfiguration {

	final Env env = new Env()

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
	 * Defines additional environment settings
	 */
	static class Env {

		/**
		 * The url of the repository servicing distribution bundle
		 */
		String artifactRepository = 'https://repo.spring.io/release/'

		/**
		 * The meta-data url of the Spring Boot project
		 */
		String springBootMetadataUrl = 'https://spring.io/project_metadata/spring-boot'

		/**
		 * The application name to use if none could be generated.
		 */
		String fallbackApplicationName = 'Application'

		/**
		 * The list of invalid application names. If such name is chosen or generated,
		 * the {@link #fallbackApplicationName} should be used instead.
		 */
		List<String> invalidApplicationNames = [
				'SpringApplication',
				'SpringBootApplication'
		]

		/**
		 * Force SSL support. When enabled, any access using http generate https links.
		 */
		boolean forceSsl = true

		void setArtifactRepository(String artifactRepository) {
			if (!artifactRepository.endsWith('/')) {
				artifactRepository = artifactRepository + '/'
			}
			this.artifactRepository = artifactRepository
		}

	}
}
