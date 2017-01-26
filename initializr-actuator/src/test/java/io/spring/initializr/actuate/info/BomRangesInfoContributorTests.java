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

package io.spring.initializr.actuate.info

import io.spring.initializr.metadata.BillOfMaterials
import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.metadata.SimpleInitializrMetadataProvider
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder
import org.junit.Test

import org.springframework.boot.actuate.info.Info

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.entry

/**
 * Tests for {@link BomRangesInfoContributor}
 *
 * @author Stephane Nicoll
 */
class BomRangesInfoContributorTests {

	@Test
	void noBom() {
		def metadata = InitializrMetadataTestBuilder.withDefaults().build()
		def info = getInfo(metadata)
		assertThat(info.details).doesNotContainKeys('bom-ranges')
	}

	@Test
	void noMapping() {
		def bom = new BillOfMaterials(groupId: 'com.example', artifactId: 'bom', version: '1.0.0')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom('foo', bom)
				.build()
		def info = getInfo(metadata)
		assertThat(info.details).doesNotContainKeys('bom-ranges')
	}

	@Test
	void withMappings() {
		BillOfMaterials bom = new BillOfMaterials(groupId: 'com.example',
				artifactId: 'bom', version: '1.0.0')
		bom.mappings << new BillOfMaterials.Mapping(
				versionRange: '[1.3.0.RELEASE,1.3.8.RELEASE]', version: '1.1.0')
		bom.mappings << new BillOfMaterials.Mapping(
				versionRange: '1.3.8.BUILD-SNAPSHOT', version: '1.1.1-SNAPSHOT')
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addBom('foo', bom)
				.build()
		def info = getInfo(metadata)
		assertThat(info.details).containsKeys('bom-ranges')
		Map<String,Object> ranges = info.details['bom-ranges'] as Map<String, Object>
		assertThat(ranges).containsOnlyKeys('foo')
		Map<String,Object> foo = ranges['foo'] as Map<String, Object>
		assertThat(foo).containsExactly(
				entry('1.1.0', 'Spring Boot >=1.3.0.RELEASE and <=1.3.8.RELEASE'),
				entry('1.1.1-SNAPSHOT', 'Spring Boot >=1.3.8.BUILD-SNAPSHOT'))
	}

	private static Info getInfo(InitializrMetadata metadata) {
		Info.Builder builder = new Info.Builder()
		new BomRangesInfoContributor(new SimpleInitializrMetadataProvider(metadata))
				.contribute(builder)
		builder.build()
	}

}
