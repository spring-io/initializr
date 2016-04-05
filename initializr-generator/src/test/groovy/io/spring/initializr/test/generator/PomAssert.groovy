/*
 * Copyright 2012-2016 the original author or authors.
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

package io.spring.initializr.test.generator

import io.spring.initializr.generator.ProjectRequest
import io.spring.initializr.metadata.BillOfMaterials
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.Repository
import org.custommonkey.xmlunit.SimpleNamespaceContext
import org.custommonkey.xmlunit.XMLUnit
import org.custommonkey.xmlunit.XpathEngine
import org.junit.Assert
import org.w3c.dom.Document
import org.w3c.dom.Element

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
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
	final Map<String, Dependency> dependencies = [:]
	final Map<String, BillOfMaterials> boms = [:]
	final Map<String, Repository> repositories = [:]

	PomAssert(String content) {
		eng = XMLUnit.newXpathEngine()
		def context = [:]
		context['pom'] = 'http://maven.apache.org/POM/4.0.0'
		def namespaceContext = new SimpleNamespaceContext(context)
		eng.namespaceContext = namespaceContext
		doc = XMLUnit.buildControlDocument(content)
		parseDependencies()
		parseBoms()
		parseRepositories()
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

	PomAssert hasDependenciesCount(int count) {
		assertEquals "Wrong number of declared dependencies -->'${dependencies.keySet()}",
				count, dependencies.size()
		this
	}

	PomAssert hasSpringBootStarterTomcat() {
		hasDependency(new Dependency(id: 'tomcat', scope: 'provided').asSpringBootStarter('tomcat'))
	}

	PomAssert hasSpringBootStarterTest() {
		hasDependency(new Dependency(id: 'test', scope: 'test').asSpringBootStarter('test'))
	}

	PomAssert hasSpringBootStarterDependency(String dependency) {
		hasDependency('org.springframework.boot', "spring-boot-starter-$dependency")
	}

	PomAssert hasSpringBootStarterRootDependency() {
		hasDependency('org.springframework.boot', 'spring-boot-starter')
	}

	PomAssert hasDependency(String groupId, String artifactId) {
		hasDependency(groupId, artifactId, null)
	}

	PomAssert hasDependency(String groupId, String artifactId, String version) {
		hasDependency(new Dependency(groupId: groupId, artifactId: artifactId, version: version))
	}

	PomAssert hasDependency(Dependency expected) {
		def id = generateDependencyId(expected.groupId, expected.artifactId)
		def dependency = dependencies[id]
		assertNotNull "No dependency found with '$id' --> ${dependencies.keySet()}", dependency
		if (expected.version) {
			assertEquals "Wrong version for $dependency", expected.version, dependency.version
		}
		if (expected.scope) {
			assertEquals "Wrong scope for $dependency", expected.scope, dependency.scope
		}
		this
	}

	PomAssert hasBom(String groupId, String artifactId, String version) {
		def id = generateBomId(groupId, artifactId)
		def bom = boms[id]
		assertNotNull "No BOM found with '$id' --> ${boms.keySet()}", bom
		assertEquals "Wrong version for $bom", version, bom.version
		this
	}

	PomAssert hasBomsCount(int count) {
		assertEquals "Wrong number of declared boms -->'${boms.keySet()}",
				count, boms.size()
		this
	}

	PomAssert hasNoRepository() {
		Assert.assertEquals 0, eng.getMatchingNodes(createRootNodeXPath('repositories'), doc).length
		this
	}

	PomAssert hasSnapshotRepository() {
		hasRepository('spring-snapshots', 'Spring Snapshots',
				'https://repo.spring.io/snapshot', true)
		hasPluginRepository('spring-snapshots')
		this
	}

	PomAssert hasRepository(String id, String name, String url, Boolean snapshotsEnabled) {
		Repository repository = repositories[id]
		assertNotNull "No repository found with '$id' --> ${repositories.keySet()}", repository
		if (name) {
			assertEquals "Wrong name for $repository", name, repository.name
		}
		if (url) {
			assertEquals "Wrong url for $repository", new URL(url), repository.url
		}
		if (snapshotsEnabled) {
			assertEquals "Wrong snapshots enabled flag for $repository", snapshotsEnabled, repository.snapshotsEnabled
		}
		this
	}

	PomAssert hasRepositoriesCount(int count) {
		assertEquals "Wrong number of declared repositories -->'${repositories.keySet()}",
				count, repositories.size()
		this
	}


	def hasPluginRepository(String name) {
		def nodes = eng.getMatchingNodes(createRootNodeXPath('pluginRepositories/pom:pluginRepository/pom:id'), doc)
		for (int i = 0; i < nodes.length; i++) {
			if (name.equals(nodes.item(i).textContent)) {
				return
			}
		}
		throw new IllegalArgumentException("No plugin repository found with id $name")
	}

	static String createPropertyNodeXpath(String propertyName) {
		createRootNodeXPath("properties/pom:$propertyName")
	}

	static String createRootNodeXPath(String node) {
		"/pom:project/pom:$node"
	}

	private def parseDependencies() {
		def nodes = eng.getMatchingNodes(createRootNodeXPath('dependencies/pom:dependency'), doc)
		for (int i = 0; i < nodes.length; i++) {
			def item = nodes.item(i)
			if (item instanceof Element) {
				def dependency = new Dependency()
				def element = (Element) item
				def groupId = element.getElementsByTagName('groupId')
				if (groupId.length > 0) {
					dependency.groupId = groupId.item(0).textContent
				}
				def artifactId = element.getElementsByTagName('artifactId')
				if (artifactId.length > 0) {
					dependency.artifactId = artifactId.item(0).textContent
				}
				def version = element.getElementsByTagName('version')
				if (version.length > 0) {
					dependency.version = version.item(0).textContent
				}
				def scope = element.getElementsByTagName('scope')
				if (scope.length > 0) {
					dependency.scope = scope.item(0).textContent
				}
				def id = dependency.generateId()
				assertFalse("Duplicate dependency with id $id", dependencies.containsKey(id))
				dependencies[id] = dependency
			}
		}
	}

	private def parseBoms() {
		def nodes = eng.getMatchingNodes(createRootNodeXPath('dependencyManagement/pom:dependencies/pom:dependency'), doc)
		for (int i = 0; i < nodes.length; i++) {
			def item = nodes.item(i)
			if (item instanceof Element) {
				def element = (Element) item
				def type = element.getElementsByTagName('type')
				def scope = element.getElementsByTagName('scope')
				if (isBom(type, scope)) {
					def bom = new BillOfMaterials()
					def groupId = element.getElementsByTagName('groupId')
					if (groupId.length > 0) {
						bom.groupId = groupId.item(0).textContent
					}
					def artifactId = element.getElementsByTagName('artifactId')
					if (artifactId.length > 0) {
						bom.artifactId = artifactId.item(0).textContent
					}
					def version = element.getElementsByTagName('version')
					if (version.length > 0) {
						bom.version = version.item(0).textContent
					}
					def id = generateBomId(bom.groupId, bom.artifactId)
					assertFalse("Duplicate BOM with id $id", boms.containsKey(id))
					boms[id] = bom
				}
			}
		}
	}

	private def parseRepositories() {
		def nodes = eng.getMatchingNodes(createRootNodeXPath('repositories/pom:repository'), doc)
		for (int i = 0; i < nodes.length; i++) {
			def item = nodes.item(i)
			if (item instanceof Element) {
				def repository = new Repository()
				def element = (Element) item
				def type = element.getElementsByTagName('id')
				def id = type.item(0).textContent
				def name = element.getElementsByTagName('name')
				if (name.length > 0) {
					repository.name = name.item(0).textContent
				}
				def url = element.getElementsByTagName('url')
				if (url.length > 0) {
					repository.url = new URL(url.item(0).textContent)
				}
				def snapshots = element.getElementsByTagName('snapshots')
				if (snapshots.length > 0) {
					Element snapshotsElement = (Element) snapshots.item(0);
					def snapshotsEnabled = snapshotsElement.getElementsByTagName('enabled')
					if (snapshotsEnabled.length > 0) {
						repository.snapshotsEnabled = snapshotsEnabled.item(0).textContent
					}
				}
				assertFalse("Duplicate Repository with id $id", repositories.containsKey(id))
				repositories[id] = repository
			}
		}
	}

	private static boolean isBom(def type, def scope) {
		if (type.length == 0 || scope.length == 0) {
			return false
		}
		String typeValue = type.item(0).textContent
		String scopeValue = scope.item(0).textContent
		return "pom".equals(typeValue) && "import".equals(scopeValue)
	}

	private static String generateBomId(def groupId, def artifactId) {
		"$groupId:$artifactId"
	}

	private static String generateDependencyId(String groupId, String artifactId) {
		def dependency = new Dependency()
		dependency.groupId = groupId
		dependency.artifactId = artifactId
		dependency.generateId()
	}

}
