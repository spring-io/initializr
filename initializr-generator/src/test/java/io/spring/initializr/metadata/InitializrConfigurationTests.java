/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.metadata;

import io.spring.initializr.metadata.InitializrConfiguration.Env.Kotlin;
import io.spring.initializr.util.Version;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InitializrConfiguration}.
 *
 * @author Stephane Nicoll
 */
public class InitializrConfigurationTests {

	private final InitializrConfiguration properties = new InitializrConfiguration();

	@Test
	public void generateApplicationNameSimple() {
		assertThat(this.properties.generateApplicationName("demo"))
				.isEqualTo("DemoApplication");
	}

	@Test
	public void generateApplicationNameSimpleApplication() {
		assertThat(this.properties.generateApplicationName("demoApplication"))
				.isEqualTo("DemoApplication");
	}

	@Test
	public void generateApplicationNameSimpleCamelCase() {
		assertThat(this.properties.generateApplicationName("myDemo"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	public void generateApplicationNameSimpleUnderscore() {
		assertThat(this.properties.generateApplicationName("my_demo"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	public void generateApplicationNameSimpleColon() {
		assertThat(this.properties.generateApplicationName("my:demo"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	public void generateApplicationNameSimpleSpace() {
		assertThat(this.properties.generateApplicationName("my demo"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	public void generateApplicationNameSimpleDash() {
		assertThat(this.properties.generateApplicationName("my-demo"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	public void generateApplicationNameUpperCaseUnderscore() {
		assertThat(this.properties.generateApplicationName("MY_DEMO"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	public void generateApplicationNameUpperCaseDash() {
		assertThat(this.properties.generateApplicationName("MY-DEMO"))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	public void generateApplicationNameMultiSpaces() {
		assertThat(this.properties.generateApplicationName("   my    demo "))
				.isEqualTo("MyDemoApplication");
	}

	@Test
	public void generateApplicationNameMultiSpacesUpperCase() {
		assertThat("MyDemoApplication")
				.isEqualTo(this.properties.generateApplicationName("   MY    DEMO "));
	}

	@Test
	public void generateApplicationNameNull() {
		assertThat(this.properties.generateApplicationName(null))
				.isEqualTo(this.properties.getEnv().getFallbackApplicationName());
	}

	@Test
	public void generateApplicationNameInvalidStartCharacter() {
		assertThat(this.properties.generateApplicationName("1MyDemo"))
				.isEqualTo(this.properties.getEnv().getFallbackApplicationName());
	}

	@Test
	public void generateApplicationNameInvalidPartCharacter() {
		assertThat(this.properties.generateApplicationName("MyDe|mo"))
				.isEqualTo(this.properties.getEnv().getFallbackApplicationName());
	}

	@Test
	public void generateApplicationNameInvalidApplicationName() {
		assertThat(this.properties.generateApplicationName("SpringBoot"))
				.isEqualTo(this.properties.getEnv().getFallbackApplicationName());
	}

	@Test
	public void generateApplicationNameAnotherInvalidApplicationName() {
		assertThat(this.properties.generateApplicationName("Spring"))
				.isEqualTo(this.properties.getEnv().getFallbackApplicationName());
	}

	@Test
	public void generatePackageNameSimple() {
		assertThat(this.properties.cleanPackageName("com.foo", "com.example"))
				.isEqualTo("com.foo");
	}

	@Test
	public void generatePackageNameSimpleUnderscore() {
		assertThat(this.properties.cleanPackageName("com.my_foo", "com.example"))
				.isEqualTo("com.my_foo");
	}

	@Test
	public void generatePackageNameSimpleColon() {
		assertThat(this.properties.cleanPackageName("com:foo", "com.example"))
				.isEqualTo("com.foo");
	}

	@Test
	public void generatePackageNameMultipleDashers() {
		assertThat(this.properties.cleanPackageName("com.foo--bar", "com.example"))
				.isEqualTo("com.foobar");
	}

	@Test
	public void generatePackageNameMultipleSpaces() {
		assertThat(this.properties.cleanPackageName("  com   foo  ", "com.example"))
				.isEqualTo("com.foo");
	}

	@Test
	public void generatePackageNameNull() {
		assertThat(this.properties.cleanPackageName(null, "com.example"))
				.isEqualTo("com.example");
	}

	@Test
	public void generatePackageNameInvalidStartCharacter() {
		assertThat(this.properties.cleanPackageName("0com.foo", "com.example"))
				.isEqualTo("com.foo");
	}

	@Test
	public void generatePackageNameVersion() {
		assertThat(this.properties.cleanPackageName("com.foo.test-1.4.5", "com.example"))
				.isEqualTo("com.foo.test145");
	}

	@Test
	public void generatePackageNameInvalidPackageName() {
		assertThat(this.properties.cleanPackageName("org.springframework", "com.example"))
				.isEqualTo("com.example");
	}

	@Test
	public void validateArtifactRepository() {
		this.properties.getEnv().setArtifactRepository("http://foo/bar");
		assertThat(this.properties.getEnv().getArtifactRepository())
				.isEqualTo("http://foo/bar/");
	}

	@Test
	public void resolveKotlinVersionMatchingMapping() {
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
	public void resolveKotlinVersionUsingDefault() {
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
