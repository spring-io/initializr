/*
 * Copyright 2012-2024 the original author or authors.
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

import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Kotlin;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InitializrConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Chris Bono
 */
class InitializrConfigurationTests {

	private static final JavaLanguage JAVA = new JavaLanguage();

	private static final Language KOTLIN = new KotlinLanguage();

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
		assertThat(this.properties.cleanPackageName("com.foo", JAVA, "com.example")).isEqualTo("com.foo");
	}

	@Test
	void generatePackageNameSimpleUnderscore() {
		assertThat(this.properties.cleanPackageName("com.my_foo", JAVA, "com.example")).isEqualTo("com.my_foo");
	}

	@Test
	void generatePackageNameSimpleColon() {
		assertThat(this.properties.cleanPackageName("com:foo", JAVA, "com.example")).isEqualTo("com.foo");
	}

	@Test
	void generatePackageNameMultipleDashes() {
		assertThat(this.properties.cleanPackageName("com.foo--bar", JAVA, "com.example")).isEqualTo("com.foo__bar");
	}

	@Test
	void generatePackageNameMultipleSpaces() {
		assertThat(this.properties.cleanPackageName("  com   foo  ", JAVA, "com.example")).isEqualTo("com.foo");
	}

	@Test
	void generatePackageNameNull() {
		assertThat(this.properties.cleanPackageName(null, JAVA, "com.example")).isEqualTo("com.example");
	}

	@Test
	void generatePackageNameDot() {
		assertThat(this.properties.cleanPackageName(".", JAVA, "com.example")).isEqualTo("com.example");
	}

	@Test
	void generatePackageNameWhitespaces() {
		assertThat(this.properties.cleanPackageName("    ", JAVA, "com.example")).isEqualTo("com.example");
	}

	@Test
	void generatePackageNameInvalidStartCharacter() {
		assertThat(this.properties.cleanPackageName("0com.foo", JAVA, "com.example")).isEqualTo("_com.foo");
	}

	@Test
	void generatePackageNameVersion() {
		assertThat(this.properties.cleanPackageName("com.foo.test-1.4.5", JAVA, "com.example"))
			.isEqualTo("com.foo.test_145");
	}

	@Test
	void generatePackageNameInvalidPackageName() {
		assertThat(this.properties.cleanPackageName("org.springframework", JAVA, "com.example"))
			.isEqualTo("com.example");
	}

	@Test
	void generatePackageNameReservedKeywordsMiddleOfPackageName() {
		assertThat(this.properties.cleanPackageName("com.return.foo", JAVA, "com.example")).isEqualTo("com.example");
	}

	@Test
	void generatePackageNameReservedKeywordsStartOfPackageName() {
		assertThat(this.properties.cleanPackageName("false.com.foo", JAVA, "com.example")).isEqualTo("com.example");
	}

	@Test
	void generatePackageNameReservedKeywordsEndOfPackageName() {
		assertThat(this.properties.cleanPackageName("com.foo.null", JAVA, "com.example")).isEqualTo("com.example");
	}

	@Test
	void generatePackageNameReservedKeywordsEntirePackageName() {
		assertThat(this.properties.cleanPackageName("public", JAVA, "com.example")).isEqualTo("com.example");
	}

	@Test
	void generateKotlinPackageNameSimple() {
		assertThat(this.properties.cleanPackageName("com.foo", KOTLIN, "com.example")).isEqualTo("com.foo");
	}

	@Test
	void generateKotlinPackageNameSimpleUnderscore() {
		assertThat(this.properties.cleanPackageName("com.my_foo", KOTLIN, "com.example")).isEqualTo("com.my_foo");
	}

	@Test
	void generateKotlinPackageNameSimpleColon() {
		assertThat(this.properties.cleanPackageName("com:foo", KOTLIN, "com.example")).isEqualTo("com.foo");
	}

	@Test
	void generateKotlinPackageNameMultipleDashes() {
		assertThat(this.properties.cleanPackageName("com.foo--bar", KOTLIN, "com.example")).isEqualTo("com.foo__bar");
	}

	@Test
	void generateKotlinPackageNameMultipleSpaces() {
		assertThat(this.properties.cleanPackageName("  com   foo  ", KOTLIN, "com.example")).isEqualTo("com.foo");
	}

	@Test
	void generateKotlinPackageNameNull() {
		assertThat(this.properties.cleanPackageName(null, KOTLIN, "com.example")).isEqualTo("com.example");
	}

	@Test
	void generateKotlinPackageNameDot() {
		assertThat(this.properties.cleanPackageName(".", KOTLIN, "com.example")).isEqualTo("com.example");
	}

	@Test
	void generateKotlinPackageNameWhitespaces() {
		assertThat(this.properties.cleanPackageName("    ", KOTLIN, "com.example")).isEqualTo("com.example");
	}

	@Test
	void generateKotlinPackageNameInvalidStartCharacter() {
		assertThat(this.properties.cleanPackageName("0com.foo", KOTLIN, "com.example")).isEqualTo("_com.foo");
	}

	@Test
	void generateKotlinPackageNameVersion() {
		assertThat(this.properties.cleanPackageName("com.foo.test-1.4.5", KOTLIN, "com.example"))
			.isEqualTo("com.foo.test_145");
	}

	@Test
	void generateKotlinPackageNameInvalidPackageName() {
		assertThat(this.properties.cleanPackageName("org.springframework", KOTLIN, "com.example"))
			.isEqualTo("com.example");
	}

	@Test
	void generateKotlinPackageNameReservedKeywordsMiddleOfPackageName() {
		assertThat(this.properties.cleanPackageName("com.return.foo", KOTLIN, "com.example"))
			.isEqualTo("com.return.foo");
	}

	@Test
	void generateKotlinPackageNameReservedKeywordsStartOfPackageName() {
		assertThat(this.properties.cleanPackageName("false.com.foo", KOTLIN, "com.example")).isEqualTo("false.com.foo");
	}

	@Test
	void generateKotlinPackageNameReservedKeywordsEndOfPackageName() {
		assertThat(this.properties.cleanPackageName("com.foo.null", KOTLIN, "com.example")).isEqualTo("com.foo.null");
	}

	@Test
	void generateKotlinPackageNameReservedChar() {
		assertThat(this.properties.cleanPackageName("com._foo.null", KOTLIN, "com.example")).isEqualTo("com._foo.null");
	}

	@Test
	void generateKotlinPackageNameJavaReservedKeywords() {
		assertThat(this.properties.cleanPackageName("public", KOTLIN, "com.example")).isEqualTo("public");
	}

	@Test
	void generateKotlinPackageNameJavaReservedKeywordsEntirePackageName() {
		assertThat(this.properties.cleanPackageName("public.package", KOTLIN, "com.example"))
			.isEqualTo("public.package");
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
