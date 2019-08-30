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
import io.spring.initializr.generator.project.ProjectDescription;

import org.springframework.core.Ordered;

/**
 * Customize the {@link Build} as early as possible based on the information held in the
 * {@link ProjectDescription}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class SimpleBuildCustomizer implements BuildCustomizer<Build> {

	private final ProjectDescription description;

	public SimpleBuildCustomizer(ProjectDescription description) {
		this.description = description;
	}

	@Override
	public void customize(Build build) {
		build.settings().group(this.description.getGroupId()).artifact(this.description.getArtifactId())
				.version(this.description.getVersion());
		this.description.getRequestedDependencies()
				.forEach((id, dependency) -> build.dependencies().add(id, dependency));
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
