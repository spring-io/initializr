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

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild.TaskCustomization;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * {@link BuildCustomizer} abstraction for Kotlin projects build with Gradle.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 * @see GroovyDslKotlinGradleBuildCustomizer
 * @see KotlinDslKotlinGradleBuildCustomizer
 */
abstract class KotlinGradleBuildCustomizer implements BuildCustomizer<GradleBuild> {

	private final KotlinProjectSettings settings;

	KotlinGradleBuildCustomizer(KotlinProjectSettings kotlinProjectSettings) {
		this.settings = kotlinProjectSettings;
	}

	@Override
	public void customize(GradleBuild build) {
		build.addPlugin("org.jetbrains.kotlin.jvm", this.settings.getVersion());
		build.addPlugin("org.jetbrains.kotlin.plugin.spring", this.settings.getVersion());
		build.customizeTasksWithType("org.jetbrains.kotlin.gradle.tasks.KotlinCompile",
				(compile) -> customizeKotlinOptions(this.settings, compile));
	}

	protected abstract void customizeKotlinOptions(KotlinProjectSettings settings,
			TaskCustomization compile);

}
