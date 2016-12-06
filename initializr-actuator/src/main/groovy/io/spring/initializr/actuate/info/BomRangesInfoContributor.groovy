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

import io.spring.initializr.metadata.InitializrMetadataProvider

import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor

/**
 * An {@link InfoContributor} that exposes the actual ranges used by each bom
 * defined in the project.
 *
 * @author Stephane Nicoll
 */
class BomRangesInfoContributor implements InfoContributor {

	private final InitializrMetadataProvider metadataProvider

	BomRangesInfoContributor(InitializrMetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider
	}

	@Override
	void contribute(Info.Builder builder) {
		def details = [:]
		metadataProvider.get().configuration.env.boms.each { k, v ->
			if (v.mappings) {
				def bom = [:]
				v.mappings.each {
					String requirement = "Spring Boot ${it.determineVersionRangeRequirement()}"
					bom[it.version] = requirement
				}
				details[k] = bom
			}
		}
		if (details) {
			builder.withDetail('bom-ranges', details)
		}
	}

}
