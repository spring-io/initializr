/*
 * Copyright 2012-2020 the original author or authors.
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A {@link ServiceCapabilityType#SINGLE_SELECT single select} capability.
 *
 * @author Stephane Nicoll
 */
public class SingleSelectCapability extends ServiceCapability<List<DefaultMetadataElement>>
		implements Defaultable<DefaultMetadataElement> {

	private final List<DefaultMetadataElement> content = new ArrayList<>();

	private final ReadWriteLock contentLock = new ReentrantReadWriteLock();

	@JsonCreator
	SingleSelectCapability(@JsonProperty("id") String id) {
		this(id, null, null);
	}

	public SingleSelectCapability(String id, String title, String description) {
		super(id, ServiceCapabilityType.SINGLE_SELECT, title, description);
	}

	@Override
	public List<DefaultMetadataElement> getContent() {
		return Collections.unmodifiableList(withReadableContent(ArrayList::new));
	}

	public void addContent(DefaultMetadataElement element) {
		withWritableContent((content) -> content.add(element));
	}

	public void setContent(List<DefaultMetadataElement> newContent) {
		withWritableContent((content) -> {
			content.clear();
			content.addAll(newContent);
		});
	}

	/**
	 * Return the default element of this capability.
	 */
	@Override
	public DefaultMetadataElement getDefault() {
		return withReadableContent(
				(content) -> content.stream().filter(DefaultMetadataElement::isDefault).findFirst().orElse(null));
	}

	/**
	 * Return the element with the specified id or {@code null} if no such element exists.
	 * @param id the ID of the element to find
	 * @return the element or {@code null}
	 */
	public DefaultMetadataElement get(String id) {
		return withReadableContent(
				(content) -> content.stream().filter((it) -> id.equals(it.getId())).findFirst().orElse(null));
	}

	@Override
	public void merge(List<DefaultMetadataElement> otherContent) {
		withWritableContent((content) -> {
			otherContent.forEach((it) -> {
				if (get(it.getId()) == null) {
					this.content.add(it);
				}
			});
		});
	}

	private <T> T withReadableContent(Function<List<DefaultMetadataElement>, T> consumer) {
		this.contentLock.readLock().lock();
		try {
			return consumer.apply(this.content);
		}
		finally {
			this.contentLock.readLock().unlock();
		}
	}

	private void withWritableContent(Consumer<List<DefaultMetadataElement>> consumer) {
		this.contentLock.writeLock().lock();
		try {
			consumer.accept(this.content);
		}
		finally {
			this.contentLock.writeLock().unlock();
		}
	}

}
