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

package io.spring.initializr.web.project;

import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DefaultProjectRequestPlatformVersionTransformer}.
 *
 * @author Stephane Nicoll
 */
class DefaultProjectRequestPlatformVersionTransformerTests {

	private final DefaultProjectRequestPlatformVersionTransformer transformer = new DefaultProjectRequestPlatformVersionTransformer();

	@Test
	void formatV1WhenV2IsExpected() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.setPlatformVersionFormatCompatibilityRange("[2.0.0.RELEASE,2.4.0-M1)", "2.4.0-M1").build();
		assertThat(this.transformer.transform(Version.parse("2.4.0.RELEASE"), metadata)).hasToString("2.4.0");
	}

	@Test
	void formatV1WhenV1IsExpected() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.setPlatformVersionFormatCompatibilityRange("[2.0.0.RELEASE,2.4.0-M1)", "2.4.0-M1").build();
		Version version = Version.parse("2.2.0.RELEASE");
		assertThat(this.transformer.transform(version, metadata)).isSameAs(version);
	}

	@Test
	void formatV2WhenV1IsExpected() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.setPlatformVersionFormatCompatibilityRange("[2.0.0.RELEASE,2.4.0-M1)", "2.4.0-M1").build();
		assertThat(this.transformer.transform(Version.parse("2.3.0-SNAPSHOT"), metadata))
				.hasToString("2.3.0.BUILD-SNAPSHOT");
	}

	@Test
	void formatV2WhenV2IsExpected() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.setPlatformVersionFormatCompatibilityRange("[2.0.0.RELEASE,2.4.0-M1)", "2.4.0-M1").build();
		Version version = Version.parse("2.4.0");
		assertThat(this.transformer.transform(version, metadata)).isSameAs(version);
	}

	@Test
	void formatV1WhenNoRangeIsConfigured() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().build();
		Version version = Version.parse("2.4.0.RELEASE");
		assertThat(this.transformer.transform(version, metadata)).isSameAs(version);
	}

	@Test
	void formatV2WhenNoRangeIsConfigured() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().build();
		Version version = Version.parse("2.2.0-SNAPSHOT");
		assertThat(this.transformer.transform(version, metadata)).isSameAs(version);
	}

}
