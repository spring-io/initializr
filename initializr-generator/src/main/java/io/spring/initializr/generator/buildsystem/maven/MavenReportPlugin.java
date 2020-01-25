/*
 * Copyright 2012-2020 the original author or authors.
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

public class MavenReportPlugin {

	private final String groupId;

	private final String artifactId;

	private final String version;

	private final String inherited;

	private final MavenConfiguration configuration;

	private final MavenReportSetContainer reportSets;

	protected MavenReportPlugin(Builder builder) {
		this.groupId = builder.groupId;
		this.artifactId = builder.artifactId;
		this.version = builder.version;
		this.inherited = builder.inherited;
		this.configuration = Optional.ofNullable(builder.configuration).map(MavenConfiguration.Builder::build)
				.orElse(null);
		this.reportSets = builder.reportSets;
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

	public String getInherited() {
		return this.inherited;
	}

	public MavenConfiguration getConfiguration() {
		return this.configuration;
	}

	public MavenReportSetContainer getReportSets() {
		return this.reportSets;
	}

	public static class Builder {

		private final String groupId;

		private final String artifactId;

		private String version;

		private String inherited;

		private MavenConfiguration.Builder configuration;

		private MavenReportSetContainer reportSets;

		protected Builder(String groupId, String artifactId) {
			this.groupId = groupId;
			this.artifactId = artifactId;
			this.reportSets = new MavenReportSetContainer();
		}

		public MavenReportPlugin.Builder inherited(String inherited) {
			this.inherited = inherited;
			return this;
		}

		public MavenReportPlugin.Builder version(String version) {
			this.version = version;
			return this;
		}

		public MavenReportPlugin.Builder configuration(Consumer<MavenConfiguration.Builder> configuration) {
			if (this.configuration == null) {
				this.configuration = new MavenConfiguration.Builder();
			}
			configuration.accept(this.configuration);
			return this;
		}

		public MavenReportPlugin.Builder reportSets(Consumer<MavenReportSetContainer> reportSets) {
			reportSets.accept(this.reportSets);
			return this;
		}

		public MavenReportPlugin build() {
			return new MavenReportPlugin(this);
		}

	}

}
