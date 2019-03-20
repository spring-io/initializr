/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import java.util.stream.Collectors;

import io.spring.initializr.actuate.stat.ProjectRequestDocument.ClientInformation;
import io.spring.initializr.actuate.stat.ProjectRequestDocument.DependencyInformation;
import io.spring.initializr.actuate.stat.ProjectRequestDocument.ErrorStateInformation;
import io.spring.initializr.actuate.stat.ProjectRequestDocument.VersionInformation;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.project.ProjectFailedEvent;
import io.spring.initializr.web.project.ProjectRequest;
import io.spring.initializr.web.project.ProjectRequestEvent;
import io.spring.initializr.web.project.WebProjectRequest;
import io.spring.initializr.web.support.Agent;

import org.springframework.util.StringUtils;

/**
 * Create {@link ProjectRequestDocument} instances.
 *
 * @author Stephane Nicoll
 */
public class ProjectRequestDocumentFactory {

	public ProjectRequestDocument createDocument(ProjectRequestEvent event) {
		InitializrMetadata metadata = event.getMetadata();
		ProjectRequest request = event.getProjectRequest();
		ProjectRequestDocument document = new ProjectRequestDocument();
		document.setGenerationTimestamp(event.getTimestamp());
		document.setGroupId(request.getGroupId());
		document.setArtifactId(request.getArtifactId());
		document.setPackageName(request.getPackageName());
		document.setVersion(determineVersionInformation(request));
		document.setClient(determineClientInformation(request));

		document.setJavaVersion(request.getJavaVersion());
		if (StringUtils.hasText(request.getJavaVersion())
				&& metadata.getJavaVersions().get(request.getJavaVersion()) == null) {
			document.triggerError().setJavaVersion(true);
		}

		document.setLanguage(request.getLanguage());
		if (StringUtils.hasText(request.getLanguage())
				&& metadata.getLanguages().get(request.getLanguage()) == null) {
			document.triggerError().setLanguage(true);
		}

		document.setPackaging(request.getPackaging());
		if (StringUtils.hasText(request.getPackaging())
				&& metadata.getPackagings().get(request.getPackaging()) == null) {
			document.triggerError().setPackaging(true);
		}

		document.setType(request.getType());
		document.setBuildSystem(determineBuildSystem(request));
		if (StringUtils.hasText(request.getType())
				&& metadata.getTypes().get(request.getType()) == null) {
			document.triggerError().setType(true);
		}

		// Let's not rely on the resolved dependencies here
		List<String> dependencies = new ArrayList<>();
		dependencies.addAll(request.getStyle());
		dependencies.addAll(request.getDependencies());
		List<String> validDependencies = dependencies.stream()
				.filter((id) -> metadata.getDependencies().get(id) != null)
				.collect(Collectors.toList());
		document.setDependencies(new DependencyInformation(validDependencies));
		List<String> invalidDependencies = dependencies.stream()
				.filter((id) -> (!validDependencies.contains(id)))
				.collect(Collectors.toList());
		if (!invalidDependencies.isEmpty()) {
			document.triggerError().triggerInvalidDependencies(invalidDependencies);
		}

		// Let's make sure that the document is flagged as invalid no matter what
		if (event instanceof ProjectFailedEvent) {
			ErrorStateInformation errorState = document.triggerError();
			ProjectFailedEvent failed = (ProjectFailedEvent) event;
			if (failed.getCause() != null) {
				errorState.setMessage(failed.getCause().getMessage());
			}
		}
		return document;
	}

	private String determineBuildSystem(ProjectRequest request) {
		String type = request.getType();
		String[] elements = type.split("-");
		return (elements.length == 2) ? elements[0] : null;
	}

	private VersionInformation determineVersionInformation(ProjectRequest request) {
		Version version = Version.safeParse(request.getBootVersion());
		if (version != null && version.getMajor() != null) {
			return new VersionInformation(version);
		}
		return null;
	}

	private ClientInformation determineClientInformation(ProjectRequest request) {
		if (request instanceof WebProjectRequest) {
			WebProjectRequest webProjectRequest = (WebProjectRequest) request;
			Agent agent = determineAgent(webProjectRequest);
			String ip = determineIp(webProjectRequest);
			String country = determineCountry(webProjectRequest);
			if (agent != null || ip != null || country != null) {
				return new ClientInformation(agent, ip, country);
			}
		}
		return null;
	}

	private Agent determineAgent(WebProjectRequest request) {
		String userAgent = (String) request.getParameters().get("user-agent");
		if (StringUtils.hasText(userAgent)) {
			return Agent.fromUserAgent(userAgent);
		}
		return null;
	}

	private String determineIp(WebProjectRequest request) {
		String candidate = (String) request.getParameters().get("cf-connecting-ip");
		return (StringUtils.hasText(candidate)) ? candidate
				: (String) request.getParameters().get("x-forwarded-for");
	}

	private String determineCountry(WebProjectRequest request) {
		String candidate = (String) request.getParameters().get("cf-ipcountry");
		if (StringUtils.hasText(candidate) && !"xx".equalsIgnoreCase(candidate)) {
			return candidate;
		}
		return null;
	}

}
