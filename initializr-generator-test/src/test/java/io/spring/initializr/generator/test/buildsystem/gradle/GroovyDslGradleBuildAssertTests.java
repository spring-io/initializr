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
 * Tests for {@link GroovyDslGradleBuildAssert}.
 *
 * @author Stephane Nicoll
 */
class GroovyDslGradleBuildAssertTests {

	@Test
	void hasPluginWithId() {
		assertThat(forSampleGradleBuild()).hasPlugin("java");
	}

	@Test
	void hasPluginWithIdAndVersion() {
		assertThat(forSampleGradleBuild()).hasPlugin("com.example", "1.0.0.RELEASE");
	}

	@Test
	void hasPluginWrongId() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleGradleBuild()).hasPlugin("com.another", "1.0.0.RELEASE"));
	}

	@Test
	void hasPluginWrongValue() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleGradleBuild()).hasPlugin("com.example", "2.0.0.RELEASE"));
	}

	@Test
	void hasVersion() {
		assertThat(forSampleGradleBuild()).hasVersion("0.0.1-SNAPSHOT");
	}

	@Test
	void hasVersionWithWrongValue() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleGradleBuild()).hasVersion("0.0.3-SNAPSHOT"));
	}

	@Test
	void hasSourceCompatibility() {
		assertThat(forSampleGradleBuild()).hasSourceCompatibility("1.8");
	}

	@Test
	void hasSourceCompatibilityWithWrongValue() {
		assertThatExceptionOfType(AssertionError.class)
				.isThrownBy(() -> assertThat(forSampleGradleBuild()).hasSourceCompatibility("11"));
	}

	@Test
	void containsOnlyExtProperties() {
		assertThat(forSampleGradleBuild()).containsOnlyExtProperties("acmeVersion", "Brussels.SR2");
	}

	@Test
	void containsOnlyExtPropertiesWithExtraValue() {
		assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(forSampleGradleBuild())
				.containsOnlyExtProperties("acmeVersion", "Brussels.SR2", "wrong", "1.0.0"));
	}

	private AssertProvider<GroovyDslGradleBuildAssert> forSampleGradleBuild() {
		String path = "project/build/gradle/sample-build.gradle";
		try (InputStream in = new ClassPathResource(path).getInputStream()) {
			String content = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
			return () -> new GroovyDslGradleBuildAssert(content);
		}
		catch (IOException ex) {
			throw new IllegalStateException("No content found at " + path, ex);
		}
	}

}
