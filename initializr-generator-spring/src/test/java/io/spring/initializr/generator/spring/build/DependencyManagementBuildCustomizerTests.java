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

package io.spring.initializr.generator.spring.build;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemResolver;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DependencyManagementBuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
class DependencyManagementBuildCustomizerTests {

	@Test
	void contributeBom() { // ProjectRequestTests#resolveAdditionalBoms
		Dependency dependency = Dependency.withId("foo");
		dependency.setBom("foo-bom");
		BillOfMaterials bom = BillOfMaterials.create("com.example", "foo-bom", "1.0.0");
		bom.getAdditionalBoms().add("bar-bom");
		BillOfMaterials additionalBom = BillOfMaterials.create("com.example", "bar-bom",
				"1.1.0");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("foo-bom", bom).addBom("bar-bom", additionalBom)
				.addDependencyGroup("test", dependency).build();
		Build build = createBuild(metadata);
		build.dependencies().add(dependency.getId());
		customizeBuild(build, metadata);
		assertThat(build.boms().items()).hasSize(2);
	}

	@Test
	void contributeRepositories() { // ProjectRequestTests#resolveAdditionalRepositories
		Dependency dependency = Dependency.withId("foo");
		dependency.setBom("foo-bom");
		dependency.setRepository("foo-repo");
		BillOfMaterials bom = BillOfMaterials.create("com.example", "foo-bom", "1.0.0");
		bom.getRepositories().add("bar-repo");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom("foo-bom", bom)
				.addRepository("foo-repo", "foo-repo", "http://example.com/foo", false)
				.addRepository("bar-repo", "bar-repo", "http://example.com/bar", false)
				.addDependencyGroup("test", dependency).build();
		Build build = createBuild(metadata);
		build.dependencies().add(dependency.getId());
		customizeBuild(build, metadata);
		assertThat(build.repositories().items()).hasSize(2);
		assertThat(build.pluginRepositories().items()).isEmpty();
	}

	private MavenBuild createBuild(InitializrMetadata metadata) {
		return new MavenBuild(
				new MetadataBuildItemResolver(metadata, Version.parse("2.0.0.RELEASE")));
	}

	private void customizeBuild(Build build, InitializrMetadata metadata) {
		ProjectDescription projectDescription = new ProjectDescription();
		projectDescription.setPlatformVersion(Version.parse("2.0.0.RELEASE"));
		new DependencyManagementBuildCustomizer(projectDescription.resolve(), metadata)
				.customize(build);
	}

}
