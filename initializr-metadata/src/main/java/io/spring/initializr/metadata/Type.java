/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jspecify.annotations.Nullable;

/**
 * Defines a particular project type. Each type is associated to a concrete action that
 * should be invoked to generate the content of that type.
 *
 * @author Stephane Nicoll
 */
public class Type extends DefaultMetadataElement implements Describable {

	private @Nullable String description;

	private @Nullable String action;

	private final Map<String, String> tags = new LinkedHashMap<>();

	/**
	 * Set the action. Prepends a {@code /} if missing.
	 * @param action the action, for example {@code /starter.zip}
	 */
	public void setAction(@Nullable String action) {
		if (action == null) {
			this.action = null;
			return;
		}
		String actionToUse = action;
		if (!actionToUse.startsWith("/")) {
			actionToUse = "/" + actionToUse;
		}
		this.action = actionToUse;
	}

	@Override
	public @Nullable String getDescription() {
		return this.description;
	}

	public void setDescription(@Nullable String description) {
		this.description = description;
	}

	public @Nullable String getAction() {
		return this.action;
	}

	public Map<String, String> getTags() {
		return this.tags;
	}

}
