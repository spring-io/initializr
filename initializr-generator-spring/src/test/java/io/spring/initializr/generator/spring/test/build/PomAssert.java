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

package io.spring.initializr.generator.spring.test.build;

import java.net.MalformedURLException;
import java.net.URL;

import io.spring.initializr.generator.test.assertj.NodeAssert;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.Repository;
import org.assertj.core.api.Condition;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Node} assertions that are specific to a standard Maven POM.
 *
 * @author Stephane Nicoll
 */
public class PomAssert {

	private final String content;

	private final NodeAssert pom;

	public PomAssert(String content) {
		this.content = content;
		this.pom = new NodeAssert(content);
	}

	/**
	 * Assert {@code pom.xml} defines the specified parent.
	 * @param groupId the groupId of the parent
	 * @param artifactId the artifactId of the parent
	 * @param version the version of the parent
	 * @return this
	 */
	public PomAssert hasParent(String groupId, String artifactId, String version) {
		return hasText("/project/parent/groupId", groupId)
				.hasText("/project/parent/artifactId", artifactId)
				.hasText("/project/parent/version", version);
	}

	/**
	 * Assert {@code pom.xml} defines the standard {@code spring-boot-starter-parent}.
	 * @param version the spring boot version
	 * @return this
	 */
	public PomAssert hasSpringBootParent(String version) {
		return hasParent("org.springframework.boot", "spring-boot-starter-parent",
				version);
	}

	/**
	 * Assert {@code pom.xml} uses the specified {@code groupId}.
	 * @param groupId the groupId of the project
	 * @return this
	 */
	public PomAssert hasGroupId(String groupId) {
		return hasText("/project/groupId", groupId);
	}

	/**
	 * Assert {@code pom.xml} uses the specified {@code artifactId}.
	 * @param artifactId the artifactId of the project
	 * @return this
	 */
	public PomAssert hasArtifactId(String artifactId) {
		return hasText("/project/artifactId", artifactId);
	}

	/**
	 * Assert {@code pom.xml} uses the specified {@code version}.
	 * @param version the version of the project
	 * @return this
	 */
	public PomAssert hasVersion(String version) {
		return hasText("/project/version", version);
	}

	/**
	 * Assert {@code pom.xml} uses the specified {@code packaging}.
	 * @param packaging the packaging of the project
	 * @return this
	 */
	public PomAssert hasPackaging(String packaging) {
		return hasText("/project/packaging", packaging);
	}

	/**
	 * Assert {@code pom.xml} uses the specified {@code name}.
	 * @param name the name of the project
	 * @return this
	 */
	public PomAssert hasName(String name) {
		return hasText("/project/name", name);
	}

	/**
	 * Assert {@code pom.xml} uses the specified {@code description}.
	 * @param description the description of the project
	 * @return this
	 */
	public PomAssert hasDescription(String description) {
		return hasText("/project/description", description);
	}

	/**
	 * Assert {@code pom.xml} defines the specified property.
	 * @param name the name of the property
	 * @param value the value of the property
	 * @return this
	 */
	public PomAssert hasProperty(String name, String value) {
		return hasText("/project/properties/" + name, value);
	}

	/**
	 * Assert {@code pom.xml} does not defined the specified property.
	 * @param name the name of the property
	 * @return this
	 */
	public PomAssert doesNotHaveProperty(String name) {
		return doesNotHaveNode("/project/properties/" + name);
	}

	/**
	 * Assert {@code pom.xml} defines the specified Java version.
	 * @param javaVersion the java version of the project
	 * @return this
	 */
	public PomAssert hasJavaVersion(String javaVersion) {
		return hasProperty("java.version", javaVersion);
	}

	/**
	 * Assert {@code pom.xml} defines the specified number of dependencies.
	 * @param count the number of dependencies
	 * @return this
	 */
	public PomAssert hasDependenciesCount(int count) {
		assertThat(this.pom).nodesAtPath("project/dependencies/dependency")
				.hasSize(count);
		return this;
	}

	/**
	 * Assert {@code pom.xml} defines the specified starter.
	 * @param starterId the id of the starter (e.g. {@code web} for
	 * {@code spring-boot-starter-web}.
	 * @return this
	 */
	public PomAssert hasSpringBootStarterDependency(String starterId) {
		return hasDependency("org.springframework.boot",
				"spring-boot-starter-" + starterId);
	}

	/**
	 * Assert {@code pom.xml} defines the specified starter in the specified scope.
	 * @param starterId the id of the starter (e.g. {@code web} for
	 * {@code spring-boot-starter-web}.
	 * @param scope the scope of the starter
	 * @return this
	 */
	public PomAssert hasSpringBootStarterDependency(String starterId, String scope) {
		return hasDependency("org.springframework.boot",
				"spring-boot-starter-" + starterId, null, scope);
	}

	/**
	 * Assert {@code pom.xml} defines the root {@code spring-boot-starter} starter.
	 * @return this
	 */
	public PomAssert hasSpringBootStarterRootDependency() {
		return hasDependency("org.springframework.boot", "spring-boot-starter");
	}

