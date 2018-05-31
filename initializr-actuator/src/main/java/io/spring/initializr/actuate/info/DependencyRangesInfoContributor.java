/*
 * Copyright 2012-2018 the original author or authors.
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

package io.spring.initializr.actuate.info;

import java.util.LinkedHashMap;
import java.util.Map;

import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.util.Version;
import io.spring.initializr.util.VersionRange;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.util.ObjectUtils;

/**
 * An {@link InfoContributor} that exposes the actual ranges used by dependencies defined
 * in the project that have an explicit version (i.e. not relying on a bom).
 *
 * @author Stephane Nicoll
 */
public class DependencyRangesInfoContributor implements InfoContributor {

	private final InitializrMetadataProvider metadataProvider;

	public DependencyRangesInfoContributor(InitializrMetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider;
	}

	@Override
	public void contribute(Info.Builder builder) {
		Map<String, Object> details = new LinkedHashMap<>();
		this.metadataProvider.get().getDependencies().getAll().forEach((d) -> {
			if (d.getBom() == null) {
				contribute(details, d);
			}
		});
		if (!details.isEmpty()) {
			builder.withDetail("dependency-ranges", details);
		}
	}

	private void contribute(Map<String, Object> details, Dependency d) {
		if (!ObjectUtils.isEmpty(d.getMappings())) {
			Map<String, VersionRange> dep = new LinkedHashMap<>();
			d.getMappings().forEach((it) -> {
				if (it.getRange() != null && it.getVersion() != null) {
					dep.put(it.getVersion(), it.getRange());
				}
			});
			if (!dep.isEmpty()) {
				if (d.getRange() == null) {
					boolean openRange = dep.values().stream()
							.anyMatch((v) -> v.getHigherVersion() == null);
					if (!openRange) {
						Version higher = getHigher(dep);
						dep.put("managed", new VersionRange(higher));
					}
				}
				Map<String, Object> depInfo = new LinkedHashMap<>();
				dep.forEach((k, r) -> {
					depInfo.put(k, "Spring Boot " + r);
				});
				details.put(d.getId(), depInfo);
			}
		}
		else if (d.getVersion() != null && d.getRange() != null) {
			Map<String, Object> dep = new LinkedHashMap<>();
			String requirement = "Spring Boot " + d.getRange();
			dep.put(d.getVersion(), requirement);
			details.put(d.getId(), dep);
		}
	}

	private Version getHigher(Map<String, VersionRange> dep) {
		Version higher = null;
		for (VersionRange versionRange : dep.values()) {
			Version candidate = versionRange.getHigherVersion();
			if (higher == null) {
				higher = candidate;
			}
			else if (candidate.compareTo(higher) > 0) {
				higher = candidate;
			}
		}
		return higher;
	}

}
