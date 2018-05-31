/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Repository;
import io.spring.initializr.metadata.Type;
import io.spring.initializr.util.Version;
import io.spring.initializr.util.VersionProperty;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

/**
 * A request to generate a project.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class ProjectRequest extends BasicProjectRequest {

	/**
	 * The id of the starter to use if no dependency is defined.
	 */
	public static final String DEFAULT_STARTER = "root_starter";

	private final Map<String, Object> parameters = new LinkedHashMap<>();

	// Resolved dependencies based on the ids provided by either "style" or "dependencies"
	private List<Dependency> resolvedDependencies;

	private final Map<String, BillOfMaterials> boms = new LinkedHashMap<>();

	private final Map<String, Repository> repositories = new LinkedHashMap<>();

	private final BuildProperties buildProperties = new BuildProperties();

	private List<String> facets = new ArrayList<>();

	private String build;

	public List<Dependency> getResolvedDependencies() {
		return this.resolvedDependencies;
	}

	public void setResolvedDependencies(List<Dependency> resolvedDependencies) {
		this.resolvedDependencies = resolvedDependencies;
	}

	public List<String> getFacets() {
		return this.facets;
	}

	public void setFacets(List<String> facets) {
		this.facets = facets;
	}

	public String getBuild() {
		return this.build;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	/**
	 * Return the additional parameters that can be used to further identify the request.
	 * @return the parameters
	 */
	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	public Map<String, BillOfMaterials> getBoms() {
		return this.boms;
	}

	public Map<String, Repository> getRepositories() {
		return this.repositories;
	}

	/**
	 * Return the build properties.
	 * @return the build properties
	 */
	public BuildProperties getBuildProperties() {
		return this.buildProperties;
	}

	/**
	 * Initializes this instance with the defaults defined in the specified
	 * {@link InitializrMetadata}.
	 * @param metadata the initializr metadata
	 */
	public void initialize(InitializrMetadata metadata) {
		BeanWrapperImpl bean = new BeanWrapperImpl(this);
		metadata.defaults().forEach((key, value) -> {
			if (bean.isWritableProperty(key)) {
				// We want to be able to infer a package name if none has been
				// explicitly set
				if (!key.equals("packageName")) {
					bean.setPropertyValue(key, value);
				}
			}
		});
	}

	/**
	 * Resolve this instance against the specified {@link InitializrMetadata}.
	 * @param metadata the initializr metadata
	 */
	public void resolve(InitializrMetadata metadata) {
		List<String> depIds = (!getStyle().isEmpty() ? getStyle() : getDependencies());
		String actualBootVersion = (getBootVersion() != null ? getBootVersion()
				: metadata.getBootVersions().getDefault().getId());
		Version requestedVersion = Version.parse(actualBootVersion);
		this.resolvedDependencies = depIds.stream().map((it) -> {
			Dependency dependency = metadata.getDependencies().get(it);
			if (dependency == null) {
				throw new InvalidProjectRequestException(
						"Unknown dependency '" + it + "' check project metadata");
			}
			return dependency.resolve(requestedVersion);
		}).collect(Collectors.toList());
		this.resolvedDependencies.forEach((it) -> {
			it.getFacets().forEach((facet) -> {
				if (!this.facets.contains(facet)) {
					this.facets.add(facet);
				}
			});
			if (!it.match(requestedVersion)) {
				throw new InvalidProjectRequestException(
						"Dependency '" + it.getId() + "' is not compatible "
								+ "with Spring Boot " + requestedVersion);
			}
			if (it.getBom() != null) {
				resolveBom(metadata, it.getBom(), requestedVersion);
			}
			if (it.getRepository() != null) {
				String repositoryId = it.getRepository();
				this.repositories.computeIfAbsent(repositoryId, (s) -> metadata
						.getConfiguration().getEnv().getRepositories().get(s));
			}
		});
		if (getType() != null) {
			Type type = metadata.getTypes().get(getType());
			if (type == null) {
				throw new InvalidProjectRequestException(
						"Unknown type '" + getType() + "' check project metadata");
			}
			String buildTag = type.getTags().get("build");
			if (buildTag != null) {
				this.build = buildTag;
			}
		}
		if (getPackaging() != null) {
			DefaultMetadataElement packaging = metadata.getPackagings()
					.get(getPackaging());
			if (packaging == null) {
				throw new InvalidProjectRequestException("Unknown packaging '"
						+ getPackaging() + "' check project metadata");
			}
		}
		if (getLanguage() != null) {
			DefaultMetadataElement language = metadata.getLanguages().get(getLanguage());
			if (language == null) {
				throw new InvalidProjectRequestException("Unknown language '"
						+ getLanguage() + "' check project metadata");
			}
		}

		if (!StringUtils.hasText(getApplicationName())) {
			setApplicationName(
					metadata.getConfiguration().generateApplicationName(getName()));
		}
		setPackageName(metadata.getConfiguration().cleanPackageName(getPackageName(),
				metadata.getPackageName().getContent()));

		initializeRepositories(metadata, requestedVersion);

		initializeProperties(metadata, requestedVersion);

		afterResolution(metadata);
	}

	/**
	 * Set the repositories that this instance should use based on the
	 * {@link InitializrMetadata} and the requested Spring Boot {@link Version}.
	 * @param metadata the initializr metadata
	 * @param requestedVersion the requested version
	 */
	protected void initializeRepositories(InitializrMetadata metadata,
			Version requestedVersion) {
		if (!"RELEASE".equals(requestedVersion.getQualifier().getQualifier())) {
			this.repositories.put("spring-snapshots", metadata.getConfiguration().getEnv()
					.getRepositories().get("spring-snapshots"));
			this.repositories.put("spring-milestones", metadata.getConfiguration()
					.getEnv().getRepositories().get("spring-milestones"));
		}
		this.boms.values().forEach((it) -> it.getRepositories().forEach((key) -> {
			this.repositories.computeIfAbsent(key,
					(s) -> metadata.getConfiguration().getEnv().getRepositories().get(s));
		}));
	}

	protected void initializeProperties(InitializrMetadata metadata,
			Version requestedVersion) {
		Supplier<String> kotlinVersion = () -> metadata.getConfiguration().getEnv()
				.getKotlin().resolveKotlinVersion(requestedVersion);
		if ("gradle".equals(this.build)) {
			this.buildProperties.getGradle().put("springBootVersion",
					this::getBootVersion);
			if ("kotlin".equals(getLanguage())) {
				this.buildProperties.getGradle().put("kotlinVersion", kotlinVersion);
			}
		}
		else {
			this.buildProperties.getMaven().put("project.build.sourceEncoding",
					() -> "UTF-8");
			this.buildProperties.getMaven().put("project.reporting.outputEncoding",
					() -> "UTF-8");
			this.buildProperties.getVersions().put(new VersionProperty("java.version"),
					this::getJavaVersion);
			if ("kotlin".equals(getLanguage())) {
				this.buildProperties.getVersions()
						.put(new VersionProperty("kotlin.version"), kotlinVersion);
			}
		}
	}

	private void resolveBom(InitializrMetadata metadata, String bomId,
			Version requestedVersion) {
		if (!this.boms.containsKey(bomId)) {
			BillOfMaterials bom = metadata.getConfiguration().getEnv().getBoms()
					.get(bomId).resolve(requestedVersion);
			bom.getAdditionalBoms()
					.forEach((id) -> resolveBom(metadata, id, requestedVersion));
			this.boms.put(bomId, bom);
		}
	}

	/**
	 * Update this request once it has been resolved with the specified
	 * {@link InitializrMetadata}.
	 * @param metadata the initializr metadata
	 */
	protected void afterResolution(InitializrMetadata metadata) {
		if ("war".equals(getPackaging())) {
			if (!hasWebFacet()) {
				// Need to be able to bootstrap the web app
				this.resolvedDependencies.add(determineWebDependency(metadata));
				this.facets.add("web");
			}
			// Add the tomcat starter in provided scope
			Dependency tomcat = new Dependency().asSpringBootStarter("tomcat");
			tomcat.setScope(Dependency.SCOPE_PROVIDED);
			this.resolvedDependencies.add(tomcat);
		}
		if (this.resolvedDependencies.stream().noneMatch(Dependency::isStarter)) {
			// There"s no starter so we add the default one
			addDefaultDependency();
		}
	}

	private Dependency determineWebDependency(InitializrMetadata metadata) {
		Dependency web = metadata.getDependencies().get("web");
		if (web != null) {
			return web;
		}
		return Dependency.withId("web").asSpringBootStarter("web");
	}

	/**
	 * Add a default dependency if the project does not define any dependency.
	 */
	protected void addDefaultDependency() {
		Dependency root = new Dependency();
		root.setId(DEFAULT_STARTER);
		root.asSpringBootStarter("");
		this.resolvedDependencies.add(root);
	}

	/**
	 * Specify if this request has the web facet enabled.
	 * @return {@code true} if the project has the web facet
	 */
	public boolean hasWebFacet() {
		return hasFacet("web");
	}

	/**
	 * Specify if this request has the specified facet enabled.
	 * @param facet the facet to check
	 * @return {@code true} if the project has the facet
	 */
	public boolean hasFacet(String facet) {
		return this.facets.contains(facet);
	}

	@Override
	public String toString() {
		return "ProjectRequest [" + "parameters=" + this.parameters + ", "
				+ (this.resolvedDependencies != null
						? "resolvedDependencies=" + this.resolvedDependencies + ", " : "")
				+ "boms=" + this.boms + ", " + "repositories=" + this.repositories + ", "
				+ "buildProperties=" + this.buildProperties + ", "
				+ (this.facets != null ? "facets=" + this.facets + ", " : "")
				+ (this.build != null ? "build=" + this.build : "") + "]";
	}

}
