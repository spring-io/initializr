/*
 * Copyright 2012 - present the original author or authors.
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

package io.spring.initializr.generator.spring.documentation;

import io.spring.initializr.generator.project.JvmVersionChangeReason;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;

import org.springframework.context.annotation.Bean;

/**
 * Dummy project generation configuration for manually checking JVM version change
 * reasons.
 *
 * @author Sijun Yang
 */
@ProjectGenerationConfiguration
public class DummyJvmVersionChangeReasonProjectGenerationConfiguration {

	private static final JvmVersionChangeReason DUMMY_WEB_DEPENDENCY_BASELINE = () -> "dummy-web-dependency-baseline";

	@Bean
	HelpDocumentCustomizer dummyJvmVersionChangeReasonHelpDocumentCustomizer(ProjectDescription description) {
		return (document) -> addDummyJvmVersionChangeWarning(description, document);
	}

	private void addDummyJvmVersionChangeWarning(ProjectDescription description, HelpDocument document) {
		boolean hasDummyWebDependencyBaseline = description.getJvmVersionChangeReasons()
			.stream()
			.map(JvmVersionChangeReason::id)
			.anyMatch(DUMMY_WEB_DEPENDENCY_BASELINE.id()::equals);
		if (hasDummyWebDependencyBaseline) {
			document.getWarnings()
				.addItem("Dummy JVM version change reason: Java version was changed to 17 because the Web "
						+ "dependency requires Java 17.");
		}
	}

}
