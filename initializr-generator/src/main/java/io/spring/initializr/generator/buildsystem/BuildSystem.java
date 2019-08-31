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

package io.spring.initializr.generator.buildsystem;

import java.nio.file.Path;
import java.util.Objects;

import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceStructure;

import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * A build system that can be used by a generated project.
 *
 * @author Andy Wilkinson
 */
public interface BuildSystem {

	/**
	 * The id of the build system.
	 * @return the id
	 */
	String id();

	/**
	 * The dialect of the build system, or {@code null} if the build system does not
	 * support multiple dialects.
	 * @return the dialect or {@code null}
	 */
	default String dialect() {
		return null;
	}

	/**
	 * Returns a {@link SourceStructure} for main sources.
	 * @param projectRoot the root of the project structure
	 * @param language the language of the project
	 * @return a {@link SourceStructure} for main assets
	 */
	default SourceStructure getMainSource(Path projectRoot, Language language) {
		return new SourceStructure(projectRoot.resolve("src/main/"), language);
	}

	/**
	 * Returns a {@link SourceStructure} for test sources.
	 * @param projectRoot the root of the project structure
	 * @param language the language of the project
	 * @return a {@link SourceStructure} for test assets
	 */
	default SourceStructure getTestSource(Path projectRoot, Language language) {
		return new SourceStructure(projectRoot.resolve("src/test/"), language);
	}

	static BuildSystem forId(String id) {
		return forIdAndDialect(id, null);
	}

	static BuildSystem forIdAndDialect(String id, String dialect) {
		return SpringFactoriesLoader.loadFactories(BuildSystemFactory.class, BuildSystem.class.getClassLoader())
				.stream().map((factory) -> factory.createBuildSystem(id, dialect)).filter(Objects::nonNull).findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"Unrecognized build system id '" + id + "' and dialect '" + dialect + "'"));
	}

}
