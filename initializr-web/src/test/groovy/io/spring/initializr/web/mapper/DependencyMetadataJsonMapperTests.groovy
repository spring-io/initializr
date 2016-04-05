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

package io.spring.initializr.web.mapper

import groovy.json.JsonSlurper
import io.spring.initializr.metadata.BillOfMaterials
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.DependencyMetadata
import io.spring.initializr.metadata.Repository
import io.spring.initializr.util.Version
import org.junit.Test

import static org.junit.Assert.*

/**
 * @author Stephane Nicoll
 */
class DependencyMetadataJsonMapperTests {

	private final DependencyMetadataJsonMapper mapper = new DependencyMetadataV21JsonMapper()
	private final JsonSlurper slurper = new JsonSlurper()

	@Test
	void mapDependency() {
		Dependency d = new Dependency(id: 'foo', groupId: 'org.foo', artifactId: 'foo',
				repository: 'my-repo', bom: 'my-bom')
		Repository repository = new Repository(name: 'foo-repo',
				url: new URL('http://example.com/foo'))
		BillOfMaterials bom = new BillOfMaterials('groupId': 'org.foo',
				artifactId: 'foo-bom', version: '1.0.0.RELEASE')
		DependencyMetadata metadata = new DependencyMetadata(Version.parse('1.2.0.RELEASE'),
				[(d.id): d], ['repo-id': repository], ['bom-id': bom])
		def content = slurper.parseText(mapper.write(metadata))
		println  content
		assertEquals 'my-bom', content.dependencies['foo'].bom
		assertEquals 'my-repo', content.dependencies['foo'].repository
		assertEquals 'foo-repo', content.repositories['repo-id'].name
		assertEquals 'foo-bom', content.boms['bom-id'].artifactId
		assertEquals '1.0.0.RELEASE', content.boms['bom-id'].version
	}

}
