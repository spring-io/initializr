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

package io.spring.initializr.web.project;

import java.util.Collections;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionRange;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Type;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link ProjectRequestToDescriptionConverter}.
 *
 * @author Madhura Bhave
 * @author Stephane Nicoll
 */
public class ProjectRequestToDescriptionConverterTests {

	private InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.build();

	private final ProjectRequestToDescriptionConverter converter = new ProjectRequestToDescriptionConverter();

	@Test
	public void convertWhenTypeIsInvalidShouldThrowException() {
		ProjectRequest request = createProjectRequest();
		request.setType("foo-build");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
				.isThrownBy(() -> this.converter.convert(request, this.metadata))
				.withMessage("Unknown type 'foo-build' check project metadata");
	}

	@Test
	public void convertWhenTypeDoesNotDefineBuildTagShouldThrowException() {
		Type type = new Type();
		type.setId("example-project");
		InitializrMetadata testMetadata = InitializrMetadataTestBuilder.withDefaults()
				.addType(type).build();
		ProjectRequest request = createProjectRequest();
		request.setType("example-project");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
				.isThrownBy(() -> this.converter.convert(request, testMetadata))
				.withMessage(
						"Invalid type 'example-project' (missing build tag) check project metadata");
	}

	@Test
	void convertWhenSpringBootVersionInvalidShouldThrowException() {
		ProjectRequest request = createProjectRequest();
		request.setBootVersion("1.2.3.M4");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
				.isThrownBy(() -> this.converter.convert(request, this.metadata))
				.withMessage(
						"Invalid Spring Boot version 1.2.3.M4 must be 1.5.0 or higher");
	}

	@Test
	public void convertWhenPackagingIsInvalidShouldThrowException() {
		ProjectRequest request = createProjectRequest();
		request.setPackaging("star");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
				.isThrownBy(() -> this.converter.convert(request, this.metadata))
				.withMessage("Unknown packaging 'star' check project metadata");
	}

