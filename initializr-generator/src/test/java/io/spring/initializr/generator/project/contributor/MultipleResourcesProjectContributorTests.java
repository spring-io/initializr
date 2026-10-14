/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project.contributor;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MultipleResourcesProjectContributor}.
 *
 * @author Toon Geens
 * @author Stephane Nicoll
 */
class MultipleResourcesProjectContributorTests {

	@Test
	void contribute(@TempDir Path directory) throws IOException {
		new MultipleResourcesProjectContributor("classpath:/data/multi").contribute(directory);
		assertThat(directory.resolve("one.properties")).exists().isRegularFile();
		assertThat(directory.resolve("two.xml")).exists().isRegularFile();
	}

	@Test
	void contributeWithTrailingSlash(@TempDir Path directory) throws IOException {
		new MultipleResourcesProjectContributor("classpath:/data/multi/").contribute(directory);
		assertThat(directory.resolve("one.properties")).exists().isRegularFile();
		assertThat(directory.resolve("two.xml")).exists().isRegularFile();
	}

}
