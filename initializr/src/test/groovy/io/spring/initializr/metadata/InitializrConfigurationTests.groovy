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

package io.spring.initializr.metadata

import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * @author Stephane Nicoll
 */
class InitializrConfigurationTests {

	private final InitializrConfiguration properties = new InitializrConfiguration()

	@Test
	void generateApplicationNameSimple() {
		assertEquals 'DemoApplication', this.properties.generateApplicationName('demo')
	}

	@Test
	void generateApplicationNameSimpleApplication() {
		assertEquals 'DemoApplication', this.properties.generateApplicationName('demoApplication')
	}

	@Test
	void generateApplicationNameSimpleCamelCase() {
		assertEquals 'MyDemoApplication', this.properties.generateApplicationName('myDemo')
	}

	@Test
	void generateApplicationNameSimpleUnderscore() {
		assertEquals 'MyDemoApplication', this.properties.generateApplicationName('my_demo')
	}

	@Test
	void generateApplicationNameSimpleColon() {
		assertEquals 'MyDemoApplication', this.properties.generateApplicationName('my:demo')
	}

	@Test
	void generateApplicationNameSimpleSpace() {
		assertEquals 'MyDemoApplication', this.properties.generateApplicationName('my demo')
	}

	@Test
	void generateApplicationNamSimpleDash() {
		assertEquals 'MyDemoApplication', this.properties.generateApplicationName('my-demo')
	}

	@Test
	void generateApplicationNameUpperCaseUnderscore() {
		assertEquals 'MyDemoApplication', this.properties.generateApplicationName('MY_DEMO')
	}

	@Test
	void generateApplicationNameUpperCaseDash() {
		assertEquals 'MyDemoApplication', this.properties.generateApplicationName('MY-DEMO')
	}

	@Test
	void generateApplicationNameMultiSpaces() {
		assertEquals 'MyDemoApplication', this.properties.generateApplicationName('   my    demo ')
	}

	@Test
	void generateApplicationNameMultiSpacesUpperCase() {
		assertEquals 'MyDemoApplication', this.properties.generateApplicationName('   MY    DEMO ')
	}

	@Test
	void generateApplicationNameNull() {
		assertEquals this.properties.env.fallbackApplicationName, this.properties.generateApplicationName(null)
	}

	@Test
	void generateApplicationNameInvalidStartCharacter() {
		assertEquals this.properties.env.fallbackApplicationName, this.properties.generateApplicationName('1MyDemo')
	}

	@Test
	void generateApplicationNameInvalidPartCharacter() {
		assertEquals this.properties.env.fallbackApplicationName, this.properties.generateApplicationName('MyDe|mo')
	}

	@Test
	void generateApplicationNameInvalidApplicationName() {
		assertEquals this.properties.env.fallbackApplicationName, this.properties.generateApplicationName('SpringBoot')
	}

	@Test
	void generateApplicationNameAnotherInvalidApplicationName() {
		assertEquals this.properties.env.fallbackApplicationName, this.properties.generateApplicationName('Spring')
	}

	@Test
	void generatePackageNameSimple() {
		assertEquals 'com.foo', this.properties.cleanPackageName('com.foo', 'com.example')
	}

	@Test
	void generatePackageNameSimpleUnderscore() {
		assertEquals 'com.my_foo', this.properties.cleanPackageName('com.my_foo', 'com.example')
	}

	@Test
	void generatePackageNameSimpleColon() {
		assertEquals 'com.foo', this.properties.cleanPackageName('com:foo', 'com.example')
	}

	@Test
	void generatePackageNameMultipleDashers() {
		assertEquals 'com.foo', this.properties.cleanPackageName('com--foo', 'com.example')
	}

	@Test
	void generatePackageNameMultipleSpaces() {
		assertEquals 'com.foo', this.properties.cleanPackageName('  com   foo  ', 'com.example')
	}

	@Test
	void generatePackageNameNull() {
		assertEquals 'com.example', this.properties.cleanPackageName(null, 'com.example')
	}

	@Test
	void generatePackageNameInvalidStartCharacter() {
		assertEquals 'com.example', this.properties.cleanPackageName('0om.foo', 'com.example')
	}

	@Test
	void generatePackageNameInvalidPackageName() {
		assertEquals 'com.example', this.properties.cleanPackageName('org.springframework', 'com.example')
	}

	@Test
	void validateArtifactRepository() {
		this.properties.env.artifactRepository = 'http://foo/bar'
		assertEquals 'http://foo/bar/', this.properties.env.artifactRepository
	}

}
