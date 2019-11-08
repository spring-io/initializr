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

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Maven DistributionManagement.
 *
 * @author Joachim Pasquali
 */
public class MavenDistributionManagement {

	private final String downloadUrl;

	private final DeploymentRepository repository;

	private final DeploymentRepository snapshotRepository;

	private final Site site;

	private final Relocation relocation;

	protected MavenDistributionManagement(final Builder builder) {
		this.downloadUrl = builder.downloadUrl;
		this.repository = builder.repository.build();
		this.snapshotRepository = builder.snapshotRepository.build();
		this.site = builder.site.build();
		this.relocation = builder.relocation.build();
	}

	public boolean isEmpty() {
		return this.downloadUrl == null && this.repository.isEmpty() && this.snapshotRepository.isEmpty()
				&& this.site.isEmpty() && this.relocation.isEmpty();
	}

	public String getDownloadUrl() {
		return this.downloadUrl;
	}

	public DeploymentRepository getRepository() {
		return this.repository;
	}

	public DeploymentRepository getSnapshotRepository() {
		return this.snapshotRepository;
	}

	public Site getSite() {
		return this.site;
	}

	public Relocation getRelocation() {
		return this.relocation;
	}

	public static class Builder {

		private String downloadUrl;

		private DeploymentRepositoryBuilder repository = new DeploymentRepositoryBuilder();

		private DeploymentRepositoryBuilder snapshotRepository = new DeploymentRepositoryBuilder();

		private SiteBuilder site = new SiteBuilder();

		private RelocationBuilder relocation = new RelocationBuilder();

		public Builder downloadUrl(final String downloadUrl) {
			this.downloadUrl = downloadUrl;
			return this;
		}

		public Builder repository(Consumer<DeploymentRepositoryBuilder> repository) {
			repository.accept(this.repository);
			return this;
		}

		public Builder snapshotRepository(Consumer<DeploymentRepositoryBuilder> snapshotRepository) {
			snapshotRepository.accept(this.snapshotRepository);
			return this;
		}

		public Builder site(Consumer<SiteBuilder> site) {
			site.accept(this.site);
			return this;
		}

		public Builder relocation(Consumer<RelocationBuilder> relocation) {
			relocation.accept(this.relocation);
			return this;
		}

		public MavenDistributionManagement build() {
			return new MavenDistributionManagement(this);
		}

	}

	public static class DeploymentRepositoryBuilder {

		private Boolean uniqueVersion;

		private RepositoryPolicyBuilder releases = new RepositoryPolicyBuilder();

		private RepositoryPolicyBuilder snapshots = new RepositoryPolicyBuilder();

		private String id;

		private String name;

		private String url;

		private String layout;

		public DeploymentRepositoryBuilder uniqueVersion(Boolean uniqueVersion) {
			this.uniqueVersion = uniqueVersion;
			return this;
		}

		public DeploymentRepositoryBuilder releases(Consumer<RepositoryPolicyBuilder> releases) {
			releases.accept(this.releases);
			return this;
		}

		public DeploymentRepositoryBuilder snapshots(Consumer<RepositoryPolicyBuilder> snapshots) {
			snapshots.accept(this.snapshots);
			return this;
		}

		public DeploymentRepositoryBuilder id(String id) {
			this.id = id;
			return this;
		}

		public DeploymentRepositoryBuilder name(String name) {
			this.name = name;
			return this;
		}

		public DeploymentRepositoryBuilder url(String url) {
			this.url = url;
			return this;
		}

		public DeploymentRepositoryBuilder layout(String layout) {
			this.layout = layout;
			return this;
		}

		public DeploymentRepository build() {
			return new DeploymentRepository(this);
		}

	}

	public static class RepositoryPolicyBuilder {

		private Boolean enabled;

		private String updatePolicy;

		private String checksumPolicy;

		public RepositoryPolicyBuilder enabled(Boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public RepositoryPolicyBuilder updatePolicy(String updatePolicy) {
			this.updatePolicy = updatePolicy;
			return this;
		}

		public RepositoryPolicyBuilder checksumPolicy(String checksumPolicy) {
			this.checksumPolicy = checksumPolicy;
			return this;
		}

		public RepositoryPolicy build() {
			return new RepositoryPolicy(this);
		}

	}

	public static class SiteBuilder {

		private String id;

		private String url;

		private String name;

		private Boolean childSiteUrlInheritAppendPath;

