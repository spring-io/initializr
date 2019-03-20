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

package io.spring.initializr.metadata;

import java.util.Arrays;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.metadata.BillOfMaterials.Mapping;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * @author Stephane Nicoll
 */
class BillOfMaterialsTests {

	@Test
	void resolveSimpleBom() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		bom.validate();
		BillOfMaterials resolved = bom.resolve(Version.parse("1.2.3.RELEASE"));
		assertThat(bom).isSameAs(resolved);
	}

	@Test
	void resolveSimpleRange() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		bom.setVersionProperty("bom.version");
		bom.getRepositories().add("repo-main");
		bom.getAdditionalBoms().add("bom-main");
		bom.getMappings().add(Mapping.create("[1.2.0.RELEASE,1.3.0.M1)", "1.1.0"));
		bom.validate();
		BillOfMaterials resolved = bom.resolve(Version.parse("1.2.3.RELEASE"));
		assertThat(resolved.getGroupId()).isEqualTo("com.example");
		assertThat(resolved.getArtifactId()).isEqualTo("bom");
		assertThat(resolved.getVersion()).isEqualTo("1.1.0");
		assertThat(resolved.getVersionProperty().toStandardFormat())
				.isEqualTo("bom.version");
		assertThat(resolved.getRepositories()).hasSize(1);
		assertThat(resolved.getRepositories().get(0)).isEqualTo("repo-main");
		assertThat(resolved.getAdditionalBoms()).hasSize(1);
		assertThat(resolved.getAdditionalBoms().get(0)).isEqualTo("bom-main");
	}

	@Test
	void resolveSimpleRangeWithGroupIdArtifactId() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		bom.setVersionProperty("bom.version");
		bom.getRepositories().add("repo-main");
		bom.getAdditionalBoms().add("bom-main");
		Mapping mapping = Mapping.create("[1.2.0.RELEASE,1.3.0.M1)", "1.1.0");
		mapping.setGroupId("com.example.override");
		mapping.setArtifactId("bom-override");
		bom.getMappings().add(mapping);
		bom.validate();
		BillOfMaterials resolved = bom.resolve(Version.parse("1.2.3.RELEASE"));
		assertThat(resolved.getGroupId()).isEqualTo("com.example.override");
		assertThat(resolved.getArtifactId()).isEqualTo("bom-override");
		assertThat(resolved.getVersion()).isEqualTo("1.1.0");
		assertThat(resolved.getVersionProperty().toStandardFormat())
				.isEqualTo("bom.version");
		assertThat(resolved.getRepositories()).hasSize(1);
		assertThat(resolved.getRepositories().get(0)).isEqualTo("repo-main");
		assertThat(resolved.getAdditionalBoms()).hasSize(1);
		assertThat(resolved.getAdditionalBoms().get(0)).isEqualTo("bom-main");
	}

	@Test
	void resolveRangeOverride() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		bom.getRepositories().add("repo-main");
		bom.getAdditionalBoms().add("bom-main");
		Mapping mapping = Mapping.create("[1.2.0.RELEASE,1.3.0.M1)", "1.1.0", "repo-foo");
		mapping.getAdditionalBoms().add("bom-foo");
		bom.getMappings().add(mapping);
		bom.validate();
		BillOfMaterials resolved = bom.resolve(Version.parse("1.2.3.RELEASE"));
		assertThat(resolved.getGroupId()).isEqualTo("com.example");
		assertThat(resolved.getArtifactId()).isEqualTo("bom");
		assertThat(resolved.getVersion()).isEqualTo("1.1.0");
		assertThat(resolved.getVersionProperty()).isNull();
		assertThat(resolved.getRepositories()).hasSize(1);
		assertThat(resolved.getRepositories().get(0)).isEqualTo("repo-foo");
		assertThat(resolved.getAdditionalBoms()).hasSize(1);
		assertThat(resolved.getAdditionalBoms().get(0)).isEqualTo("bom-foo");
	}

	@Test
	void resolveRangeOverrideAndMapping() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		bom.setVersionProperty("example.version");
		bom.getMappings().add(Mapping.create("[1.2.0.RELEASE,1.3.0.M1)", "1.1.0"));
		bom.validate();
		BillOfMaterials resolved = bom.resolve(Version.parse("1.2.3.RELEASE"));
		assertThat(resolved.getGroupId()).isEqualTo("com.example");
		assertThat(resolved.getArtifactId()).isEqualTo("bom");
		assertThat(resolved.getVersion()).isEqualTo("1.1.0");
		assertThat(resolved.getVersionProperty().toStandardFormat())
				.isEqualTo("example.version");
	}

	@Test
	void noRangeAvailable() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom");
		bom.getMappings().add(Mapping.create("[1.2.0.RELEASE,1.3.0.M1)", "1.1.0"));
		bom.getMappings().add(Mapping.create("[1.3.0.M1, 1.4.0.M1)", "1.2.0"));
		bom.validate();
		assertThatIllegalStateException()
				.isThrownBy(() -> bom.resolve(Version.parse("1.4.1.RELEASE")))
				.withMessageContaining("1.4.1.RELEASE");
	}

	@Test
	void resolveRangeWithVariablePatch() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		bom.getMappings().add(Mapping.create("[1.3.0.RELEASE,1.3.x.RELEASE]", "1.1.0"));
		bom.getMappings().add(BillOfMaterials.Mapping
				.create("[1.3.x.BUILD-SNAPSHOT,1.4.0.RELEASE)", "1.1.1-SNAPSHOT"));
		bom.validate();

		bom.updateVersionRange(new VersionParser(Arrays.asList(
				Version.parse("1.3.8.RELEASE"), Version.parse("1.3.9.BUILD-SNAPSHOT"))));
		assertThat(bom.resolve(Version.parse("1.3.8.RELEASE")).getVersion())
				.isEqualTo("1.1.0");
		assertThat(bom.resolve(Version.parse("1.3.9.RELEASE")).getVersion())
				.isEqualTo("1.1.1-SNAPSHOT");

		bom.updateVersionRange(new VersionParser(Arrays.asList(
				Version.parse("1.3.9.RELEASE"), Version.parse("1.3.10.BUILD-SNAPSHOT"))));
		assertThat(bom.resolve(Version.parse("1.3.8.RELEASE")).getVersion())
				.isEqualTo("1.1.0");
		assertThat(bom.resolve(Version.parse("1.3.9.RELEASE")).getVersion())
				.isEqualTo("1.1.0");
	}

}
