/*
 * Copyright 2014-2018 the original author or authors.
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

package io.spring.initializr.web.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A {@code JsonFieldProcessor} processes a payload's fields, allowing them to be
 * extracted and removed.
 *
 * @author Andy Wilkinson
 */
// Copied from RestDocs to make it visible
final class JsonFieldProcessor {

	boolean hasField(JsonFieldPath fieldPath, Object payload) {
		final AtomicReference<Boolean> hasField = new AtomicReference<>(false);
		traverse(new ProcessingContext(payload, fieldPath),
				(match) -> hasField.set(true));
		return hasField.get();
	}

	Object extract(JsonFieldPath path, Object payload) {
		final List<Object> matches = new ArrayList<>();
		traverse(new ProcessingContext(payload, path),
				(match) -> matches.add(match.getValue()));
		if (matches.isEmpty()) {
			throw new IllegalArgumentException("Field does not exist: " + path);
		}
		if ((!path.isArray()) && path.isPrecise()) {
			return matches.get(0);
		}
		else {
			return matches;
		}
	}

	void remove(final JsonFieldPath path, Object payload) {
		traverse(new ProcessingContext(payload, path), Match::remove);
	}

	private void traverse(ProcessingContext context, MatchCallback matchCallback) {
		final String segment = context.getSegment();
		if (JsonFieldPath.isArraySegment(segment)) {
			if (context.getPayload() instanceof List) {
				handleListPayload(context, matchCallback);
			}
		}
		else if (context.getPayload() instanceof Map
				&& ((Map<?, ?>) context.getPayload()).containsKey(segment)) {
			handleMapPayload(context, matchCallback);
		}
	}

	private void handleListPayload(ProcessingContext context,
			MatchCallback matchCallback) {
		List<?> list = context.getPayload();
		final Iterator<?> items = list.iterator();
		if (context.isLeaf()) {
			while (items.hasNext()) {
				Object item = items.next();
				matchCallback.foundMatch(
						new ListMatch(items, list, item, context.getParentMatch()));
			}
		}
		else {
			while (items.hasNext()) {
				Object item = items.next();
				traverse(
						context.descend(item,
								new ListMatch(items, list, item, context.parent)),
						matchCallback);
			}
		}
	}

	private void handleMapPayload(ProcessingContext context,
			MatchCallback matchCallback) {
		Map<?, ?> map = context.getPayload();
		Object item = map.get(context.getSegment());
		MapMatch mapMatch = new MapMatch(item, map, context.getSegment(),
				context.getParentMatch());
		if (context.isLeaf()) {
			matchCallback.foundMatch(mapMatch);
		}
		else {
			traverse(context.descend(item, mapMatch), matchCallback);
		}
	}

	private static final class MapMatch implements Match {

		private final Object item;

		private final Map<?, ?> map;

		private final String segment;

		private final Match parent;

		private MapMatch(Object item, Map<?, ?> map, String segment, Match parent) {
			this.item = item;
			this.map = map;
			this.segment = segment;
			this.parent = parent;
		}

		@Override
		public Object getValue() {
			return this.item;
		}

		@Override
		public void remove() {
			this.map.remove(this.segment);
			if (this.map.isEmpty() && this.parent != null) {
				this.parent.remove();
			}
		}

	}

	private static final class ListMatch implements Match {

		private final Iterator<?> items;

		private final List<?> list;

		private final Object item;

		private final Match parent;

		private ListMatch(Iterator<?> items, List<?> list, Object item, Match parent) {
			this.items = items;
			this.list = list;
			this.item = item;
			this.parent = parent;
		}

		@Override
		public Object getValue() {
			return this.item;
		}

		@Override
		public void remove() {
			this.items.remove();
			if (this.list.isEmpty() && this.parent != null) {
				this.parent.remove();
			}
		}

	}

	private interface MatchCallback {

		void foundMatch(Match match);

	}

	private interface Match {

		Object getValue();

		void remove();

	}

	private static final class ProcessingContext {

		private final Object payload;

		private final List<String> segments;

		private final Match parent;

		private final JsonFieldPath path;

		private ProcessingContext(Object payload, JsonFieldPath path) {
			this(payload, path, null, null);
		}

		private ProcessingContext(Object payload, JsonFieldPath path,
				List<String> segments, Match parent) {
			this.payload = payload;
			this.path = path;
			this.segments = (segments != null ? segments : path.getSegments());
			this.parent = parent;
		}

		private String getSegment() {
			return this.segments.get(0);
		}

		@SuppressWarnings("unchecked")
		private <T> T getPayload() {
			return (T) this.payload;
		}

		private boolean isLeaf() {
			return this.segments.size() == 1;
		}

		private Match getParentMatch() {
			return this.parent;
		}

		private ProcessingContext descend(Object payload, Match match) {
			return new ProcessingContext(payload, this.path,
					this.segments.subList(1, this.segments.size()), match);
		}

	}

}
