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

package io.spring.initializr.metadata;

import java.util.Arrays;
import java.util.List;

import io.spring.initializr.generator.version.InvalidVersionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link VersionCapability}.
 */
class VersionCapabilityTests {

	@Test
	void parseVersionResolvesWildcardToLatestMatchingVersion() {
		VersionCapability capability = new VersionCapability("test", "test", "test");
		capability.setContent(Arrays.asList(DefaultMetadataElement.create("1.3.6", false),
				DefaultMetadataElement.create("1.3.7", false), DefaultMetadataElement.create("1.4.0", false)));
		assertThat(capability.parseVersion("1.3.x")).hasToString("1.3.7");
	}

	@Test
	void parseVersionThrowsExceptionWhenNoVersionMatches() {
		VersionCapability capability = new VersionCapability("test", "test", "test");
		capability.setContent(Arrays.asList(DefaultMetadataElement.create("1.3.6", false),
				DefaultMetadataElement.create("1.4.0", false)));
		assertThatExceptionOfType(InvalidVersionException.class).isThrownBy(() -> capability.parseVersion("2.x.x"))
			.withMessage("Could not determine latest version based on '2.x.x'");
	}

	@Test
	void safeParseVersionReturnsNullWhenVersionCannotBeParsed() {
		VersionCapability capability = new VersionCapability("test", "test", "test");
		capability.setContent(Arrays.asList(DefaultMetadataElement.create("1.3.6", false),
				DefaultMetadataElement.create("1.4.0", false)));
		assertThat(capability.safeParseVersion(null)).isNull();
		assertThat(capability.safeParseVersion("invalid")).isNull();
		assertThat(capability.safeParseVersion("2.x.x")).isNull();
	}

	@Test
	void parseVersionUsesAddedContent() {
		VersionCapability capability = new VersionCapability("test", "test", "test");
		capability.addContent(DefaultMetadataElement.create("1.3.7", false));
		assertThat(capability.parseVersion("1.3.x")).hasToString("1.3.7");
	}

	@Test
	void parseVersionUsesReplacedContent() {
		VersionCapability capability = new VersionCapability("test", "test", "test");
		capability.setContent(List.of(DefaultMetadataElement.create("1.3.7", false)));
		assertThat(capability.parseVersion("1.3.x")).hasToString("1.3.7");

		capability.setContent(List.of(DefaultMetadataElement.create("1.3.8", false)));
		assertThat(capability.parseVersion("1.3.x")).hasToString("1.3.8");
	}

	@Test
	void parseVersionUsesMergedContent() {
		VersionCapability capability = new VersionCapability("test", "test", "test");
		capability.setContent(List.of(DefaultMetadataElement.create("1.3.7", false)));
		capability.merge(List.of(DefaultMetadataElement.create("1.3.8", false)));
		assertThat(capability.parseVersion("1.3.x")).hasToString("1.3.8");
	}

}
