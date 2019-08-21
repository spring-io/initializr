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

package io.spring.initializr.generator.test.buildsystem.gradle;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.assertj.core.api.AssertProvider;
import org.junit.jupiter.api.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Test for {@link GroovyDslGradleSettingsAssert}.
 *
 * @author Stephane Nicoll
 */
class GroovyDslGradleSettingsAssertTests {

	@Test
	void hasProjectName() {
		assertThat(forSampleGradleSettings()).hasProjectName("demo");
	}

	@Test
	void hasProjectNameWithWrongValue() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleGradleSettings()).hasProjectName("another-project"));
	}

	private AssertProvider<GroovyDslGradleSettingsAssert> forSampleGradleSettings() {
		String path = "project/build/gradle/sample-settings.gradle";
		try (InputStream in = new ClassPathResource(path).getInputStream()) {
			String content = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
			return () -> new GroovyDslGradleSettingsAssert(content);
		}
		catch (IOException ex) {
			throw new IllegalStateException("No content found at " + path, ex);
		}
	}

}
