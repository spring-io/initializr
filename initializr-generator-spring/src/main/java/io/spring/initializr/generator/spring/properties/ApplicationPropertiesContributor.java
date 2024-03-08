/*
 * Copyright 2012-2024 the original author or authors.
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

package io.spring.initializr.generator.spring.properties;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * A {@link ProjectContributor} that contributes a {@code application.properties} file to
 * a project.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public class ApplicationPropertiesContributor implements ProjectContributor {

	private static final String FILE = "src/main/resources/application.properties";

	private final ApplicationProperties properties;

	public ApplicationPropertiesContributor(ApplicationProperties properties) {
		this.properties = properties;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		Path output = projectRoot.resolve(FILE);
		if (!Files.exists(output)) {
			Files.createDirectories(output.getParent());
			Files.createFile(output);
		}
		try (PrintWriter writer = new PrintWriter(Files.newOutputStream(output, StandardOpenOption.APPEND), false,
				StandardCharsets.UTF_8)) {
			this.properties.writeTo(writer);
		}
	}

}