	/**
	 * Assert {@code pom.xml} defines the {@code spring-boot-starter-test} starter.
	 * @return this
	 */
	public PomAssert hasSpringBootStarterTest() {
		return hasSpringBootStarterDependency("test", "test");
	}

	/**
	 * Assert {@code pom.xml} defines the specified dependency with no version and compile
	 * scope.
	 * @param groupId the groupId of the dependency
	 * @param artifactId the artifactId of the dependency
	 * @return this
	 */
	public PomAssert hasDependency(String groupId, String artifactId) {
		return hasDependency(groupId, artifactId, null);
	}

	/**
	 * Assert {@code pom.xml} defines the specified dependency with compile scope.
	 * @param groupId the groupId of the dependency
	 * @param artifactId the artifactId of the dependency
	 * @param version the version of the dependency
	 * @return this
	 */
	public PomAssert hasDependency(String groupId, String artifactId, String version) {
		return hasDependency(Dependency.create(groupId, artifactId, version, "compile"));
	}

	/**
	 * Assert {@code pom.xml} defines the specified dependency with the specified scope.
	 * @param groupId the groupId of the dependency
	 * @param artifactId the artifactId of the dependency
	 * @param version the version of the dependency
	 * @param scope the scope of the dependency
	 * @return this
	 */
	public PomAssert hasDependency(String groupId, String artifactId, String version,
			String scope) {
		return hasDependency(Dependency.create(groupId, artifactId, version, scope));
	}

	/**
	 * Assert {@code pom.xml} defines the specified dependency.
	 * @param dependency the dependency
	 * @return this
	 */
	public PomAssert hasDependency(Dependency dependency) {
		assertThat(this.pom).nodesAtPath("/project/dependencies/dependency").areExactly(1,
				new Condition<>((candidate) -> {
					Dependency actual = toDependency(candidate);
					if (dependency.getGroupId().equals(actual.getGroupId()) && dependency
							.getArtifactId().equals(actual.getArtifactId())) {
						if (dependency.getVersion() != null) {
							assertThat(actual.getVersion())
									.isEqualTo(dependency.getVersion());
						}
						if (dependency.getScope() != null) {
							assertThat(actual.getScope())
									.isEqualTo(dependency.getScope());
						}
						if (dependency.getType() != null) {
							assertThat(actual.getType()).isEqualTo(dependency.getType());
						}
						return true;
					}
					return false;
				}, "matching dependency"));
		return this;
	}

	/**
	 * Assert that {@code pom.xml} does not define a dependency with the specified
	 * {@code groupId} and {@code artifactId}.
	 * @param groupId the dependency's groupId
	 * @param artifactId the dependency's artifactId
	 * @return this
	 */
	public PomAssert doesNotHaveDependency(String groupId, String artifactId) {
		assertThat(this.pom).nodesAtPath("/project/dependencies/dependency")
				.noneMatch((candidate) -> {
					Dependency actual = toDependency(candidate);
					return groupId.equals(actual.getGroupId())
							&& artifactId.equals(actual.getArtifactId());
				});
		return this;
	}

	/**
	 * Assert {@code pom.xml} defines the specified number of boms.
	 * @param count the number of boms
	 * @return this
	 */
	public PomAssert hasBomsCount(int count) {
		assertThat(this.pom)
				.nodesAtPath("/project/dependencyManagement/dependencies/dependency")
				.hasSize(count);
		return this;
	}

	/**
	 * Assert {@code pom.xml} defines the specified bom.
	 * @param groupId the groupId of the bom
	 * @param artifactId the artifactId of the bom
	 * @param version the version of the bom
	 * @return this
	 */
	public PomAssert hasBom(String groupId, String artifactId, String version) {
		assertThat(this.pom)
				.nodesAtPath("/project/dependencyManagement/dependencies/dependency")
				.areExactly(1, new Condition<>((candidate) -> {
					BillOfMaterials actual = toBom(candidate);
					return (actual != null && actual.getGroupId().equals(groupId)
							&& actual.getArtifactId().equals(artifactId)
							&& actual.getVersion().equals(version));
				}, "matching bom"));
		return this;
	}

	/**
	 * Assert {@code pom.xml} defines the specified number of repositories.
	 * @param count the number of repositories
	 * @return this
	 */
	public PomAssert hasRepositoriesCount(int count) {
		assertThat(this.pom).nodesAtPath("/project/repositories/repository")
				.hasSize(count);
		return this;
	}

