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

/**
 * Maven Scm.
 *
 * @author Joachim Pasquali
 */
public class Scm {

	private final String connection;

	private final String developerConnection;

	private final String tag;

	private final String url;

	private final Boolean childScmConnectionInheritAppendPath;

	private final Boolean childScmDeveloperConnectionInheritAppendPath;

	private final Boolean childScmUrlInheritAppendPath;

	protected Scm(Builder builder) {
		this.connection = builder.connection;
		this.developerConnection = builder.developerConnection;
		this.tag = builder.tag;
		this.url = builder.url;
		this.childScmConnectionInheritAppendPath = builder.childScmConnectionInheritAppendPath;
		this.childScmDeveloperConnectionInheritAppendPath = builder.childScmDeveloperConnectionInheritAppendPath;
		this.childScmUrlInheritAppendPath = builder.childScmUrlInheritAppendPath;
	}

	public boolean isEmpty() {
		return this.connection == null && this.developerConnection == null && this.tag == null
				&& this.childScmUrlInheritAppendPath == null;
	}

	public String getConnection() {
		return this.connection;
	}

	public String getDeveloperConnection() {
		return this.developerConnection;
	}

	public String getTag() {
		return Optional.ofNullable(this.tag).orElse("HEAD");
	}

	public String getUrl() {
		return this.url;
	}

	public Boolean getChildScmConnectionInheritAppendPath() {
		return this.childScmConnectionInheritAppendPath;
	}

	public Boolean getChildScmDeveloperConnectionInheritAppendPath() {
		return this.childScmDeveloperConnectionInheritAppendPath;
	}

	public Boolean getChildScmUrlInheritAppendPath() {
		return this.childScmUrlInheritAppendPath;
	}

	public static class Builder {

		private String connection;

		private String developerConnection;

		private String tag;

		private String url;

		private Boolean childScmConnectionInheritAppendPath;

		private Boolean childScmDeveloperConnectionInheritAppendPath;

		private Boolean childScmUrlInheritAppendPath;

		public Builder connection(String connection) {
			this.connection = connection;
			return this;
		}

		public Builder developerConnection(String developerConnection) {
			this.developerConnection = developerConnection;
			return this;
		}

		public Builder tag(String tag) {
			this.tag = tag;
			return this;
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public Builder childScmConnectionInheritAppendPath(Boolean childScmConnectionInheritAppendPath) {
			this.childScmConnectionInheritAppendPath = childScmConnectionInheritAppendPath;
			return this;
		}

		public Builder childScmDeveloperConnectionInheritAppendPath(
				Boolean childScmDeveloperConnectionInheritAppendPath) {
			this.childScmDeveloperConnectionInheritAppendPath = childScmDeveloperConnectionInheritAppendPath;
			return this;
		}

		public Builder childScmUrlInheritAppendPath(Boolean childScmUrlInheritAppendPath) {
			this.childScmUrlInheritAppendPath = childScmUrlInheritAppendPath;
			return this;
		}

		public Scm build() {
			return new Scm(this);
		}

	}

}
