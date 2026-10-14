/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.actuate.info;

import java.util.LinkedHashMap;
import java.util.Map;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import org.jspecify.annotations.Nullable;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.util.CollectionUtils;

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
		this.metadataProvider.get().getConfiguration().getEnv().getBoms().forEach((k, v) -> {
			if (!CollectionUtils.isEmpty(v.getMappings())) {
				Map<@Nullable String, Object> bom = new LinkedHashMap<>();
				v.getMappings().forEach((it) -> {
					String requirement = "Spring Boot " + it.determineCompatibilityRangeRequirement();
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
