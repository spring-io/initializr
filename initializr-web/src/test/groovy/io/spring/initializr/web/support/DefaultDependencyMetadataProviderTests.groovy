/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.web.support

import io.spring.initializr.metadata.BillOfMaterials
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.DependencyMetadata
import io.spring.initializr.metadata.DependencyMetadataProvider
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder
import io.spring.initializr.util.Version
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertSame;

/**
 * @author Stephane Nicoll
 */
class DefaultDependencyMetadataProviderTests {

	private final DependencyMetadataProvider provider = new DefaultDependencyMetadataProvider()

	@Test
	void filterDependencies() {
		def first = new Dependency(id: 'first', groupId: 'org.foo', artifactId: 'first',
				versionRange: '1.1.4.RELEASE')
		def second = new Dependency(id: 'second', groupId: 'org.foo', artifactId: 'second')
		def third = new Dependency(id: 'third', groupId: 'org.foo', artifactId: 'third',
				versionRange: '1.1.8.RELEASE')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('test', first, second, third).build()
		def dependencyMetadata = provider.get(metadata, Version.parse('1.1.5.RELEASE'))
		assertEquals 2, dependencyMetadata.dependencies.size()
		assertEquals 0, dependencyMetadata.repositories.size()
		assertEquals 0, dependencyMetadata.boms.size()
		assertSame first, dependencyMetadata.dependencies['first']
		assertSame second, dependencyMetadata.dependencies['second']
	}

	@Test
	void resolveDependencies() {
		def first = new Dependency(id: 'first', groupId: 'org.foo', artifactId: 'first')
		first.versions << new Dependency.Mapping(versionRange: '[1.0.0.RELEASE, 1.1.0.RELEASE)',
				version: '0.1.0.RELEASE')
		first.versions << new Dependency.Mapping(versionRange: '1.1.0.RELEASE',
				version: '0.2.0.RELEASE')
		def second = new Dependency(id: 'second', groupId: 'org.foo', artifactId: 'second')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('test', first, second).build()

		def dependencyMetadata = provider.get(metadata, Version.parse('1.0.5.RELEASE'))
		assertEquals 2, dependencyMetadata.dependencies.size()
		assertEquals('0.1.0.RELEASE', dependencyMetadata.dependencies['first'].version)

		def anotherDependencyMetadata = provider.get(metadata, Version.parse('1.1.0.RELEASE'))
		assertEquals 2, anotherDependencyMetadata.dependencies.size()
		assertEquals('0.2.0.RELEASE', anotherDependencyMetadata.dependencies['first'].version)
	}

	@Test
	void addRepoAndRemoveDuplicates() {
		def first = new Dependency(id: 'first', groupId: 'org.foo', artifactId: 'first',
				repository: 'repo-foo')
		def second = new Dependency(id: 'second', groupId: 'org.foo', artifactId: 'second')
		def third = new Dependency(id: 'third', groupId: 'org.foo', artifactId: 'third',
				repository: 'repo-foo')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addRepository('repo-foo', 'my-repo', 'http://localhost', false)
				.addDependencyGroup('test', first, second, third).build()
		def dependencyMetadata = provider.get(metadata, Version.parse('1.1.5.RELEASE'))
		assertEquals 3, dependencyMetadata.dependencies.size()
		assertEquals 1, dependencyMetadata.repositories.size()
		assertEquals 0, dependencyMetadata.boms.size()
		assertSame metadata.configuration.env.repositories.get('repo-foo'),
				dependencyMetadata.repositories['repo-foo']
	}

