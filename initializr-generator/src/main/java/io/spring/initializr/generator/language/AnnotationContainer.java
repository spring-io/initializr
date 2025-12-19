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

package io.spring.initializr.generator.language;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.spring.initializr.generator.language.Annotation.Builder;
import org.jspecify.annotations.Nullable;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * A container for annotations defined on an annotated element.
 * <p>
 * Supports both single and repeatable annotations. Single annotations can be customized
 * even after they have been added.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public class AnnotationContainer {

	private final Map<ClassName, Builder> singleAnnotations;

	private final MultiValueMap<ClassName, Builder> repeatableAnnotations;

	public AnnotationContainer() {
		this(new LinkedHashMap<>(), new LinkedMultiValueMap<>());
	}

	private AnnotationContainer(Map<ClassName, Builder> singleAnnotations,
			MultiValueMap<ClassName, Builder> repeatableAnnotations) {
		this.singleAnnotations = singleAnnotations;
		this.repeatableAnnotations = repeatableAnnotations;
	}

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no annotation is registered
	 */
	public boolean isEmpty() {
		return this.singleAnnotations.isEmpty() && this.repeatableAnnotations.isEmpty();
	}

	/**
	 * Specify if this container has an annotation with the specified class name.
	 * Considers both single and repeatable annotations.
	 * @param className the class name of an annotation
	 * @return {@code true} if the annotation with the specified class name exists
	 */
	public boolean has(ClassName className) {
		return this.singleAnnotations.containsKey(className) || this.repeatableAnnotations.containsKey(className);
	}

	/**
	 * Whether this container has a single annotation with the specified class name.
	 * @param className the class name of an annotation
	 * @return whether this container has the annotation
	 */
	public boolean hasSingle(ClassName className) {
		return this.singleAnnotations.containsKey(className);
	}

	/**
	 * Whether this container has repeatable annotations with the specified class name.
	 * @param className the class name of an annotation
	 * @return whether this container has the annotation
	 */
	public boolean hasRepeatable(ClassName className) {
		return this.repeatableAnnotations.containsKey(className);
	}

	/**
	 * Return the annotations. Returns both single and repeatable annotations.
	 * @return the annotations
	 */
	public Stream<Annotation> values() {
		return Stream
			.concat(this.singleAnnotations.values().stream(),
					this.repeatableAnnotations.values().stream().flatMap(Collection::stream))
			.map(Builder::build);
	}

	/**
	 * Add a single annotation with the specified class name. Does nothing If the
	 * annotation has already been added.
	 * @param className the class name of an annotation
	 * @deprecated in favor of {@link #addSingle(ClassName)} and
	 * {@link #addRepeatable(ClassName)}
	 */
	@Deprecated(forRemoval = true)
	public void add(ClassName className) {
		add(className, null);
	}

	/**
	 * Add a single {@link Annotation} with the specified class name and {@link Consumer}
	 * to customize it. If the annotation has already been added, the consumer can be used
	 * to further tune attributes.
	 * @param className the class name of an annotation
	 * @param annotation a {@link Consumer} to customize the {@link Annotation}
	 * @deprecated in favor of {@link #addSingle(ClassName, Consumer)} and
	 * {@link #addRepeatable(ClassName)}
	 */
	@Deprecated(forRemoval = true)
	public void add(ClassName className, @Nullable Consumer<Builder> annotation) {
		if (hasRepeatable(className)) {
			throw new IllegalArgumentException(
					"%s has already been used for repeatable annotations".formatted(className));
		}
		Builder builder = this.singleAnnotations.computeIfAbsent(className, (key) -> new Builder(className));
		if (annotation != null) {
			annotation.accept(builder);
		}
	}

	/**
	 * Add a single annotation.
	 * @param className the class name of an annotation
	 * @return whether the annotation has been added
	 * @throws IllegalStateException if the annotation has already been used for
	 * repeatable annotations
	 */
	public boolean addSingle(ClassName className) {
		return addSingle(className, null);
	}

	/**
	 * Add a single annotation with the specified class name. If the annotation already
	 * exists, this method does nothing.
	 * @param className the class name of an annotation
	 * @param annotation a {@link Consumer} to customize the annotation
	 * @return whether the annotation has been added
	 * @throws IllegalStateException if the annotation has already been used for
	 * repeatable annotations
	 */
	public boolean addSingle(ClassName className, @Nullable Consumer<Builder> annotation) {
		if (hasSingle(className)) {
			return false;
		}
		if (hasRepeatable(className)) {
			throw new IllegalStateException("%s has already been used for repeatable annotations".formatted(className));
		}
		Builder builder = new Builder(className);
		if (annotation != null) {
			annotation.accept(builder);
		}
		this.singleAnnotations.put(className, builder);
		return true;
	}

	/**
	 * Customize a single annotation if it exists. This method does nothing if the
	 * annotation doesn't exist.
	 * @param className the class name of an annotation
	 * @param customizer the customizer for the annotation
	 */
	public void customizeSingle(ClassName className, Consumer<Builder> customizer) {
		Builder builder = this.singleAnnotations.get(className);
		if (builder != null) {
			customizer.accept(builder);
		}
	}

	/**
	 * Add a repeatable annotation.
	 * @param className the class name of an annotation
	 * @throws IllegalStateException if the annotation has already been added as a single
	 * annotation
	 */
	public void addRepeatable(ClassName className) {
		addRepeatable(className, null);
	}

	/**
	 * Add a repeatable annotation.
	 * @param className the class name of an annotation
	 * @param annotation a {@link Consumer} to customize the annotation
	 * @throws IllegalStateException if the annotation has already been added as a single
	 * annotation
	 */
	public void addRepeatable(ClassName className, @Nullable Consumer<Builder> annotation) {
		if (hasSingle(className)) {
			throw new IllegalStateException("%s has already been added as a single annotation".formatted(className));
		}
		Builder builder = new Builder(className);
		if (annotation != null) {
			annotation.accept(builder);
		}
		this.repeatableAnnotations.add(className, builder);
	}

	/**
	 * Remove the annotation with the specified classname from either the single
	 * annotation or the repeatable annotations.
	 * @param className the class name of the annotation
	 * @return {@code true} if such an annotation exists, {@code false} otherwise
	 */
	public boolean remove(ClassName className) {
		return this.singleAnnotations.remove(className) != null || this.repeatableAnnotations.remove(className) != null;
	}

	/**
	 * Remove a single with the specified classname.
	 * @param className the class name of an annotation
	 * @return whether the annotation has been removed
	 */
	public boolean removeSingle(ClassName className) {
		return this.singleAnnotations.remove(className) != null;
	}

	/**
	 * Remove all repeatable annotations with the specified classname.
	 * @param className the class name of an annotation
	 * @return whether any annotation has been removed
	 */
	public boolean removeAllRepeatable(ClassName className) {
		return this.repeatableAnnotations.remove(className) != null;
	}

	public AnnotationContainer deepCopy() {
		Map<ClassName, Builder> singleAnnotations = new LinkedHashMap<>();
		this.singleAnnotations.forEach((className, builder) -> singleAnnotations.put(className, new Builder(builder)));
		MultiValueMap<ClassName, Builder> repeatableAnnotations = new LinkedMultiValueMap<>();
		this.repeatableAnnotations.forEach((className, builders) -> builders
			.forEach((builder) -> repeatableAnnotations.add(className, new Builder(builder))));
		return new AnnotationContainer(singleAnnotations, repeatableAnnotations);
	}

}
