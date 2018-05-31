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

package io.spring.initializr.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link VersionProperty}.
 *
 * @author Stephane Nicoll
 */
public class VersionPropertyTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void testStandardProperty() {
		assertThat(new VersionProperty("spring-boot.version").toStandardFormat())
				.isEqualTo("spring-boot.version");
	}

	@Test
	public void testCamelCaseProperty() {
		assertThat(new VersionProperty("spring-boot.version").toCamelCaseFormat())
				.isEqualTo("springBootVersion");
	}

	@Test
	public void testStandardPropertyWithNoSeparator() {
		assertThat(new VersionProperty("springbootversion").toStandardFormat())
				.isEqualTo("springbootversion");
	}

	@Test
	public void testCamelCasePropertyWithNoSeparator() {
		assertThat(new VersionProperty("springbootversion").toCamelCaseFormat())
				.isEqualTo("springbootversion");
	}

	@Test
	public void testInvalidPropertyUpperCase() {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("upper case");
		new VersionProperty("Spring-boot.version");
	}

	@Test
	public void testInvalidPropertyIllegalCharacter() {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown.expectMessage("Unsupported character");
		new VersionProperty("spring-boot_version");
	}

}
