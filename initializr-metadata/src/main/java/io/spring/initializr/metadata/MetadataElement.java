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

package io.spring.initializr.metadata;

/**
 * A basic metadata element.
 *
 * @author Stephane Nicoll
 */
public class MetadataElement {

	/**
	 * A visual representation of this element.
	 */
	private String name;

	/**
	 * The unique id of this element for a given capability.
	 */
	private String id;

	public MetadataElement() {
	}

	public MetadataElement(MetadataElement other) {
		this(other.id, other.name);
	}

	public MetadataElement(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return (this.name != null) ? this.name : this.id;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

}
