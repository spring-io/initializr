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

import java.util.LinkedHashMap;
import java.util.function.Function;

/**
 * A {@link BuildItemContainer} implementation for repositories.
 *
 * @author Stephane Nicoll
 */
public class MavenRepositoryContainer
		extends BuildItemContainer<String, MavenRepository> {

	MavenRepositoryContainer(Function<String, MavenRepository> itemResolver) {
		super(new LinkedHashMap<>(), itemResolver);
	}

	/**
	 * Register the specified {@link MavenRepository repository}.
	 * @param repository the repository to register
	 */
	public void add(MavenRepository repository) {
		add(repository.getId(), repository);
	}

	/**
	 * Register a {@link MavenRepository repository} with snapshots disabled.
	 * @param id the id of the repository
	 * @param name the repository name
	 * @param url the repository url
	 */
	public void add(String id, String name, String url) {
		add(id, name, url, false);
	}

	/**
	 * Register a {@link MavenRepository repository} and specify whether snapshots are
	 * enabled.
	 * @param id the id of the repository
	 * @param name the repository name
	 * @param url the repository url
	 * @param snapshotsEnabled whether snapshots are enabled
	 */
	public void add(String id, String name, String url, boolean snapshotsEnabled) {
		add(id, new MavenRepository(id, name, url, snapshotsEnabled));
	}

}
