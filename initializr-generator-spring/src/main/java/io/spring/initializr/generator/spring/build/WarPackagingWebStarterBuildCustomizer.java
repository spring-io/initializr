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
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionRange;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;

import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/**
 * A {@link BuildCustomizer} that configures the necessary web-related dependency when
 * packaging an application as a war.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public class WarPackagingWebStarterBuildCustomizer implements BuildCustomizer<Build> {

	private static final VersionRange SPRING_BOOT_4_OR_LATER = VersionParser.DEFAULT.parseRange("4.0.0");

	private final InitializrMetadata metadata;

	private final Version platformVersion;

	private final BuildMetadataResolver buildMetadataResolver;

	public WarPackagingWebStarterBuildCustomizer(InitializrMetadata metadata, ProjectDescription projectDescription) {
		this.metadata = metadata;
		Version platformVersion = projectDescription.getPlatformVersion();
		Assert.state(platformVersion != null, "'platformVersion' must not be null");
		this.platformVersion = platformVersion;
		this.buildMetadataResolver = new BuildMetadataResolver(metadata, platformVersion);
	}

	@Override
	public void customize(Build build) {
		addWebDependency(build);
		if (isBoot4OrLater() && isGradleBuild(build)) {
			customizeGradle(build);
			return;
		}
		addTomcatInProvidedScope(build);
	}

	private boolean isGradleBuild(Build build) {
		return build instanceof GradleBuild;
	}

	private boolean isBoot4OrLater() {
		return SPRING_BOOT_4_OR_LATER.match(this.platformVersion);
	}

	private void addWebDependency(Build build) {
		if (!this.buildMetadataResolver.hasFacet(build, "web")) {
			// Need to be able to bootstrap the web app
			Dependency dependency = determineWebDependency(this.metadata);
			String id = dependency.getId();
			Assert.state(id != null, "'id' must not be null");
			build.dependencies()
				.add(id, MetadataBuildItemMapper.toDependency(dependency.resolve(this.platformVersion)));
		}
	}

	private void addTomcatInProvidedScope(Build build) {
		Dependency tomcat = determineTomcatDependency(this.metadata);
		tomcat.setScope(Dependency.SCOPE_PROVIDED);
		build.dependencies().add("tomcat", MetadataBuildItemMapper.toDependency(tomcat.resolve(this.platformVersion)));
	}

	private void customizeGradle(Build build) {
		build.dependencies().remove("tomcat");
		build.dependencies()
			.add("tomcat-runtime", MetadataBuildItemMapper.toDependency(Dependency.create("org.springframework.boot",
					"spring-boot-starter-tomcat-runtime", null, Dependency.SCOPE_PROVIDED)));
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
		return (tomcat != null) ? tomcat : Dependency.createSpringBootStarter("tomcat");
	}

}
