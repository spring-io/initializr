/*
 * Copyright 2012-2017 the original author or authors.
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

package io.spring.initializr.service.extension;

import java.util.Arrays;
import java.util.List;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.util.Version;

import org.springframework.stereotype.Component;

/**
 * Handle corner cases when Java9 is required.
 *
 * @author Stephane Nicoll
 */
@Component
class Java9RequestPostProcessor implements ProjectRequestPostProcessor {

	private static final Version VERSION_2_0_0_M1 = Version.parse("2.0.0.M1");

	private static final List<String> UNSUPPORTED_LANGUAGES = Arrays.asList("groovy", "kotlin");

	@Override
	public void postProcessAfterResolution(ProjectRequest request, InitializrMetadata metadata) {
		Version requestVersion = Version.safeParse(request.getBootVersion());
		if (!"9".equals(request.getJavaVersion())) {
			return;
		}
		// Not supported for Spring Boot 1.x
		if (VERSION_2_0_0_M1.compareTo(requestVersion) > 0) {
			request.setJavaVersion("1.8");
		}
		// Not supported for Kotlin & Groovy
		if (UNSUPPORTED_LANGUAGES.contains(request.getLanguage())) {
			request.setJavaVersion("1.8");
		}
	}

}
