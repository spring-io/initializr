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

package io.spring.initializr.generator.buildsystem;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A container for items.
 *
 * @param <I> the type of the identifier
 * @param <V> the type of the item
 * @author Stephane Nicoll
 */
public class BuildItemContainer<I, V> {

	private final Map<I, V> items;

	private final Function<I, V> itemResolver;

	protected BuildItemContainer(Map<I, V> items, Function<I, V> itemResolver) {
		this.items = items;
		this.itemResolver = itemResolver;
	}

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no item is registered
	 */
	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	/**
	 * Specify if this container has an item with the specified id.
	 * @param id the id of an item
	 * @return {@code true} if an item with the specified {@code id} is registered
	 */
	public boolean has(I id) {
		return this.items.containsKey(id);
	}

	/**
	 * Return a {@link Stream} of registered identifiers.
	 * @return a stream of ids
	 */
	public Stream<I> ids() {
		return this.items.keySet().stream();
	}

	/**
	 * Return a {@link Stream} of registered items.
	 * @return a stream of items
	 */
	public Stream<V> items() {
		return this.items.values().stream();
	}

	/**
	 * Return the item with the specified {@code id} or {@code null} if no such item
	 * exists.
	 * @param id the id of an item
	 * @return the item or {@code null}
	 */
	public V get(I id) {
		return this.items.get(id);
	}

	/**
	 * Lookup the item with the specified {@code id} and register it to this container.
	 * @param id the id of an item
	 */
	public void add(I id) {
		V item = this.itemResolver.apply(id);
		if (item == null) {
			throw new IllegalArgumentException("No such value with id '" + id + "'");
		}
		add(id, item);
	}

	/**
	 * Register the specified {@code item} with the specified {@code id}.
	 * @param id the id of the item
	 * @param item the item to register
	 */
	public void add(I id, V item) {
		this.items.put(id, item);
	}

	/**
	 * Remove the item with the specified {@code id}.
	 * @param id the id of the item to remove
	 * @return {@code true} if such an item was registered, {@code false} otherwise
	 */
	public boolean remove(I id) {
		return this.items.remove(id) != null;
	}

}
