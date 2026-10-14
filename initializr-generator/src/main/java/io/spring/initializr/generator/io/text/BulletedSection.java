/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.io.text;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.spring.initializr.generator.io.template.TemplateRenderer;

/**
 * {@link Section} for list of items using a {@link TemplateRenderer}. The template is
 * rendered with the registered items set in the model with a configurable item name.
 *
 * @param <T> the type of the item in the bullets
 * @author Madhura Bhave
 */
public class BulletedSection<T> implements Section {

	private final TemplateRenderer templateRenderer;

	private final String templateName;

	private final String itemName;

	private final List<T> items = new ArrayList<>();

	/**
	 * Create a new instance adding items in the model with the {@code items} key.
	 * @param templateRenderer the {@linkplain TemplateRenderer template renderer} to use
	 * @param templateName the name of the template
	 */
	public BulletedSection(TemplateRenderer templateRenderer, String templateName) {
		this(templateRenderer, templateName, "items");
	}

	/**
	 * Create a new instance.
	 * @param templateRenderer the {@linkplain TemplateRenderer template renderer} to use
	 * @param templateName the name of the template
	 * @param itemName the key of the items in the model
	 */
	public BulletedSection(TemplateRenderer templateRenderer, String templateName, String itemName) {
		this.templateRenderer = templateRenderer;
		this.templateName = templateName;
		this.itemName = itemName;
	}

	/**
	 * Add an item to the list.
	 * @param item the item to add
	 * @return this for method chaining
	 */
	public BulletedSection<T> addItem(T item) {
		this.items.add(item);
		return this;
	}

	/**
	 * Specify whether this section is empty.
	 * @return {@code true} if no item is registered
	 */
	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	/**
	 * Return an immutable list of the registered items.
	 * @return the registered items
	 */
	public List<T> getItems() {
		return Collections.unmodifiableList(this.items);
	}

	@Override
	public void write(PrintWriter writer) throws IOException {
		if (!isEmpty()) {
			Map<String, Object> model = new HashMap<>();
			model.put(this.itemName, this.items);
			writer.println(this.templateRenderer.render(this.templateName, model));
		}
	}

}
