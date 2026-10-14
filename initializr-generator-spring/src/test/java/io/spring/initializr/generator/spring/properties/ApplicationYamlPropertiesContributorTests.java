/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import io.spring.initializr.generator.buildsystem.SourceSet;
import io.spring.initializr.generator.test.project.ProjectStructure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link ApplicationYamlPropertiesContributor}. Profile to file mapping is
 * covered by {@link ApplicationPropertiesContributorTests}, this class only covers the
 * YAML specifics.
 *
 * @author Sijun Yang
 * @author Moritz Halbritter
 */
class ApplicationYamlPropertiesContributorTests {

	@TempDir
	@SuppressWarnings("NullAway.Init")
	Path directory;

	@Test
	void shouldWriteEmptyMainFileByDefault() throws IOException {
		new ApplicationYamlPropertiesContributor(new ApplicationProperties(), TestProjectDescriptions.mavenJava())
			.contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).textFile("src/main/resources/application.yaml").isEmpty();
	}

	@Test
	void shouldWriteStringProperty() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("spring.application.name", "test");
		new ApplicationYamlPropertiesContributor(properties, TestProjectDescriptions.mavenJava())
			.contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).textFile("src/main/resources/application.yaml")
			.lines()
			.containsExactly("spring:", "  application:", "    name: test");
	}

	@Test
	void shouldNestDottedKeysKeepingInsertionOrder() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("name", "testapp");
		properties.add("app.version", "1.0");
		properties.add("db.host", "localhost");
		properties.add("app.config.debug", true);
		new ApplicationYamlPropertiesContributor(properties, TestProjectDescriptions.mavenJava())
			.contribute(this.directory);
		// Nested keys are grouped under their first occurrence
		assertThat(new ProjectStructure(this.directory)).textFile("src/main/resources/application.yaml")
			.lines()
			.containsExactly("name: testapp", "app:", "  version: 1.0", "  config:", "    debug: true", "db:",
					"  host: localhost");
	}

	@Test
	void shouldWriteCollections() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test.values", List.of(1, 2));
		properties.add("test.empty", Collections.emptyList());
		new ApplicationYamlPropertiesContributor(properties, TestProjectDescriptions.mavenJava())
			.contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).textFile("src/main/resources/application.yaml")
			.lines()
			.containsExactly("test:", "  values:", "    - 1", "    - 2", "  empty: []");
	}

	@Test
	void shouldFailOnValueAndNestedMapForSameKey() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("app", "value");
		properties.add("app.name", "nested");
		ApplicationYamlPropertiesContributor contributor = new ApplicationYamlPropertiesContributor(properties,
				TestProjectDescriptions.mavenJava());
		assertThatIllegalStateException().isThrownBy(() -> contributor.contribute(this.directory))
			.withMessage("Property 'app' can't be a value and a nested map at the same time");
	}

	@Test
	void shouldFailOnNestedMapAndValueForSameKey() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("app.name", "nested");
		properties.add("app", "value");
		ApplicationYamlPropertiesContributor contributor = new ApplicationYamlPropertiesContributor(properties,
				TestProjectDescriptions.mavenJava());
		assertThatIllegalStateException().isThrownBy(() -> contributor.contribute(this.directory))
			.withMessage("Property 'app' can't be a value and a nested map at the same time");
	}

	@Test
	void shouldUseYamlExtensionForProfileFiles() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.file(SourceSet.TEST, "integration").add("spring.application.name", "it");
		new ApplicationYamlPropertiesContributor(properties, TestProjectDescriptions.mavenJava())
			.contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).textFile("src/test/resources/application-integration.yaml")
			.lines()
			.containsExactly("spring:", "  application:", "    name: it");
	}

}
