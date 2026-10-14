/*
 * Copyright 2012 - present the original author or authors.
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
			.url("https://www.apache.org/licenses/LICENSE-2.0")
			.distribution(Distribution.MANUAL)
			.comments("A business-friendly OSS license")
			.build();
		assertThat(license.getName()).isEqualTo("Apache License, Version 2.0");
		assertThat(license.getUrl()).isEqualTo("https://www.apache.org/licenses/LICENSE-2.0");
		assertThat(license.getDistribution()).isEqualTo(Distribution.MANUAL);
		assertThat(license.getComments()).isEqualTo("A business-friendly OSS license");
	}

}
