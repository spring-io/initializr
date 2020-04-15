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

/**
 * Tests for {@link MavenScm}.
 *
 * @author Joachim Pasquali
 */
public class MavenScmTest {

	@Test
	void isEmptyWithNoData() {
		MavenScm mavenScm = new MavenScm.Builder().build();
		assertThat(mavenScm.isEmpty()).isTrue();
	}

	@Test
	void isEmptyWithData() {
		MavenScm mavenScm = new MavenScm.Builder().connection("some-connection").build();
		assertThat(mavenScm.isEmpty()).isFalse();
	}

	@Test
	void allElementsTest() {
		MavenScm mavenScm = new MavenScm.Builder().connection("connection").developerConnection("developerConnection")
				.url("url").tag("tag").build();
		assertThat(mavenScm.getConnection()).isEqualTo("connection");
		assertThat(mavenScm.getDeveloperConnection()).isEqualTo("developerConnection");
		assertThat(mavenScm.getTag()).isEqualTo("tag");
		assertThat(mavenScm.getUrl()).isEqualTo("url");
	}

}
