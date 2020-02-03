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

class MavenReportPluginTests {

	@Test
	void reportPluginWithGroupIdArtifactIdOnly() {
		MavenReportPlugin reportPlugin = new MavenReportPlugin.Builder("com.example", "demo").build();

		assertThat(reportPlugin.getGroupId()).isEqualTo("com.example");
		assertThat(reportPlugin.getArtifactId()).isEqualTo("demo");
		assertThat(reportPlugin.getConfiguration()).isNull();
		assertThat(reportPlugin.getInherited()).isNull();
		assertThat(reportPlugin.getVersion()).isNull();
		assertThat(reportPlugin.getReportSets()).isNull();
	}

	@Test
	void reportPluginWithFullData() {
		MavenReportPlugin reportPlugin = new MavenReportPlugin.Builder("com.example", "demo")
				.configuration(conf -> conf.add("property1", "value1")).inherited("inherited1").version("version1")
				.reportSets(reportSets -> reportSets.add("reportSet1").add("reportSet2")).build();

		assertThat(reportPlugin.getGroupId()).isEqualTo("com.example");
		assertThat(reportPlugin.getArtifactId()).isEqualTo("demo");
		assertThat(reportPlugin.getConfiguration().getSettings()).hasOnlyOneElementSatisfying(settings -> {
			assertThat(settings.getName()).isEqualTo("property1");
			assertThat(settings.getValue()).isEqualTo("value1");
		});
		assertThat(reportPlugin.getInherited()).isEqualTo("inherited1");
		assertThat(reportPlugin.getVersion()).isEqualTo("version1");
		assertThat(reportPlugin.getReportSets().has("reportSet1")).isTrue();
		assertThat(reportPlugin.getReportSets().has("reportSet2")).isTrue();
	}

}
