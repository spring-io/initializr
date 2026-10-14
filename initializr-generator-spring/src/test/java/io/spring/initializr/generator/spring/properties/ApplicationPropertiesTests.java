/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

import io.spring.initializr.generator.buildsystem.SourceSet;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link ApplicationProperties}.
 *
 * @author Moritz Halbritter
 */
class ApplicationPropertiesTests {

	@Test
	void shouldSeePropertyAddedViaMainSourceSet() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.file(SourceSet.MAIN).add("test", "value");
		assertThat(properties.get("test")).isEqualTo("value");
	}

	@Test
	void shouldSeePropertyAddedViaRootInMainSourceSet() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", "value");
		assertThat(properties.file(SourceSet.MAIN).get("test")).isEqualTo("value");
	}

	@Test
	void shouldUseMainSourceSetForProfile() {
		ApplicationProperties properties = new ApplicationProperties();
		assertThat(properties.profile("dev")).isSameAs(properties.file(SourceSet.MAIN, "dev"));
	}

	@Test
	void shouldReturnSameFileForSameSourceSetAndProfile() {
		ApplicationProperties properties = new ApplicationProperties();
		assertThat(properties.file(SourceSet.TEST)).isSameAs(properties.file(SourceSet.TEST));
		assertThat(properties.file(SourceSet.TEST, "dev")).isSameAs(properties.file(SourceSet.TEST, "dev"));
	}

	@Test
	void shouldKeepFilesIsolated() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", "main-value");
		properties.file(SourceSet.TEST).add("test", "test-value");
		properties.profile("dev").add("test", "dev-value");
		assertThat(properties.get("test")).isEqualTo("main-value");
		assertThat(properties.file(SourceSet.TEST).get("test")).isEqualTo("test-value");
		assertThat(properties.profile("dev").get("test")).isEqualTo("dev-value");
	}

	@Test
	void shouldAlwaysHaveMainDefaultFile() {
		ApplicationProperties properties = new ApplicationProperties();
		assertThat(properties.files(SourceSet.MAIN).keySet()).containsExactly(ProfileName.DEFAULT);
	}

	@Test
	void shouldReturnFilesInAdditionOrder() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.profile("dev");
		properties.profile("prod");
		assertThat(properties.files(SourceSet.MAIN).keySet()).containsExactly(ProfileName.DEFAULT,
				new ProfileName("dev"), new ProfileName("prod"));
	}

	@Test
	void shouldRejectProfileEscapingTheResourcesDirectory() {
		ApplicationProperties properties = new ApplicationProperties();
		assertThatIllegalStateException().isThrownBy(() -> properties.file(SourceSet.TEST, "../../etc"))
			.withMessageContaining("must start and end with a letter or digit");
		assertThatIllegalStateException().isThrownBy(() -> properties.profile("../../etc"))
			.withMessageContaining("must start and end with a letter or digit");
	}

	@Test
	void shouldReturnNoFilesForUnusedSourceSet() {
		ApplicationProperties properties = new ApplicationProperties();
		assertThat(properties.files(SourceSet.TEST)).isEmpty();
	}

}
