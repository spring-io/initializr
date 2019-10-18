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

package io.spring.initializr.metadata;

import java.util.Arrays;
import java.util.stream.Stream;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Kotlin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InitializrConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Chris Bono
 */
class InitializrConfigurationTests {

	// Taken from https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.9
	private static final String[] RESERVED_KEYWORDS = { "abstract", "assert", "boolean", "break", "byte", "case",
			"catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends",
			"false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
			"interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return",
			"short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient",
			"try", "true", "void", "volatile", "while" };

	private final InitializrConfiguration properties = new InitializrConfiguration();

	@Test
	void generateApplicationNameSimple() {
		assertThat(this.properties.generateApplicationName("demo")).isEqualTo("DemoApplication");
	}

	@Test
	void generateApplicationNameSimpleApplication() {
		assertThat(this.properties.generateApplicationName("demoApplication")).isEqualTo("DemoApplication");
	}

	@Test
	void generateApplicationNameSimpleCamelCase() {
		assertThat(this.properties.generateApplicationName("myDemo")).isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameSimpleUnderscore() {
		assertThat(this.properties.generateApplicationName("my_demo")).isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameSimpleColon() {
		assertThat(this.properties.generateApplicationName("my:demo")).isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameSimpleSpace() {
		assertThat(this.properties.generateApplicationName("my demo")).isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameSimpleDash() {
		assertThat(this.properties.generateApplicationName("my-demo")).isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameUpperCaseUnderscore() {
		assertThat(this.properties.generateApplicationName("MY_DEMO")).isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameUpperCaseDash() {
		assertThat(this.properties.generateApplicationName("MY-DEMO")).isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameMultiSpaces() {
		assertThat(this.properties.generateApplicationName("   my    demo ")).isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameMultiSpacesUpperCase() {
		assertThat("MyDemoApplication").isEqualTo(this.properties.generateApplicationName("   MY    DEMO "));
	}

	@Test
	void generateApplicationNameNull() {
		assertThat(this.properties.generateApplicationName(null))
				.isEqualTo(this.properties.getEnv().getFallbackApplicationName());
	}

	@Test
	void generateApplicationNameInvalidStartCharacter() {
		assertThat(this.properties.generateApplicationName("1MyDemo"))
				.isEqualTo(this.properties.getEnv().getFallbackApplicationName());
	}

	@Test
	void generateApplicationNameInvalidPartCharacter() {
		assertThat(this.properties.generateApplicationName("MyDe|mo"))
				.isEqualTo(this.properties.getEnv().getFallbackApplicationName());
	}

	@Test
	void generateApplicationNameInvalidApplicationName() {
		assertThat(this.properties.generateApplicationName("SpringBoot"))
				.isEqualTo(this.properties.getEnv().getFallbackApplicationName());
	}

	@Test
	void generateApplicationNameAnotherInvalidApplicationName() {
		assertThat(this.properties.generateApplicationName("Spring"))
				.isEqualTo(this.properties.getEnv().getFallbackApplicationName());
	}

	@Test
	void generatePackageNameSimple() {
		assertThat(this.properties.cleanPackageName("com.foo", "com.example")).isEqualTo("com.foo");
	}

	@Test
	void generatePackageNameSimpleUnderscore() {
		assertThat(this.properties.cleanPackageName("com.my_foo", "com.example")).isEqualTo("com.my_foo");
	}

	@Test
	void generatePackageNameSimpleColon() {
		assertThat(this.properties.cleanPackageName("com:foo", "com.example")).isEqualTo("com.foo");
	}

	@Test
	void generatePackageNameMultipleDashers() {
		assertThat(this.properties.cleanPackageName("com.foo--bar", "com.example")).isEqualTo("com.foobar");
	}

	@Test
	void generatePackageNameMultipleSpaces() {
		assertThat(this.properties.cleanPackageName("  com   foo  ", "com.example")).isEqualTo("com.foo");
	}

	@Test
	void generatePackageNameNull() {
		assertThat(this.properties.cleanPackageName(null, "com.example")).isEqualTo("com.example");
	}

	@Test
	void generatePackageNameDot() {
		assertThat(this.properties.cleanPackageName(".", "com.example")).isEqualTo("com.example");
	}

	@Test
	void generatePackageNameWhitespaces() {
		assertThat(this.properties.cleanPackageName("    ", "com.example")).isEqualTo("com.example");
	}

	@Test
	void generatePackageNameInvalidStartCharacter() {
		assertThat(this.properties.cleanPackageName("0com.foo", "com.example")).isEqualTo("com.foo");
	}

	@Test
	void generatePackageNameVersion() {
		assertThat(this.properties.cleanPackageName("com.foo.test-1.4.5", "com.example")).isEqualTo("com.foo.test145");
	}

	@Test
	void generatePackageNameInvalidPackageName() {
		assertThat(this.properties.cleanPackageName("org.springframework", "com.example")).isEqualTo("com.example");
	}

	@ParameterizedTest
	@MethodSource("reservedKeywords")
	void generatePackageNameReservedKeywordsMiddleOfPackageName(final String keyword) {
		final String badPackageName = String.format("com.%s.foo", keyword);
		assertThat(this.properties.cleanPackageName(badPackageName, "com.example")).isEqualTo("com.example");
	}

	@ParameterizedTest
	@MethodSource("reservedKeywords")
	void generatePackageNameReservedKeywordsStartOfPackageName(final String keyword) {
		final String badPackageName = String.format("%s.com.foo", keyword);
		assertThat(this.properties.cleanPackageName(badPackageName, "com.example")).isEqualTo("com.example");
	}

	@ParameterizedTest
	@MethodSource("reservedKeywords")
	void generatePackageNameReservedKeywordsEndOfPackageName(final String keyword) {
		final String badPackageName = String.format("com.foo.%s", keyword);
		assertThat(this.properties.cleanPackageName(badPackageName, "com.example")).isEqualTo("com.example");
	}

	@ParameterizedTest
	@MethodSource("reservedKeywords")
	void generatePackageNameReservedKeywordsEntirePackageName(final String keyword) {
		assertThat(this.properties.cleanPackageName(keyword, "com.example")).isEqualTo("com.example");
	}

	private static Stream<String> reservedKeywords() {
		return Arrays.stream(RESERVED_KEYWORDS);
	}

	@Test
	void validateArtifactRepository() {
		this.properties.getEnv().setArtifactRepository("http://foo/bar");
		assertThat(this.properties.getEnv().getArtifactRepository()).isEqualTo("http://foo/bar/");
	}

	@Test
	void resolveKotlinVersionMatchingMapping() {
		Kotlin kotlin = this.properties.getEnv().getKotlin();
		kotlin.setDefaultVersion("1.2.3");
		kotlin.getMappings().add(createKotlinVersionMapping("[1.4.0.RELEASE,1.5.0.RELEASE)", "1.5"));
		kotlin.getMappings().add(createKotlinVersionMapping("1.5.0.RELEASE", "1.6"));
		kotlin.validate();
		assertThat(kotlin.resolveKotlinVersion(Version.parse("1.5.3.RELEASE"))).isEqualTo("1.6");
	}

	@Test
	void resolveKotlinVersionUsingDefault() {
		Kotlin kotlin = this.properties.getEnv().getKotlin();
		kotlin.setDefaultVersion("1.2.3");
		kotlin.getMappings().add(createKotlinVersionMapping("[1.4.0.RELEASE,1.5.0.RELEASE)", "1.5"));
		kotlin.validate();
		assertThat(kotlin.resolveKotlinVersion(Version.parse("1.3.2.RELEASE"))).isEqualTo("1.2.3");
	}

	private Kotlin.Mapping createKotlinVersionMapping(String compatibilityRange, String kotlinVersion) {
		Kotlin.Mapping mapping = new Kotlin.Mapping();
		mapping.setCompatibilityRange(compatibilityRange);
		mapping.setVersion(kotlinVersion);
		return mapping;
	}

}
