/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinGradleBuildCustomizer}.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 * @author Moritz Halbritter
 */
class KotlinGradleBuildCustomizerTests {

	@Test
	void kotlinPluginsAreConfigured() {
		GradleBuild build = new GradleBuild();
		new KotlinGradleBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70"), '"').customize(build);
		assertThat(build.plugins().values()).extracting("id", "version")
			.containsExactlyInAnyOrder(Tuple.tuple("org.jetbrains.kotlin.jvm", "1.2.70"),
					Tuple.tuple("org.jetbrains.kotlin.plugin.spring", "1.2.70"));
	}

	@Test
	void shouldCustomizeCompilerOptions() {
		GradleBuild build = new GradleBuild();
		new KotlinGradleBuildCustomizer(new SimpleKotlinProjectSettings("1.2.70"), '\'').customize(build);
		assertThat(build.extensions().values()).singleElement().satisfies((kotlin) -> {
			assertThat(kotlin.getName()).isEqualTo("kotlin");
			assertThat(kotlin.getNested().values()).singleElement().satisfies((compilerOptions) -> {
				assertThat(compilerOptions.getName()).isEqualTo("compilerOptions");
				assertThat(compilerOptions.getInvocations()).singleElement().satisfies((freeCompilerArgs) -> {
					assertThat(freeCompilerArgs.getTarget()).isEqualTo("freeCompilerArgs.addAll");
					assertThat(freeCompilerArgs.getArguments()).containsExactly("'-Xjsr305=strict'");
				});
			});
		});
	}

}
