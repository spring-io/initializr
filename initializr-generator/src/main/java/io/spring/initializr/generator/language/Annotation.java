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

package io.spring.initializr.generator.language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * An annotation.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public final class Annotation {

	private final String name;

	private final List<Attribute> attributes;

	private Annotation(Builder builder) {
		this.name = builder.name;
		this.attributes = Collections
				.unmodifiableList(new ArrayList<>(builder.attributes.values()));
	}

	public String getName() {
		return this.name;
	}

	public List<Attribute> getAttributes() {
		return this.attributes;
	}

	public static Annotation name(String name) {
		return name(name, null);
	}

	public static Annotation name(String name, Consumer<Builder> annotation) {
		Builder builder = new Builder(name);
		if (annotation != null) {
			annotation.accept(builder);
		}
		return new Annotation(builder);
	}

	/**
	 * Builder for creating an {@link Annotation}.
	 */
	public static final class Builder {

		private final String name;

		private final Map<String, Attribute> attributes = new LinkedHashMap<>();

		private Builder(String name) {
			this.name = name;
		}

		public Builder attribute(String name, Class<?> type, String... values) {
			this.attributes.put(name, new Attribute(name, type, values));
			return this;
		}

	}

	/**
	 * Define an attribute of an annotation.
	 */
	public static final class Attribute {

		private final String name;

		private final Class<?> type;

		private final List<String> values;

		private Attribute(String name, Class<?> type, String... values) {
			this.name = name;
			this.type = type;
			this.values = Arrays.asList(values);
		}

		public String getName() {
			return this.name;
		}

		public Class<?> getType() {
			return this.type;
		}

		public List<String> getValues() {
			return this.values;
		}

	}

}
