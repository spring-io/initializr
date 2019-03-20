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
		container.add("root", "org.springframework.boot", "spring-boot-dependencies",
				VersionReference.ofProperty("spring-boot.version"));
		assertThat(container.ids()).containsOnly("root");
		assertThat(container.items()).hasSize(1);
		assertThat(container.isEmpty()).isFalse();
		assertThat(container.has("root")).isTrue();
		BillOfMaterials bom = container.get("root");
		assertThat(bom).isNotNull();
		assertThat(bom.getGroupId()).isEqualTo("org.springframework.boot");
		assertThat(bom.getArtifactId()).isEqualTo("spring-boot-dependencies");
		assertThat(bom.getVersion())
				.isEqualTo(VersionReference.ofProperty("spring-boot.version"));
		assertThat(bom.getOrder()).isEqualTo(Integer.MAX_VALUE);
	}

	@Test
	void addBomWithOrder() {
		BomContainer container = createTestContainer();
		container.add("custom", "com.example", "acme", VersionReference.ofValue("1.0.0"),
				42);
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
