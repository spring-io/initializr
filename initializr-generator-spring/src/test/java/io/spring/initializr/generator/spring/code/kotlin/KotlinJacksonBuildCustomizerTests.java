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

import java.util.Collections;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemResolver;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinJacksonBuildCustomizer}.
 *
 * @author Sebastien Deleuze
 * @author Stephane Nicoll
 * @author Madhura Bhave
 */
class KotlinJacksonBuildCustomizerTests {

	@Test
	void customizeWhenJsonFacetPresentShouldAddJacksonKotlinModule() {
		Dependency dependency = Dependency.withId("foo");
		dependency.setFacets(Collections.singletonList("json"));
		MutableProjectDescription description = new MutableProjectDescription();
		description.setLanguage(new KotlinLanguage());
		MavenBuild build = getCustomizedBuild(dependency, description);
		io.spring.initializr.generator.buildsystem.Dependency jacksonKotlin = build.dependencies()
				.get("jackson-module-kotlin");
		assertThat(jacksonKotlin.getArtifactId()).isEqualTo("jackson-module-kotlin");
		assertThat(jacksonKotlin.getGroupId()).isEqualTo("com.fasterxml.jackson.module");
	}

	@Test
	void jacksonModuleKotlinIsNotAddedWithoutKotlin() {
		Dependency dependency = Dependency.withId("foo");
		dependency.setFacets(Collections.singletonList("json"));
		MutableProjectDescription description = new MutableProjectDescription();
		description.setLanguage(new JavaLanguage());
		MavenBuild build = getCustomizedBuild(dependency, description);
		io.spring.initializr.generator.buildsystem.Dependency jacksonKotlin = build.dependencies()
				.get("jackson-module-kotlin");
		assertThat(jacksonKotlin).isNull();
	}

	@Test
	void jacksonModuleKotlinIsNotAddedWithoutJsonFacet() {
		Dependency dependency = Dependency.withId("foo");
		MutableProjectDescription description = new MutableProjectDescription();
		description.setLanguage(new KotlinLanguage());
		MavenBuild build = getCustomizedBuild(dependency, description);
		io.spring.initializr.generator.buildsystem.Dependency jacksonKotlin = build.dependencies()
				.get("jackson-module-kotlin");
		assertThat(jacksonKotlin).isNull();
	}

	private MavenBuild getCustomizedBuild(Dependency dependency, MutableProjectDescription description) {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", dependency).build();
		KotlinJacksonBuildCustomizer customizer = new KotlinJacksonBuildCustomizer(metadata, description);
		MavenBuild build = new MavenBuild(new MetadataBuildItemResolver(metadata, Version.parse("2.0.0.RELEASE")));
		build.dependencies().add("foo");
		customizer.customize(build);
		return build;
	}

}
