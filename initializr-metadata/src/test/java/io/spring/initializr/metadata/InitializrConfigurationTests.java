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

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Kotlin;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InitializrConfiguration}.
 *
 * @author Stephane Nicoll
 */
class InitializrConfigurationTests {

	private final InitializrConfiguration properties = new InitializrConfiguration();

	@Test
	void generateApplicationNameSimple() {
		assertThat(this.properties.generateApplicationName("demo"))
				.isEqualTo("DemoApplication");
	}

	@Test
	void generateApplicationNameSimpleApplication() {
		assertThat(this.properties.generateApplicationName("demoApplication"))
				.isEqualTo("DemoApplication");
	}

	@Test
	void generateApplicationNameSimpleCamelCase() {
		assertThat(this.properties.generateApplicationName("myDemo"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameSimpleUnderscore() {
		assertThat(this.properties.generateApplicationName("my_demo"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameSimpleColon() {
		assertThat(this.properties.generateApplicationName("my:demo"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameSimpleSpace() {
		assertThat(this.properties.generateApplicationName("my demo"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameSimpleDash() {
		assertThat(this.properties.generateApplicationName("my-demo"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameUpperCaseUnderscore() {
		assertThat(this.properties.generateApplicationName("MY_DEMO"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameUpperCaseDash() {
		assertThat(this.properties.generateApplicationName("MY-DEMO"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameMultiSpaces() {
		assertThat(this.properties.generateApplicationName("   my    demo "))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	void generateApplicationNameMultiSpacesUpperCase() {
		assertThat("MyDemoApplication")
				.isEqualTo(this.properties.generateApplicationName("   MY    DEMO "));
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
		assertThat(this.properties.cleanPackageName("com.foo", "com.example"))
				.isEqualTo("com.foo");
	}

	@Test
	void generatePackageNameSimpleUnderscore() {
		assertThat(this.properties.cleanPackageName("com.my_foo", "com.example"))
				.isEqualTo("com.my_foo");
	}

	@Test
	void generatePackageNameSimpleColon() {
		assertThat(this.properties.cleanPackageName("com:foo", "com.example"))
				.isEqualTo("com.foo");
	}

	@Test
	void generatePackageNameMultipleDashers() {
		assertThat(this.properties.cleanPackageName("com.foo--bar", "com.example"))
				.isEqualTo("com.foobar");
	}

	@Test
	void generatePackageNameMultipleSpaces() {
		assertThat(this.properties.cleanPackageName("  com   foo  ", "com.example"))
				.isEqualTo("com.foo");
	}

	@Test
	void generatePackageNameNull() {
		assertThat(this.properties.cleanPackageName(null, "com.example"))
				.isEqualTo("com.example");
	}

	@Test
	void generatePackageNameDot() {
		assertThat(this.properties.cleanPackageName(".", "com.example"))
				.isEqualTo("com.example");
	}

	@Test
	void generatePackageNameWhitespaces() {
		assertThat(this.properties.cleanPackageName("    ", "com.example"))
				.isEqualTo("com.example");
	}

	@Test
	void generatePackageNameInvalidStartCharacter() {
		assertThat(this.properties.cleanPackageName("0com.foo", "com.example"))
				.isEqualTo("com.foo");
	}

	@Test
	void generatePackageNameVersion() {
		assertThat(this.properties.cleanPackageName("com.foo.test-1.4.5", "com.example"))
				.isEqualTo("com.foo.test145");
	}

	@Test
	void generatePackageNameInvalidPackageName() {
		assertThat(this.properties.cleanPackageName("org.springframework", "com.example"))
				.isEqualTo("com.example");
	}

	@Test
	void validateArtifactRepository() {
		this.properties.getEnv().setArtifactRepository("http://foo/bar");
		assertThat(this.properties.getEnv().getArtifactRepository())
				.isEqualTo("http://foo/bar/");
	}

	@Test
	void resolveKotlinVersionMatchingMapping() {
		Kotlin kotlin = this.properties.getEnv().getKotlin();
		kotlin.setDefaultVersion("1.2.3");
		kotlin.getMappings()
				.add(createKotlinVersionMapping("[1.4.0.RELEASE,1.5.0.RELEASE)", "1.5"));
		kotlin.getMappings().add(createKotlinVersionMapping("1.5.0.RELEASE", "1.6"));
		kotlin.validate();
		assertThat(kotlin.resolveKotlinVersion(Version.parse("1.5.3.RELEASE")))
				.isEqualTo("1.6");
	}

	@Test
	void resolveKotlinVersionUsingDefault() {
		Kotlin kotlin = this.properties.getEnv().getKotlin();
		kotlin.setDefaultVersion("1.2.3");
		kotlin.getMappings()
				.add(createKotlinVersionMapping("[1.4.0.RELEASE,1.5.0.RELEASE)", "1.5"));
		kotlin.validate();
		assertThat(kotlin.resolveKotlinVersion(Version.parse("1.3.2.RELEASE")))
				.isEqualTo("1.2.3");
	}

	private Kotlin.Mapping createKotlinVersionMapping(String versionRange,
			String kotlinVersion) {
		Kotlin.Mapping mapping = new Kotlin.Mapping();
		mapping.setVersionRange(versionRange);
		mapping.setVersion(kotlinVersion);
		return mapping;
	}

}
