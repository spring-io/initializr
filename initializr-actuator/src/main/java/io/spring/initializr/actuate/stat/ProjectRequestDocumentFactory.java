/*
 * Copyright 2012-2018 the original author or authors.
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

package io.spring.initializr.actuate.stat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.spring.initializr.generator.ProjectFailedEvent;
import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestEvent;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.util.Agent;

import org.springframework.util.StringUtils;

/**
 * Create {@link ProjectRequestDocument} instances.
 *
 * @author Stephane Nicoll
 */
public class ProjectRequestDocumentFactory {

	private static final Pattern IP_PATTERN = Pattern
			.compile("[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*");

	private final InitializrMetadataProvider metadataProvider;

	public ProjectRequestDocumentFactory(InitializrMetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider;
	}

	public ProjectRequestDocument createDocument(ProjectRequestEvent event) {
		InitializrMetadata metadata = this.metadataProvider.get();
		ProjectRequest request = event.getProjectRequest();

		ProjectRequestDocument document = new ProjectRequestDocument();
		document.setGenerationTimestamp(event.getTimestamp());

		handleCloudFlareHeaders(request, document);
		String candidate = (String) request.getParameters().get("x-forwarded-for");
		if (!StringUtils.hasText(document.getRequestIp()) && candidate != null) {
			document.setRequestIp(candidate);
			document.setRequestIpv4(extractIpv4(candidate));
		}

		Agent agent = extractAgentInformation(request);
		if (agent != null) {
			document.setClientId(agent.getId().getId());
			document.setClientVersion(agent.getVersion());
		}

		document.setGroupId(request.getGroupId());
		document.setArtifactId(request.getArtifactId());
		document.setPackageName(request.getPackageName());
		document.setBootVersion(request.getBootVersion());

		document.setJavaVersion(request.getJavaVersion());
		if (StringUtils.hasText(request.getJavaVersion())
				&& metadata.getJavaVersions().get(request.getJavaVersion()) == null) {
			document.setInvalid(true);
			document.setInvalidJavaVersion(true);
		}

		document.setLanguage(request.getLanguage());
		if (StringUtils.hasText(request.getLanguage())
				&& metadata.getLanguages().get(request.getLanguage()) == null) {
			document.setInvalid(true);
			document.setInvalidLanguage(true);
		}

		document.setPackaging(request.getPackaging());
		if (StringUtils.hasText(request.getPackaging())
				&& metadata.getPackagings().get(request.getPackaging()) == null) {
			document.setInvalid(true);
			document.setInvalidPackaging(true);
		}

		document.setType(request.getType());
		if (StringUtils.hasText(request.getType())
				&& metadata.getTypes().get(request.getType()) == null) {
			document.setInvalid(true);
			document.setInvalidType(true);
		}

		// Let's not rely on the resolved dependencies here
		List<String> dependencies = new ArrayList<>();
		dependencies.addAll(request.getStyle());
		dependencies.addAll(request.getDependencies());
		dependencies.forEach((id) -> {
			if (metadata.getDependencies().get(id) != null) {
				document.getDependencies().add(id);
			}
			else {
				document.setInvalid(true);
				document.getInvalidDependencies().add(id);
			}
		});

		// Let's make sure that the document is flagged as invalid no matter what
		if (event instanceof ProjectFailedEvent) {
			ProjectFailedEvent failed = (ProjectFailedEvent) event;
			document.setInvalid(true);
			if (failed.getCause() != null) {
				document.setErrorMessage(failed.getCause().getMessage());
			}
		}

		return document;
	}

	private static void handleCloudFlareHeaders(ProjectRequest request,
			ProjectRequestDocument document) {
		String candidate = (String) request.getParameters().get("cf-connecting-ip");
		if (StringUtils.hasText(candidate)) {
			document.setRequestIp(candidate);
			document.setRequestIpv4(extractIpv4(candidate));
		}
		String country = (String) request.getParameters().get("cf-ipcountry");
		if (StringUtils.hasText(country) && !"xx".equalsIgnoreCase(country)) {
			document.setRequestCountry(country);
		}
	}

	private static Agent extractAgentInformation(ProjectRequest request) {
		String userAgent = (String) request.getParameters().get("user-agent");
		if (StringUtils.hasText(userAgent)) {
			return Agent.fromUserAgent(userAgent);
		}
		return null;
	}

	private static String extractIpv4(String candidate) {
		if (StringUtils.hasText(candidate)) {
			Matcher matcher = IP_PATTERN.matcher(candidate);
			if (matcher.find()) {
				return matcher.group();
			}
		}
		return null;
	}

}
