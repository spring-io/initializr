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

import java.util.function.Consumer;

public class MavenReporting {

	private final Boolean excludeDefaults;

	private final String outputDirectory;

	private final MavenReportPluginContainer reportPlugins;

	protected MavenReporting(Builder builder) {
		this.excludeDefaults = builder.excludeDefaults;
		this.outputDirectory = builder.outputDirectory;
		this.reportPlugins = builder.reportPlugins;
	}

	public Boolean isExcludeDefaults() {
		return this.excludeDefaults;
	}

	public String getOutputDirectory() {
		return this.outputDirectory;
	}

	public MavenReportPluginContainer getReportPlugins() {
		return this.reportPlugins;
	}

	public static class Builder {

		private Boolean excludeDefaults;

		private String outputDirectory;

		private MavenReportPluginContainer reportPlugins;

		protected Builder() {
			this.reportPlugins = new MavenReportPluginContainer();
		}

		public MavenReporting.Builder excludeDefaults(boolean excludeDefaults) {
			this.excludeDefaults = excludeDefaults;
			return this;
		}

		public MavenReporting.Builder outputDirectory(String outputDirectory) {
			this.outputDirectory = outputDirectory;
			return this;
		}

		public MavenReporting.Builder reportPlugins(Consumer<MavenReportPluginContainer> reportPlugins) {
			reportPlugins.accept(this.reportPlugins);
			return this;
		}

		public MavenReporting build() {
			return new MavenReporting(this);
		}

	}

}
