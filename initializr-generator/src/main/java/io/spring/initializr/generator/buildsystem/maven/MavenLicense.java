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

/**
 * A {@code <license>} in a Maven pom.
 *
 * @author Jafer Khan Shamshad
 * @author Stephane Nicoll
 */
public class MavenLicense {

	private final String name;

	private final String url;

	private final Distribution distribution;

	private final String comments;

	MavenLicense(Builder builder) {
		this.name = builder.name;
		this.url = builder.url;
		this.distribution = builder.distribution;
		this.comments = builder.comments;
	}

	/**
	 * Return the name of the license.
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the URL of the license.
	 * @return the URL
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Return the distribution mechanism of the project associated with the license.
	 * @return the distribution mechanism
	 */
	public Distribution getDistribution() {
		return this.distribution;
	}

	/**
	 * Return the comments associated with the license.
	 * @return the comments
	 */
	public String getComments() {
		return this.comments;
	}

	/**
	 * Builder for a {@link MavenLicense}.
	 */
	public static class Builder {

		private String name;

		private String url;

		private Distribution distribution;

		private String comments;

		/**
		 * Set the name of the license.
		 * @param name the name of the license or {@code null}
		 * @return this for method chaining
		 */
		public Builder name(String name) {
			this.name = name;
			return this;
		}

		/**
		 * Set the URL of the license.
		 * @param url the URL of the license or {@code null}
		 * @return this for method chaining
		 */
		public Builder url(String url) {
			this.url = url;
			return this;
		}

		/**
		 * Set the distribution mechanism of the project associated with the license.
		 * @param distribution the distribution mechanism of the project or {@code null}
		 * @return this for method chaining
		 */
		public Builder distribution(Distribution distribution) {
			this.distribution = distribution;
			return this;
		}

		/**
		 * Set comments associated with the license.
		 * @param comments the comments for the license or {@code null}
		 * @return this for method chaining
		 */
		public Builder comments(String comments) {
			this.comments = comments;
			return this;
		}

		public MavenLicense build() {
			return new MavenLicense(this);
		}

	}

	/**
	 * Describes how the project may be legally distributed.
	 */
	public enum Distribution {

		/**
		 * May be downloaded from a Maven repository.
		 */
		REPO,

		/**
		 * Must be manually installed.
		 */
		MANUAL

	}

}
