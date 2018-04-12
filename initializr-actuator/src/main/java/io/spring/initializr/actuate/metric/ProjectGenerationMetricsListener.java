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

package io.spring.initializr.actuate.metric;

import java.util.List;

import io.micrometer.core.instrument.MeterRegistry;
import io.spring.initializr.generator.ProjectFailedEvent;
import io.spring.initializr.generator.ProjectGeneratedEvent;
import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.util.Agent;

import org.springframework.context.event.EventListener;
import org.springframework.util.StringUtils;

/**
 * A {@link ProjectGeneratedEvent} listener that uses a {@link MeterRegistry} to update
 * various project related metrics.
 *
 * @author Stephane Nicoll
 */
public class ProjectGenerationMetricsListener {

	private final MeterRegistry meterRegistry;

	public ProjectGenerationMetricsListener(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	@EventListener
	public void onGeneratedProject(ProjectGeneratedEvent event) {
		handleProjectRequest(event.getProjectRequest());
	}

	@EventListener
	public void onFailedProject(ProjectFailedEvent event) {
		handleProjectRequest(event.getProjectRequest());
		increment(key("failures"));
	}

	protected void handleProjectRequest(ProjectRequest request) {
		increment(key("requests")); // Total number of requests
		handleDependencies(request);
		handleType(request);
		handleJavaVersion(request);
		handlePackaging(request);
		handleLanguage(request);
		handleBootVersion(request);
		handleUserAgent(request);
	}

	protected void handleDependencies(ProjectRequest request) {
		List<Dependency> dependencies = request.getResolvedDependencies();
		if (dependencies != null) {
			dependencies.forEach((it) -> {
				if (!ProjectRequest.DEFAULT_STARTER.equals(it.getId())) {
					String id = sanitize(it.getId());
					increment(key("dependency." + id));
				}
			});
		}
	}

	protected void handleType(ProjectRequest request) {
		if (StringUtils.hasText(request.getType())) {
			String type = sanitize(request.getType());
			increment(key("type." + type));
		}
	}

	protected void handleJavaVersion(ProjectRequest request) {
		if (StringUtils.hasText(request.getJavaVersion())) {
			String javaVersion = sanitize(request.getJavaVersion());
			increment(key("java_version." + javaVersion));
		}
	}

	protected void handlePackaging(ProjectRequest request) {
		if (StringUtils.hasText(request.getPackaging())) {
			String packaging = sanitize(request.getPackaging());
			increment(key("packaging." + packaging));
		}
	}

	protected void handleLanguage(ProjectRequest request) {
		if (StringUtils.hasText(request.getLanguage())) {
			String language = sanitize(request.getLanguage());
			increment(key("language." + language));
		}
	}

	protected void handleBootVersion(ProjectRequest request) {
		if (StringUtils.hasText(request.getBootVersion())) {
			String bootVersion = sanitize(request.getBootVersion());
			increment(key("boot_version." + bootVersion));
		}
	}

	protected void handleUserAgent(ProjectRequest request) {
		String userAgent = (String) request.getParameters().get("user-agent");
		if (userAgent != null) {
			Agent agent = Agent.fromUserAgent(userAgent);
			if (agent != null) {
				increment(key("client_id." + agent.getId().getId()));
			}
		}
	}

	protected void increment(String key) {
		this.meterRegistry.counter(key).increment();
	}

	protected String key(String part) {
		return "initializr." + part;
	}

	protected String sanitize(String s) {
		return s.replace(".", "_");
	}

}
