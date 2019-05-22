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

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionReference;

/**
 * {@link BuildCustomizer} that adds the dependencies required by projects written in
 * Kotlin.
 *
 * @author Andy Wilkinson
 */
class KotlinDependenciesConfigurer implements BuildCustomizer<Build> {

	private final Version springBootVersion;

	KotlinDependenciesConfigurer(Version springBootVersion) {
		this.springBootVersion = springBootVersion;
	}

	@Override
	public void customize(Build build) {
		VersionReference version = determineDependencyVersion(build);
		build.dependencies()
				.add("kotlin-stdlib", Dependency
						.withCoordinates("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
						.version(version).scope(DependencyScope.COMPILE));
		build.dependencies().add("kotlin-reflect",
				Dependency.withCoordinates("org.jetbrains.kotlin", "kotlin-reflect")
						.version(version).scope(DependencyScope.COMPILE));
	}

	private VersionReference determineDependencyVersion(Build build) {
		if (build instanceof MavenBuild
				&& this.springBootVersion.compareTo(Version.parse("2.0.0.M1")) <= 0) {
			return VersionReference.ofProperty("kotlin.version");
		}
		return null;
	}

}
