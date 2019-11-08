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

package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenDistributionManagement}
 *
 * @author Joachim Pasquali
 */
class MavenDistributionManagementTests {

	private static final String TEST_URL = "testURL";

	@Test
	void emptyDistributionManagement() {
		MavenDistributionManagement result = builder().build();
		assertThat(result.isEmpty()).isTrue();
		assertThat(result.getRelocation().isEmpty()).isTrue();
		assertThat(result.getRepository().isEmpty()).isTrue();
		assertThat(result.getSite().isEmpty()).isTrue();
		assertThat(result.getSnapshotRepository().isEmpty()).isTrue();
	}

	@Test
	void addDownloadUrl() {
		MavenDistributionManagement result = builder().downloadUrl(TEST_URL).build();
		assertThat(result.getDownloadUrl()).isEqualTo(TEST_URL);
	}

	@Test
	void addRelocation() {
		MavenDistributionManagement result = builder().relocation((relocation) -> relocation.artifactId("artifactId")
				.groupId("groupId").version("version").message("message")).build();
		assertThat(result.getRelocation().getArtifactId()).isEqualTo("artifactId");
		assertThat(result.getRelocation().getGroupId()).isEqualTo("groupId");
		assertThat(result.getRelocation().getVersion()).isEqualTo("version");
		assertThat(result.getRelocation().getMessage()).isEqualTo("message");
	}

	@Test
	void addRepository() {
		MavenDistributionManagement result = builder().repository((repository) -> repository.id("id").layout("layout")
				.name("name")
				.releases((releases) -> releases.checksumPolicy("checksumPolicy").enabled(Boolean.FALSE)
						.updatePolicy("updatePolicy"))
				.snapshots((snapshots) -> snapshots.checksumPolicy("checksumPolicy").updatePolicy("updatePolicy"))
				.uniqueVersion(Boolean.FALSE).url("url")).build();
		assertThat(result.getRepository().getId()).isEqualTo("id");
		assertThat(result.getRepository().getUrl()).isEqualTo("url");
		assertThat(result.getRepository().getUniqueVersion()).isFalse();
		assertThat(result.getRepository().getLayout()).isEqualTo("layout");
		assertThat(result.getRepository().getName()).isEqualTo("name");
		assertThat(result.getRepository().getReleases().isEnabled()).isFalse();
		assertThat(result.getRepository().getReleases().getChecksumPolicy()).isEqualTo("checksumPolicy");
		assertThat(result.getRepository().getReleases().getUpdatePolicy()).isEqualTo("updatePolicy");

		assertThat(result.getRepository().getSnapshots().getChecksumPolicy()).isEqualTo("checksumPolicy");
		assertThat(result.getRepository().getSnapshots().getUpdatePolicy()).isEqualTo("updatePolicy");
		assertThat(result.getRepository().getSnapshots().isEnabled()).isTrue();

	}

	@Test
	void addSnapshotRepository() {
		MavenDistributionManagement result = builder().snapshotRepository((repository) -> repository.id("id")
				.layout("layout").name("name").releases((releases) -> releases.checksumPolicy("checksumPolicy")
						.enabled(Boolean.TRUE).updatePolicy("updatePolicy"))
				.uniqueVersion(Boolean.FALSE).url("url")).build();
		assertThat(result.getSnapshotRepository().getId()).isEqualTo("id");
		assertThat(result.getSnapshotRepository().getLayout()).isEqualTo("layout");
		assertThat(result.getSnapshotRepository().getName()).isEqualTo("name");
		assertThat(result.getSnapshotRepository().getReleases().getChecksumPolicy()).isEqualTo("checksumPolicy");
		assertThat(result.getSnapshotRepository().getReleases().getUpdatePolicy()).isEqualTo("updatePolicy");

		assertThat(result.getSnapshotRepository().getSnapshots().isEmpty()).isTrue();

	}

	@Test
	void addSite() {
		MavenDistributionManagement result = builder()
				.site((site) -> site.id("id").name("name").url("url").childSiteUrlInheritAppendPath(Boolean.FALSE))
				.build();
		assertThat(result.getSite().getId()).isEqualTo("id");
		assertThat(result.getSite().getName()).isEqualTo("name");
		assertThat(result.getSite().getUrl()).isEqualTo("url");
		assertThat(result.getSite().getChildSiteUrlInheritAppendPath()).isFalse();
	}

	@Test
	void addSiteWithNullAttribute() {
		MavenDistributionManagement result = builder().site((site) -> site.id("id")).build();
		assertThat(result.getSite().getChildSiteUrlInheritAppendPath()).isNull();
	}

	private MavenDistributionManagement.Builder builder() {
		return new MavenDistributionManagement.Builder();
	}

}
