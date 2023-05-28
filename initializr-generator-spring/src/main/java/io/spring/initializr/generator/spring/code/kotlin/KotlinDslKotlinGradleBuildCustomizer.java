/*
 * Copyright 2012-2023 the original author or authors.
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

import java.util.stream.Collectors;
import io.spring.initializr.generator.buildsystem.gradle.GradleTask;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * {@link BuildCustomizer} for Kotlin projects build with Gradle (Kotlin DSL).
 *
 * @author Jean-Baptiste Nizet
 */
class KotlinDslKotlinGradleBuildCustomizer extends KotlinGradleBuildCustomizer {

    KotlinDslKotlinGradleBuildCustomizer(KotlinProjectSettings kotlinProjectSettings) {
        super(kotlinProjectSettings);
    }

    @Override
    protected void customizeKotlinOptions(KotlinProjectSettings settings, GradleTask.Builder compile) {
        compile.nested("kotlinOptions", (kotlinOptions) -> {
            String compilerArgs = settings.getCompilerArgs().stream().map((arg) -> "\"" + arg + "\"").collect(Collectors.joining(", "));
            kotlinOptions.attribute("freeCompilerArgs", "listOf(" + compilerArgs + ")");
            kotlinOptions.attribute("jvmTarget", "\"" + settings.getJvmTarget() + "\"");
        });
    }
}
