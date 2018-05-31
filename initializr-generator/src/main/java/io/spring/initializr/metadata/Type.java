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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines a particular project type. Each type is associated to a concrete action that
 * should be invoked to generate the content of that type.
 *
 * @author Stephane Nicoll
 */
public class Type extends DefaultMetadataElement implements Describable {

	private String description;

	@Deprecated
	private String stsId;

	private String action;

	private final Map<String, String> tags = new LinkedHashMap<>();

	public void setAction(String action) {
		String actionToUse = action;
		if (!actionToUse.startsWith("/")) {
			actionToUse = "/" + actionToUse;
		}
		this.action = actionToUse;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStsId() {
		return this.stsId;
	}

	public void setStsId(String stsId) {
		this.stsId = stsId;
	}

	public String getAction() {
		return this.action;
	}

	public Map<String, String> getTags() {
		return this.tags;
	}

}
