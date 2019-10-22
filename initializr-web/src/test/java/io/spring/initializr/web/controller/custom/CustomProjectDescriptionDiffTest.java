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

package io.spring.initializr.web.controller.custom;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Simple sanity test around the custom diff extension model.
 */
class CustomProjectDescriptionDiffTest {

	@Test
	void sanityCheck() {

		CustomProjectDescription description = customProjectDescription();
		CustomProjectDescriptionDiffFactory diffFactory = new CustomProjectDescriptionDiffFactory();
		CustomProjectDescriptionDiff diff = (CustomProjectDescriptionDiff) diffFactory.create(description);

		// copied
		assertThat(diff.getOriginal()).usingRecursiveComparison().isEqualTo(description);
		assertThat(diff.getOriginal()).isNotSameAs(description);

		// no changes
		diff.ifCUstomFlagChanged(description, (v1, v2) -> fail("Values should not have changed"));

		// changes
		boolean originalValue = description.isCustomFlag();
		description.setCustomFlag(!originalValue);

		// TODO could use the CallTrackingBiConsumer that I used in initializr-generator
		// tests but then where to put it?
		final boolean[] called = { false };
		diff.ifCUstomFlagChanged(description, (prev, curr) -> {
			assertThat(prev).isEqualTo(originalValue);
			assertThat(curr).isEqualTo(description.isCustomFlag());
			called[0] = true;
		});
		assertThat(called[0]).isTrue();
	}

	private CustomProjectDescription customProjectDescription() {
		CustomProjectDescription description = new CustomProjectDescription();
		description.setBuildSystem(BuildSystem.forId(MavenBuildSystem.ID));
		description.setLanguage(Language.forId(JavaLanguage.ID, "11"));
		description.setPlatformVersion(Version.parse("2.2.0.RELEASE"));
		description.setGroupId("com.example");
		description.setArtifactId("demo");
		description.setName("demo");
		description.setVersion("0.0.8");
		description.setApplicationName("DemoApplication");
		description.setPackageName("com.example.demo");
		description.setPackaging(Packaging.forId("jar"));
		description.setBaseDirectory(".");
		description.setCustomFlag(true);
		return description;
	}

}
