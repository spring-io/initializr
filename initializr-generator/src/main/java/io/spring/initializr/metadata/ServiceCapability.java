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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Defines a capability of the initializr service. Each capability is defined by a id and
 * a {@link ServiceCapabilityType type}.
 *
 * @param <T> The content type
 * @author Stephane Nicoll
 */
@JsonIgnoreProperties({ "default", "all" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ServiceCapability<T> implements Cloneable {

	private final String id;

	private final ServiceCapabilityType type;

	/**
	 * A title of the capability, used as a header text or label.
	 */
	private String title;

	/**
	 * A description of the capability, used in help usage or UI tooltips.
	 */
	private String description;

	protected ServiceCapability(String id, ServiceCapabilityType type, String title,
			String description) {
		this.id = id;
		this.type = type;
		this.title = title;
		this.description = description;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return this.id;
	}

	public ServiceCapabilityType getType() {
		return this.type;
	}

	/**
	 * Return the "content" of this capability. The structure of the content vastly
	 * depends on the {@link ServiceCapability type} of the capability.
	 * @return the content
	 */
	public abstract T getContent();

	/**
	 * Merge the content of this instance with the specified content.
	 * @param otherContent the content to merge
	 * @see #merge(io.spring.initializr.metadata.ServiceCapability)
	 */
	public abstract void merge(T otherContent);

	/**
	 * Merge this capability with the specified argument. The service capabilities should
	 * match (i.e have the same {@code id} and {@code type}). Sub-classes may merge
	 * additional content.
	 * @param other the content to merge
	 */
	public void merge(ServiceCapability<T> other) {
		Assert.notNull(other, "Other must not be null");
		Assert.isTrue(this.id.equals(other.id), "Ids must be equals");
		Assert.isTrue(this.type.equals(other.type), "Types must be equals");
		if (StringUtils.hasText(other.title)) {
			this.title = other.title;
		}
		if (StringUtils.hasText(other.description)) {
			this.description = other.description;
		}
		merge(other.getContent());
	}

}
