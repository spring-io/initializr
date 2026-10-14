/*
 * Copyright 2012 - present the original author or authors.
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
 * Tests for {@link KotlinDslGradleSettingsAssert}
 *
 * @author Prithvi singh
 */
class KotlinDslGradleSettingsAssertTests {

	@Test
	void hasProjectName() {
		assertThat(forSampleGradleSettings()).hasProjectName("demo");
	}

	@Test
	void hasProjectNameWithWrongValue() {
		assertThatExceptionOfType(AssertionError.class)
			.isThrownBy(() -> assertThat(forSampleGradleSettings()).hasProjectName("another-project"));
	}

	private AssertProvider<KotlinDslGradleSettingsAssert> forSampleGradleSettings() {
		String path = "project/build/gradle/sample-settings.gradle.kts";
		try (InputStream in = new ClassPathResource(path).getInputStream()) {
			String content = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
			return () -> new KotlinDslGradleSettingsAssert(content);
		}
		catch (IOException ex) {
			throw new IllegalStateException("No content found at " + path, ex);
		}
	}

}
