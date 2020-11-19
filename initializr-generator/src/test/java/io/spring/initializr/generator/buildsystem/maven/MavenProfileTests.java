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

import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.maven.MavenProfile.Settings;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenProfile}.
 */
class MavenProfileTests {

	@Test
	void profileWithNoCustomization() {
		MavenProfile profile = createProfile("test");
		assertThat(profile.getId()).isEqualTo("test");
		assertThat(profile.getActivation().isEmpty()).isTrue();
		assertThat(profile.properties().isEmpty()).isTrue();
		assertThat(profile.dependencies().isEmpty()).isTrue();
		assertThat(profile.resources().isEmpty()).isTrue();
		assertThat(profile.testResources().isEmpty()).isTrue();
		assertThat(profile.plugins().isEmpty()).isTrue();
		assertThat(profile.boms().isEmpty()).isTrue();
		assertThat(profile.repositories().isEmpty()).isTrue();
		assertThat(profile.pluginRepositories().isEmpty()).isTrue();
		assertThat(profile.getDistributionManagement().isEmpty()).isTrue();
	}

	@Test
	void profileWithActivation() {
		MavenProfile profile = createProfile("test");
		profile.activation().jdk("15").property("test", "value").jdk(null);
		assertThat(profile.getActivation().getProperty()).satisfies((property) -> {
			assertThat(property.getName()).isEqualTo("test");
			assertThat(property.getValue()).isEqualTo("value");
		});
		assertThat(profile.getActivation().getJdk()).isNull();
	}

	@Test
	void profileWithDefaultGoal() {
		MavenProfile profile = createProfile("test");
		profile.settings().defaultGoal("verify");
		Settings settings = profile.getSettings();
		assertThat(settings.getDefaultGoal()).isEqualTo("verify");
		assertThat(settings.getFinalName()).isNull();
	}

	@Test
	void profileWithFinalName() {
		MavenProfile profile = createProfile("test");
		profile.settings().finalName("test-app");
		Settings settings = profile.getSettings();
		assertThat(settings.getDefaultGoal()).isNull();
		assertThat(settings.getFinalName()).isEqualTo("test-app");
	}

	@Test
	void profileWithDistributionManagement() {
		MavenProfile profile = createProfile("test");
		profile.distributionManagement().downloadUrl("https://example.com/download");
		MavenDistributionManagement dm = profile.getDistributionManagement();
		assertThat(dm.getDownloadUrl()).isEqualTo("https://example.com/download");
		assertThat(dm.getRepository().isEmpty()).isTrue();
		assertThat(dm.getSnapshotRepository().isEmpty()).isTrue();
		assertThat(dm.getSite().isEmpty()).isTrue();
		assertThat(dm.getRepository().isEmpty()).isTrue();
	}

	private MavenProfile createProfile(String id) {
		return new MavenProfile(id, BuildItemResolver.NO_OP);
	}

}
