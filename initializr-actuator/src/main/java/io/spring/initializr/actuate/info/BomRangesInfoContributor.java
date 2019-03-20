/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.actuate.info;

import java.util.LinkedHashMap;
import java.util.Map;

import io.spring.initializr.metadata.InitializrMetadataProvider;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;

/**
 * An {@link InfoContributor} that exposes the actual ranges used by each bom defined in
 * the project.
 *
 * @author Stephane Nicoll
 */
public class BomRangesInfoContributor implements InfoContributor {

	private final InitializrMetadataProvider metadataProvider;

	public BomRangesInfoContributor(InitializrMetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider;
	}

	@Override
	public void contribute(Info.Builder builder) {
		Map<String, Object> details = new LinkedHashMap<>();
		this.metadataProvider.get().getConfiguration().getEnv().getBoms()
				.forEach((k, v) -> {
					if (v.getMappings() != null && !v.getMappings().isEmpty()) {
						Map<String, Object> bom = new LinkedHashMap<>();
						v.getMappings().forEach((it) -> {
							String requirement = "Spring Boot "
									+ it.determineVersionRangeRequirement();
							bom.put(it.getVersion(), requirement);
						});
						details.put(k, bom);
					}
				});
		if (!details.isEmpty()) {
			builder.withDetail("bom-ranges", details);
		}
	}

}
