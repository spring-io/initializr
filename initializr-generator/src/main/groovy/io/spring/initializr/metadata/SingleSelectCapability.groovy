/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.metadata

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A {@link ServiceCapabilityType#SINGLE_SELECT single select} capability.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class SingleSelectCapability extends ServiceCapability<List<DefaultMetadataElement>> {

	final List<DefaultMetadataElement> content = []

	@JsonCreator
	SingleSelectCapability(@JsonProperty("id") String id) {
		this(id, null, null)
	}

	SingleSelectCapability(String id, String title, String description) {
		super(id, ServiceCapabilityType.SINGLE_SELECT, title, description)
	}

	/**
	 * Return the default element of this capability.
	 */
	DefaultMetadataElement getDefault() {
	   return content.find { it.default }
	}

	/**
	 * Return the element with the specified id or {@code null} if no such
	 * element exists.
	 */
	DefaultMetadataElement get(String id) {
		return content.find { id.equals(it.id)}
	}

	@Override
	void merge(List<DefaultMetadataElement> otherContent) {
		otherContent.each {
			if (!get(it.id)) {
				content << it
			}
		}
	}

}
