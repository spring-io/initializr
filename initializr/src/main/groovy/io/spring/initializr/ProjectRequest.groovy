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

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A request to generate a project.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @since 1.0
 */
class ProjectRequest {

	private static final Logger logger = LoggerFactory.getLogger(ProjectRequest.class)

	def style = []

	String name
	String type
	String description
	String groupId
	String artifactId
	String version
	String bootVersion
	String packaging
	String language
	String packageName
	String javaVersion

	def dependencies = []

	/**
	 * Resolve this instance against the specified {@link InitializrMetadata}
	 */
	void resolve(InitializrMetadata metadata) {
		if (packaging == 'war' && !isWebStyle()) {
			style << 'web'
		}
		if (style == null || style.size() == 0) {
			style = []
		}
		if (!style.class.isArray() && !(style instanceof Collection)) {
			style = [style]
		}
		dependencies = style.collect {
			InitializrMetadata.Dependency dependency = metadata.getDependency(it)
			if (dependency == null) {
				if (it.contains(':')) {
					throw new IllegalArgumentException('Unknown dependency ' + it + ' check project metadata')
				}
				logger.warn('No known dependency for style ' + it + ' assuming spring-boot-starter')
				dependency = new InitializrMetadata.Dependency()
				dependency.asSpringBootStarter(it)
			}
			dependency
		}
	}

	boolean isWebStyle() {
		style.any { webStyle(it) }
	}

	private boolean webStyle(String style) {
		style.contains('web') || style.contains('thymeleaf') || style.contains('freemarker') || style.contains('velocity') || style.contains('groovy-template')
	}

}
