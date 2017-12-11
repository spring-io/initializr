/*
 * Copyright 2012-2015 the original author or authors.
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
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link InitializrConfiguration}.
 *
 * @author Stephane Nicoll
 */
public class InitializrConfigurationTests {

	private final InitializrConfiguration properties = new InitializrConfiguration();

	@Test
	public void generateApplicationNameSimple() {
		assertEquals("DemoApplication", this.properties.generateApplicationName("demo"));
	}

	@Test
	public void generateApplicationNameSimpleApplication() {
		assertEquals("DemoApplication", this.properties.generateApplicationName("demoApplication"));
	}

	@Test
	public void generateApplicationNameSimpleCamelCase() {
		assertEquals("MyDemoApplication", this.properties.generateApplicationName("myDemo"));
	}

	@Test
	public void generateApplicationNameSimpleUnderscore() {
		assertEquals("MyDemoApplication", this.properties.generateApplicationName("my_demo"));
	}

	@Test
	public void generateApplicationNameSimpleColon() {
		assertEquals("MyDemoApplication", this.properties.generateApplicationName("my:demo"));
	}

	@Test
	public void generateApplicationNameSimpleSpace() {
		assertEquals("MyDemoApplication", this.properties.generateApplicationName("my demo"));
	}

	@Test
	public void generateApplicationNameSimpleDash() {
		assertEquals("MyDemoApplication", this.properties.generateApplicationName("my-demo"));
	}

	@Test
	public void generateApplicationNameUpperCaseUnderscore() {
		assertEquals("MyDemoApplication", this.properties.generateApplicationName("MY_DEMO"));
	}

	@Test
	public void generateApplicationNameUpperCaseDash() {
		assertEquals("MyDemoApplication", this.properties.generateApplicationName("MY-DEMO"));
	}

	@Test
	public void generateApplicationNameMultiSpaces() {
		assertEquals("MyDemoApplication", this.properties.generateApplicationName("   my    demo "));
	}

	@Test
	public void generateApplicationNameMultiSpacesUpperCase() {
		assertEquals("MyDemoApplication", this.properties.generateApplicationName("   MY    DEMO "));
	}

	@Test
	public void generateApplicationNameNull() {
		assertEquals(this.properties.getEnv().getFallbackApplicationName(), this.properties.generateApplicationName(null));
	}

	@Test
	public void generateApplicationNameInvalidStartCharacter() {
		assertEquals(this.properties.getEnv().getFallbackApplicationName(), this.properties.generateApplicationName("1MyDemo"));
	}

	@Test
	public void generateApplicationNameInvalidPartCharacter() {
		assertEquals(this.properties.getEnv().getFallbackApplicationName(), this.properties.generateApplicationName("MyDe|mo"));
	}

	@Test
	public void generateApplicationNameInvalidApplicationName() {
		assertEquals(this.properties.getEnv().getFallbackApplicationName(), this.properties.generateApplicationName("SpringBoot"));
	}

	@Test
	public void generateApplicationNameAnotherInvalidApplicationName() {
		assertEquals(this.properties.getEnv().getFallbackApplicationName(), this.properties.generateApplicationName("Spring"));
	}

	@Test
	public void generatePackageNameSimple() {
		assertEquals("com.foo", this.properties.cleanPackageName("com.foo", "com.example"));
	}

	@Test
	public void generatePackageNameSimpleUnderscore() {
		assertEquals("com.my_foo", this.properties.cleanPackageName("com.my_foo", "com.example"));
	}

	@Test
	public void generatePackageNameSimpleColon() {
		assertEquals("com.foo", this.properties.cleanPackageName("com:foo", "com.example"));
	}

	@Test
	public void generatePackageNameMultipleDashers() {
		assertEquals("com.foobar", this.properties.cleanPackageName("com.foo--bar", "com.example"));
	}

	@Test
	public void generatePackageNameMultipleSpaces() {
		assertEquals("com.foo", this.properties.cleanPackageName("  com   foo  ", "com.example"));
	}

	@Test
	public void generatePackageNameNull() {
		assertEquals("com.example", this.properties.cleanPackageName(null, "com.example"));
	}

	@Test
	public void generatePackageNameInvalidStartCharacter() {
		assertEquals("com.foo", this.properties.cleanPackageName("0com.foo", "com.example"));
	}

	@Test
	public void generatePackageNameVersion() {
		assertEquals("com.foo.test145", this.properties.cleanPackageName(
				"com.foo.test-1.4.5", "com.example"));
	}

	@Test
	public void generatePackageNameInvalidPackageName() {
		assertEquals("com.example", this.properties.cleanPackageName("org.springframework", "com.example"));
	}

	@Test
	public void validateArtifactRepository() {
		this.properties.getEnv().setArtifactRepository("http://foo/bar");
		assertEquals("http://foo/bar/", this.properties.getEnv().getArtifactRepository());
	}

	@Test
	public void resolveKotlinVersionMatchingMapping() {
		Kotlin kotlin = this.properties.getEnv().getKotlin();
		kotlin.setDefaultVersion("1.2.3");
		kotlin.getMappings().add(createKotlinVersionMapping(
				"[1.4.0.RELEASE,1.5.0.RELEASE)", "1.5"));
		kotlin.getMappings().add(createKotlinVersionMapping("1.5.0.RELEASE", "1.6"));
		kotlin.validate();
		assertThat(kotlin.resolveKotlinVersion(Version.parse("1.5.3.RELEASE")))
				.isEqualTo("1.6");
	}

	@Test
	public void resolveKotlinVersionUsingDefault() {
		Kotlin kotlin = this.properties.getEnv().getKotlin();
		kotlin.setDefaultVersion("1.2.3");
		kotlin.getMappings().add(createKotlinVersionMapping(
				"[1.4.0.RELEASE,1.5.0.RELEASE)", "1.5"));
		kotlin.validate();
		assertThat(kotlin.resolveKotlinVersion(Version.parse("1.3.2.RELEASE")))
				.isEqualTo("1.2.3");
	}

	private Kotlin.Mapping createKotlinVersionMapping(String versionRange,
			String kotlinVersion){
		Kotlin.Mapping mapping = new Kotlin.Mapping();
		mapping.setVersionRange(versionRange);
		mapping.setVersion(kotlinVersion);
		return mapping;
	}

}
