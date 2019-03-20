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

package io.spring.initializr.generator.buildsystem;

/**
 * A Maven repository.
 *
 * @author Andy Wilkinson
 */
public class MavenRepository {

	/**
	 * Maven Central.
	 */
	public static final MavenRepository MAVEN_CENTRAL = new MavenRepository(
			"maven-central", "Maven Central", "https://repo.maven.apache.org/maven2");

	private final String id;

	private final String name;

	private final String url;

	private final boolean snapshotsEnabled;

	public MavenRepository(String id, String name, String url) {
		this(id, name, url, false);
	}

	public MavenRepository(String id, String name, String url, boolean snapshotsEnabled) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.snapshotsEnabled = snapshotsEnabled;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getUrl() {
		return this.url;
	}

	public boolean isSnapshotsEnabled() {
		return this.snapshotsEnabled;
	}

}
