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

package io.spring.initializr.actuate.metric

import io.spring.initializr.generator.ProjectFailedEvent
import io.spring.initializr.generator.ProjectGeneratedEvent
import io.spring.initializr.generator.ProjectRequest
import io.spring.initializr.util.Agent

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.metrics.CounterService
import org.springframework.context.event.EventListener
import org.springframework.util.StringUtils

/**
 * A {@link ProjectGeneratedEvent} listener that uses a {@link CounterService} to update
 * various project related metrics.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class ProjectGenerationMetricsListener {

	private final CounterService counterService

	@Autowired
	ProjectGenerationMetricsListener(CounterService counterService) {
		this.counterService = counterService
	}

	@EventListener
	void onGeneratedProject(ProjectGeneratedEvent event) {
		handleProjectRequest(event.projectRequest)
	}

	@EventListener
	void onFailedProject(ProjectFailedEvent event) {
		handleProjectRequest(event.projectRequest)
		increment(key('failures'))
	}

	protected void handleProjectRequest(ProjectRequest request) {
		increment(key('requests')) // Total number of requests
		handleDependencies(request)
		handleType(request)
		handleJavaVersion(request)
		handlePackaging(request)
		handleLanguage(request)
		handleBootVersion(request)
		handleUserAgent(request)
	}

	protected void handleDependencies(ProjectRequest request) {
		request.resolvedDependencies.each {
			if (!ProjectRequest.DEFAULT_STARTER.equals(it.id)) {
				def id = sanitize(it.id)
				increment(key("dependency.$id"))
			}
		}
	}

	protected void handleType(ProjectRequest request) {
		if (StringUtils.hasText(request.type)) {
			def type = sanitize(request.type)
			increment(key("type.$type"))
		}
	}

	protected void handleJavaVersion(ProjectRequest request) {
		if (StringUtils.hasText(request.javaVersion)) {
			def javaVersion = sanitize(request.javaVersion)
			increment(key("java_version.$javaVersion"))
		}
	}

	protected void handlePackaging(ProjectRequest request) {
		if (StringUtils.hasText(request.packaging)) {
			def packaging = sanitize(request.packaging)
			increment(key("packaging.$packaging"))
		}
	}

	protected void handleLanguage(ProjectRequest request) {
		if (StringUtils.hasText(request.language)) {
			def language = sanitize(request.language)
			increment(key("language.$language"))
		}
	}

	protected void handleBootVersion(ProjectRequest request) {
		if (StringUtils.hasText(request.bootVersion)) {
			def bootVersion = sanitize(request.bootVersion)
			increment(key("boot_version.$bootVersion"))
		}
	}

	protected void handleUserAgent(ProjectRequest request) {
		String userAgent = request.parameters['user-agent']
		if (userAgent) {
			Agent agent = Agent.fromUserAgent(userAgent)
			if (agent) {
				increment(key("client_id.$agent.id.id"))
			}
		}
	}

	protected void increment(String key) {
		counterService.increment(key)
	}

	protected String key(String part) {
		"initializr.$part"
	}

	protected String sanitize(String s) {
		s.replace('.', '_')
	}

}
