/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

import org.jspecify.annotations.Nullable;

/**
 * General build settings.
 *
 * @author Stephane Nicoll
 */
public class BuildSettings {

	private final @Nullable String group;

	private final @Nullable String artifact;

	private final @Nullable String version;

	protected BuildSettings(Builder<?> builder) {
		this.group = builder.group;
		this.artifact = builder.artifact;
		this.version = builder.version;
	}

	/**
	 * Return the identifier of the group for the project.
	 * @return the group identifier or {@code null}
	 */
	public @Nullable String getGroup() {
		return this.group;
	}

	/**
	 * Return the identifier of the project.
	 * @return the project identifier or {@code null}
	 */
	public @Nullable String getArtifact() {
		return this.artifact;
	}

	/**
	 * Return the version of the project.
	 * @return the project version or {@code null}
	 */
	public @Nullable String getVersion() {
		return this.version;
	}

	/**
	 * Builder for build settings.
	 *
	 * @param <B> builder type
	 */
	public abstract static class Builder<B extends Builder<B>> {

		private @Nullable String group;

		private @Nullable String artifact;

		private @Nullable String version = "0.0.1-SNAPSHOT";

		protected Builder() {
		}

		/**
		 * Set the group ID of the project.
		 * @param group the group ID
		 * @return this for method chaining
		 */
		public B group(@Nullable String group) {
			this.group = group;
			return self();
		}

		/**
		 * Set the artifact ID of the project.
		 * @param artifact the artifact ID
		 * @return this for method chaining
		 */
		public B artifact(@Nullable String artifact) {
			this.artifact = artifact;
			return self();
		}

		/**
		 * Set the version of the project.
		 * @param version the version
		 * @return this for method chaining
		 */
		public B version(@Nullable String version) {
			this.version = version;
			return self();
		}

		/**
		 * Return this builder.
		 * @return this builder
		 */
		@SuppressWarnings("unchecked")
		protected B self() {
			return (B) this;
		}

		/**
		 * Build a {@link BuildSettings} with the current state of this builder.
		 * @return a {@link BuildSettings}
		 */
		public BuildSettings build() {
			return new BuildSettings(this);
		}

	}

}
