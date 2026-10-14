/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.actuate.info;

import java.util.Map;

import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.SimpleInitializrMetadataProvider;
import org.junit.jupiter.api.Test;

import org.springframework.boot.actuate.info.Info;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link BomRangesInfoContributor}
 *
 * @author Stephane Nicoll
 */
class BomRangesInfoContributorTests {

	@Test
	void noBom() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().build();
		Info info = getInfo(metadata);
		assertThat(info.getDetails()).doesNotContainKeys("bom-ranges");
	}

	@Test
	void noMapping() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().addBom("foo", bom).build();
		Info info = getInfo(metadata);
		assertThat(info.getDetails()).doesNotContainKeys("bom-ranges");
	}

	@Test
	void withMappings() {
		BillOfMaterials bom = BillOfMaterials.create("com.example", "bom", "1.0.0");
		bom.getMappings().add(BillOfMaterials.Mapping.create("[1.3.0.RELEASE,1.3.8.RELEASE]", "1.1.0"));
		bom.getMappings().add(BillOfMaterials.Mapping.create("1.3.8.BUILD-SNAPSHOT", "1.1.1-SNAPSHOT"));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().addBom("foo", bom).build();
		Info info = getInfo(metadata);
		assertThat(info.getDetails()).containsKeys("bom-ranges");
		@SuppressWarnings("unchecked")
		Map<String, Object> ranges = (Map<String, Object>) info.getDetails().get("bom-ranges");
		assertThat(ranges).isNotNull();
		assertThat(ranges).containsOnlyKeys("foo");
		@SuppressWarnings("unchecked")
		Map<String, Object> foo = (Map<String, Object>) ranges.get("foo");
		assertThat(foo).containsExactly(entry("1.1.0", "Spring Boot >=1.3.0.RELEASE and <=1.3.8.RELEASE"),
				entry("1.1.1-SNAPSHOT", "Spring Boot >=1.3.8.BUILD-SNAPSHOT"));
	}

	private static Info getInfo(InitializrMetadata metadata) {
		Info.Builder builder = new Info.Builder();
		new BomRangesInfoContributor(new SimpleInitializrMetadataProvider(metadata)).contribute(builder);
		return builder.build();
	}

}
