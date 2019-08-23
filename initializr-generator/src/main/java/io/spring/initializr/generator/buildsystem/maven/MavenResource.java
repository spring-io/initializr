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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A resource of a {@link MavenBuild}.
 *
 * @author Stephane Nicoll
 */
public class MavenResource {

	private final String directory;

	private final String targetPath;

	private final boolean filtering;

	private final List<String> includes;

	private final List<String> excludes;

	public MavenResource(Builder builder) {
		this.directory = builder.directory;
		this.targetPath = builder.targetPath;
		this.filtering = builder.filtering;
		this.includes = builder.includes;
		this.excludes = builder.excludes;
	}

	/**
	 * Return the directory where resources are to be found. Can use regular maven token
	 * such as {@code ${basedir}/src/main}.
	 * @return the resources directory
	 */
	public String getDirectory() {
		return this.directory;
	}

	/**
	 * Return the directory structure to place the set of resources from a build. Return
	 * {@code null} by default which represents the root directory.
	 * @return the target path or {@code null}
	 */
	public String getTargetPath() {
		return this.targetPath;
	}

	/**
	 * Specify if filtering is enabled when copying resources.
	 * @return {@code true} if filtering is enabled
	 */
	public boolean isFiltering() {
		return this.filtering;
	}

	/**
	 * Return files patterns which specify the files to include as resources under that
	 * specified directory. Can use {@code *} for all.
	 * @return the include patterns
	 */
	public List<String> getIncludes() {
		return this.includes;
	}

	/**
	 * Return files patterns which specify the files to ignore as resources under that
	 * specified directory. In conflicts between {@code include} and {@code exclude},
	 * {@code exclude} wins.
	 * @return the exclude patterns
	 */
	public List<String> getExcludes() {
		return this.excludes;
	}

	/**
	 * Builder for a resource.
	 */
	public static class Builder {

		private final String directory;

		private String targetPath;

		private boolean filtering;

		private List<String> includes = new ArrayList<>();

		private List<String> excludes = new ArrayList<>();

		public Builder(String directory) {
			this.directory = directory;
		}

		public Builder targetPath(String targetPath) {
			this.targetPath = targetPath;
			return this;
		}

		public Builder filtering(Boolean filtering) {
			this.filtering = filtering;
			return this;
		}

		public Builder includes(String... includes) {
			this.includes = Arrays.asList(includes);
			return this;
		}

		public Builder excludes(String... excludes) {
			this.excludes = Arrays.asList(excludes);
			return this;
		}

		public MavenResource build() {
			return new MavenResource(this);
		}

	}

}
