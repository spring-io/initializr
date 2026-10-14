/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import org.jspecify.annotations.Nullable;

/**
 * Gradle-specific {@link Dependency}.
 *
 * @author Stephane Nicoll
 */
public class GradleDependency extends Dependency {

	private final @Nullable String configuration;

	protected GradleDependency(Builder builder) {
		super(builder);
		this.configuration = builder.configuration;
	}

	/**
	 * Initialize a new dependency {@link Builder} with the specified coordinates.
	 * @param groupId the group ID of the dependency
	 * @param artifactId the artifact ID of the dependency
	 * @return a new builder
	 */
	public static Builder withCoordinates(String groupId, String artifactId) {
		return new Builder(groupId, artifactId);
	}

	/**
	 * Initialize a new dependency {@link Builder} with the state of the specified
	 * {@link Dependency}.
	 * @param dependency the dependency to use to initialize the builder
	 * @return a new builder initialized with the same state as the {@code dependency}
	 */
	public static Builder from(Dependency dependency) {
		return new Builder(dependency.getGroupId(), dependency.getArtifactId()).initialize(dependency);
	}

	/**
	 * Return the configuration to use for the dependency. If not set, a default
	 * configuration is inferred from the {@linkplain #getScope() scope} of the
	 * dependency.
	 * @return the custom configuration name to use or {@code null}
	 */
	public @Nullable String getConfiguration() {
		return this.configuration;
	}

	/**
	 * Builder for a Gradle dependency.
	 *
	 * @see GradleDependency#withCoordinates(String, String)
	 */
	public static class Builder extends Dependency.Builder<Builder> {

		private @Nullable String configuration;

		protected Builder(String groupId, String artifactId) {
			super(groupId, artifactId);
		}

		/**
		 * Specify the configuration to use for the dependency. Overrides the
		 * {@linkplain DependencyScope scope}.
		 * @param configuration the name of the configuration to use
		 * @return this for method chaining
		 */
		public Builder configuration(@Nullable String configuration) {
			this.configuration = configuration;
			return self();
		}

		@Override
		protected Builder initialize(Dependency dependency) {
			super.initialize(dependency);
			if (dependency instanceof GradleDependency gradleDependency) {
				configuration(gradleDependency.getConfiguration());
			}
			return self();
		}

		@Override
		public GradleDependency build() {
			return new GradleDependency(this);
		}

	}

}
