/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Defines a capability of the initializr service. Each capability is defined by a id and
 * a {@link ServiceCapabilityType type}.
 *
 * @param <T> the content type
 * @author Stephane Nicoll
 */
@JsonIgnoreProperties({ "default", "all" })
@JsonInclude(Include.NON_NULL)
public abstract class ServiceCapability<T> implements Cloneable {

	private final String id;

	private final ServiceCapabilityType type;

	/**
	 * A title of the capability, used as a header text or label.
	 */
	private @Nullable String title;

	/**
	 * A description of the capability, used in help usage or UI tooltips.
	 */
	private @Nullable String description;

	protected ServiceCapability(String id, ServiceCapabilityType type, @Nullable String title,
			@Nullable String description) {
		this.id = id;
		this.type = type;
		this.title = title;
		this.description = description;
	}

	public @Nullable String getTitle() {
		return this.title;
	}

	public void setTitle(@Nullable String title) {
		this.title = title;
	}

	public @Nullable String getDescription() {
		return this.description;
	}

	public void setDescription(@Nullable String description) {
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
	public abstract @Nullable T getContent();

	/**
	 * Merge the content of this instance with the specified content.
	 * @param otherContent the content to merge
	 * @see #merge(io.spring.initializr.metadata.ServiceCapability)
	 */
	public abstract void merge(@Nullable T otherContent);

	/**
	 * Merge this capability with the specified argument. The service capabilities should
	 * match (i.e have the same {@code id} and {@code type}). Sub-classes may merge
	 * additional content.
	 * @param other the content to merge
	 */
	public void merge(ServiceCapability<T> other) {
		Assert.notNull(other, "Other must not be null");
		Assert.isTrue(this.id.equals(other.id), "Ids must be equal");
		Assert.isTrue(this.type.equals(other.type), "Types must be equal");
		if (StringUtils.hasText(other.title)) {
			this.title = other.title;
		}
		if (StringUtils.hasText(other.description)) {
			this.description = other.description;
		}
		T content = other.getContent();
		merge(content);
	}

}
