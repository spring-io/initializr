/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.configuration.format;

import io.spring.initializr.generator.configuration.format.properties.PropertiesFormat;
import io.spring.initializr.generator.configuration.format.yaml.YamlFormat;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link ConfigurationFileFormat}.
 *
 * @author Moritz Halbritter
 */
class ConfigurationFileFormatTests {

	@Test
	void propertiesFormat() {
		ConfigurationFileFormat format = ConfigurationFileFormat.forId(PropertiesFormat.ID);
		assertThat(format).isInstanceOf(PropertiesFormat.class);
		assertThat(format.id()).isEqualTo("properties");
		assertThat(format.fileExtension()).isEqualTo(".properties");
		assertThat(format).hasToString("properties");
	}

	@Test
	void yamlFormat() {
		ConfigurationFileFormat format = ConfigurationFileFormat.forId(YamlFormat.ID);
		assertThat(format).isInstanceOf(YamlFormat.class);
		assertThat(format.id()).isEqualTo("yaml");
		assertThat(format.fileExtension()).isEqualTo(".yaml");
		assertThat(format).hasToString("yaml");
	}

	@Test
	void unknownFormat() {
		assertThatIllegalStateException().isThrownBy(() -> ConfigurationFileFormat.forId("unknown"))
			.withMessageContaining("Unrecognized configuration file format id 'unknown'");
	}

}
