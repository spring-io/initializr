/*
 * Copyright 2012-2014 the original author or authors.
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

package io.spring.initializr.support

import io.spring.initializr.InitializrMetadata
import io.spring.initializr.ProjectRequest
import org.custommonkey.xmlunit.SimpleNamespaceContext
import org.custommonkey.xmlunit.XMLUnit
import org.custommonkey.xmlunit.XpathEngine
import org.w3c.dom.Document
import org.w3c.dom.Element

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

/**
 * XPath assertions that are specific to a standard Maven POM.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class PomAssert {

	final XpathEngine eng
	final Document doc
	final Map<String, InitializrMetadata.Dependency> dependencies = new HashMap<String, InitializrMetadata.Dependency>()

	PomAssert(String content) {
		eng = XMLUnit.newXpathEngine()
		Map<String, String> context = new HashMap<String, String>()
		context.put 'pom', 'http://maven.apache.org/POM/4.0.0'
		SimpleNamespaceContext namespaceContext = new SimpleNamespaceContext(context)
		eng.namespaceContext = namespaceContext
		doc = XMLUnit.buildControlDocument(content)
		parseDependencies()
	}

	/**
	 * Validate that this generated pom validates against its request.
	 */
	PomAssert validateProjectRequest(ProjectRequest request) {
		hasGroupId(request.groupId).hasArtifactId(request.artifactId).hasVersion(request.version).
				hasPackaging(request.packaging).hasName(request.name).hasDescription(request.description).
				hasBootVersion(request.bootVersion).hasJavaVersion(request.javaVersion)

	}

	PomAssert hasGroupId(String groupId) {
		assertEquals groupId, eng.evaluate(createRootNodeXPath('groupId'), doc)
		this
	}

	PomAssert hasArtifactId(String artifactId) {
		assertEquals artifactId, eng.evaluate(createRootNodeXPath('artifactId'), doc)
		this
	}

	PomAssert hasVersion(String version) {
		assertEquals version, eng.evaluate(createRootNodeXPath('version'), doc)
		this
	}

	PomAssert hasPackaging(String packaging) {
		assertEquals packaging, eng.evaluate(createRootNodeXPath('packaging'), doc)
		this
	}

	PomAssert hasName(String name) {
		assertEquals name, eng.evaluate(createRootNodeXPath('name'), doc)
		this
	}

	PomAssert hasDescription(String description) {
		assertEquals description, eng.evaluate(createRootNodeXPath('description'), doc)
		this
	}

	PomAssert hasBootVersion(String bootVersion) {
		assertEquals bootVersion, eng.evaluate(createRootNodeXPath('parent/pom:version'), doc)
		this
	}

	PomAssert hasJavaVersion(String javaVersion) {
		assertEquals javaVersion, eng.evaluate(createPropertyNodeXpath('java.version'), doc)
		this
	}

	PomAssert hasStartClass(String fqn) {
		assertEquals fqn, eng.evaluate(createPropertyNodeXpath('start-class'), doc)
		this
	}

	PomAssert hasDependenciesCount(int count) {
		assertEquals 'Wrong number of declared dependencies -->' + dependencies.keySet(), count, dependencies.size()
		this
	}

	PomAssert hasSpringBootStarterDependency(String dependency) {
		hasDependency('org.springframework.boot', 'spring-boot-starter-' + dependency)
	}

	PomAssert hasSpringBootStarterRootDependency() {
		hasDependency('org.springframework.boot', 'spring-boot-starter')
	}

	PomAssert hasDependency(String groupId, String artifactId) {
		hasDependency(groupId, artifactId, null)
	}

	PomAssert hasDependency(String groupId, String artifactId, String version) {
		def id = generateId(groupId, artifactId)
		def dependency = dependencies.get(id)
		assertNotNull 'No dependency found with ' + id + ' --> ' + dependencies.keySet(), dependency
		if (version != null) {
			assertEquals 'Wrong version for '+dependency, version, dependency.version
		}
		this
	}

	PomAssert hasNoRepository() {
		assertEquals 0, eng.getMatchingNodes(createRootNodeXPath('repositories'), doc).length
		this
	}

	PomAssert hasSnapshotRepository() {
		hasRepository('spring-snapshots')
		hasPluginRepository('spring-snapshots')
		this
	}

	def hasRepository(String name) {
		def nodes = eng.getMatchingNodes(createRootNodeXPath('repositories/pom:repository/pom:id'), doc)
		for (int i = 0; i < nodes.getLength(); i++) {
			if (name.equals(nodes.item(i).getTextContent())) {
				return
			}
		}
		throw new IllegalArgumentException('No repository found with id ' + name)
	}

	def hasPluginRepository(String name) {
		def nodes = eng.getMatchingNodes(createRootNodeXPath('pluginRepositories/pom:pluginRepository/pom:id'), doc)
		for (int i = 0; i < nodes.getLength(); i++) {
			if (name.equals(nodes.item(i).getTextContent())) {
				return
			}
		}
		throw new IllegalArgumentException('No plugin repository found with id ' + name)
	}

	static String createPropertyNodeXpath(String propertyName) {
		createRootNodeXPath('properties/pom:' + propertyName)
	}

	static String createRootNodeXPath(String node) {
		'/pom:project/pom:' + node
	}

	private def parseDependencies() {
		def nodes = eng.getMatchingNodes(createRootNodeXPath('dependencies/pom:dependency'), doc)
		for (int i = 0; i < nodes.length; i++) {
			def item = nodes.item(i)
			if (item instanceof Element) {
				InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
				Element element = (Element) item
				def groupId = element.getElementsByTagName('groupId')
				if (groupId.length > 0) {
					dependency.groupId = groupId.item(0).getTextContent()
				}
				def artifactId = element.getElementsByTagName('artifactId')
				if (artifactId.length > 0) {
					dependency.artifactId = artifactId.item(0).getTextContent()
				}
				def version = element.getElementsByTagName('version')
				if (version.length > 0) {
					dependency.version = version.item(0).getTextContent()
				}
				dependencies.put(dependency.generateId(), dependency)
			}
		}
	}

	private static String generateId(String groupId, String artifactId) {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.groupId = groupId
		dependency.artifactId = artifactId
		dependency.generateId()
	}

}
