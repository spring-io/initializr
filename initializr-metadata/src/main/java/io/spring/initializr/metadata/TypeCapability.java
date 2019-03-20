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

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link ServiceCapabilityType#ACTION action} capability.
 *
 * @author Stephane Nicoll
 */
public class TypeCapability extends ServiceCapability<List<Type>>
		implements Defaultable<Type> {

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
	public Type get(String id) {
		return this.content.stream().filter((it) -> id.equals(it.getId())).findFirst()
				.orElse(null);
	}

	/**
	 * Return the default {@link Type}.
	 */
	@Override
	public Type getDefault() {
		return this.content.stream().filter(DefaultMetadataElement::isDefault).findFirst()
				.orElse(null);
	}

	@Override
	public void merge(List<Type> otherContent) {
		otherContent.forEach((it) -> {
			if (get(it.getId()) == null) {
				this.content.add(it);
			}
		});
	}

}
