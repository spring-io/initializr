/*
 * Copyright 2012 - present the original author or authors.
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
