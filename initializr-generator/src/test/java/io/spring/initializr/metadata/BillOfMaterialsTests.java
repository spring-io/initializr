/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.metadata;

import java.util.Arrays;

import io.spring.initializr.metadata.BillOfMaterials.Mapping;
import io.spring.initializr.util.Version;
import io.spring.initializr.util.VersionParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * @author Stephane Nicoll
 */
public class BillOfMaterialsTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void resolveSimpleBom() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		bom.validate();
		BillOfMaterials resolved = bom.resolve(Version.parse("1.2.3.RELEASE"));
		assertThat(bom, sameInstance(resolved));
	}

	@Test
	public void resolveSimpleRange() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		bom.setVersionProperty("bom.version");
		bom.getRepositories().add("repo-main");
		bom.getAdditionalBoms().add("bom-main");
		bom.getMappings()
				.add(BillOfMaterials.Mapping.create("[1.2.0.RELEASE,1.3.0.M1)", "1.1.0"));
		bom.validate();
		BillOfMaterials resolved = bom.resolve(Version.parse("1.2.3.RELEASE"));
		assertThat(resolved.getGroupId(), equalTo("com.example"));
		assertThat(resolved.getArtifactId(), equalTo("bom"));
		assertThat(resolved.getVersion(), equalTo("1.1.0"));
		assertThat(resolved.getVersionProperty().toStandardFormat(),
				equalTo("bom.version"));
		assertThat(resolved.getRepositories().size(), equalTo(1));
		assertThat(resolved.getRepositories().get(0), equalTo("repo-main"));
		assertThat(resolved.getAdditionalBoms().size(), equalTo(1));
		assertThat(resolved.getAdditionalBoms().get(0), equalTo("bom-main"));
	}

	@Test
	public void resolveRangeOverride() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		bom.getRepositories().add("repo-main");
		bom.getAdditionalBoms().add("bom-main");
		Mapping mapping = BillOfMaterials.Mapping.create("[1.2.0.RELEASE,1.3.0.M1)",
				"1.1.0", "repo-foo");
		mapping.getAdditionalBoms().add("bom-foo");
		bom.getMappings().add(mapping);
		bom.validate();
		BillOfMaterials resolved = bom.resolve(Version.parse("1.2.3.RELEASE"));
		assertThat(resolved.getGroupId(), equalTo("com.example"));
		assertThat(resolved.getArtifactId(), equalTo("bom"));
		assertThat(resolved.getVersion(), equalTo("1.1.0"));
		assertThat(resolved.getVersionProperty(), nullValue());
		assertThat(resolved.getRepositories().size(), equalTo(1));
		assertThat(resolved.getRepositories().get(0), equalTo("repo-foo"));
		assertThat(resolved.getAdditionalBoms().size(), equalTo(1));
		assertThat(resolved.getAdditionalBoms().get(0), equalTo("bom-foo"));
	}

	@Test
	public void resolveRangeOverrideAndMapping() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		bom.setVersionProperty("example.version");
		bom.getMappings()
				.add(BillOfMaterials.Mapping.create("[1.2.0.RELEASE,1.3.0.M1)", "1.1.0"));
		bom.validate();
		BillOfMaterials resolved = bom.resolve(Version.parse("1.2.3.RELEASE"));
		assertThat(resolved.getGroupId(), equalTo("com.example"));
		assertThat(resolved.getArtifactId(), equalTo("bom"));
		assertThat(resolved.getVersion(), equalTo("1.1.0"));
		assertThat(resolved.getVersionProperty().toStandardFormat(),
				equalTo("example.version"));
	}

	@Test
	public void noRangeAvailable() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom");
		bom.getMappings()
				.add(BillOfMaterials.Mapping.create("[1.2.0.RELEASE,1.3.0.M1)", "1.1.0"));
		bom.getMappings()
				.add(BillOfMaterials.Mapping.create("[1.3.0.M1, 1.4.0.M1)", "1.2.0"));
		bom.validate();

		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("1.4.1.RELEASE");
		bom.resolve(Version.parse("1.4.1.RELEASE"));
	}

	@Test
	public void resolveRangeWithVariablePatch() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		bom.getMappings().add(
				BillOfMaterials.Mapping.create("[1.3.0.RELEASE,1.3.x.RELEASE]", "1.1.0"));
		bom.getMappings().add(BillOfMaterials.Mapping
				.create("[1.3.x.BUILD-SNAPSHOT,1.4.0.RELEASE)", "1.1.1-SNAPSHOT"));
		bom.validate();

		bom.updateVersionRange(new VersionParser(Arrays.asList(
				Version.parse("1.3.8.RELEASE"), Version.parse("1.3.9.BUILD-SNAPSHOT"))));
		assertThat(bom.resolve(Version.parse("1.3.8.RELEASE")).getVersion(),
				equalTo("1.1.0"));
		assertThat(bom.resolve(Version.parse("1.3.9.RELEASE")).getVersion(),
				equalTo("1.1.1-SNAPSHOT"));

		bom.updateVersionRange(new VersionParser(Arrays.asList(
				Version.parse("1.3.9.RELEASE"), Version.parse("1.3.10.BUILD-SNAPSHOT"))));
		assertThat(bom.resolve(Version.parse("1.3.8.RELEASE")).getVersion(),
				equalTo("1.1.0"));
		assertThat(bom.resolve(Version.parse("1.3.9.RELEASE")).getVersion(),
				equalTo("1.1.0"));
	}

}
