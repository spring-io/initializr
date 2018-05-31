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
 * Defines the supported service capability type.
 *
 * @author Stephane Nicoll
 */
public enum ServiceCapabilityType {

	/**
	 * A special type that defines the action to use.
	 */
	ACTION("action"),

	/**
	 * A simple text value with no option.
	 */
	TEXT("text"),

	/**
	 * A simple value to be chosen amongst the specified options.
	 */
	SINGLE_SELECT("single-select"),

	/**
	 * A hierarchical set of values (values in values) with the ability to select multiple
	 * values.
	 */
	HIERARCHICAL_MULTI_SELECT("hierarchical-multi-select");

	private final String name;

	ServiceCapabilityType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}
