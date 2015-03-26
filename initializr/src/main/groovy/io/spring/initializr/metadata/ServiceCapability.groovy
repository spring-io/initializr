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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

import org.springframework.util.Assert

/**
 * Defines a capability of the initializr service. Each capability
 * is defined by a id and a {@link ServiceCapabilityType type}.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@JsonIgnoreProperties(["default", "all"])
@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class ServiceCapability<T> {

	final String id

	final ServiceCapabilityType type

	String description

	protected ServiceCapability(String id, ServiceCapabilityType type) {
		this.id = id
		this.type = type
	}

	/**
	 * Return the "content" of this capability. The structure of the content
	 * vastly depends on the {@link ServiceCapability type} of the capability.
	 */
	abstract T getContent()

	/**
	 * Merge the content of this instance with the specified content.
	 * @see #merge(io.spring.initializr.metadata.ServiceCapability)
	 */
	abstract void merge(T otherContent)

	/**
	 * Merge this capability with the specified argument. The service capabilities
	 * should match (i.e have the same {@code id} and {@code type}). Sub-classes
	 * may merge additional content.
	 */
	void merge(ServiceCapability<T> other) {
		Assert.notNull(other, "Other must not be null")
		Assert.state(this.id.equals(other.id))
		Assert.state(this.type.equals(other.type))
		if (other.description) {
			this.description = other.description
		}
		merge(other.content)
	}

}

