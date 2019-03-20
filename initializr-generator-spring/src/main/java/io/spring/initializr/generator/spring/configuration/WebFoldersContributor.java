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

package io.spring.initializr.generator.spring.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.spring.build.BuildMetadataResolver;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.core.Ordered;

/**
 * A {@link ProjectContributor} that creates web-specific directories when a web-related
 * project is detected.
 *
 * @author Stephane Nicoll
 */
public class WebFoldersContributor implements ProjectContributor {

	private final Build build;

	private final BuildMetadataResolver buildMetadataResolver;

	public WebFoldersContributor(Build build, InitializrMetadata metadata) {
		this.build = build;
		this.buildMetadataResolver = new BuildMetadataResolver(metadata);
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		if (this.buildMetadataResolver.hasFacet(this.build, "web")) {
			Files.createDirectories(projectRoot.resolve("src/main/resources/templates"));
			Files.createDirectories(projectRoot.resolve("src/main/resources/static"));
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 10;
	}

}
