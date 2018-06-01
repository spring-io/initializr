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

package io.spring.initializr.service.extension;

import java.util.Arrays;
import java.util.List;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.util.Version;

import org.springframework.stereotype.Component;

/**
 * Validate that the requested java version is compatible with the chosen Spring Boot
 * generation and adapt the request if necessary.
 *
 * @author Stephane Nicoll
 */
@Component
class JavaVersionRequestPostProcessor implements ProjectRequestPostProcessor {

	private static final Version VERSION_2_0_0_M1 = Version.parse("2.0.0.M1");

	private static final Version VERSION_2_0_1 = Version.parse("2.0.1.RELEASE");

	private static final List<String> UNSUPPORTED_LANGUAGES = Arrays.asList("groovy",
			"kotlin");

	@Override
	public void postProcessAfterResolution(ProjectRequest request,
			InitializrMetadata metadata) {
		Integer javaGeneration = determineJavaGeneration(request.getJavaVersion());
		if (javaGeneration == null) {
			return;
		}
		Version requestVersion = Version.safeParse(request.getBootVersion());
		// Not supported for Spring Boot 1.x
		if (VERSION_2_0_0_M1.compareTo(requestVersion) > 0) {
			request.setJavaVersion("1.8");
		}
		// Not supported for Kotlin & Groovy
		if (UNSUPPORTED_LANGUAGES.contains(request.getLanguage())) {
			request.setJavaVersion("1.8");
		}
		// 10 support only as of 2.0.1
		if (javaGeneration == 10 && VERSION_2_0_1.compareTo(requestVersion) > 0) {
			request.setJavaVersion("1.8");
		}
	}

	private Integer determineJavaGeneration(String javaVersion) {
		try {
			int generation = Integer.valueOf(javaVersion);
			return ((generation > 8 && generation <= 10) ? generation : null);
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}

}
