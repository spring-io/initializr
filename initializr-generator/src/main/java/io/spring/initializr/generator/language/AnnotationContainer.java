/*
 * Copyright 2012-2023 the original author or authors.
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.spring.initializr.generator.language.Annotation.Builder;

/**
 * A container for {@linkplain Annotation annotations} defined on an annotated element.
 *
 * @author Stephane Nicoll
 */
public class AnnotationContainer {

	private final Map<ClassName, Builder> annotations;

	public AnnotationContainer() {
		this(new LinkedHashMap<>());
	}

	private AnnotationContainer(Map<ClassName, Builder> annotations) {
		this.annotations = annotations;
	}

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no annotation is registered
	 */
	public boolean isEmpty() {
		return this.annotations.isEmpty();
	}

	/**
	 * Specify if this container has a an annotation with the specified {@link ClassName}.
	 * @param className the class name of an annotation
	 * @return {@code true} if the annotation with the specified class name exists
	 */
	public boolean has(ClassName className) {
		return this.annotations.containsKey(className);
	}

	/**
	 * Return the {@link Annotation annotations}.
	 * @return the annotations
	 */
	public Stream<Annotation> values() {
		return this.annotations.values().stream().map(Builder::build);
	}

	/**
	 * Add a single {@link Annotation} with the specified class name and {@link Consumer}
	 * to customize it. If the annotation has already been added, the consumer can be used
	 * to further tune attributes
	 * @param className the class name of an annotation
	 * @param annotation a {@link Consumer} to customize the {@link Annotation}
	 */
	public void add(ClassName className, Consumer<Builder> annotation) {
		Builder builder = this.annotations.computeIfAbsent(className, (key) -> new Builder(className));
		if (annotation != null) {
			annotation.accept(builder);
		}
	}

	/**
	 * Add a single {@link Annotation} with the specified class name. Does nothing If the
	 * annotation has already been added.
	 * @param className the class name of an annotation
	 */
	public void add(ClassName className) {
		add(className, null);
	}

	/**
	 * Remove the annotation with the specified {@link ClassName}.
	 * @param className the class name of the annotation
	 * @return {@code true} if such an annotation exists, {@code false} otherwise
	 */
	public boolean remove(ClassName className) {
		return this.annotations.remove(className) != null;
	}

	public AnnotationContainer deepCopy() {
		Map<ClassName, Builder> copy = new LinkedHashMap<>();
		this.annotations.forEach((className, builder) -> copy.put(className, new Builder(builder)));
		return new AnnotationContainer(copy);
	}

}
