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

package io.spring.initializr.generator.project.diff;

import java.util.function.BiConsumer;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class ProjectDescriptionDiffTest {

	@Test
	void originalIsCopied() {
		ProjectDescription original = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(original);
		assertThat(diff.getOriginal()).usingRecursiveComparison().isEqualTo(original);
		assertThat(diff.getOriginal()).isNotSameAs(original);
	}

	@Test
	void noChanges() {
		MutableProjectDescription description = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		BiConsumer failIfCalled = (v1, v2) -> fail("Values should not have changed");
		diff.ifPlatformVersionChanged(description, failIfCalled);
		diff.ifBuildSystemChanged(description, failIfCalled);
		diff.ifPackagingChanged(description, failIfCalled);
		diff.ifLanguageChanged(description, failIfCalled);
		diff.ifGroupIdChanged(description, failIfCalled);
		diff.ifArtifactIdChanged(description, failIfCalled);
		diff.ifVersionChanged(description, failIfCalled);
		diff.ifNameChanged(description, failIfCalled);
		diff.ifDescriptionChanged(description, failIfCalled);
		diff.ifApplicationNameChanged(description, failIfCalled);
		diff.ifPackageNameChanged(description, failIfCalled);
		diff.ifBaseDirectoryChanged(description, failIfCalled);
		diff.ifPropertyChanged(description, "name", String.class, failIfCalled);
	}

	@Test
	void platformVersionChanged() {
		MutableProjectDescription description = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		Version original = description.getPlatformVersion();
		description.setPlatformVersion(Version.parse("2.0.0.RELEASE"));
		CallTrackingBiConsumer changeHandler = new CallTrackingBiConsumer<>((prev, curr) -> {
			assertThat(prev).isEqualTo(original);
			assertThat(curr).isEqualTo(description.getPlatformVersion());
		});
		diff.ifPlatformVersionChanged(description, changeHandler);
		diff.ifPropertyChanged(description, "platformVersion", Version.class, changeHandler);
		assertThat(changeHandler.timesCalled()).isEqualTo(2);
	}

	@Test
	void buildSystemChanged() {
		MutableProjectDescription description = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		BuildSystem original = description.getBuildSystem();
		description.setBuildSystem(BuildSystem.forId(GradleBuildSystem.ID));
		CallTrackingBiConsumer changeHandler = new CallTrackingBiConsumer<>((prev, curr) -> {
			assertThat(prev).isEqualTo(original);
			assertThat(curr).isEqualTo(description.getBuildSystem());
		});
		diff.ifBuildSystemChanged(description, changeHandler);
		diff.ifPropertyChanged(description, "buildSystem", BuildSystem.class, changeHandler);
		assertThat(changeHandler.timesCalled()).isEqualTo(2);
	}

	@Test
	void packagingChanged() {
		MutableProjectDescription description = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		Packaging original = description.getPackaging();
		description.setPackaging(Packaging.forId("war"));
		CallTrackingBiConsumer changeHandler = new CallTrackingBiConsumer<>((prev, curr) -> {
			assertThat(prev).isEqualTo(original);
			assertThat(curr).isEqualTo(description.getPackaging());
		});
		diff.ifPackagingChanged(description, changeHandler);
		diff.ifPropertyChanged(description, "packaging", Packaging.class, changeHandler);
		assertThat(changeHandler.timesCalled()).isEqualTo(2);
	}

	@Test
	void languageChanged() {
		MutableProjectDescription description = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		Language original = description.getLanguage();
		description.setLanguage(Language.forId(JavaLanguage.ID, "13"));
		CallTrackingBiConsumer changeHandler = new CallTrackingBiConsumer<>((prev, curr) -> {
			assertThat(prev).isEqualTo(original);
			assertThat(curr).isEqualTo(description.getLanguage());
		});
		diff.ifLanguageChanged(description, changeHandler);
		diff.ifPropertyChanged(description, "language", String.class, changeHandler);
		assertThat(changeHandler.timesCalled()).isEqualTo(2);
	}

	@Test
	void groupIdChanged() {
		MutableProjectDescription description = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		String original = description.getGroupId();
		description.setGroupId("group5150");
		CallTrackingBiConsumer changeHandler = new CallTrackingBiConsumer<>((prev, curr) -> {
			assertThat(prev).isEqualTo(original);
			assertThat(curr).isEqualTo(description.getGroupId());
		});
		diff.ifGroupIdChanged(description, changeHandler);
		diff.ifPropertyChanged(description, "groupId", String.class, changeHandler);
		assertThat(changeHandler.timesCalled()).isEqualTo(2);
	}

	@Test
	void artifactIdChanged() {
		MutableProjectDescription description = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		String original = description.getArtifactId();
		description.setArtifactId("artifact5150");
		CallTrackingBiConsumer changeHandler = new CallTrackingBiConsumer<>((prev, curr) -> {
			assertThat(prev).isEqualTo(original);
			assertThat(curr).isEqualTo(description.getArtifactId());
		});
		diff.ifArtifactIdChanged(description, changeHandler);
		diff.ifPropertyChanged(description, "artifactId", String.class, changeHandler);
		assertThat(changeHandler.timesCalled()).isEqualTo(2);
	}

	@Test
	void versionChanged() {
		MutableProjectDescription description = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		String original = description.getVersion();
		description.setVersion("version5150");
		CallTrackingBiConsumer changeHandler = new CallTrackingBiConsumer<>((prev, curr) -> {
			assertThat(prev).isEqualTo(original);
			assertThat(curr).isEqualTo(description.getVersion());
		});
		diff.ifVersionChanged(description, changeHandler);
		diff.ifPropertyChanged(description, "version", String.class, changeHandler);
		assertThat(changeHandler.timesCalled()).isEqualTo(2);
	}

	@Test
	void nameChanged() {
		MutableProjectDescription description = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		String original = description.getName();
		description.setName("name5150");
		CallTrackingBiConsumer changeHandler = new CallTrackingBiConsumer<>((prev, curr) -> {
			assertThat(prev).isEqualTo(original);
			assertThat(curr).isEqualTo(description.getName());
		});
		diff.ifNameChanged(description, changeHandler);
		diff.ifPropertyChanged(description, "name", String.class, changeHandler);
		assertThat(changeHandler.timesCalled()).isEqualTo(2);
	}

	@Test
	void descriptionChanged() {
		MutableProjectDescription description = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		String original = description.getDescription();
		description.setDescription("desc5150");
		CallTrackingBiConsumer changeHandler = new CallTrackingBiConsumer<>((prev, curr) -> {
			assertThat(prev).isEqualTo(original);
			assertThat(curr).isEqualTo(description.getDescription());
		});
		diff.ifDescriptionChanged(description, changeHandler);
		diff.ifPropertyChanged(description, "description", String.class, changeHandler);
		assertThat(changeHandler.timesCalled()).isEqualTo(2);
	}

	@Test
	void applicationNameChanged() {
		MutableProjectDescription description = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		String original = description.getApplicationName();
		description.setApplicationName("appname5150");
		CallTrackingBiConsumer changeHandler = new CallTrackingBiConsumer<>((prev, curr) -> {
			assertThat(prev).isEqualTo(original);
			assertThat(curr).isEqualTo(description.getApplicationName());
		});
		diff.ifApplicationNameChanged(description, changeHandler);
		diff.ifPropertyChanged(description, "applicationName", String.class, changeHandler);
		assertThat(changeHandler.timesCalled()).isEqualTo(2);
	}

	@Test
	void packageNameChanged() {
		MutableProjectDescription description = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		String original = description.getPackageName();
		description.setPackageName("pkg5150");
		CallTrackingBiConsumer changeHandler = new CallTrackingBiConsumer<>((prev, curr) -> {
			assertThat(prev).isEqualTo(original);
			assertThat(curr).isEqualTo(description.getPackageName());
		});
		diff.ifPackageNameChanged(description, changeHandler);
		diff.ifPropertyChanged(description, "packageName", String.class, changeHandler);
		assertThat(changeHandler.timesCalled()).isEqualTo(2);
	}

	@Test
	void baseDirectoryChanged() {
		MutableProjectDescription description = createProjectDescription();
		ProjectDescriptionDiff diff = new ProjectDescriptionDiff(description);
		String original = description.getBaseDirectory();
		description.setBaseDirectory("baseDir5150");
		CallTrackingBiConsumer changeHandler = new CallTrackingBiConsumer<>((prev, curr) -> {
			assertThat(prev).isEqualTo(original);
			assertThat(curr).isEqualTo(description.getBaseDirectory());
		});
		diff.ifBaseDirectoryChanged(description, changeHandler);
		diff.ifPropertyChanged(description, "baseDirectory", String.class, changeHandler);
		assertThat(changeHandler.timesCalled()).isEqualTo(2);
	}

	private MutableProjectDescription createProjectDescription() {
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

	static class CallTrackingBiConsumer<T, U> implements BiConsumer<T, U> {

		private final BiConsumer delegate;

		private int callCount;

		CallTrackingBiConsumer(BiConsumer delegate) {
			this.delegate = delegate;
		}

		@Override
		public void accept(T t, U u) {
			this.callCount++;
			this.delegate.accept(t, u);
		}

		int timesCalled() {
			return this.callCount;
		}

	}

}
