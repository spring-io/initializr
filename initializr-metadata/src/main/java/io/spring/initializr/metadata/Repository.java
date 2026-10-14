/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import java.net.URL;
import java.util.StringJoiner;

import org.jspecify.annotations.Nullable;

/**
 * Define a repository to be represented in the generated project if a dependency refers
 * to it.
 *
 * @author Stephane Nicoll
 */
public class Repository {

	private @Nullable String name;

	private @Nullable URL url;

	private boolean releasesEnabled = true;

	private boolean snapshotsEnabled;

	public Repository() {
	}

	public Repository(@Nullable String name, @Nullable URL url) {
		this(name, url, true, false);
	}

	public Repository(@Nullable String name, @Nullable URL url, boolean releasesEnabled, boolean snapshotsEnabled) {
		this.name = name;
		this.url = url;
		this.releasesEnabled = releasesEnabled;
		this.snapshotsEnabled = snapshotsEnabled;
	}

	public @Nullable String getName() {
		return this.name;
	}

	public void setName(@Nullable String name) {
		this.name = name;
	}

	public @Nullable URL getUrl() {
		return this.url;
	}

	public void setUrl(@Nullable URL url) {
		this.url = url;
	}

	public boolean isReleasesEnabled() {
		return this.releasesEnabled;
	}

	public void setReleasesEnabled(boolean releasesEnabled) {
		this.releasesEnabled = releasesEnabled;
	}

	public boolean isSnapshotsEnabled() {
		return this.snapshotsEnabled;
	}

	public void setSnapshotsEnabled(boolean snapshotsEnabled) {
		this.snapshotsEnabled = snapshotsEnabled;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Repository other = (Repository) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.releasesEnabled != other.releasesEnabled) {
			return false;
		}
		if (this.snapshotsEnabled != other.snapshotsEnabled) {
			return false;
		}
		if (this.url == null) {
			if (other.url != null) {
				return false;
			}
		}
		else if (!this.url.equals(other.url)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + (this.releasesEnabled ? 1231 : 1237);
		result = prime * result + (this.snapshotsEnabled ? 1231 : 1237);
		result = prime * result + ((this.url == null) ? 0 : this.url.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", Repository.class.getSimpleName() + "[", "]").add("name='" + this.name + "'")
			.add("url=" + this.url)
			.add("releasesEnabled=" + this.releasesEnabled)
			.add("snapshotsEnabled=" + this.snapshotsEnabled)
			.toString();
	}

}
