/*
 * Copyright 2012 - present the original author or authors.
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
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionRange;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;

import org.springframework.core.Ordered;

/**
 * A {@link BuildCustomizer} that configures the necessary web-related dependency when
 * packaging an application as a war.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public class WarPackagingWebStarterBuildCustomizer implements BuildCustomizer<Build> {

	private static final VersionRange SPRING_BOOT_4_RC1_OR_LATER = VersionParser.DEFAULT.parseRange("4.0.0-RC1");

	private final InitializrMetadata metadata;

	private final Version platformVersion;

	private final BuildMetadataResolver buildMetadataResolver;

	public WarPackagingWebStarterBuildCustomizer(InitializrMetadata metadata, ProjectDescription projectDescription) {
		this.metadata = metadata;
		this.platformVersion = projectDescription.getPlatformVersion();
		this.buildMetadataResolver = new BuildMetadataResolver(metadata, this.platformVersion);
	}

	@Override
	public void customize(Build build) {
		if (!this.buildMetadataResolver.hasFacet(build, "web")) {
			// Need to be able to bootstrap the web app
			Dependency dependency = determineWebDependency(this.metadata);
			build.dependencies()
				.add(dependency.getId(),
						MetadataBuildItemMapper.toDependency(dependency.resolve(this.platformVersion)));
		}
		// Add the tomcat starter in provided scope
		Dependency tomcat = determineTomcatDependency(this.metadata);
		tomcat.setScope(Dependency.SCOPE_PROVIDED);
		build.dependencies().add("tomcat", MetadataBuildItemMapper.toDependency(tomcat.resolve(this.platformVersion)));
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 10;
	}

	private Dependency determineWebDependency(InitializrMetadata metadata) {
		Dependency web = metadata.getDependencies().get("web");
		return (web != null) ? web : Dependency.createSpringBootStarter("web");
	}

	private Dependency determineTomcatDependency(InitializrMetadata metadata) {
		Dependency tomcat = metadata.getDependencies().get("tomcat");
		if (tomcat != null) {
			return tomcat;
		}
		if (SPRING_BOOT_4_RC1_OR_LATER.match(this.platformVersion)) {
			return Dependency.create("org.springframework.boot", "spring-boot-tomcat-runtime", null,
					Dependency.SCOPE_PROVIDED);
		}
		return Dependency.createSpringBootStarter("tomcat");
	}

}
