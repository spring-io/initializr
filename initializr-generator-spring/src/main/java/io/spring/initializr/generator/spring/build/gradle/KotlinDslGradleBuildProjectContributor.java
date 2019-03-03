/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.spring.build.gradle;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.KotlinDslGradleBuildWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

/**
 * {@link ProjectContributor} for the project's {@code build.gradle.kts} file.
 *
 * @author Jean-Baptiste Nizet
 */
public class KotlinDslGradleBuildProjectContributor
		extends GradleBuildProjectContributor {

	KotlinDslGradleBuildProjectContributor(KotlinDslGradleBuildWriter buildWriter,
			GradleBuild build, IndentingWriterFactory indentingWriterFactory) {
		super(buildWriter, build, indentingWriterFactory, "build.gradle.kts");
	}

}