		public SiteBuilder childSiteUrlInheritAppendPath(Boolean childSiteUrlInheritAppendPath) {
			this.childSiteUrlInheritAppendPath = childSiteUrlInheritAppendPath;
			return this;
		}

		public SiteBuilder id(String id) {
			this.id = id;
			return this;
		}

		public SiteBuilder url(String url) {
			this.url = url;
			return this;
		}

		public SiteBuilder name(String name) {
			this.name = name;
			return this;
		}

		public Site build() {
			return new Site(this);
		}

	}

	public static class RelocationBuilder {

		private String groupId;

		private String artifactId;

		private String version;

		private String message;

		public RelocationBuilder groupId(String groupId) {
			this.groupId = groupId;
			return this;
		}

		public RelocationBuilder artifactId(String artifactId) {
			this.artifactId = artifactId;
			return this;
		}

		public RelocationBuilder version(String version) {
			this.version = version;
			return this;
		}

		public RelocationBuilder message(String message) {
			this.message = message;
			return this;
		}

		public Relocation build() {
			return new Relocation(this);
		}

	}

	public static class DeploymentRepository {

		private final Boolean uniqueVersion;

		private final RepositoryPolicy releases;

		private final RepositoryPolicy snapshots;

		private final String id;

		private final String name;

		private final String url;

		private final String layout;

		protected DeploymentRepository(DeploymentRepositoryBuilder builder) {
			this.uniqueVersion = builder.uniqueVersion;
			this.releases = builder.releases.build();
			this.snapshots = builder.snapshots.build();
			this.id = builder.id;
			this.name = builder.name;
			this.url = builder.url;
			this.layout = builder.layout;
		}

		public boolean isEmpty() {
			return this.uniqueVersion == null && this.id == null && this.name == null && this.url == null
					&& this.layout == null && this.releases.isEmpty() && this.snapshots.isEmpty();
		}

		public Boolean getUniqueVersion() {
			return Optional.ofNullable(this.uniqueVersion).orElse(Boolean.TRUE);
		}

		public RepositoryPolicy getReleases() {
			return this.releases;
		}

		public RepositoryPolicy getSnapshots() {
			return this.snapshots;
		}

		public String getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public String getUrl() {
			return this.url;
		}

		public String getLayout() {
			return this.layout;
		}

	}

	public static class RepositoryPolicy {

		private final Boolean enabled;

		private final String updatePolicy;

		private final String checksumPolicy;

		protected RepositoryPolicy(RepositoryPolicyBuilder builder) {
			this.enabled = builder.enabled;
			this.updatePolicy = builder.updatePolicy;
			this.checksumPolicy = builder.checksumPolicy;
		}

		public boolean isEmpty() {
			return this.enabled == null && this.updatePolicy == null && this.checksumPolicy == null;
		}

		public Boolean isEnabled() {
			return Optional.ofNullable(this.enabled).orElse(Boolean.TRUE);
		}

		public String getUpdatePolicy() {
			return this.updatePolicy;
		}

		public String getChecksumPolicy() {
			return this.checksumPolicy;
		}

	}

	public static class Site {

		private final String id;

		private final String url;

		private final String name;

		private final Boolean childSiteUrlInheritAppendPath;

		public Site(SiteBuilder builder) {
			this.id = builder.id;
			this.url = builder.url;
			this.name = builder.name;
			this.childSiteUrlInheritAppendPath = builder.childSiteUrlInheritAppendPath;
		}

		public boolean isEmpty() {
			return this.id == null && this.url == null && this.name == null;
		}

		public String getId() {
			return this.id;
		}

		public String getUrl() {
			return this.url;
		}

		public String getName() {
			return this.name;
		}

		public Boolean getChildSiteUrlInheritAppendPath() {
			return Optional.ofNullable(this.childSiteUrlInheritAppendPath).orElse(Boolean.TRUE);
		}

	}

	public static class Relocation {

		private final String groupId;

		private final String artifactId;

		private final String version;

		private final String message;

		public Relocation(RelocationBuilder builder) {
			this.groupId = builder.groupId;
			this.artifactId = builder.artifactId;
			this.version = builder.version;
			this.message = builder.message;
		}

		public boolean isEmpty() {
			return this.groupId == null && this.artifactId == null && this.version == null && this.message == null;
		}

		public String getGroupId() {
			return this.groupId;
		}

		public String getArtifactId() {
			return this.artifactId;
		}

		public String getVersion() {
			return this.version;
		}

		public String getMessage() {
			return this.message;
		}

	}

}
