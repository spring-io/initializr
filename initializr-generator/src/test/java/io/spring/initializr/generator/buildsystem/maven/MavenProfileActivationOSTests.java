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

class MavenProfileActivationOSTests {

	@Test
	void profileActivationOSEmpty() {
		MavenProfileActivationOS profileActivationOS = new MavenProfileActivationOS.Builder().build();
		assertThat(profileActivationOS.getName()).isNull();
		assertThat(profileActivationOS.getFamily()).isNull();
		assertThat(profileActivationOS.getArch()).isNull();
		assertThat(profileActivationOS.getVersion()).isNull();
	}

	@Test
	void profileActivationOSWithFullData() {
		MavenProfileActivationOS profileActivationOS = new MavenProfileActivationOS.Builder().name("name1")
				.family("family1").arch("arch1").version("version1").build();

		assertThat(profileActivationOS.getName()).isEqualTo("name1");
		assertThat(profileActivationOS.getFamily()).isEqualTo("family1");
		assertThat(profileActivationOS.getArch()).isEqualTo("arch1");
		assertThat(profileActivationOS.getVersion()).isEqualTo("version1");
	}

}
