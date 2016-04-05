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

package io.spring.initializr.metadata

import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

/**
 * @author Stephane Nicoll
 */
class InitializrMetadataTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none()

	@Test
	void invalidBom() {
		def foo = new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', bom: 'foo-bom')
		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom('my-bom', 'org.acme', 'foo', '1.2.3')
				.addDependencyGroup('test', foo);

		thrown.expect(InvalidInitializrMetadataException)
		thrown.expectMessage("foo-bom")
		thrown.expectMessage("my-bom")
		builder.build()
	}

	@Test
	void invalidRepository() {
		def foo = new Dependency(id: 'foo', groupId: 'org.acme', artifactId: 'foo', repository: 'foo-repo')
		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addRepository('my-repo', 'repo', 'http://example.com/repo', true)
				.addDependencyGroup('test', foo);

		thrown.expect(InvalidInitializrMetadataException)
		thrown.expectMessage("foo-repo")
		thrown.expectMessage("my-repo")
		builder.build()
	}

	@Test
	void invalidBomNoVersion() {
		def bom = new BillOfMaterials(groupId: 'org.acme', artifactId: 'foo-bom')

		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom('foo-bom', bom)

		thrown.expect(InvalidInitializrMetadataException)
		thrown.expectMessage("No version")
		thrown.expectMessage("foo-bom")
		builder.build()
	}

	@Test
	void invalidBomUnknownRepository() {
		def bom = new BillOfMaterials(groupId: 'org.acme', artifactId: 'foo-bom',
				version: '1.0.0.RELEASE', repositories: ['foo-repo'])

		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom('foo-bom', bom)

		thrown.expect(InvalidInitializrMetadataException)
		thrown.expectMessage("invalid repository id foo-repo")
		thrown.expectMessage("foo-bom")
		builder.build()
	}

	@Test
	void invalidBomUnknownAdditionalBom() {
		def bom = new BillOfMaterials(groupId: 'org.acme', artifactId: 'foo-bom',
				version: '1.0.0.RELEASE', additionalBoms: ['bar-bom', 'biz-bom'])
		def barBom = new BillOfMaterials(groupId: 'org.acme', artifactId: 'bar-bom',
				version: '1.0.0.RELEASE')

		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom('foo-bom', bom).addBom('bar-bom', barBom)

		thrown.expect(InvalidInitializrMetadataException)
		thrown.expectMessage("invalid additional bom")
		thrown.expectMessage("biz-bom")
		builder.build()
	}

	@Test
	void invalidBomVersionRangeMapping() {
		def bom = new BillOfMaterials(groupId: 'org.acme', artifactId: 'foo-bom')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '[1.2.0.RELEASE,1.3.0.M1)', version: '1.0.0')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: 'FOO_BAR', version: '1.2.0')

		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom('foo-bom', bom)

		thrown.expect(InvalidInitializrMetadataException)
		thrown.expectMessage("FOO_BAR")
		thrown.expectMessage("foo-bom")
		builder.build()
	}

	@Test
	void invalidBomVersionRangeMappingUnknownRepo() {
		def bom = new BillOfMaterials(groupId: 'org.acme', artifactId: 'foo-bom')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '[1.0.0.RELEASE,1.3.0.M1)', version: '1.0.0')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '1.3.0.M2', version: '1.2.0', repositories: ['foo-repo'])

		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom('foo-bom', bom)

		thrown.expect(InvalidInitializrMetadataException)
		thrown.expectMessage("invalid repository id foo-repo")
		thrown.expectMessage('1.3.0.M2')
		thrown.expectMessage("foo-bom")
		builder.build()
	}

	@Test
	void invalidBomVersionRangeMappingUnknownAdditionalBom() {
		def bom = new BillOfMaterials(groupId: 'org.acme', artifactId: 'foo-bom')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '[1.0.0.RELEASE,1.3.0.M1)', version: '1.0.0')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '1.3.0.M2', version: '1.2.0',
				additionalBoms: ['bar-bom'])

		InitializrMetadataTestBuilder builder = InitializrMetadataTestBuilder
				.withDefaults().addBom('foo-bom', bom)

		thrown.expect(InvalidInitializrMetadataException)
		thrown.expectMessage("invalid additional bom")
		thrown.expectMessage('1.3.0.M2')
		thrown.expectMessage("bar-bom")
		builder.build()
	}

}
