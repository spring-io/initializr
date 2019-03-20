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
 * A {@link BuildCustomizer} that adds the default starter if none is detected.
 *
 * @author Stephane Nicoll
 */
class DefaultStarterBuildCustomizer implements BuildCustomizer<Build> {

	/**
	 * The id of the starter to use if no dependency is defined.
	 */
	static final String DEFAULT_STARTER = "root_starter";

	private final BuildMetadataResolver buildResolver;

	DefaultStarterBuildCustomizer(InitializrMetadata metadata) {
		this.buildResolver = new BuildMetadataResolver(metadata);
	}

	@Override
	public void customize(Build build) {
		boolean hasStarter = this.buildResolver.dependencies(build)
				.anyMatch(this::isValidStarter);
		if (!hasStarter) {
			Dependency root = new Dependency();
			root.setId(DEFAULT_STARTER);
			root.asSpringBootStarter("");
			build.dependencies().add(DEFAULT_STARTER,
					MetadataBuildItemMapper.toDependency(root));
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	private boolean isValidStarter(Dependency dependency) {
		return dependency.isStarter()
				&& Dependency.SCOPE_COMPILE.equals(dependency.getScope());
	}

}
