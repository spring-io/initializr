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

package io.spring.initializr.actuate.stat

import java.util.regex.Matcher
import java.util.regex.Pattern

import io.spring.initializr.generator.ProjectFailedEvent
import io.spring.initializr.generator.ProjectRequest
import io.spring.initializr.generator.ProjectRequestEvent
import io.spring.initializr.metadata.InitializrMetadataProvider
import io.spring.initializr.util.Agent

/**
 * Create {@link ProjectRequestDocument} instances.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class ProjectRequestDocumentFactory {

	private static final IP_PATTERN = Pattern.compile("[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*")

	private final InitializrMetadataProvider metadataProvider

	ProjectRequestDocumentFactory(InitializrMetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider
	}

	ProjectRequestDocument createDocument(ProjectRequestEvent event) {
		def metadata = metadataProvider.get()
		def request = event.projectRequest

		ProjectRequestDocument document = new ProjectRequestDocument()
		document.generationTimestamp = event.timestamp

		handleCloudFlareHeaders(request, document)
		def candidate = request.parameters['x-forwarded-for']
		if (!document.requestIp && candidate) {
			document.requestIp = candidate
			document.requestIpv4 = extractIpv4(candidate)
		}

		Agent agent = extractAgentInformation(request)
		if (agent) {
			document.clientId = agent.id.id
			document.clientVersion = agent.version
		}

		document.groupId = request.groupId
		document.artifactId = request.artifactId
		document.packageName = request.packageName
		document.bootVersion = request.bootVersion

		document.javaVersion = request.javaVersion
		if (request.javaVersion && !metadata.javaVersions.get(request.javaVersion)) {
			document.invalid = true
			document.invalidJavaVersion = true
		}

		document.language = request.language
		if (request.language && !metadata.languages.get(request.language)) {
			document.invalid = true
			document.invalidLanguage = true
		}

		document.packaging = request.packaging
		if (request.packaging && !metadata.packagings.get(request.packaging)) {
			document.invalid = true
			document.invalidPackaging = true
		}

		document.type = request.type
		if (request.type && !metadata.types.get(request.type)) {
			document.invalid = true
			document.invalidType = true
		}

		// Let's not rely on the resolved dependencies here
		def dependencies = []
		dependencies.addAll(request.style)
		dependencies.addAll(request.dependencies)
		dependencies.each { id ->
			if (metadata.dependencies.get(id)) {
				document.dependencies << id
			} else {
				document.invalid = true
				document.invalidDependencies << id
			}
		}

		// Let's make sure that the document is flagged as invalid no matter what
		if (event instanceof ProjectFailedEvent) {
			document.invalid = true
			if (event.cause) {
				document.errorMessage = event.cause.message
			}
		}

		document
	}

	private static void handleCloudFlareHeaders(ProjectRequest request, ProjectRequestDocument document) {
		def candidate = request.parameters['cf-connecting-ip']
		if (candidate) {
			document.requestIp = candidate
			document.requestIpv4 = extractIpv4(candidate)
		}
		String country = request.parameters['cf-ipcountry']
		if (country && !country.toLowerCase().equals('xx')) {
			document.requestCountry = country
		}
	}

	private static Agent extractAgentInformation(ProjectRequest request) {
		String userAgent = request.parameters['user-agent']
		if (userAgent) {
			return Agent.fromUserAgent(userAgent)
		}
		return null
	}

	private static String extractIpv4(def candidate) {
		if (candidate) {
			Matcher matcher = IP_PATTERN.matcher(candidate)
			if (matcher.find()) {
				return matcher.group()
			}
		}
		return null
	}

}
