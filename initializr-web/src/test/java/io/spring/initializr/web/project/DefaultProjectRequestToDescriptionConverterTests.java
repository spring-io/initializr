/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.project;

import java.util.Collections;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionRange;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link DefaultProjectRequestToDescriptionConverter}.
 *
 * @author Madhura Bhave
 * @author Stephane Nicoll
 * @author HaiTao Zhang
 */
class DefaultProjectRequestToDescriptionConverterTests {

	private InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().build();

	private final DefaultProjectRequestToDescriptionConverter converter = new DefaultProjectRequestToDescriptionConverter();

	@Test
	void convertWhenArtifactIdHasAccentCleanValue() {
		ProjectRequest request = createProjectRequest();
		request.setArtifactId("tëst-âpp");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getArtifactId()).isEqualTo("test-app");
	}

	@Test
	void convertWhenGroupIdHasAccentCleanValue() {
		ProjectRequest request = createProjectRequest();
		request.setGroupId("com.êxample.äpp");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getGroupId()).isEqualTo("com.example.app");
	}

	@Test
	void convertWhenNameHasAccentCleanValue() {
		ProjectRequest request = createProjectRequest();
		request.setName("My Demö");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getName()).isEqualTo("My Demo");
	}

	@Test
	void convertWhenPackageNameHasAccentCleanValue() {
		ProjectRequest request = createProjectRequest();
		request.setPackageName("com.êxample.äpp");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getPackageName()).isEqualTo("com.example.app");
	}

	@Test
	void convertWhenBaseDirContainsPathSeparatorShouldThrowException() {
		ProjectRequest request = createProjectRequest();
		request.setBaseDir("my/project");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
			.isThrownBy(() -> this.converter.convert(request, this.metadata))
			.withMessageContaining("Invalid baseDir 'my/project'");
	}

	@Test
	void convertWhenBaseDirContainsWindowsPathSeparatorShouldThrowException() {
		ProjectRequest request = createProjectRequest();
		request.setBaseDir("my\\project");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
			.isThrownBy(() -> this.converter.convert(request, this.metadata))
			.withMessageContaining("Invalid baseDir 'my\\project'");
	}

	@Test
	void convertWhenBaseDirContainsDotDotShouldThrowException() {
		ProjectRequest request = createProjectRequest();
		request.setBaseDir("../dirup");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
			.isThrownBy(() -> this.converter.convert(request, this.metadata))
			.withMessageContaining("Invalid baseDir '../dirup'");
	}

	@Test
	void convertWhenBaseDirIsExactlyDotDotShouldThrowException() {
		ProjectRequest request = createProjectRequest();
		request.setBaseDir("..");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
			.isThrownBy(() -> this.converter.convert(request, this.metadata))
			.withMessageContaining("Invalid baseDir '..'");
	}

	@Test
	void convertWhenBaseDirIsValidShouldSetBaseDirectory() {
		ProjectRequest request = createProjectRequest();
		request.setBaseDir("my-project");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getBaseDirectory()).isEqualTo("my-project");
	}

	@Test
	void convertWhenTypeIsInvalidShouldThrowException() {
		ProjectRequest request = createProjectRequest();
		request.setType("foo-build");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
			.isThrownBy(() -> this.converter.convert(request, this.metadata))
			.withMessage("Unknown type 'foo-build' check project metadata");
	}

	@Test
	void convertWhenTypeDoesNotDefineBuildTagShouldThrowException() {
		Type type = new Type();
		type.setId("example-project");
		InitializrMetadata testMetadata = InitializrMetadataTestBuilder.withDefaults().addType(type).build();
		ProjectRequest request = createProjectRequest();
		request.setType("example-project");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
			.isThrownBy(() -> this.converter.convert(request, testMetadata))
			.withMessage("Invalid type 'example-project' (missing build tag) check project metadata");
	}

	@Test
	void convertWhenTypeDoesNotDefineDialectTagShouldUseDefaultDialect() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addType("foo", true, "/foo.zip", GradleBuildSystem.ID, null, "test")
			.build();
		ProjectRequest request = createProjectRequest();
		request.setType("foo");
		ProjectDescription description = this.converter.convert(request, metadata);
		assertThat(description.getBuildSystem()).isNotNull();
		assertThat(description.getBuildSystem().dialect()).isEqualTo(GradleBuildSystem.DIALECT_GROOVY);
	}

	@Test
	void convertWhenTypeDefinesDialectTagShouldUseDialect() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addType("foo", true, "/foo.zip", GradleBuildSystem.ID, GradleBuildSystem.DIALECT_KOTLIN, "test")
			.build();
		ProjectRequest request = createProjectRequest();
		request.setType("foo");
		ProjectDescription description = this.converter.convert(request, metadata);
		assertThat(description.getBuildSystem()).isNotNull();
		assertThat(description.getBuildSystem().dialect()).isEqualTo(GradleBuildSystem.DIALECT_KOTLIN);
	}

	@Test
	void convertWhenPlatformCompatibilityRangeIsNotSetShouldNotThrowException() {
		this.metadata = InitializrMetadataTestBuilder.withDefaults().setPlatformCompatibilityRange(null).build();
		ProjectRequest request = createProjectRequest();
		request.setBootVersion("1.5.9.RELEASE");
		assertThat(this.converter.convert(request, this.metadata).getPlatformVersion())
			.isEqualTo(Version.parse("1.5.9.RELEASE"));
	}

	@Test
	void convertShouldCallProjectRequestVersionTransformer() {
		ProjectRequestPlatformVersionTransformer transformer = mock(ProjectRequestPlatformVersionTransformer.class);
		Version v1Format = Version.parse("2.4.0.RELEASE");
		given(transformer.transform(v1Format, this.metadata)).willReturn(Version.parse("2.4.0"));
		ProjectRequest request = createProjectRequest();
		request.setBootVersion("2.4.0.RELEASE");
		ProjectDescription description = new DefaultProjectRequestToDescriptionConverter(transformer).convert(request,
				this.metadata);
		assertThat(description.getPlatformVersion()).hasToString("2.4.0");
		verify(transformer).transform(v1Format, this.metadata);
	}

	@Test
	void convertWhenSpringBootVersionInvalidShouldThrowException() {
		this.metadata = InitializrMetadataTestBuilder.withDefaults()
			.setPlatformCompatibilityRange("[2.0.0.RELEASE,2.3.0.M1)")
			.build();
		ProjectRequest request = createProjectRequest();
		request.setBootVersion("1.5.9.RELEASE");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
			.isThrownBy(() -> this.converter.convert(request, this.metadata))
			.withMessage(
					"Invalid Spring Boot version '1.5.9.RELEASE', Spring Boot compatibility range is >=2.0.0.RELEASE and <2.3.0.M1");
	}

	@ParameterizedTest
	@ValueSource(strings = { "not-a-version", "", "99999999999999", "2.99999999999999" })
	void convertWhenSpringBootVersionIsNotAVersionShouldThrowException(String bootVersion) {
		ProjectRequest request = createProjectRequest();
		request.setBootVersion(bootVersion);
		assertThatExceptionOfType(InvalidProjectRequestException.class)
			.isThrownBy(() -> this.converter.convert(request, this.metadata))
			.withMessage("Invalid Spring Boot version '" + bootVersion + "' check project metadata");
	}

	@Test
	void convertWhenSpringBootVersionIsNotSetShouldUseDefault() {
		ProjectRequest request = createProjectRequest();
		request.setBootVersion(null);
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getPlatformVersion()).isEqualTo(Version.parse("2.4.1"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "2", "2.x", "2.x.x", "2.4", "2.4.x" })
	void convertShouldResolveWildcardSpringBootVersion(String bootVersion) {
		ProjectRequest request = createProjectRequest();
		request.setBootVersion(bootVersion);
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getPlatformVersion()).isEqualTo(Version.parse("2.4.1"));
	}

	@Test
	void convertShouldResolveWildcardSpringBootVersionToVersionWithQualifier() {
		ProjectRequest request = createProjectRequest();
		request.setBootVersion("2.2.x");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getPlatformVersion()).isEqualTo(Version.parse("2.2.17.RELEASE"));
	}

	@Test
	void convertWhenWildcardSpringBootVersionDoesNotMatchShouldThrowException() {
		ProjectRequest request = createProjectRequest();
		request.setBootVersion("2.6.x");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
			.isThrownBy(() -> this.converter.convert(request, this.metadata))
			.withMessage("Invalid Spring Boot version '2.6.x' check project metadata");
	}

	@ParameterizedTest
	@ValueSource(strings = { "2.x.3", "2.4.x-M1" })
	void convertWhenSpringBootVersionUsesUnsupportedWildcardShouldThrowException(String bootVersion) {
		ProjectRequest request = createProjectRequest();
		request.setBootVersion(bootVersion);
		assertThatExceptionOfType(InvalidProjectRequestException.class)
			.isThrownBy(() -> this.converter.convert(request, this.metadata))
			.withMessage("Invalid Spring Boot version '" + bootVersion + "' check project metadata");
	}

	@Test
	void convertWhenWildcardSpringBootVersionResolvesToIncompatibleVersionShouldThrowException() {
		this.metadata = InitializrMetadataTestBuilder.withDefaults()
			.setPlatformCompatibilityRange("[2.0.0.RELEASE,2.3.0.M1)")
			.build();
		ProjectRequest request = createProjectRequest();
		request.setBootVersion("2.x.x");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
			.isThrownBy(() -> this.converter.convert(request, this.metadata))
			.withMessage(
					"Invalid Spring Boot version '2.4.1', Spring Boot compatibility range is >=2.0.0.RELEASE and <2.3.0.M1");
	}

	@Test
	void convertWhenPackagingIsInvalidShouldThrowException() {
		ProjectRequest request = createProjectRequest();
		request.setPackaging("star");
		assertThatExceptionOfType(InvalidProjectRequestException.class)
			.isThrownBy(() -> this.converter.convert(request, this.metadata))
			.withMessage("Unknown packaging 'star' check project metadata");
	}

	@Test
	void convertWhenLanguageIsInvalidShouldThrowException() {
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
		dependency.setRange(new VersionRange(Version.parse("2.5.0.M1")));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("foo", dependency)
			.build();
		ProjectRequest request = createProjectRequest();
		request.setDependencies(Collections.singletonList("foo"));
		assertThatExceptionOfType(InvalidProjectRequestException.class)
			.isThrownBy(() -> this.converter.convert(request, metadata))
			.withMessage("Dependency 'foo' is not compatible " + "with Spring Boot 2.4.1");
	}

	@Test
	void convertShouldSetApplicationNameForProjectDescriptionFromRequestWhenPresent() {
		ProjectRequest request = createProjectRequest();
		request.setApplicationName("MyApplication");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getApplicationName()).isEqualTo("MyApplication");
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
		InitializrMetadata testMetadata = InitializrMetadataTestBuilder.withDefaults().addType(type).build();
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
		assertThat(description.getPackaging()).isNotNull();
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
	void convertShouldSetLanguageForProjectDescriptionFromRequest() {
		ProjectRequest request = createProjectRequest();
		request.setJavaVersion("1.8");
		ProjectDescription description = this.converter.convert(request, this.metadata);
		assertThat(description.getLanguage()).isNotNull();
		assertThat(description.getLanguage().id()).isEqualTo("java");
		assertThat(description.getLanguage().jvmVersion()).isEqualTo("1.8");
	}

	private ProjectRequest createProjectRequest() {
		WebProjectRequest request = new WebProjectRequest();
		request.initialize(this.metadata);
		return request;
	}

}
