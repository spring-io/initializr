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

import io.spring.initializr.generator.buildsystem.maven.MavenProfileActivation.Builder;
import io.spring.initializr.generator.buildsystem.maven.MavenProfileActivation.Os;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenProfileActivation}.
 */
public class MavenProfileActivationTests {

	@Test
	void profileWithNoActivation() {
		assertThat(createProfileActivation().build().isEmpty()).isTrue();
	}

	@Test
	void profileActiveByDefault() {
		assertThat(createProfileActivation().activeByDefault(true).build().getActiveByDefault()).isTrue();
	}

	@Test
	void profileActiveByDefaultCanBeAmended() {
		assertThat(createProfileActivation().activeByDefault(true).activeByDefault(null).build().getActiveByDefault())
				.isNull();
	}

	@Test
	void profileActivationJdk() {
		assertThat(createProfileActivation().jdk("15").build().getJdk()).isEqualTo("15");
	}

	@Test
	void profileActivationCanBeAmended() {
		assertThat(createProfileActivation().jdk("15").jdk(null).build().getJdk()).isNull();
	}

	@Test
	void profileActivationOs() {
		Os os = createProfileActivation().os("test-name", null, "arm64", null).build().getOs();
		assertThat(os).isNotNull();
		assertThat(os.getName()).isEqualTo("test-name");
		assertThat(os.getFamily()).isNull();
		assertThat(os.getArch()).isEqualTo("arm64");
		assertThat(os.getVersion()).isNull();
	}

	@Test
	void profileActivationOsCanBeDisabled() {
		assertThat(
				createProfileActivation().os("test-name", null, null, null).os(null, null, null, null).build().getOs())
						.isNull();
	}

	@Test
	void profileActivationProperty() {
		assertThat(createProfileActivation().property("test", "1").build().getProperty()).satisfies((property) -> {
			assertThat(property).isNotNull();
			assertThat(property.getName()).isEqualTo("test");
			assertThat(property.getValue()).isEqualTo("1");
		});
	}

	@Test
	void profileActivationPropertyCanBeDisabled() {
		assertThat(createProfileActivation().property("test", "1").property(null, null).build().getProperty()).isNull();
	}

	@Test
	void profileActivationFileExisting() {
		assertThat(createProfileActivation().fileExists("test.txt").build().getFile()).satisfies((file) -> {
			assertThat(file).isNotNull();
			assertThat(file.getExists()).isEqualTo("test.txt");
			assertThat(file.getMissing()).isNull();
		});
	}

	@Test
	void profileActivationFileMissing() {
		assertThat(createProfileActivation().fileMissing("test.txt").build().getFile()).satisfies((file) -> {
			assertThat(file).isNotNull();
			assertThat(file.getExists()).isNull();
			assertThat(file.getMissing()).isEqualTo("test.txt");
		});
	}

	@Test
	void profileActivationFileCanBeDisabled() {
		assertThat(createProfileActivation().fileMissing("test.txt").fileMissing(null).build().getFile()).isNull();
	}

	private MavenProfileActivation.Builder createProfileActivation() {
		return new Builder();
	}

}
