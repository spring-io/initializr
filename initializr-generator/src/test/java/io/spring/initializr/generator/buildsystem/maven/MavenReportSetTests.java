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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class MavenReportSetTests {

	@Test
	void reportSetWithIdOnly() {
		MavenReportSet reportSet = new MavenReportSet.Builder("id").build();
		assertThat(reportSet.getId()).isEqualTo("id");
		assertThat(reportSet.getConfiguration()).isNull();
		assertThat(reportSet.getInherited()).isNull();
		assertThat(reportSet.getReports()).isNull();
	}

	@Test
	void reportSetWithFullData() {
		MavenReportSet reportSet = new MavenReportSet.Builder("id")
				.configuration(conf -> conf.add("property1", "value1")).inherited("inherited1").report("report1")
				.report("report2").build();

		assertThat(reportSet.getId()).isEqualTo("id");
		assertThat(reportSet.getConfiguration().getSettings()).hasOnlyOneElementSatisfying(settings -> {
			assertThat(settings.getName()).isEqualTo("property1");
			assertThat(settings.getValue()).isEqualTo("value1");
		});
		assertThat(reportSet.getInherited()).isEqualTo("inherited1");
		assertThat(reportSet.getReports()).isEqualTo(Arrays.asList("report1", "report2"));
	}

}
