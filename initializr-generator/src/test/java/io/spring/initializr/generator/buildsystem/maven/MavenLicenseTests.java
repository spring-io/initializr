/*
 * Copyright 2012-2019 the original author or authors.
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

import io.spring.initializr.generator.buildsystem.maven.MavenLicense.Distribution;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenLicense}.
 *
 * @author Jafer Khan Shamshad
 */
class MavenLicenseTests {

	@Test
	void licenseWithNameOnly() {
		MavenLicense license = new MavenLicense.Builder().name("Apache License, Version 2.0").build();
		assertThat(license.getName()).isEqualTo("Apache License, Version 2.0");
		assertThat(license.getUrl()).isNull();
		assertThat(license.getDistribution()).isNull();
		assertThat(license.getComments()).isNull();
	}

	@Test
	void licenseWithFullDetails() {
		MavenLicense license = new MavenLicense.Builder().name("Apache License, Version 2.0")
				.url("https://www.apache.org/licenses/LICENSE-2.0").distribution(Distribution.MANUAL)
				.comments("A business-friendly OSS license").build();
		assertThat(license.getName()).isEqualTo("Apache License, Version 2.0");
		assertThat(license.getUrl()).isEqualTo("https://www.apache.org/licenses/LICENSE-2.0");
		assertThat(license.getDistribution()).isEqualTo(Distribution.MANUAL);
		assertThat(license.getComments()).isEqualTo("A business-friendly OSS license");
	}

}
