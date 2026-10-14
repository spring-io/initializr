/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.maven;

import org.jspecify.annotations.Nullable;

/**
 * The {@code <parent>} in a Maven pom.
 *
 * @author Andy Wilkinson
 */
public class MavenParent {

	private final String groupId;

	private final String artifactId;

	private final String version;

	private final @Nullable String relativePath;

	MavenParent(String groupId, String artifactId, String version, @Nullable String relativePath) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.relativePath = relativePath;
	}

	/**
	 * Return the group ID of the parent.
	 * @return the group ID
	 */
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * Return the artifact ID of the parent.
	 * @return the artifact ID
	 */
	public String getArtifactId() {
		return this.artifactId;
	}

	/**
	 * Return the version of the parent.
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Return the relative path of this parent.
	 * @return the relative path of this parent or {@code null}.
	 */
	public @Nullable String getRelativePath() {
		return this.relativePath;
	}

}
