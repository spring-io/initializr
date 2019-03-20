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
 */
public class WarPackagingWebStarterBuildCustomizer implements BuildCustomizer<Build> {

	private final InitializrMetadata metadata;

	private final BuildMetadataResolver buildMetadataResolver;

	public WarPackagingWebStarterBuildCustomizer(InitializrMetadata metadata) {
		this.metadata = metadata;
		this.buildMetadataResolver = new BuildMetadataResolver(metadata);
	}

	@Override
	public void customize(Build build) {
		if (!this.buildMetadataResolver.hasFacet(build, "web")) {
			// Need to be able to bootstrap the web app
			Dependency dependency = determineWebDependency(this.metadata);
			build.dependencies().add(dependency.getId(),
					MetadataBuildItemMapper.toDependency(dependency));
		}
		// Add the tomcat starter in provided scope
		Dependency tomcat = new Dependency().asSpringBootStarter("tomcat");
		tomcat.setScope(Dependency.SCOPE_PROVIDED);
		build.dependencies().add("tomcat", MetadataBuildItemMapper.toDependency(tomcat));
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 10;
	}

	private Dependency determineWebDependency(InitializrMetadata metadata) {
		Dependency web = metadata.getDependencies().get("web");
		return (web != null) ? web : Dependency.withId("web").asSpringBootStarter("web");
	}

}