	@Test
	void addBomAndRemoveDuplicates() {
		def first = new Dependency(id: 'first', groupId: 'org.foo', artifactId: 'first',
				bom: 'bom-foo')
		def second = new Dependency(id: 'second', groupId: 'org.foo', artifactId: 'second')
		def third = new Dependency(id: 'third', groupId: 'org.foo', artifactId: 'third',
				bom: 'bom-foo')

		def bom = new BillOfMaterials(groupId: 'org.foo', artifactId: 'bom')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '[1.0.0.RELEASE, 1.1.8.RELEASE)',
				version: '1.0.0.RELEASE')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '1.1.8.RELEASE',
				version: '2.0.0.RELEASE')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom('bom-foo',bom)
				.addDependencyGroup('test', first, second, third).build()
		def dependencyMetadata = provider.get(metadata, Version.parse('1.1.5.RELEASE'))
		assertEquals 3, dependencyMetadata.dependencies.size()
		assertEquals 0, dependencyMetadata.repositories.size()
		assertEquals 1, dependencyMetadata.boms.size()
		assertEquals 'org.foo', dependencyMetadata.boms['bom-foo'].groupId
		assertEquals 'bom', dependencyMetadata.boms['bom-foo'].artifactId
		assertEquals '1.0.0.RELEASE', dependencyMetadata.boms['bom-foo'].version
	}

	@Test
	void repoFromBomAccordingToVersion() {
		def dependencyMetadata = testRepoFromBomAccordingToVersion('1.0.9.RELEASE')
		assertEquals(Version.parse('1.0.9.RELEASE'), dependencyMetadata.bootVersion)
		assertEquals 3, dependencyMetadata.dependencies.size()
		assertEquals 2, dependencyMetadata.repositories.size()
		assertEquals 1, dependencyMetadata.boms.size()
		assertEquals 'foo', dependencyMetadata.repositories['repo-foo'].name
		assertEquals 'bar', dependencyMetadata.repositories['repo-bar'].name
		assertEquals 'org.foo', dependencyMetadata.boms['bom-foo'].groupId
		assertEquals 'bom', dependencyMetadata.boms['bom-foo'].artifactId
		assertEquals '2.0.0.RELEASE', dependencyMetadata.boms['bom-foo'].version
	}

	@Test
	void repoFromBomAccordingToAnotherVersion() {
		def dependencyMetadata = testRepoFromBomAccordingToVersion('1.1.5.RELEASE')
		assertEquals(Version.parse('1.1.5.RELEASE'), dependencyMetadata.bootVersion)
		assertEquals 3, dependencyMetadata.dependencies.size()
		assertEquals 2, dependencyMetadata.repositories.size()
		assertEquals 1, dependencyMetadata.boms.size()
		assertEquals 'foo', dependencyMetadata.repositories['repo-foo'].name
		assertEquals 'biz', dependencyMetadata.repositories['repo-biz'].name
		assertEquals 'org.foo', dependencyMetadata.boms['bom-foo'].groupId
		assertEquals 'bom', dependencyMetadata.boms['bom-foo'].artifactId
		assertEquals '3.0.0.RELEASE', dependencyMetadata.boms['bom-foo'].version
	}

	private DependencyMetadata testRepoFromBomAccordingToVersion(bootVersion) {
		def first = new Dependency(id: 'first', groupId: 'org.foo', artifactId: 'first',
				repository: 'repo-foo')
		def second = new Dependency(id: 'second', groupId: 'org.foo', artifactId: 'second')
		def third = new Dependency(id: 'third', groupId: 'org.foo', artifactId: 'third',
				bom: 'bom-foo')

		BillOfMaterials bom = new BillOfMaterials(groupId: 'org.foo', artifactId: 'bom')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '[1.0.0.RELEASE, 1.1.0.RELEASE)',
				version: '2.0.0.RELEASE', repositories: ['repo-foo', 'repo-bar'])
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '1.1.0.RELEASE',
				version: '3.0.0.RELEASE', repositories: ['repo-biz'])
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom('bom-foo', bom)
				.addRepository('repo-foo', 'foo', 'http://localhost', false)
				.addRepository('repo-bar', 'bar', 'http://localhost', false)
				.addRepository('repo-biz', 'biz', 'http://localhost', false)
				.addDependencyGroup('test', first, second, third).build()
		provider.get(metadata, Version.parse(bootVersion))
	}

}
