/*
 * Copyright 2012-2021 the original author or authors.
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

package io.spring.initializr.generator.buildsystem;

/**
 * A Maven repository.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class MavenRepository {

	/**
	 * Maven Central.
	 */
	public static final MavenRepository MAVEN_CENTRAL = MavenRepository
			.withIdAndUrl("maven-central", "https://repo.maven.apache.org/maven2").name("Maven Central").onlyReleases()
			.build();

	private final String id;

	private final String name;

	private final String url;

	private final boolean releasesEnabled;

	private final boolean snapshotsEnabled;

	protected MavenRepository(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.url = builder.url;
		this.releasesEnabled = builder.releasesEnabled;
		this.snapshotsEnabled = builder.snapshotsEnabled;
	}

	/**
	 * Initialize a new repository {@link Builder} with the specified id and url. The name
	 * of the repository is initialized with the id.
	 * @param id the identifier of the repository
	 * @param url the url of the repository
	 * @return a new builder
	 */
	public static Builder withIdAndUrl(String id, String url) {
		return new Builder(id, url);
	}

	/**
	 * Return the identifier of the repository.
	 * @return the repository ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Return the name of the repository.
	 * @return the repository name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the url of the repository.
	 * @return the repository url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Return whether releases are enabled on the repository.
	 * @return {@code true} to enable releases, {@code false} otherwise
	 */
	public boolean isReleasesEnabled() {
		return this.releasesEnabled;
	}

	/**
	 * Return whether snapshots are enabled on the repository.
	 * @return {@code true} to enable snapshots, {@code false} otherwise
	 */
	public boolean isSnapshotsEnabled() {
		return this.snapshotsEnabled;
	}

	public static class Builder {

		private String id;

		private String name;

		private String url;

		private boolean releasesEnabled = true;

		private boolean snapshotsEnabled;

		public Builder(String id, String url) {
			this.id = id;
			this.name = id;
			this.url = url;
		}

		/**
		 * Set the id of the repository.
		 * @param id the identifier
		 * @return this for method chaining
		 */
		public Builder id(String id) {
			this.id = id;
			return this;
		}

		/**
		 * Set the name of the repository.
		 * @param name the name
		 * @return this for method chaining
		 */
		public Builder name(String name) {
			this.name = name;
			return this;
		}

		/**
		 * Set the url of the repository.
		 * @param url the url
		 * @return this for method chaining
		 */
		public Builder url(String url) {
			this.url = url;
			return this;
		}

		/**
		 * Specify whether releases are enabled.
		 * @param releasesEnabled whether releases are served by the repository
		 * @return this for method chaining
		 */
		public Builder releasesEnabled(boolean releasesEnabled) {
			this.releasesEnabled = releasesEnabled;
			return this;
		}

		/**
		 * Specify whether snapshots are enabled.
		 * @param snapshotsEnabled whether snapshots are served by the repository
		 * @return this for method chaining
		 */
		public Builder snapshotsEnabled(boolean snapshotsEnabled) {
			this.snapshotsEnabled = snapshotsEnabled;
			return this;
		}

		/**
		 * Specify that the repository should only be used for releases.
		 * @return this for method chaining
		 */
		public Builder onlyReleases() {
			return releasesEnabled(true).snapshotsEnabled(false);
		}

		/**
		 * Specify that the repository should only be used for snapshots.
		 * @return this for method chaining
		 */
		public Builder onlySnapshots() {
			return snapshotsEnabled(true).releasesEnabled(false);
		}

		/**
		 * Build a {@link MavenRepository} with the current state of this builder.
		 * @return a {@link MavenRepository}
		 */
		public MavenRepository build() {
			return new MavenRepository(this);
		}

	}

}
