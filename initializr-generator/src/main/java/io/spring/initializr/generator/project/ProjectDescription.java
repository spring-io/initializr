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

package io.spring.initializr.generator.project;

import java.util.Map;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.version.Version;

/**
 * Description of a project to generate.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public interface ProjectDescription {

	/**
	 * Create a full copy of this description so that any additional changes made on this
	 * instance are not reflected on the returned copy.
	 * @return a clone of this instance
	 */
	default ProjectDescription createCopy() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return a immutable mapping of requested {@link Dependency dependencies}.
	 * @return the requested dependencies
	 */
	Map<String, Dependency> getRequestedDependencies();

	/**
	 * Return the requested platform {@link Version}.
	 * @return the requested platform version or {@code null}
	 */
	Version getPlatformVersion();

	/**
	 * Return the {@link BuildSystem} to use.
	 * @return the build system or {@code null}
	 */
	BuildSystem getBuildSystem();

	/**
	 * Return the build {@link Packaging} to use.
	 * @return the build packaging or {@code null}
	 */
	Packaging getPackaging();

	/**
	 * Return the primary {@link Language} of the project.
	 * @return the primary language or {@code null}
	 */
	Language getLanguage();

	/**
	 * Return the build {@code groupId}.
	 * @return the groupId or {@code null}
	 */
	String getGroupId();

	/**
	 * Return the build {@code artifactId}.
	 * @return the artifactId or {@code null}
	 */
	String getArtifactId();

	/**
	 * Return the version of the project.
	 * @return the version of {@code null}
	 */
	String getVersion();

	/**
	 * Return a simple name for the project.
	 * @return the name of the project or {@code null}
	 */
	String getName();

	/**
	 * Return a human readable description of the project.
	 * @return the description of the project or {@code null}
	 */
	String getDescription();

	/**
	 * Return the name of the application as a standard Java identifier.
	 * @return the name of the application or {@code null}
	 */
	String getApplicationName();

	/**
	 * Return the root package name of the project.
	 * @return the package name or {@code null}
	 */
	String getPackageName();

	/**
	 * Return the base directory of the project or {@code null} to use the root directory.
	 * @return the base directory
	 */
	String getBaseDirectory();

}
