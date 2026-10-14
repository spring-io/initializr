/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import org.jspecify.annotations.Nullable;

/**
 * A basic metadata element.
 *
 * @author Stephane Nicoll
 */
public class MetadataElement {

	/**
	 * A visual representation of this element.
	 */
	private @Nullable String name;

	/**
	 * The unique id of this element for a given capability.
	 */
	private @Nullable String id;

	public MetadataElement() {
	}

	public MetadataElement(MetadataElement other) {
		this(other.id, other.name);
	}

	public MetadataElement(@Nullable String id, @Nullable String name) {
		this.id = id;
		this.name = name;
	}

	public @Nullable String getName() {
		return (this.name != null) ? this.name : this.id;
	}

	public @Nullable String getId() {
		return this.id;
	}

	public void setId(@Nullable String id) {
		this.id = id;
	}

	public void setName(@Nullable String name) {
		this.name = name;
	}

}
