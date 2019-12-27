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

package io.spring.initializr.generator.project;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.packaging.jar.JarPackaging;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link ProjectDescriptionDiff}.
 *
 * @author Chris Bono
 * @author Stephane Nicoll
 */
class ProjectDescriptionDiffTests {

	@Test
	void projectDescriptionDiffCopySource() {
		ProjectDescription original = createFullProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(original);
		assertThat(diff.getOriginal()).usingRecursiveComparison().isEqualTo(original);
		assertThat(diff.getOriginal()).isNotSameAs(original);
	}

	@Test
	void projectDescriptionDiffWithUnmodifiedDescriptionDoesNotInvokeConsumer() {
		ProjectDescription description = createFullProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		diff.ifPlatformVersionChanged(description, (original, actual) -> fail("Values should not have changed"));
		diff.ifBuildSystemChanged(description, (original, actual) -> fail("Values should not have changed"));
		diff.ifPackagingChanged(description, (original, actual) -> fail("Values should not have changed"));
		diff.ifLanguageChanged(description, (original, actual) -> fail("Values should not have changed"));
		diff.ifGroupIdChanged(description, (original, actual) -> fail("Values should not have changed"));
		diff.ifArtifactIdChanged(description, (original, actual) -> fail("Values should not have changed"));
		diff.ifVersionChanged(description, (original, actual) -> fail("Values should not have changed"));
		diff.ifNameChanged(description, (original, actual) -> fail("Values should not have changed"));
		diff.ifDescriptionChanged(description, (original, actual) -> fail("Values should not have changed"));
		diff.ifApplicationNameChanged(description, (original, actual) -> fail("Values should not have changed"));
		diff.ifPackageNameChanged(description, (original, actual) -> fail("Values should not have changed"));
		diff.ifBaseDirectoryChanged(description, (original, actual) -> fail("Values should not have changed"));
	}

	@Test
	void projectDescriptionDiffWithModifiedPlatformVersionInvokesConsumer() {
		Version original = Version.parse("2.1.0.RELEASE");
		Version actual = Version.parse("2.2.0.RELEASE");
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(original);
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		description.setPlatformVersion(actual);
		validateConsumer(original, actual, (consumer) -> diff.ifPlatformVersionChanged(description, consumer));
	}

	@Test
	void projectDescriptionDiffWithModifiedBuildSystemInvokesConsumer() {
		BuildSystem original = BuildSystem.forId(MavenBuildSystem.ID);
		BuildSystem actual = BuildSystem.forId(GradleBuildSystem.ID);
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBuildSystem(original);
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		description.setBuildSystem(actual);
		validateConsumer(original, actual, (consumer) -> diff.ifBuildSystemChanged(description, consumer));
	}

	@Test
	void projectDescriptionDiffWithModifiedPackagingInvokesConsumer() {
		Packaging original = Packaging.forId(JarPackaging.ID);
		Packaging actual = Packaging.forId("war");
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPackaging(original);
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		description.setPackaging(actual);
		validateConsumer(original, actual, (consumer) -> diff.ifPackagingChanged(description, consumer));
	}

	@Test
	void projectDescriptionDiffWithModifiedLanguageInvokesConsumer() {
		Language original = Language.forId(JavaLanguage.ID, "11");
		Language actual = Language.forId(JavaLanguage.ID, "13");
		MutableProjectDescription description = new MutableProjectDescription();
		description.setLanguage(original);
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		description.setLanguage(actual);
		validateConsumer(original, actual, (consumer) -> diff.ifLanguageChanged(description, consumer));
	}

	@Test
	void projectDescriptionDiffWithModifiedGroupIdInvokesConsumer() {
		String original = "com.example";
		String actual = "com.example.app";
		MutableProjectDescription description = new MutableProjectDescription();
		description.setGroupId(original);
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		description.setGroupId(actual);
		validateConsumer(original, actual, (consumer) -> diff.ifGroupIdChanged(description, consumer));
	}

	@Test
	void projectDescriptionDiffWithModifiedArtifactIdInvokesConsumer() {
		String original = "demo";
		String actual = "app";
		MutableProjectDescription description = new MutableProjectDescription();
		description.setArtifactId(original);
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		description.setArtifactId(actual);
		validateConsumer(original, actual, (consumer) -> diff.ifArtifactIdChanged(description, consumer));
	}

	@Test
	void projectDescriptionDiffWithModifiedVersionInvokesConsumer() {
		String original = "1.0.0";
		String actual = "1.1.0";
		MutableProjectDescription description = new MutableProjectDescription();
		description.setVersion(original);
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		description.setVersion(actual);
		validateConsumer(original, actual, (consumer) -> diff.ifVersionChanged(description, consumer));
	}

	@Test
	void projectDescriptionDiffWithModifiedNameInvokesConsumer() {
		String original = "demo";
		String actual = "application";
		MutableProjectDescription description = new MutableProjectDescription();
		description.setName(original);
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		description.setName(actual);
		validateConsumer(original, actual, (consumer) -> diff.ifNameChanged(description, consumer));
	}

	@Test
	void projectDescriptionDiffWithModifiedDescriptionInvokesConsumer() {
		String original = "Demo Application";
		String actual = "Application";
		MutableProjectDescription description = new MutableProjectDescription();
		description.setDescription(original);
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		description.setDescription(actual);
		validateConsumer(original, actual, (consumer) -> diff.ifDescriptionChanged(description, consumer));
	}

	@Test
	void projectDescriptionDiffWithModifiedApplicationNameInvokesConsumer() {
		String original = "DemoApplication";
		String actual = "Application";
		MutableProjectDescription description = new MutableProjectDescription();
		description.setApplicationName(original);
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		description.setApplicationName(actual);
		validateConsumer(original, actual, (consumer) -> diff.ifApplicationNameChanged(description, consumer));
	}

	@Test
	void projectDescriptionDiffWithModifiedPackageNameInvokesConsumer() {
		String original = "com.example";
		String actual = "com.example.app";
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPackageName(original);
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		description.setPackageName(actual);
		validateConsumer(original, actual, (consumer) -> diff.ifPackageNameChanged(description, consumer));
	}

	@Test
	void projectDescriptionDiffWithModifiedBaseDirectoryInvokesConsumer() {
		String original = null;
		String actual = "demo-app";
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBaseDirectory(original);
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		description.setBaseDirectory(actual);
		validateConsumer(original, actual, (consumer) -> diff.ifBaseDirectoryChanged(description, consumer));
	}

	private MutableProjectDescription createFullProjectDescription() {
		MutableProjectDescription description = new MutableProjectDescription();
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
		return description;
	}

	@SuppressWarnings("unchecked")
	private <T> void validateConsumer(T original, T actual, Consumer<BiConsumer<T, T>> test) {
		BiConsumer<T, T> consumer = mock(BiConsumer.class);
		test.accept(consumer);
		verify(consumer).accept(original, actual);
	}

}
