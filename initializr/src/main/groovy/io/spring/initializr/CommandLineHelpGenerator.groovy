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

import static io.spring.initializr.support.GroovyTemplate.template

/**
 * Generate help pages for command-line clients.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class CommandLineHelpGenerator {

	private static final String logo = '''
  .   ____          _            __ _ _
 /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\
( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\
 \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 '''

	/**
	 * Generate the capabilities of the service as a generic plain text
	 * document. Used when no particular agent was detected.
	 */
	String generateGenericCapabilities(InitializrMetadata metadata, String serviceUrl) {
		def model = initializeModel(metadata, serviceUrl)
		model['hasExamples'] = false
		doGenerateCapabilities(model)
	}


	/**
	 * Generate the capabilities of the service using "curl" as a plain text
	 * document.
	 */
	String generateCurlCapabilities(InitializrMetadata metadata, String serviceUrl) {
		def model = initializeModel(metadata, serviceUrl)
		model['examples'] = template 'curl-examples.txt', model
		model['hasExamples'] = true
		doGenerateCapabilities(model)
	}

	/**
	 * Generate the capabilities of the service using "HTTPie" as a plain text
	 * document.
	 */
	String generateHttpieCapabilities(InitializrMetadata metadata, String serviceUrl) {
		def model = initializeModel(metadata, serviceUrl)
		model['examples'] = template 'httpie-examples.txt', model
		model['hasExamples'] = true
		doGenerateCapabilities(model)
	}

	private doGenerateCapabilities(def model) {
		template 'cli-capabilities.txt', model
	}


	private Map initializeModel(InitializrMetadata metadata, serviceUrl) {
		Map model = [:]
		model['logo'] = logo
		model['serviceUrl'] = serviceUrl

		Map dependencies = [:]
		new ArrayList(metadata.allDependencies).sort { a, b -> a.id <=> b.id }.each {
			String description = it.name
			if (it.description) {
				description += ": $it.description"
			}
			dependencies[it.id] = description
		}
		model['dependencies'] = dependencies

		Map types = [:]

		new ArrayList<>(metadata.types).sort { a, b -> a.id <=> b.id }.each {
			String description = it.description
			if (!description) {
				description = it.name
			}
			types[it.id] = description
		}
		model['types'] = types

		Map defaults = [:]
		metadata.defaults.properties.sort().each {
			if (!(it.key in ['class', 'metaClass', 'DEFAULT_NAME'])) {
				defaults[it.key] = it.value
			}
		}
		defaults['applicationName'] = ProjectRequest.generateApplicationName(metadata.defaults.name,
				ProjectRequest.DEFAULT_APPLICATION_NAME)
		model['defaults'] = defaults

		model
	}

}
