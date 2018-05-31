/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.metadata;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A group of {@link Dependency} instances identified by a name.
 *
 * @author Stephane Nicoll
 */
public class DependencyGroup {

	private String name;

	@JsonIgnore
	private String versionRange;

	@JsonIgnore
	private String bom;

	@JsonIgnore
	private String repository;

	final List<Dependency> content = new ArrayList<>();

	/**
	 * Return the name of this group.
	 * @return the name of the group
	 */
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the default version range to apply to all dependencies of this group unless
	 * specified otherwise.
	 * @return the version range
	 */
	public String getVersionRange() {
		return this.versionRange;
	}

	public void setVersionRange(String versionRange) {
		this.versionRange = versionRange;
	}

	/**
	 * Return the default bom to associate to all dependencies of this group unless
	 * specified otherwise.
	 * @return the BOM
	 */
	public String getBom() {
		return this.bom;
	}

	public void setBom(String bom) {
		this.bom = bom;
	}

	/**
	 * Return the default repository to associate to all dependencies of this group unless
	 * specified otherwise.
	 * @return the repository
	 */
	public String getRepository() {
		return this.repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	/**
	 * Return the {@link Dependency dependencies} of this group.
	 * @return the content
	 */
	public List<Dependency> getContent() {
		return this.content;
	}

	/**
	 * Create a new {@link DependencyGroup} instance with the given name.
	 * @param name the name of the group
	 * @return a new {@link DependencyGroup} instance
	 */
	public static DependencyGroup create(String name) {
		DependencyGroup group = new DependencyGroup();
		group.setName(name);
		return group;
	}

}
