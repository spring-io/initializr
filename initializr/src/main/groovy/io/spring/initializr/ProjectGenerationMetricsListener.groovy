/*
 * Copyright 2012-2014 the original author or authors.
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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.metrics.CounterService
import org.springframework.util.StringUtils

/**
 * A {@link ProjectGenerationListener} implementation that uses a {@link CounterService}
 * to update various project related metrics.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class ProjectGenerationMetricsListener implements ProjectGenerationListener {

	private final CounterService counterService

	@Autowired
	ProjectGenerationMetricsListener(CounterService counterService) {
		this.counterService = counterService
	}

	@Override
	void onGeneratedProject(ProjectRequest request) {
		increment(key('requests')) // Total number of requests
		handleDependencies(request)
		handleType(request)
		handleJavaVersion(request)
		handlePackaging(request)
		handleLanguage(request)
		handleBootVersion(request)
	}

	protected void handleDependencies(ProjectRequest request) {
		request.dependencies.each {
			increment(key('dependency.' + sanitize(it.id)))
		}
	}

	protected void handleType(ProjectRequest request) {
		if (StringUtils.hasText(request.type)) {
			increment(key('type.' + sanitize(request.type)))
		}
	}

	protected void handleJavaVersion(ProjectRequest request) {
		if (StringUtils.hasText(request.javaVersion)) {
			increment(key('java_version.' + sanitize(request.javaVersion)))
		}
	}

	protected void handlePackaging(ProjectRequest request) {
		if (StringUtils.hasText(request.packaging)) {
			increment(key('packaging.' + sanitize(request.packaging)))
		}
	}

	protected void handleLanguage(ProjectRequest request) {
		if (StringUtils.hasText(request.language)) {
			increment(key('language.' + sanitize(request.language)))
		}
	}

	protected void handleBootVersion(ProjectRequest request) {
		if (StringUtils.hasText(request.bootVersion)) {
			increment(key('boot_version.' + sanitize(request.bootVersion)))
		}
	}

	protected void increment(String key) {
		counterService.increment(key)
	}

	protected String key(String part) {
		'initializr.' + part
	}

	protected String sanitize(String s) {
		s.replace('.', '_')
	}

}
