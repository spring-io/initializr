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

import java.util.Collections;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradlePlugin;
import io.spring.initializr.generator.buildsystem.gradle.StandardGradlePlugin;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemResolver;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinJpaGradleBuildCustomizer}.
 *
 * @author Madhura Bhave
 */
class KotlinJpaGradleBuildCustomizerTests {

    @Test
    void customizeWhenJpaFacetPresentShouldAddKotlinJpaPlugin() {
        Dependency dependency = Dependency.withId("foo");
        dependency.setFacets(Collections.singletonList("jpa"));
        GradleBuild build = getCustomizedBuild(dependency);
        assertThat(build.plugins().values().filter(GradlePlugin::isApply)).isEmpty();
        assertThat(build.plugins().values()).singleElement().satisfies((plugin) -> {
            assertThat(plugin.getId()).isEqualTo("org.jetbrains.kotlin.plugin.jpa");
            assertThat(((StandardGradlePlugin) plugin).getVersion()).isEqualTo("1.2.70");
        });
    }

    @Test
    void customizeWhenJpaFacetAbsentShouldNotAddKotlinJpaPlugin() {
        Dependency dependency = Dependency.withId("foo");
        GradleBuild build = getCustomizedBuild(dependency);
        assertThat(build.plugins().values().filter(GradlePlugin::isApply)).isEmpty();
        assertThat(build.plugins().values()).isEmpty();
    }

    private GradleBuild getCustomizedBuild(Dependency dependency) {
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().addDependencyGroup("test", dependency).build();
        SimpleKotlinProjectSettings settings = new SimpleKotlinProjectSettings("1.2.70");
        KotlinJpaGradleBuildCustomizer customizer = new KotlinJpaGradleBuildCustomizer(metadata, settings);
        GradleBuild build = new GradleBuild(new MetadataBuildItemResolver(metadata, Version.parse("2.0.0.RELEASE")));
        build.dependencies().add("foo");
        customizer.customize(build);
        return build;
    }
}
