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

package io.spring.initializr.generator.test.buildsystem.maven;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import io.spring.initializr.generator.test.io.AbstractTextAssert;
import io.spring.initializr.generator.test.io.NodeAssert;
import io.spring.initializr.generator.test.io.TextTestUtils;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.Repository;
import org.assertj.core.api.BooleanAssert;
import org.assertj.core.api.Condition;
import org.assertj.core.api.StringAssert;
import org.assertj.core.api.UrlAssert;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Assertions for a Maven build.
 *
 * @author Stephane Nicoll
 */
public class MavenBuildAssert extends AbstractTextAssert<MavenBuildAssert> {

	private final NodeAssert pom;

	public MavenBuildAssert(String content) {
		super(content, MavenBuildAssert.class);
		this.pom = new NodeAssert(content);
	}

	public MavenBuildAssert(Path pomFile) {
		this(TextTestUtils.readContent(pomFile));
	}

	/**
	 * Assert {@code pom.xml} defines the specified parent.
	 * @param groupId the groupId of the parent
	 * @param artifactId the artifactId of the parent
	 * @param version the version of the parent
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasParent(String groupId, String artifactId, String version) {
		return hasText("/project/parent/groupId", groupId).hasText("/project/parent/artifactId", artifactId)
				.hasText("/project/parent/version", version);
	}

	/**
	 * Assert {@code pom.xml} uses the specified {@code groupId}.
	 * @param groupId the groupId of the project
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasGroupId(String groupId) {
		return hasText("/project/groupId", groupId);
	}

	/**
	 * Assert {@code pom.xml} uses the specified {@code artifactId}.
	 * @param artifactId the artifactId of the project
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasArtifactId(String artifactId) {
		return hasText("/project/artifactId", artifactId);
	}

	/**
	 * Assert {@code pom.xml} uses the specified {@code version}.
	 * @param version the version of the project
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasVersion(String version) {
		return hasText("/project/version", version);
	}

	/**
	 * Assert {@code pom.xml} uses the specified {@code packaging}.
	 * @param packaging the packaging of the project
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasPackaging(String packaging) {
		return hasText("/project/packaging", packaging);
	}

	/**
	 * Assert {@code pom.xml} uses the specified {@code name}.
	 * @param name the name of the project
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasName(String name) {
		return hasText("/project/name", name);
	}

	/**
	 * Assert {@code pom.xml} uses the specified {@code description}.
	 * @param description the description of the project
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasDescription(String description) {
		return hasText("/project/description", description);
	}

	/**
	 * Assert {@code pom.xml} defines the specified property.
	 * @param name the name of the property
	 * @param value the value of the property
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasProperty(String name, String value) {
		return hasText("/project/properties/" + name, value);
	}

	/**
	 * Assert {@code pom.xml} does not define the specified property.
	 * @param name the name of the property
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert doesNotHaveProperty(String name) {
		return doesNotHaveNode("/project/properties/" + name);
	}

	/**
	 * Assert {@code pom.xml} defines the specified number of dependencies.
	 * @param size the number of dependencies
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasDependenciesSize(int size) {
		this.pom.nodesAtPath("project/dependencies/dependency").hasSize(size);
		return this;
	}

	/**
	 * Assert {@code pom.xml} defines the specified dependency with no version and compile
	 * scope.
	 * @param groupId the groupId of the dependency
	 * @param artifactId the artifactId of the dependency
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasDependency(String groupId, String artifactId) {
		return hasDependency(groupId, artifactId, null);
	}

	/**
	 * Assert {@code pom.xml} defines the specified dependency with compile scope.
	 * @param groupId the groupId of the dependency
	 * @param artifactId the artifactId of the dependency
	 * @param version the version of the dependency
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasDependency(String groupId, String artifactId, String version) {
		return hasDependency(Dependency.create(groupId, artifactId, version, "compile"));
	}

	/**
	 * Assert {@code pom.xml} defines the specified dependency with the specified scope.
	 * @param groupId the groupId of the dependency
	 * @param artifactId the artifactId of the dependency
	 * @param version the version of the dependency
	 * @param scope the scope of the dependency
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasDependency(String groupId, String artifactId, String version, String scope) {
		return hasDependency(Dependency.create(groupId, artifactId, version, scope));
	}

	/**
	 * Assert {@code pom.xml} defines the specified dependency.
	 * @param dependency the dependency
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasDependency(Dependency dependency) {
		this.pom.nodesAtPath("/project/dependencies/dependency").areExactly(1, new Condition<>((candidate) -> {
			Dependency actual = toDependency(candidate);
			if (dependency.getGroupId().equals(actual.getGroupId())
					&& dependency.getArtifactId().equals(actual.getArtifactId())) {
				if (dependency.getVersion() != null) {
					new StringAssert(actual.getVersion()).isEqualTo(dependency.getVersion());
				}
				if (dependency.getScope() != null) {
					new StringAssert(actual.getScope()).isEqualTo(dependency.getScope());
				}
				if (dependency.getType() != null) {
					new StringAssert(actual.getType()).isEqualTo(dependency.getType());
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
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert doesNotHaveDependency(String groupId, String artifactId) {
		this.pom.nodesAtPath("/project/dependencies/dependency").noneMatch((candidate) -> {
			Dependency actual = toDependency(candidate);
			return groupId.equals(actual.getGroupId()) && artifactId.equals(actual.getArtifactId());
		});
		return this;
	}

	/**
	 * Assert {@code pom.xml} defines the specified number of boms.
	 * @param size the number of boms
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasBomsSize(int size) {
		this.pom.nodesAtPath("/project/dependencyManagement/dependencies/dependency").hasSize(size);
		return this;
	}

	/**
	 * Assert {@code pom.xml} defines the specified bom.
	 * @param groupId the groupId of the bom
	 * @param artifactId the artifactId of the bom
	 * @param version the version of the bom
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasBom(String groupId, String artifactId, String version) {
		this.pom.nodesAtPath("/project/dependencyManagement/dependencies/dependency").areExactly(1,
				new Condition<>((candidate) -> {
					BillOfMaterials actual = toBom(candidate);
					return (actual != null && actual.getGroupId().equals(groupId)
							&& actual.getArtifactId().equals(artifactId) && actual.getVersion().equals(version));
				}, "matching bom"));
		return this;
	}

	/**
	 * Assert that {@code pom.xml} does not define the specified bom.
	 * @param groupId the groupId of the bom
	 * @param artifactId the artifactId of the bom
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert doesNotHaveBom(String groupId, String artifactId) {
		this.pom.nodesAtPath("/project/dependencyManagement/dependencies/dependency").noneMatch((candidate) -> {
			BillOfMaterials actual = toBom(candidate);
			return groupId.equals(actual.getGroupId()) && artifactId.equals(actual.getArtifactId());
		});
		return this;
	}

	/**
	 * Assert {@code pom.xml} defines the specified number of repositories.
	 * @param size the number of repositories
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasRepositoriesSize(int size) {
		this.pom.nodesAtPath("/project/repositories/repository").hasSize(size);
		return this;
	}

	/**
	 * Assert {@code pom.xml} defines the specified repository.
	 * @param id the id of the repository
	 * @param name the name of the repository
	 * @param url the url of the repository
	 * @param snapshotsEnabled whether snapshot is enabled for the repository
	 * @return {@code this} assertion object
	 */
	public MavenBuildAssert hasRepository(String id, String name, String url, Boolean snapshotsEnabled) {
		this.pom.nodesAtPath("/project/repositories/repository").areExactly(1, new Condition<>((candidate) -> {
			String actualId = ((Element) candidate).getElementsByTagName("id").item(0).getTextContent();
			if (actualId.equals(id)) {
				Repository repository = toRepository(candidate);
				if (name != null) {
					new StringAssert(repository.getName()).isEqualTo(name);
				}
				if (url != null) {
					try {
						new UrlAssert(repository.getUrl()).isEqualTo(new URL(url));
					}
					catch (MalformedURLException ex) {
						throw new IllegalArgumentException("Cannot parse URL", ex);
					}
				}
				if (snapshotsEnabled) {
					new BooleanAssert(repository.isSnapshotsEnabled()).isEqualTo(snapshotsEnabled);
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
	public MavenBuildAssert doesNotHaveNode(String path) {
		this.pom.nodeAtPath(path).isNull();
		return this;
	}

	/**
	 * Assert {@code pom.xml} contains the specified value at the specified path.
	 * @param path the path to the element
	 * @param value the expected value of the element
	 * @return this
	 */
	public MavenBuildAssert hasText(String path, String value) {
		this.pom.textAtPath(path).isEqualTo(value);
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
				repository.setSnapshotsEnabled("true".equals(snapshotsEnabled.item(0).getTextContent()));
			}
		}
		return repository;
	}

}
