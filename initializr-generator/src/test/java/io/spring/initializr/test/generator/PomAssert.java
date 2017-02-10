/*
 * Copyright 2012-2017 the original author or authors.
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

package io.spring.initializr.test.generator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Maven.ParentPom;
import io.spring.initializr.metadata.Repository;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Assert;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * XPath assertions that are specific to a standard Maven POM.
 *
 * @author Stephane Nicoll
 */
public class PomAssert {

	private final XpathEngine eng;
	private final Document doc;
	private final ParentPom parentPom;
	private final Map<String, String> properties = new LinkedHashMap<>();
	private final Map<String, Dependency> dependencies = new LinkedHashMap<>();
	private final Map<String, BillOfMaterials> boms = new LinkedHashMap<>();
	private final Map<String, Repository> repositories = new LinkedHashMap<>();

	public PomAssert(String content) {
		eng = XMLUnit.newXpathEngine();
		Map<String, String> context = new LinkedHashMap<>();
		context.put("pom", "http://maven.apache.org/POM/4.0.0");
		SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext(context);
		eng.setNamespaceContext(namespaceContext);
		try {
			doc = XMLUnit.buildControlDocument(content);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Cannot parse XML", e);
		}
		this.parentPom = parseParent();
		parseProperties();
		parseDependencies();
		parseBoms();
		parseRepositories();
	}

	/**
	 * Validate that this generated pom validates against its request.
	 */
	public PomAssert validateProjectRequest(ProjectRequest request) {
		return hasGroupId(request.getGroupId()).hasArtifactId(request.getArtifactId())
				.hasVersion(request.getVersion()).hasPackaging(request.getPackaging())
				.hasName(request.getName()).hasDescription(request.getDescription())
				.hasJavaVersion(request.getJavaVersion());
	}