	@Test
	public void convertWhenLanguageIsInvalidShouldThrowException() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage("english");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
				.isThrownBy(() -> this.converter.convert(request, this.metadata))
				.withMessage("Unknown language 'english' check project metadata");
	}

	@Test
	void convertWhenDependencyNotPresentShouldThrowException() {
		ProjectRequest request = createProjectRequest();
		request.setDependencies(Collections.singletonList("invalid"));
		assertThatExceptionOfType(InvalidProjectRequestException.class)
				.isThrownBy(() -> this.converter.convert(request, this.metadata))
				.withMessage("Unknown dependency 'invalid' check project metadata");
	}

	@Test
	void convertWhenDependencyOutOfRangeShouldThrowException() {
		Dependency dependency = Dependency.withId("foo");
		dependency.setRange(new VersionRange(Version.parse("2.2.0.M1")));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("foo", dependency).build();
		ProjectRequest request = createProjectRequest();
		request.setDependencies(Collections.singletonList("foo"));
		assertThatExceptionOfType(InvalidProjectRequestException.class)
				.isThrownBy(() -> this.converter.convert(request, metadata))
				.withMessage("Dependency 'foo' is not compatible "
						+ "with Spring Boot 2.1.1.RELEASE");
	}

	@Test
	void convertShouldSetApplicationNameForProjectDescriptionFromRequestWhenPresent() {
		ProjectRequest request = createProjectRequest();
		request.setApplicationName("MyApplication");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getApplicationName()).isEqualTo("MyApplication");
	}

	@Test
	void convertShouldSetApplicationNameForProjectDescriptionUsingNameWhenAbsentFromRequest() {
		ProjectRequest request = createProjectRequest();
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getApplicationName()).isEqualTo("DemoApplication");
	}

	@Test
	void convertShouldSetGroupIdAndArtifactIdFromRequest() {
		ProjectRequest request = createProjectRequest();
		request.setArtifactId("foo");
		request.setGroupId("com.example");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getGroupId()).isEqualTo("com.example");
		assertThat(description.getArtifactId()).isEqualTo("foo");
	}

	@Test
	void convertShouldSetVersionFromRequest() {
		ProjectRequest request = createProjectRequest();
		request.setVersion("1.0.2-SNAPSHOT");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getVersion()).isEqualTo("1.0.2-SNAPSHOT");
	}

	@Test
	void convertShouldUseDefaultFromMetadataOnEmptyVersion() {
		ProjectRequest request = createProjectRequest();
		request.setVersion("  ");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getVersion()).isEqualTo("0.0.1-SNAPSHOT");
	}

	@Test
	void convertShouldSetBaseDirectoryFromRequest() {
		ProjectRequest request = createProjectRequest();
		request.setBaseDir("my-path");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getBaseDirectory()).isEqualTo("my-path");
	}

	@Test
	void convertShouldSetBuildSystemFromRequestTypeAndBuildTag() {
		Type type = new Type();
		type.setId("example-type");
		type.getTags().put("build", "gradle");
		InitializrMetadata testMetadata = InitializrMetadataTestBuilder.withDefaults()
				.addType(type).build();
		ProjectRequest request = createProjectRequest();
		request.setType("example-type");
		ProjectDescription description = this.converter.convert(request, testMetadata);
		assertThat(description.getBuildSystem()).isInstanceOf(GradleBuildSystem.class);
	}

	@Test
	void convertShouldSetDescriptionFromRequest() {
		ProjectRequest request = createProjectRequest();
		request.setDescription("This is my demo project");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getDescription()).isEqualTo("This is my demo project");
	}

	@Test
	void convertShouldSetPackagingFromRequest() {
		ProjectRequest request = createProjectRequest();
		request.setPackaging("war");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getPackaging().id()).isEqualTo("war");
	}

	@Test
	void convertShouldSetPlatformVersionFromRequest() {
		ProjectRequest request = createProjectRequest();
		request.setBootVersion("2.0.3");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getPlatformVersion()).isEqualTo(Version.parse("2.0.3"));
	}

	@Test
	void convertShouldUseDefaultPlatformVersionFromMetadata() {
		ProjectRequest request = createProjectRequest();
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getPlatformVersion())
				.isEqualTo(Version.parse("2.1.1.RELEASE"));
	}

	@Test
	void convertShouldSetLanguageForProjectDescriptionFromRequest() {
		ProjectRequest request = createProjectRequest();
		request.setJavaVersion("1.8");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getLanguage().id()).isEqualTo("java");
		assertThat(description.getLanguage().jvmVersion()).isEqualTo("1.8");
	}

	@Test
	void convertShouldUseDefaultFromMetadataOnEmptyGroup() {
		ProjectRequest request = createProjectRequest();
		request.setGroupId("  ");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getGroupId()).isEqualTo("com.example");
	}

	@Test
	void convertShouldUseDefaultFromMetadataOnEmptyArtifact() {
		ProjectRequest request = createProjectRequest();
		request.setArtifactId("");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getArtifactId()).isEqualTo("demo");
	}

	@Test
	void convertShouldUseDefaultFromMetadataOnEmptyName() {
		ProjectRequest request = createProjectRequest();
		request.setName("    ");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getName()).isEqualTo("demo");
	}

	@Test
	void convertShouldUseDefaultFromMetadataOnEmptyDescription() {
		ProjectRequest request = createProjectRequest();
		request.setDescription("    ");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getDescription())
				.isEqualTo("Demo project for Spring Boot");
	}

	@Test
	void convertShouldUseDefaultFromMetadataOnEmptyPackageName() {
		ProjectRequest request = createProjectRequest();
		request.setPackageName(" ");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getPackageName()).isEqualTo("com.example.demo");
	}

	@Test
	void convertShouldUseDefaultFromMetadataWhenGeneratingPackageNameWithEmptyGroup() {
		ProjectRequest request = createProjectRequest();
		request.setGroupId("  ");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getPackageName()).isEqualTo("com.example.demo");
	}

	@Test
	void convertShouldUseDefaultFromMetadataWhenGeneratingPackageNameWithEmptyArtifact() {
		ProjectRequest request = createProjectRequest();
		request.setArtifactId("  ");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getPackageName()).isEqualTo("com.example.demo");
	}

	private ProjectRequest createProjectRequest() {
		WebProjectRequest request = new WebProjectRequest();
		request.initialize(this.metadata);
		return request;
	}

}