	/**
	 * Assert {@code pom.xml} defines the specified repository.
	 * @param id the id of the repository
	 * @param name the name of the repository
	 * @param url the url of the repository
	 * @param snapshotsEnabled whether snapshot is enabled for the repository
	 * @return this
	 */
	public PomAssert hasRepository(String id, String name, String url,
			Boolean snapshotsEnabled) {
		assertThat(this.pom).nodesAtPath("/project/repositories/repository").areExactly(1,
				new Condition<>((candidate) -> {
					String actualId = ((Element) candidate).getElementsByTagName("id")
							.item(0).getTextContent();
					if (actualId.equals(id)) {
						Repository repository = toRepository(candidate);
						if (name != null) {
							assertThat(repository.getName()).isEqualTo(name);
						}
						if (url != null) {
							try {
								assertThat(repository.getUrl()).isEqualTo(new URL(url));
							}
							catch (MalformedURLException ex) {
								throw new IllegalArgumentException("Cannot parse URL",
										ex);
							}
						}
						if (snapshotsEnabled) {
							assertThat(repository.isSnapshotsEnabled())
									.isEqualTo(snapshotsEnabled);
						}
						return true;
					}
					return false;
				}, "matching repository"));
		return this;
	}

	/**
	 * Assert {@code pom.xml} does not define a node with the specified {@code path}.
	 * @param path the path of the node
	 * @return this
	 */
	public PomAssert doesNotHaveNode(String path) {
		assertThat(this.pom.nodeAtPath(path)).isNull();
		return this;
	}

	/**
	 * Assert {@code pom.xml} contains the specified expression.
	 * @param expression an expected expression
	 * @return this
	 */
	public PomAssert contains(String expression) {
		assertThat(this.content).contains(expression);
		return this;
	}

	/**
	 * Assert {@code pom.xml} does not contain the specified expression.
	 * @param expression an unexpected expression
	 * @return this
	 */
	public PomAssert doesNotContain(String expression) {
		assertThat(this.content).doesNotContain(expression);
		return this;
	}

	private PomAssert hasText(String path, String value) {
		assertThat(this.pom).textAtPath(path).isEqualTo(value);
		return this;
	}

	private static Dependency toDependency(Node item) {
		if (item instanceof Element) {
			Dependency dependency = new Dependency();
			Element element = (Element) item;
			NodeList groupId = element.getElementsByTagName("groupId");
			if (groupId.getLength() > 0) {
				dependency.setGroupId(groupId.item(0).getTextContent());
			}
			NodeList artifactId = element.getElementsByTagName("artifactId");
			if (artifactId.getLength() > 0) {
				dependency.setArtifactId(artifactId.item(0).getTextContent());
			}
			NodeList version = element.getElementsByTagName("version");
			if (version.getLength() > 0) {
				dependency.setVersion(version.item(0).getTextContent());
			}
			NodeList scope = element.getElementsByTagName("scope");
			if (scope.getLength() > 0) {
				dependency.setScope(scope.item(0).getTextContent());
			}
			NodeList type = element.getElementsByTagName("type");
			if (type.getLength() > 0) {
				dependency.setType(type.item(0).getTextContent());
			}
			return dependency;
		}
		return null;
	}

	private static BillOfMaterials toBom(Node item) {
		if (item instanceof Element) {
			Element element = (Element) item;
			NodeList type = element.getElementsByTagName("type");
			NodeList scope = element.getElementsByTagName("scope");
			if (isBom(type, scope)) {
				BillOfMaterials bom = new BillOfMaterials();
				NodeList groupId = element.getElementsByTagName("groupId");
				if (groupId.getLength() > 0) {
					bom.setGroupId(groupId.item(0).getTextContent());
				}
				NodeList artifactId = element.getElementsByTagName("artifactId");
				if (artifactId.getLength() > 0) {
					bom.setArtifactId(artifactId.item(0).getTextContent());
				}
				NodeList version = element.getElementsByTagName("version");
				if (version.getLength() > 0) {
					bom.setVersion(version.item(0).getTextContent());
				}
				return bom;
			}
		}
		return null;
	}

	private static boolean isBom(NodeList type, NodeList scope) {
		if (type.getLength() == 0 || scope.getLength() == 0) {
			return false;
		}
		String typeValue = type.item(0).getTextContent();
		String scopeValue = scope.item(0).getTextContent();
		return "pom".equals(typeValue) && "import".equals(scopeValue);
	}

	private static Repository toRepository(Node item) {
		Repository repository = new Repository();
		Element element = (Element) item;
		NodeList name = element.getElementsByTagName("name");
		if (name.getLength() > 0) {
			repository.setName(name.item(0).getTextContent());
		}
		NodeList url = element.getElementsByTagName("url");
		if (url.getLength() > 0) {
			try {
				repository.setUrl(new URL(url.item(0).getTextContent()));
			}
			catch (MalformedURLException | DOMException ex) {
				throw new IllegalStateException("Cannot parse URL", ex);
			}
		}
		NodeList snapshots = element.getElementsByTagName("snapshots");
		if (snapshots.getLength() > 0) {
			Element snapshotsElement = (Element) snapshots.item(0);
			NodeList snapshotsEnabled = snapshotsElement.getElementsByTagName("enabled");
			if (snapshotsEnabled.getLength() > 0) {
				repository.setSnapshotsEnabled(
						"true".equals(snapshotsEnabled.item(0).getTextContent()));
			}
		}
		return repository;
	}

}
