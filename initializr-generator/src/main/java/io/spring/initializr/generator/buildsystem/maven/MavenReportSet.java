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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MavenReportSet {

	private final String id;

	private final MavenConfiguration configuration;

	private final String inherited;

	private final List<String> reports;

	public MavenReportSet(Builder builder) {
		this.id = builder.id;
		this.configuration = Optional.ofNullable(builder.configuration).map(MavenConfiguration.Builder::build)
				.orElse(null);
		this.inherited = builder.inherited;
		this.reports = Optional.ofNullable(builder.reports).map(Collections::unmodifiableList).orElse(null);
	}

	public String getId() {
		return this.id;
	}

	public MavenConfiguration getConfiguration() {
		return this.configuration;
	}

	public String getInherited() {
		return this.inherited;
	}

	public List<String> getReports() {
		return this.reports;
	}

	public static class Builder {

		private final String id;

		private MavenConfiguration.Builder configuration;

		private String inherited;

		private List<String> reports;

		protected Builder(String id) {
			this.id = id;
		}

		public MavenReportSet.Builder configuration(Consumer<MavenConfiguration.Builder> configuration) {
			if (this.configuration == null) {
				this.configuration = new MavenConfiguration.Builder();
			}
			configuration.accept(this.configuration);
			return this;
		}

		public MavenReportSet.Builder inherited(String inherited) {
			this.inherited = inherited;
			return this;
		}

		public MavenReportSet.Builder report(String report) {
			if (this.reports == null) {
				this.reports = new LinkedList<>();
			}
			this.reports.add(report);
			return this;
		}

		public MavenReportSet build() {
			return new MavenReportSet(this);
		}

	}

}
