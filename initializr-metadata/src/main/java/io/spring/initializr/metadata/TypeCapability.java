/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

/**
 * An {@link ServiceCapabilityType#ACTION action} capability.
 *
 * @author Stephane Nicoll
 */
public class TypeCapability extends ServiceCapability<List<Type>> implements Defaultable<Type> {

	private final List<Type> content = new ArrayList<>();

	public TypeCapability() {
		super("type", ServiceCapabilityType.ACTION, "Type", "project type");
	}

	@Override
	public List<Type> getContent() {
		return this.content;
	}

	/**
	 * Return the {@link Type} with the specified id or {@code null} if no such type
	 * exists.
	 * @param id the ID to find
	 * @return the Type or {@code null}
	 */
	public @Nullable Type get(@Nullable String id) {
		if (id == null) {
			return null;
		}
		return this.content.stream().filter((it) -> id.equals(it.getId())).findFirst().orElse(null);
	}

	/**
	 * Return the default {@link Type}.
	 */
	@Override
	public @Nullable Type getDefault() {
		return this.content.stream().filter(DefaultMetadataElement::isDefault).findFirst().orElse(null);
	}

	@Override
	public void merge(@Nullable List<Type> otherContent) {
		if (otherContent == null) {
			return;
		}
		otherContent.forEach((it) -> {
			String id = it.getId();
			if (id != null && get(id) == null) {
				this.content.add(it);
			}
		});
	}

}
