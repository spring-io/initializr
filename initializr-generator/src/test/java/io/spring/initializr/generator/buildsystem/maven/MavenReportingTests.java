/*
 * Copyright 2012-2020 the original author or authors.
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

package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MavenReportingTests {

	@Test
	void reportingEmpty() {
		MavenReporting reporting = new MavenReporting.Builder().build();
		assertThat(reporting.getOutputDirectory()).isNull();
		assertThat(reporting.isExcludeDefaults()).isNull();
		assertThat(reporting.getReportPlugins()).isNull();
	}

	@Test
	void reportingWithFullData() {
		MavenReporting reporting = new MavenReporting.Builder().excludeDefaults(true).outputDirectory("output")
				.reportPlugins((reportPlugins) -> reportPlugins.add("com.example", "demo").add("com.example", "demo2"))
				.build();

		assertThat(reporting.isExcludeDefaults()).isTrue();
		assertThat(reporting.getOutputDirectory()).isEqualTo("output");
		assertThat(reporting.getReportPlugins().has("com.example", "demo")).isTrue();
		assertThat(reporting.getReportPlugins().has("com.example", "demo2")).isTrue();
	}

}
