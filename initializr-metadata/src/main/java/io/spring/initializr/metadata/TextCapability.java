/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;

/**
 * A {@link ServiceCapabilityType#TEXT text} capability.
 *
 * @author Stephane Nicoll
 */
public class TextCapability extends ServiceCapability<String> {

	private @Nullable String content;

	@JsonCreator
	TextCapability(@JsonProperty("id") String id) {
		this(id, null, null);
	}

	/**
	 * Creates a new instance.
	 * @param id the id of this capability
	 * @param title the title of this capability
	 * @param description the description of this capability
	 */
	public TextCapability(String id, @Nullable String title, @Nullable String description) {
		super(id, ServiceCapabilityType.TEXT, title, description);
	}

	@Override
	public @Nullable String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public void merge(@Nullable String otherContent) {
		if (otherContent != null) {
			this.content = otherContent;
		}
	}

}
