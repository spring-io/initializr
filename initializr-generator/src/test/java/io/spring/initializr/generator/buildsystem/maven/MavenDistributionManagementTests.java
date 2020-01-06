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

import io.spring.initializr.generator.buildsystem.maven.MavenDistributionManagement.DeploymentRepository;
import io.spring.initializr.generator.buildsystem.maven.MavenDistributionManagement.Site;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenDistributionManagement}.
 *
 * @author Joachim Pasquali
 * @author Stephane Nicoll
 */
class MavenDistributionManagementTests {

	@Test
	void distributionManagementEmpty() {
		MavenDistributionManagement result = builder().build();
		assertThat(result.isEmpty()).isTrue();
		assertThat(result.getRelocation().isEmpty()).isTrue();
		assertThat(result.getRepository().isEmpty()).isTrue();
		assertThat(result.getSite().isEmpty()).isTrue();
		assertThat(result.getSnapshotRepository().isEmpty()).isTrue();
	}

	@Test
	void distributionManagementWithDownloadUrl() {
		MavenDistributionManagement mdm = builder().downloadUrl("https://example.com/download").build();
		assertThat(mdm.getDownloadUrl()).isEqualTo("https://example.com/download");
	}

	@Test
	void distributionManagementWithRepository() {
		MavenDistributionManagement mdm = builder()
				.repository((repository) -> repository.id("released-repo").name("released repo")
						.url("https://upload.example.com/releases"))
				.repository((repository) -> repository.layout("default")).build();
		DeploymentRepository repository = mdm.getRepository();
		assertThat(repository.getId()).isEqualTo("released-repo");
		assertThat(repository.getName()).isEqualTo("released repo");
		assertThat(repository.getUrl()).isEqualTo("https://upload.example.com/releases");
		assertThat(repository.getLayout()).isEqualTo("default");
		assertThat(repository.getUniqueVersion()).isNull();
	}

	@Test
	void distributionManagementWithSnapshotRepository() {
		MavenDistributionManagement mdm = builder()
				.snapshotRepository((repository) -> repository.id("snapshot-repo").name("snapshot repo")
						.url("scp://upload.example.com/snapshots"))
				.snapshotRepository((repository) -> repository.uniqueVersion(true)).build();
		DeploymentRepository snapshotRepository = mdm.getSnapshotRepository();
		assertThat(snapshotRepository.getId()).isEqualTo("snapshot-repo");
		assertThat(snapshotRepository.getName()).isEqualTo("snapshot repo");
		assertThat(snapshotRepository.getUrl()).isEqualTo("scp://upload.example.com/snapshots");
		assertThat(snapshotRepository.getLayout()).isNull();
		assertThat(snapshotRepository.getUniqueVersion()).isTrue();
	}

	@Test
	void distributionManagementWithSite() {
		MavenDistributionManagement mdm = builder().site((site) -> site.id("website").name("web site"))
				.site((site) -> site.url("scp://www.example.com/www/docs/project")).build();
		Site site = mdm.getSite();
		assertThat(site.getId()).isEqualTo("website");
		assertThat(site.getName()).isEqualTo("web site");
		assertThat(site.getUrl()).isEqualTo("scp://www.example.com/www/docs/project");
	}

	@Test
	void distributionManagementWithRelocation() {
		MavenDistributionManagement mdm = builder()
				.relocation(
						(relocation) -> relocation.groupId("com.example.new").artifactId("project").version("1.0.0"))
				.relocation((relocation) -> relocation.message("Moved to com.example.new")).build();
		assertThat(mdm.getRelocation().getGroupId()).isEqualTo("com.example.new");
		assertThat(mdm.getRelocation().getArtifactId()).isEqualTo("project");
		assertThat(mdm.getRelocation().getVersion()).isEqualTo("1.0.0");
		assertThat(mdm.getRelocation().getMessage()).isEqualTo("Moved to com.example.new");
	}

	private MavenDistributionManagement.Builder builder() {
		return new MavenDistributionManagement.Builder();
	}

}
