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

/**
 * A {@link MetadataElement} that specifies if its the default for a given capability.
 *
 * @author Stephane Nicoll
 */
public class DefaultMetadataElement extends MetadataElement {

	private boolean defaultValue;

	public DefaultMetadataElement() {
	}

	public DefaultMetadataElement(String id, String name, boolean defaultValue) {
		super(id, name);
		this.defaultValue = defaultValue;
	}

	public DefaultMetadataElement(String id, boolean defaultValue) {
		this(id, null, defaultValue);
	}

	public void setDefault(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isDefault() {
		return this.defaultValue;
	}

	public static DefaultMetadataElement create(String id, boolean defaultValue) {
		return new DefaultMetadataElement(id, defaultValue);
	}

	public static DefaultMetadataElement create(String id, String name,
			boolean defaultValue) {
		return new DefaultMetadataElement(id, name, defaultValue);
	}

}
