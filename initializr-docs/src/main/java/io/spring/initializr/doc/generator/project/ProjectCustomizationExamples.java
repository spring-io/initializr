/*
 * Copyright 2012-2020 the original author or authors.
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
package io.spring.initializr.doc.generator.project;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * Examples of customizers.
 *
 * @author Stephane Nicoll
 */
public class ProjectCustomizationExamples {

    // tag::war-plugin-contributor[]
    @Bean
    @ConditionalOnBuildSystem(GradleBuildSystem.ID)
    @ConditionalOnPackaging(WarPackaging.ID)
    public BuildCustomizer<GradleBuild> warPluginContributor() {
        return (build) -> build.plugins().add("war");
    }
    // end::war-plugin-contributor[]
}
