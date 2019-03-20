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

package io.spring.initializr.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A {@link ServiceCapabilityType#TEXT text} capability.
 *
 * @author Stephane Nicoll
 */
public class TextCapability extends ServiceCapability<String> {

	private String content;

	@JsonCreator
	TextCapability(@JsonProperty("id") String id) {
		this(id, null, null);
	}

	TextCapability(String id, String title, String description) {
		super(id, ServiceCapabilityType.TEXT, title, description);
	}

	@Override
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public void merge(String otherContent) {
		if (otherContent != null) {
			this.content = otherContent;
		}
	}

}
