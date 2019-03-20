/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * {@link Section} for list of items using a {@link TemplateRenderer}.
 *
 * @param <T> the type of the item in the bullets
 * @author Madhura Bhave
 */
public class BulletedSection<T> implements Section {

	private final TemplateRenderer templateRenderer;

	private final String templateName;

	private final String itemName;

	private List<T> items = new ArrayList<>();

	public BulletedSection(TemplateRenderer templateRenderer, String templateName) {
		this(templateRenderer, templateName, "items");
	}

	public BulletedSection(TemplateRenderer templateRenderer, String templateName,
			String itemName) {
		this.templateRenderer = templateRenderer;
		this.templateName = templateName;
		this.itemName = itemName;
	}

	public BulletedSection addItem(T item) {
		this.items.add(item);
		return this;
	}

	public boolean isEmpty() {
		return this.items.isEmpty();
	}

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
