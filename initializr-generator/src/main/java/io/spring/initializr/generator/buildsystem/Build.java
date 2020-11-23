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

package io.spring.initializr.generator.buildsystem;

/**
 * Build configuration for a project.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public abstract class Build {

	private final PropertyContainer properties;

	private final DependencyContainer dependencies;

	private final BomContainer boms;

	private final MavenRepositoryContainer repositories;

	private final MavenRepositoryContainer pluginRepositories;

	protected Build(BuildItemResolver buildItemResolver) {
		BuildItemResolver resolver = determineBuildItemResolver(buildItemResolver);
		this.properties = new PropertyContainer();
		this.dependencies = new DependencyContainer(resolver::resolveDependency);
		this.boms = new BomContainer(resolver::resolveBom);
		this.repositories = new MavenRepositoryContainer(resolver::resolveRepository);
		this.pluginRepositories = new MavenRepositoryContainer(resolver::resolveRepository);
	}

	protected static BuildItemResolver determineBuildItemResolver(BuildItemResolver buildItemResolver) {
		if (buildItemResolver != null) {
			return buildItemResolver;
		}
		return new SimpleBuildItemResolver((id) -> null, (id) -> null,
				(id) -> id.equals("maven-central") ? MavenRepository.MAVEN_CENTRAL : null);
	}

	/**
	 * Return a builder to configure the general settings of this build.
	 * @return a builder for {@link BuildSettings}.
	 */
	public abstract BuildSettings.Builder<?> settings();

	/**
	 * Return the settings of this build.
	 * @return a {@link BuildSettings}
	 */
	public abstract BuildSettings getSettings();

	/**
	 * Return the {@linkplain PropertyContainer property container} to use to configure
	 * properties.
	 * @return the {@link PropertyContainer}
	 */
	public PropertyContainer properties() {
		return this.properties;
	}

	/**
	 * Return the {@linkplain DependencyContainer dependency container} to use to
	 * configure dependencies.
	 * @return the {@link DependencyContainer}
	 */
	public DependencyContainer dependencies() {
		return this.dependencies;
	}

	/**
	 * Return the {@linkplain BomContainer bom container} to use to configure Bill of
	 * Materials.
	 * @return the {@link BomContainer}
	 */
	public BomContainer boms() {
		return this.boms;
	}

	/**
	 * Return the {@linkplain MavenRepositoryContainer repository container} to use to
	 * configure repositories.
	 * @return the {@link MavenRepositoryContainer} for repositories
	 */
	public MavenRepositoryContainer repositories() {
		return this.repositories;
	}

	/**
	 * Return the {@linkplain MavenRepositoryContainer repository container} to use to
	 * configure plugin repositories.
	 * @return the {@link MavenRepositoryContainer} for plugin repositories
	 */
	public MavenRepositoryContainer pluginRepositories() {
		return this.pluginRepositories;
	}

}
