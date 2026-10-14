/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link BomContainer}.
 *
 * @author Stephane Nicoll
 */
class BomContainerTests {

	@Test
	void addBom() {
		BomContainer container = createTestContainer();
		container.add("root", BillOfMaterials.withCoordinates("org.springframework.boot", "spring-boot-dependencies")
			.version(VersionReference.ofProperty("spring-boot.version")));
		assertThat(container.ids()).containsOnly("root");
		assertThat(container.items()).hasSize(1);
		assertThat(container.isEmpty()).isFalse();
		assertThat(container.has("root")).isTrue();
		BillOfMaterials bom = container.get("root");
		assertThat(bom).isNotNull();
		assertThat(bom.getGroupId()).isEqualTo("org.springframework.boot");
		assertThat(bom.getArtifactId()).isEqualTo("spring-boot-dependencies");
		assertThat(bom.getVersion()).isEqualTo(VersionReference.ofProperty("spring-boot.version"));
		assertThat(bom.getOrder()).isEqualTo(Integer.MAX_VALUE);
	}

	@Test
	void addBomWithOrder() {
		BomContainer container = createTestContainer();
		container.add("custom",
				BillOfMaterials.withCoordinates("com.example", "acme")
					.version(VersionReference.ofValue("1.0.0"))
					.order(42));
		assertThat(container.ids()).containsOnly("custom");
		assertThat(container.items()).hasSize(1);
		assertThat(container.isEmpty()).isFalse();
		assertThat(container.has("custom")).isTrue();
		BillOfMaterials bom = container.get("custom");
		assertThat(bom).isNotNull();
		assertThat(bom.getGroupId()).isEqualTo("com.example");
		assertThat(bom.getArtifactId()).isEqualTo("acme");
		assertThat(bom.getVersion()).isEqualTo(VersionReference.ofValue("1.0.0"));
		assertThat(bom.getOrder()).isEqualTo(42);
	}

	private BomContainer createTestContainer() {
		return new BomContainer((id) -> null);
	}

}
