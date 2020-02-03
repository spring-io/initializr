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

class MavenReportPluginContainerTests {

	@Test
	void addReportPlugin() {
		MavenReportPluginContainer reportPluginContainer = new MavenReportPluginContainer();
		reportPluginContainer.add("com.example", "demo");
		assertThat(reportPluginContainer.values()).hasOnlyOneElementSatisfying((reportPlugin) -> {
			assertThat(reportPlugin.getGroupId()).isEqualTo("com.example");
			assertThat(reportPlugin.getArtifactId()).isEqualTo("demo");
			assertThat(reportPlugin.getConfiguration()).isNull();
			assertThat(reportPlugin.getInherited()).isNull();
			assertThat(reportPlugin.getVersion()).isNull();
			assertThat(reportPlugin.getReportSets()).isNull();
		});
	}

	@Test
	void addReportPluginWithConsumer() {
		MavenReportPluginContainer reportPluginContainer = new MavenReportPluginContainer();
		reportPluginContainer.add("com.example", "demo",
				(reportPlugin) -> reportPlugin.configuration((conf) -> conf.add("property1", "value1"))
						.inherited("inherited1").version("version1")
						.reportSets((reportSets) -> reportSets.add("reportSet1").add("reportSet2")));

		assertThat(reportPluginContainer.values()).hasOnlyOneElementSatisfying((reportPlugin) -> {
			assertThat(reportPlugin.getGroupId()).isEqualTo("com.example");
			assertThat(reportPlugin.getArtifactId()).isEqualTo("demo");
			assertThat(reportPlugin.getConfiguration().getSettings()).hasOnlyOneElementSatisfying((settings) -> {
				assertThat(settings.getName()).isEqualTo("property1");
				assertThat(settings.getValue()).isEqualTo("value1");
			});
			assertThat(reportPlugin.getInherited()).isEqualTo("inherited1");
			assertThat(reportPlugin.getVersion()).isEqualTo("version1");
			assertThat(reportPlugin.getReportSets().has("reportSet1")).isTrue();
			assertThat(reportPlugin.getReportSets().has("reportSet2")).isTrue();
		});
	}

	@Test
	void addReportPluginSeveralTimeReuseConfiguration() {
		MavenReportPluginContainer reportPluginContainer = new MavenReportPluginContainer();
		reportPluginContainer.add("com.example", "demo", (reportPlugin) -> reportPlugin.inherited("inherited1"));
		reportPluginContainer.add("com.example", "demo", (reportPlugin) -> reportPlugin.inherited("inherited2"));
		assertThat(reportPluginContainer.values()).hasOnlyOneElementSatisfying((reportPlugin) -> {
			assertThat(reportPlugin.getGroupId()).isEqualTo("com.example");
			assertThat(reportPlugin.getArtifactId()).isEqualTo("demo");
			assertThat(reportPlugin.getConfiguration()).isNull();
			assertThat(reportPlugin.getInherited()).isEqualTo("inherited2");
			assertThat(reportPlugin.getVersion()).isNull();
			assertThat(reportPlugin.getReportSets()).isNull();
		});
	}

	@Test
	void isEmptyWithEmptyContainer() {
		MavenReportPluginContainer reportPluginContainer = new MavenReportPluginContainer();
		assertThat(reportPluginContainer.isEmpty()).isTrue();
	}

	@Test
	void isEmptyWithRegisteredReportPlugin() {
		MavenReportPluginContainer reportPluginContainer = new MavenReportPluginContainer();
		reportPluginContainer.add("com.example", "demo");
		assertThat(reportPluginContainer.isEmpty()).isFalse();
	}

	@Test
	void hasReportPluginWithMatchingReportPlugin() {
		MavenReportPluginContainer reportPluginContainer = new MavenReportPluginContainer();
		reportPluginContainer.add("com.example", "demo");
		assertThat(reportPluginContainer.has("com.example", "demo")).isTrue();
	}

	@Test
	void hasReportPluginWithNonMatchingReportPlugin() {
		MavenReportPluginContainer reportPluginContainer = new MavenReportPluginContainer();
		reportPluginContainer.add("com.example", "demo");
		assertThat(reportPluginContainer.has("com.example", "demo1")).isFalse();
	}

	@Test
	void removeWithMatchingReportPlugin() {
		MavenReportPluginContainer reportPluginContainer = new MavenReportPluginContainer();
		reportPluginContainer.add("com.example", "demo");
		assertThat(reportPluginContainer.remove("com.example", "demo")).isTrue();
		assertThat(reportPluginContainer.isEmpty()).isTrue();
	}

	@Test
	void removeWithNonMatchingReportPlugin() {
		MavenReportPluginContainer reportPluginContainer = new MavenReportPluginContainer();
		reportPluginContainer.add("com.example", "demo");
		assertThat(reportPluginContainer.remove("com.example", "demo2")).isFalse();
		assertThat(reportPluginContainer.isEmpty()).isFalse();
	}

}
