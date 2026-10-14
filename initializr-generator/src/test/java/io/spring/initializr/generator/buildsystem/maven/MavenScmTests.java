/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenScm}.
 *
 * @author Joachim Pasquali
 */
class MavenScmTests {

	@Test
	void isEmptyWithNoData() {
		MavenScm mavenScm = new MavenScm.Builder().build();
		assertThat(mavenScm.isEmpty()).isTrue();
	}

	@Test
	void isEmptyWithData() {
		MavenScm mavenScm = new MavenScm.Builder().connection("some-connection").build();
		assertThat(mavenScm.isEmpty()).isFalse();
	}

	@Test
	void allElementsTest() {
		MavenScm mavenScm = new MavenScm.Builder().connection("connection")
			.developerConnection("developerConnection")
			.url("url")
			.tag("tag")
			.build();
		assertThat(mavenScm.getConnection()).isEqualTo("connection");
		assertThat(mavenScm.getDeveloperConnection()).isEqualTo("developerConnection");
		assertThat(mavenScm.getTag()).isEqualTo("tag");
		assertThat(mavenScm.getUrl()).isEqualTo("url");
	}

}
