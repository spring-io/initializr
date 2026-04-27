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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.spring.initializr.generator.project.JvmVersionAdjustment;
import io.spring.initializr.generator.project.ProjectDescription;
import org.jspecify.annotations.Nullable;

import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

/**
 * {@link HelpDocumentCustomizer} that documents {@link JvmVersionAdjustment}s recorded on
 * the {@link ProjectDescription}.
 *
 * @author Moritz Halbritter
 */
public class JvmVersionAdjustmentsHelpDocumentCustomizer implements HelpDocumentCustomizer {

	private final ProjectDescription description;

	public JvmVersionAdjustmentsHelpDocumentCustomizer(ProjectDescription description) {
		this.description = description;
	}

	@Override
	public void customize(HelpDocument document) {
		List<JvmVersionAdjustment> adjustments = this.description.getJvmVersionAdjustments();
		if (adjustments.isEmpty()) {
			return;
		}
		Map<String, Object> model = new HashMap<>();
		model.put("items", adjustments.stream().map(this::toBulletLine).toList());
		document.addSection("documentation/jvm-version-adjustments", model);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 10;
	}

	private String toBulletLine(JvmVersionAdjustment adjustment) {
		String from = adjustment.fromVersion();
		String to = adjustment.toVersion();
		return switch (adjustment.reason()) {
			case SPRING_BOOT ->
				"The JVM level was changed from %s to %s to match the requirements of the selected Spring Boot version."
					.formatted(from, to);
			case KOTLIN_COMPILER -> formatKotlin(from, to, adjustment.detail());
			case SELECTED_DEPENDENCY -> formatDependency(from, to, adjustment.dependencyId(), adjustment.detail());
		};
	}

	private String formatKotlin(String from, String to, @Nullable String kotlinVersion) {
		if (StringUtils.hasText(kotlinVersion)) {
			return ("The JVM level was changed from %s to %s because Kotlin %s does not support the previously selected JVM level for this project.")
				.formatted(from, to, kotlinVersion);
		}
		return ("The JVM level was changed from %s to %s because the selected Kotlin version does not support the previously selected JVM level for this project.")
			.formatted(from, to);
	}

	private String formatDependency(String from, String to, @Nullable String dependencyId, @Nullable String detail) {
		String label = firstNonEmpty(detail, dependencyId);
		if (StringUtils.hasText(label)) {
			return ("The JVM level was changed from %s to %s because the dependency \"%s\" requires a newer JVM level.")
				.formatted(from, to, label);
		}
		return ("The JVM level was changed from %s to %s because a selected dependency requires a newer JVM level.")
			.formatted(from, to);
	}

	private static @Nullable String firstNonEmpty(@Nullable String a, @Nullable String b) {
		if (StringUtils.hasText(a)) {
			return a;
		}
		if (StringUtils.hasText(b)) {
			return b;
		}
		return null;
	}

}
