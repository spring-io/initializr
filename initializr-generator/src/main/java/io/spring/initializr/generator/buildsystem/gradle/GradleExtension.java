/*
 * Copyright 2012-2024 the original author or authors.
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

package io.spring.initializr.generator.buildsystem.gradle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A customization for a Gradle extension.
 *
 * @author Moritz Halbritter
 */
public class GradleExtension {

	private final String name;

	private final List<Attribute> attributes;

	private final List<Invocation> invocations;

	private final Map<String, GradleExtension> nested;

	private final Set<String> importedTypes;

	protected GradleExtension(Builder builder) {
		this.name = builder.name;
		this.attributes = List.copyOf(builder.attributes.values());
		this.invocations = List.copyOf(builder.invocations);
		this.nested = Collections.unmodifiableMap(resolve(builder.nested));
		this.importedTypes = collectImportedTypes(builder);
	}

	private static Set<String> collectImportedTypes(Builder builder) {
		Set<String> result = new HashSet<>();
		addImportedTypes(result, builder);
		return Collections.unmodifiableSet(result);
	}

	private static void addImportedTypes(Set<String> importedTypes, Builder builder) {
		importedTypes.addAll(builder.importedTypes);
		for (Builder nested : builder.nested.values()) {
			addImportedTypes(importedTypes, nested);
		}
	}

	private static Map<String, GradleExtension> resolve(Map<String, Builder> extensions) {
		Map<String, GradleExtension> result = new LinkedHashMap<>();
		extensions.forEach((name, builder) -> result.put(name, builder.build()));
		return result;
	}

	/**
	 * Return the name of the extension.
	 * @return the extension name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the attributes that should be configured for this extension.
	 * @return extension attributes
	 */
	public List<Attribute> getAttributes() {
		return this.attributes;
	}

	/**
	 * Return the {@link Invocation invocations} of this extension.
	 * @return extension invocations
	 */
	public List<Invocation> getInvocations() {
		return this.invocations;
	}

	/**
	 * Return nested {@link GradleExtension extensions}.
	 * @return nested extensions
	 */
	public Map<String, GradleExtension> getNested() {
		return this.nested;
	}

	/**
	 * Return the imported types.
	 * @return imported types
	 */
	public Set<String> getImportedTypes() {
		return this.importedTypes;
	}

	/**
	 * Builder for {@link GradleExtension}.
	 */
	public static class Builder {

		private final String name;

		private final Map<String, Attribute> attributes = new LinkedHashMap<>();

		private final List<Invocation> invocations = new ArrayList<>();

		private final Map<String, Builder> nested = new LinkedHashMap<>();

		private final Set<String> importedTypes = new HashSet<>();

		protected Builder(String name) {
			this.name = name;
		}

		/**
		 * Import a given type.
		 * @param type the type to import
		 */
		public void importType(String type) {
			this.importedTypes.add(type);
		}

		/**
		 * Set a extension attribute.
		 * @param target the name of the attribute
		 * @param value the value
		 */
		public void attribute(String target, String value) {
			this.attributes.put(target, Attribute.set(target, value));
		}

		/**
		 * Set an extension attribute with a type.
		 * @param target the name of the attribute
		 * @param value the value
		 * @param type the type to import
		 */
		public void attributeWithType(String target, String value, String type) {
			importType(type);
			attribute(target, value);
		}

		/**
		 * Configure an extension attribute by appending the specified value.
		 * @param target the name of the attribute
		 * @param value the value to append
		 */
		public void append(String target, String value) {
			this.attributes.put(target, Attribute.append(target, value));
		}

		/**
		 * Configure an extension attribute by appending the specified value and type.
		 * @param target the name of the attribute
		 * @param value the value to append
		 * @param type the type to import
		 */
		public void appendWithType(String target, String value, String type) {
			importType(type);
			append(target, value);
		}

		/**
		 * Invoke an extension method.
		 * @param target the name of the method
		 * @param arguments the arguments
		 */
		public void invoke(String target, String... arguments) {
			this.invocations.add(new Invocation(target, Arrays.asList(arguments)));
		}

		/**
		 * Invoke an extension method.
		 * @param target the name of the method
		 * @param arguments the arguments
		 */
		public void invoke(String target, Collection<String> arguments) {
			this.invocations.add(new Invocation(target, List.copyOf(arguments)));
		}

		/**
		 * Invoke an extension method.
		 * @param target the name of the method
		 * @param type the type to import
		 * @param arguments the arguments
		 */
		public void invokeWithType(String target, String type, String... arguments) {
			importType(type);
			invoke(target, arguments);
		}

		/**
		 * Invoke an extension method.
		 * @param target the name of the method
		 * @param type the type to import
		 * @param arguments the arguments
		 */
		public void invokeWithType(String target, String type, Collection<String> arguments) {
			importType(type);
			invoke(target, arguments);
		}

		/**
		 * Customize a nested extension for the specified name. If such nested extension
		 * has already been added, the consumer can be used to further tune the existing
		 * extension configuration.
		 * @param name a extension name
		 * @param customizer a {@link Consumer} to customize the nested extension
		 */
		public void nested(String name, Consumer<Builder> customizer) {
			customizer.accept(this.nested.computeIfAbsent(name, (ignored) -> new Builder(name)));
		}

		/**
		 * Build a {@link GradleExtension} with the current state of this builder.
		 * @return a {@link GradleExtension}
		 */
		public GradleExtension build() {
			return new GradleExtension(this);
		}

	}

}
