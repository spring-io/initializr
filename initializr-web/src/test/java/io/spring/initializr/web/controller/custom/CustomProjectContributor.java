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

package io.spring.initializr.web.controller.custom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * A {@link ProjectContributor} that adds an {@code custom.txt} file at the root of the
 * project when the registered description is a {@link CustomProjectDescription} and its
 * {@code customFlag} is {@code enabled}.
 *
 * @author Stephane Nicoll
 */
class CustomProjectContributor implements ProjectContributor {

	private final ProjectDescription description;

	CustomProjectContributor(ProjectDescription description) {
		this.description = description;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		if (this.description instanceof CustomProjectDescription
				&& ((CustomProjectDescription) this.description).isCustomFlag()) {
			Files.createFile(projectRoot.resolve("custom.txt"));
		}
	}

}
