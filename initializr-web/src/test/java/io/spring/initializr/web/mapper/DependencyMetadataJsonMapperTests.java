/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.mapper;

import java.net.URL;
import java.util.Collections;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.DependencyMetadata;
import io.spring.initializr.metadata.Repository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DependencyMetadataJsonMapper}.
 *
 * @author Stephane Nicoll
 */
class DependencyMetadataJsonMapperTests {

	private final DependencyMetadataJsonMapper mapper = new DependencyMetadataV21JsonMapper();

	@Test
	void mapDependency() throws Exception {
		Dependency d = Dependency.withId("foo", "org.foo", "foo");
		d.setRepository("my-repo");
		d.setBom("my-bom");
		Repository repository = new Repository();
		repository.setName("foo-repo");
		repository.setUrl(new URL("https://example.com/foo"));
		BillOfMaterials bom = BillOfMaterials.create("org.foo", "foo-bom", "1.0.0.RELEASE");
		DependencyMetadata metadata = new DependencyMetadata(Version.parse("1.2.0.RELEASE"),
				Collections.singletonMap(d.getId(), d), Collections.singletonMap("repo-id", repository),
				Collections.singletonMap("bom-id", bom));
		JSONObject content = new JSONObject(this.mapper.write(metadata));
		assertThat(content.getJSONObject("dependencies").getJSONObject("foo").getString("bom")).isEqualTo("my-bom");
		assertThat(content.getJSONObject("dependencies").getJSONObject("foo").getString("repository"))
			.isEqualTo("my-repo");
		assertThat(content.getJSONObject("repositories").getJSONObject("repo-id").getString("name"))
			.isEqualTo("foo-repo");
		assertThat(content.getJSONObject("boms").getJSONObject("bom-id").getString("artifactId")).isEqualTo("foo-bom");
		assertThat(content.getJSONObject("boms").getJSONObject("bom-id").getString("version"))
			.isEqualTo("1.0.0.RELEASE");
	}

}
