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

import io.spring.initializr.util.Version
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.sameInstance
import static org.junit.Assert.assertThat

/**
 * @author Stephane Nicoll
 */
class BillOfMaterialsTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none()

	@Test
	void resolveSimpleBom() {
		BillOfMaterials bom = new BillOfMaterials(groupId: 'com.example',
				artifactId: 'bom', version: '1.0.0')
		bom.validate()
		BillOfMaterials resolved = bom.resolve(Version.parse('1.2.3.RELEASE'))
		assertThat(bom, sameInstance(resolved))
	}

	@Test
	void resolveSimpleRange() {
		BillOfMaterials bom = new BillOfMaterials(groupId: 'com.example', artifactId: 'bom',
				version: '1.0.0', repositories: ['repo-main'], additionalBoms: ['bom-main'])
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '[1.2.0.RELEASE,1.3.0.M1)',
				version: '1.1.0')
		bom.validate()
		BillOfMaterials resolved = bom.resolve(Version.parse('1.2.3.RELEASE'))
		assertThat(resolved.groupId, equalTo('com.example'))
		assertThat(resolved.artifactId, equalTo('bom'))
		assertThat(resolved.version, equalTo('1.1.0'))
		assertThat(resolved.repositories.size(), equalTo(1))
		assertThat(resolved.repositories[0], equalTo('repo-main'))
		assertThat(resolved.additionalBoms.size(), equalTo(1))
		assertThat(resolved.additionalBoms[0], equalTo('bom-main'))
	}

	@Test
	void resolveRangeOverride() {
		BillOfMaterials bom = new BillOfMaterials(groupId: 'com.example',
				artifactId: 'bom', version: '1.0.0', repositories: ['repo-main'], additionalBoms: ['bom-main'])
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '[1.2.0.RELEASE,1.3.0.M1)',
				version: '1.1.0', repositories: ['repo-foo'], additionalBoms: ['bom-foo'])
		bom.validate()
		BillOfMaterials resolved = bom.resolve(Version.parse('1.2.3.RELEASE'))
		assertThat(resolved.groupId, equalTo('com.example'))
		assertThat(resolved.artifactId, equalTo('bom'))
		assertThat(resolved.version, equalTo('1.1.0'))
		assertThat(resolved.repositories.size(), equalTo(1))
		assertThat(resolved.repositories[0], equalTo('repo-foo'))
		assertThat(resolved.additionalBoms.size(), equalTo(1))
		assertThat(resolved.additionalBoms[0], equalTo('bom-foo'))
	}

	@Test
	void noRangeAvailable() {
		BillOfMaterials bom = new BillOfMaterials(groupId: 'com.example', artifactId: 'bom')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '[1.2.0.RELEASE,1.3.0.M1)',
				version: '1.1.0')
		bom.mappings << new BillOfMaterials.Mapping(versionRange: '[1.3.0.M1,1.4.0.M1)',
				version: '1.2.0')
		bom.validate()

		thrown.expect(IllegalStateException)
		thrown.expectMessage('1.4.1.RELEASE')
		bom.resolve(Version.parse('1.4.1.RELEASE'))
	}

}
