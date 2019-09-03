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

package io.spring.initializr.generator.spring.build.maven;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Maven;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Maven.ParentPom;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;

/**
 * The default {@link Maven} {@link BuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
public class DefaultMavenBuildCustomizer implements BuildCustomizer<MavenBuild> {

	private final ProjectDescription description;

	private final InitializrMetadata metadata;

	public DefaultMavenBuildCustomizer(ProjectDescription description, InitializrMetadata metadata) {
		this.description = description;
		this.metadata = metadata;
	}

	@Override
	public void customize(MavenBuild build) {
		build.settings().name(this.description.getName()).description(this.description.getDescription());
		build.properties().property("java.version", this.description.getLanguage().jvmVersion());
		build.plugins().add("org.springframework.boot", "spring-boot-maven-plugin");

		Maven maven = this.metadata.getConfiguration().getEnv().getMaven();
		String springBootVersion = this.description.getPlatformVersion().toString();
		ParentPom parentPom = maven.resolveParentPom(springBootVersion);
		if (parentPom.isIncludeSpringBootBom()) {
			String versionProperty = "spring-boot.version";
			BillOfMaterials springBootBom = MetadataBuildItemMapper
					.toBom(this.metadata.createSpringBootBom(springBootVersion, versionProperty));
			if (!hasBom(build, springBootBom)) {
				build.properties().version(VersionProperty.of(versionProperty, true), springBootVersion);
				build.boms().add("spring-boot", springBootBom);
			}
		}
		if (!maven.isSpringBootStarterParent(parentPom)) {
			build.properties().property("project.build.sourceEncoding", "UTF-8")
					.property("project.reporting.outputEncoding", "UTF-8");
		}
		build.settings().parent(parentPom.getGroupId(), parentPom.getArtifactId(), parentPom.getVersion());
	}

	private boolean hasBom(MavenBuild build, BillOfMaterials bom) {
		return build.boms().items().anyMatch((candidate) -> candidate.getGroupId().equals(bom.getGroupId())
				&& candidate.getArtifactId().equals(bom.getArtifactId()));
	}

}
