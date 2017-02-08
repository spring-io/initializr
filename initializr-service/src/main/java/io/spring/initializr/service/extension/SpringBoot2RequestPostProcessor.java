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

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessorAdapter;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.util.Version;

import org.springframework.stereotype.Component;

/**
 * As of Spring Boot 2.0, Java8 is mandatory so this extension makes sure that the
 * java version is forced.
 *
 * @author Stephane Nicoll
 */
@Component
class SpringBoot2RequestPostProcessor extends ProjectRequestPostProcessorAdapter {

	private static final Version VERSION_2_0_0_M1 = Version.parse("2.0.0.M1");

	@Override
	public void postProcessAfterResolution(ProjectRequest request, InitializrMetadata metadata) {
		Version requestVersion = Version.safeParse(request.getBootVersion());
		if (VERSION_2_0_0_M1.compareTo(requestVersion) <= 0) {
			request.setJavaVersion("1.8");
		}
	}

}
