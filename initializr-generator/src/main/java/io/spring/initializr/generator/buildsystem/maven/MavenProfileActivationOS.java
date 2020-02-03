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

public class MavenProfileActivationOS {

	private final String name;

	private final String family;

	private final String arch;

	private final String version;

	public MavenProfileActivationOS(Builder builder) {
		this.name = builder.name;
		this.family = builder.family;
		this.arch = builder.arch;
		this.version = builder.version;
	}

	public String getName() {
		return this.name;
	}

	public String getFamily() {
		return this.family;
	}

	public String getArch() {
		return this.arch;
	}

	public String getVersion() {
		return this.version;
	}

	public static class Builder {

		private String name;

		private String family;

		private String arch;

		private String version;

		protected Builder() {

		}

		public MavenProfileActivationOS.Builder name(String name) {
			this.name = name;
			return this;
		}

		public MavenProfileActivationOS.Builder family(String family) {
			this.family = family;
			return this;
		}

		public MavenProfileActivationOS.Builder arch(String arch) {
			this.arch = arch;
			return this;
		}

		public MavenProfileActivationOS.Builder version(String version) {
			this.version = version;
			return this;
		}

		public MavenProfileActivationOS build() {
			return new MavenProfileActivationOS(this);
		}

	}

}
