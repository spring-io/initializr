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

import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MetadataProjectDescriptionCustomizer}.
 *
 * @author Stephane Nicoll
 */
class MetadataProjectDescriptionCustomizerTests {

	private final InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().build();

	@Test
	void customizeShouldUseDefaultApplicationNameFromMetadata() {
		assertThat(customize(new MutableProjectDescription()).getApplicationName()).isEqualTo("Application");
	}

	@Test
	void customizeShouldSetApplicationNameUsingNameWhenAbsent() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setName("MyTest");
		assertThat(customize(description).getApplicationName()).isEqualTo("MyTestApplication");
	}

	@Test
	void customizeShouldUseDefaultPlatformVersionFromMetadata() {
		assertThat(customize(new MutableProjectDescription()).getPlatformVersion()).isEqualTo(Version.parse("2.4.1"));
	}

	@Test
	void customizeShouldUseDefaultFromMetadataOnEmptyGroup() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setGroupId("  ");
		assertThat(customize(description).getGroupId()).isEqualTo("com.example");
	}

	@Test
	void customizeShouldUseDefaultFromMetadataOnEmptyArtifact() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setArtifactId("");
		assertThat(customize(description).getArtifactId()).isEqualTo("demo");
	}

	@Test
	void customizeShouldUseDefaultFromMetadataOnEmptyName() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setName("    ");
		assertThat(customize(description).getName()).isEqualTo("demo");
	}

	@Test
	void customizeShouldUseDefaultFromMetadataOnEmptyDescription() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setDescription("    ");
		assertThat(customize(description).getDescription()).isEqualTo("Demo project for Spring Boot");
	}

	@Test
	void customizeShouldUseDefaultFromMetadataOnEmptyPackageName() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPackageName(" ");
		assertThat(customize(description).getPackageName()).isEqualTo("com.example.demo");
	}

	@Test
	void customizeShouldUseDefaultFromMetadataWhenGeneratingPackageNameWithEmptyGroup() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setGroupId("  ");
		assertThat(customize(description).getPackageName()).isEqualTo("com.example.demo");
	}

	@Test
	void customizeShouldUseDefaultFromMetadataWhenGeneratingPackageNameWithEmptyArtifact() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setArtifactId("  ");
		assertThat(customize(description).getPackageName()).isEqualTo("com.example.demo");
	}

	@Test
	void customizeShouldUseDefaultFromMetadataOnEmptyVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setVersion("  ");
		assertThat(customize(description).getVersion()).isEqualTo("0.0.1-SNAPSHOT");
	}

	@Test
	void customizeShouldNotCleanBaseDirWhenNotSameAsArtifactId() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setArtifactId("correct ! ID @");
		description.setBaseDirectory("test");
		assertThat(customize(description).getBaseDirectory()).isEqualTo("test");
	}

	@Test
	void customizeShouldCleanBaseDirWhenSameAsArtifactId() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setArtifactId("correct ! ID @");
		description.setBaseDirectory("correct ! ID @");
		assertThat(customize(description).getBaseDirectory()).isEqualTo("correct-ID");
	}

	@Test
	void customizeShouldNotCleanNameWhenNotSameAsArtifactId() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setArtifactId("correct ! ID @");
		description.setName("test");
		assertThat(customize(description).getName()).isEqualTo("test");
	}

	@Test
	void customizeShouldCleanNameWhenSameAsArtifactId() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setArtifactId("correct ! ID @");
		description.setName("correct ! ID @");
		assertThat(customize(description).getName()).isEqualTo("correct-ID");
	}

	@Test
	void customizeShouldNotCleanArtifactIdWithValidChars() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setArtifactId("correct_test");
		assertThat(customize(description).getArtifactId()).isEqualTo("correct_test");
	}

	@Test
	void customizeShouldCleanInvalidArtifactIdWithHyphenSeparator() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setArtifactId("correct ! ID @");
		assertThat(customize(description).getArtifactId()).isEqualTo("correct-ID");
	}

	@Test
	void customizeWithCleanedArtifactIdShouldNotContainHyphenBeforeOrAfterValidSpecialCharacter() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setArtifactId("correct !_!ID @");
		assertThat(customize(description).getArtifactId()).isEqualTo("correct_ID");
	}

	@Test
	void customizeShouldNotCleanGroupIdWithValidChars() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setGroupId("correct.ID12");
		assertThat(customize(description).getGroupId()).isEqualTo("correct.ID12");
	}

	@Test
	void customizeShouldCleanInvalidGroupIdWithDotDelimiter() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setGroupId("correct !  ID12 @");
		assertThat(customize(description).getGroupId()).isEqualTo("correct.ID12");
	}

	MutableProjectDescription customize(MutableProjectDescription description) {
		new MetadataProjectDescriptionCustomizer(this.metadata).customize(description);
		return description;
	}

}
