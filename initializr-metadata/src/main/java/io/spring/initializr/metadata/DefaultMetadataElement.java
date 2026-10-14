/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import org.jspecify.annotations.Nullable;

/**
 * A {@link MetadataElement} that specifies if its the default for a given capability.
 *
 * @author Stephane Nicoll
 */
public class DefaultMetadataElement extends MetadataElement {

	private boolean defaultValue;

	public DefaultMetadataElement() {
	}

	public DefaultMetadataElement(String id, @Nullable String name, boolean defaultValue) {
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

	/**
	 * Create a {@link DefaultMetadataElement}.
	 * @param id the ID
	 * @param defaultValue whether this is the default element
	 * @return the element
	 */
	public static DefaultMetadataElement create(String id, boolean defaultValue) {
		return new DefaultMetadataElement(id, defaultValue);
	}

	/**
	 * Create a {@link DefaultMetadataElement}.
	 * @param id the ID
	 * @param name the name
	 * @param defaultValue whether this is the default element
	 * @return the element
	 */
	public static DefaultMetadataElement create(String id, String name, boolean defaultValue) {
		return new DefaultMetadataElement(id, name, defaultValue);
	}

}