	public PomAssert hasGroupId(String groupId) {
		try {
			assertEquals(groupId, eng.evaluate(createRootNodeXPath("groupId"), doc));
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);
		}
		return this;
	}

	public PomAssert hasArtifactId(String artifactId) {
		try {
			assertEquals(artifactId,
					eng.evaluate(createRootNodeXPath("artifactId"), doc));
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);
		}
		return this;
	}

	public PomAssert hasVersion(String version) {
		try {
			assertEquals(version, eng.evaluate(createRootNodeXPath("version"), doc));
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);
		}
		return this;
	}

	public PomAssert hasPackaging(String packaging) {
		try {
			assertEquals(packaging, eng.evaluate(createRootNodeXPath("packaging"), doc));
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);
		}
		return this;
	}

	public PomAssert hasName(String name) {
		try {
			assertEquals(name, eng.evaluate(createRootNodeXPath("name"), doc));
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);
		}
		return this;
	}

	public PomAssert hasDescription(String description) {
		try {
			assertEquals(description,
					eng.evaluate(createRootNodeXPath("description"), doc));
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);
		}
		return this;
	}

	public PomAssert hasJavaVersion(String javaVersion) {
		try {
			assertEquals(javaVersion,
					eng.evaluate(createPropertyNodeXpath("java.version"), doc));
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);
		}
		return this;
	}

	public PomAssert hasProperty(String name, String value) {
		assertTrue("No property " + name + " found", properties.containsKey(name));
		assertEquals("Wrong value for property " + name, value, properties.get(name));
		return this;
	}

	public PomAssert hasNoProperty(String name) {
		assertFalse("No property " + name + " should have been found",
				properties.containsKey(name));
		return this;
	}

	public PomAssert hasDependenciesCount(int count) {
		assertEquals("Wrong number of declared dependencies --> " + dependencies.keySet(),
				count, dependencies.size());
		return this;
	}

	public PomAssert hasSpringBootStarterTomcat() {
		return hasDependency(
				Dependency.withId("tomcat", "provided").asSpringBootStarter("tomcat"));
	}

	public PomAssert hasSpringBootStarterTest() {
		return hasDependency(
				Dependency.withId("test", "test").asSpringBootStarter("test"));
	}

	public PomAssert hasSpringBootStarterDependency(String dependency) {
		return hasDependency("org.springframework.boot",
				"spring-boot-starter-" + dependency);
	}

	public PomAssert hasSpringBootStarterRootDependency() {
		return hasDependency("org.springframework.boot", "spring-boot-starter");
	}

	public PomAssert hasDependency(String groupId, String artifactId) {
		return hasDependency(groupId, artifactId, null);
	}

	public PomAssert hasDependency(String groupId, String artifactId, String version) {
		return hasDependency(Dependency.create(groupId, artifactId, version, "compile"));
	}

	public PomAssert hasParent(String groupId, String artifactId, String version) {
		assertEquals(groupId, this.parentPom.getGroupId());
		assertEquals(artifactId, this.parentPom.getArtifactId());
		assertEquals(version, this.parentPom.getVersion());
		return this;
	}

	public PomAssert hasSpringBootParent(String version) {
		return hasParent("org.springframework.boot", "spring-boot-starter-parent",
				version);
	}

	public PomAssert hasDependency(Dependency expected) {
		String id = generateDependencyId(expected.getGroupId(), expected.getArtifactId());
		Dependency dependency = dependencies.get(id);
		assertNotNull("No dependency found with '" + id + "' --> " + dependencies.keySet(),
				dependency);
		if (expected.getVersion() != null) {
			assertEquals("Wrong version for " + dependency, expected.getVersion(),
					dependency.getVersion());
		}
		if (expected.getScope() != null) {
			assertEquals("Wrong scope for " + dependency, expected.getScope(),
					dependency.getScope());
		}
		if (expected.getType() != null) {
			assertEquals("Wrong type for " + dependency, expected.getType(),
					dependency.getType());
		}
		return this;
	}

	public PomAssert hasBom(String groupId, String artifactId, String version) {
		String id = generateBomId(groupId, artifactId);
		BillOfMaterials bom = boms.get(id);
		assertNotNull("No BOM found with '" + id + "' --> " + boms.keySet(), bom);
		assertEquals("Wrong version for " + bom, version, bom.getVersion());
		return this;
	}

	public PomAssert hasBomsCount(int count) {
		assertEquals("Wrong number of declared boms -->" + boms.keySet(), count,
				boms.size());
		return this;
	}

	public PomAssert hasNoRepository() {
		try {
			Assert.assertEquals(0,
					eng.getMatchingNodes(createRootNodeXPath("repositories"), doc)
							.getLength());
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);
		}
		return this;
	}

	public PomAssert hasSnapshotRepository() {
		hasRepository("spring-snapshots", "Spring Snapshots",
				"https://repo.spring.io/snapshot", true);
		hasPluginRepository("spring-snapshots");
		return this;
	}

	public PomAssert hasRepository(String id, String name, String url,
			Boolean snapshotsEnabled) {
		Repository repository = repositories.get(id);
		assertNotNull(
				"No repository found with '" + id + "' --> " + repositories.keySet(),
				repository);
		if (name != null) {
			assertEquals("Wrong name for " + repository, name, repository.getName());
		}
		if (url != null) {
			try {
				assertEquals("Wrong url for " + repository, new URL(url),
						repository.getUrl());
			}
			catch (MalformedURLException e) {
				throw new IllegalArgumentException("Cannot parse URL", e);
			}
		}
		if (snapshotsEnabled) {
			assertEquals("Wrong snapshots enabled flag for " + repository,
					snapshotsEnabled, repository.isSnapshotsEnabled());
		}
		return this;
	}

	public PomAssert hasRepositoriesCount(int count) {
		assertEquals("Wrong number of declared repositories -->" + repositories.keySet(),
				count, repositories.size());
		return this;
	}

	private PomAssert hasPluginRepository(String name) {
		NodeList nodes;
		try {
			nodes = eng.getMatchingNodes(
					createRootNodeXPath("pluginRepositories/pom:pluginRepository/pom:id"),
					doc);
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);
		}
		for (int i = 0; i < nodes.getLength(); i++) {
			if (name.equals(nodes.item(i).getTextContent())) {
				return this;
			}
		}
		throw new IllegalArgumentException("No plugin repository found with id " + name);
	}

	public static String createPropertyNodeXpath(String propertyName) {
		return createRootNodeXPath("properties/pom:" + propertyName);
	}

	public static String createRootNodeXPath(String node) {
		return "/pom:project/pom:" + node;
	}

	private ParentPom parseParent() {
		ParentPom parent = new ParentPom();
		try {
			parent.setGroupId(
					eng.evaluate(createRootNodeXPath("parent/pom:groupId"), doc));
			parent.setArtifactId(
					eng.evaluate(createRootNodeXPath("parent/pom:artifactId"), doc));
			parent.setVersion(
					eng.evaluate(createRootNodeXPath("parent/pom:version"), doc));
			return parent;
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);
		}
	}

	private void parseProperties() {
		NodeList nodes;
		try {
			nodes = eng.getMatchingNodes(createRootNodeXPath("properties/*"), doc);
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);
		}
		for (int i = 0; i < nodes.getLength(); i++) {
			Node item = nodes.item(i);
			if (item instanceof Element) {
				Element element = (Element) item;
				properties.put(element.getTagName(), element.getTextContent());
			}
		}
	}

	private void parseDependencies() {
		NodeList nodes;
		try {
			nodes = eng.getMatchingNodes(
					createRootNodeXPath("dependencies/pom:dependency"), doc);
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);
		}
		for (int i = 0; i < nodes.getLength(); i++) {
			Node item = nodes.item(i);
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
				String id = dependency.generateId();
				assertFalse("Duplicate dependency with id " + id,
						dependencies.containsKey(id));
				dependencies.put(id, dependency);
			}
		}
	}

	private void parseBoms() {
		NodeList nodes;
		try {
			nodes = eng
					.getMatchingNodes(
							createRootNodeXPath(
									"dependencyManagement/pom:dependencies/pom:dependency"),
							doc);
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);
		}
		for (int i = 0; i < nodes.getLength(); i++) {
			Node item = nodes.item(i);
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
					String id = generateBomId(bom.getGroupId(), bom.getArtifactId());
					assertFalse("Duplicate BOM with id " + id, boms.containsKey(id));
					boms.put(id, bom);
				}
			}
		}
	}

	private void parseRepositories() {
		NodeList nodes;
		try {
			nodes = eng.getMatchingNodes(
					createRootNodeXPath("repositories/pom:repository"), doc);
		}
		catch (XpathException e) {
			throw new IllegalStateException("Cannot find path", e);

		}
		for (int i = 0; i < nodes.getLength(); i++) {
			Node item = nodes.item(i);
			if (item instanceof Element) {
				Repository repository = new Repository();
				Element element = (Element) item;
				NodeList type = element.getElementsByTagName("id");
				String id = type.item(0).getTextContent();
				NodeList name = element.getElementsByTagName("name");
				if (name.getLength() > 0) {
					repository.setName(name.item(0).getTextContent());
				}
				NodeList url = element.getElementsByTagName("url");
				if (url.getLength() > 0) {
					try {
						repository.setUrl(new URL(url.item(0).getTextContent()));
					}
					catch (MalformedURLException | DOMException e) {
						throw new IllegalStateException("Cannot parse URL", e);
					}
				}
				NodeList snapshots = element.getElementsByTagName("snapshots");
				if (snapshots.getLength() > 0) {
					Element snapshotsElement = (Element) snapshots.item(0);
					NodeList snapshotsEnabled = snapshotsElement
							.getElementsByTagName("enabled");
					if (snapshotsEnabled.getLength() > 0) {
						repository.setSnapshotsEnabled(
								"true".equals(snapshotsEnabled.item(0).getTextContent()));
					}
				}
				assertFalse("Duplicate Repository with id " + id,
						repositories.containsKey(id));
				repositories.put(id, repository);
			}
		}
	}

	private static boolean isBom(NodeList type, NodeList scope) {
		if (type.getLength() == 0 || scope.getLength() == 0) {
			return false;
		}
		String typeValue = type.item(0).getTextContent();
		String scopeValue = scope.item(0).getTextContent();
		return "pom".equals(typeValue) && "import".equals(scopeValue);
	}

	private static String generateBomId(String groupId, String artifactId) {
		return groupId + ":" + artifactId;
	}

	private static String generateDependencyId(String groupId, String artifactId) {
		Dependency dependency = new Dependency();
		dependency.setGroupId(groupId);
		dependency.setArtifactId(artifactId);
		return dependency.generateId();
	}

}
