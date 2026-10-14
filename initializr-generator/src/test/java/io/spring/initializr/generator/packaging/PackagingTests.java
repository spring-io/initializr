/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.packaging;

import io.spring.initializr.generator.packaging.jar.JarPackaging;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link Packaging}.
 *
 * @author Stephane Nicoll
 */
class PackagingTests {

	@Test
	void jarPackaging() {
		Packaging jar = Packaging.forId("jar");
		assertThat(jar).isInstanceOf(JarPackaging.class);
		assertThat(jar.id()).isEqualTo("jar");
		assertThat(jar.toString()).isEqualTo("jar");
	}

	@Test
	void warPackaging() {
		Packaging war = Packaging.forId("war");
		assertThat(war).isInstanceOf(WarPackaging.class);
		assertThat(war.id()).isEqualTo("war");
		assertThat(war.toString()).isEqualTo("war");
	}

	@Test
	void unknownPackaging() {
		assertThatIllegalStateException().isThrownBy(() -> Packaging.forId("unknown"))
			.withMessageContaining("Unrecognized packaging id 'unknown'");
	}

}
