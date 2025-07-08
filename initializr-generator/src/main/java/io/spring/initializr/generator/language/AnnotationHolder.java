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

import java.util.function.Consumer;
import java.util.stream.Stream;

import io.spring.initializr.generator.language.Annotation.Builder;

/**
 * A holder for {@linkplain Annotation annotations} defined on an annotated element.
 *
 * @author Stephane Nicoll
 * @author Sijun Yang
 */
public interface AnnotationHolder {

	/**
	 * Specify if this holder is empty.
	 * @return {@code true} if no annotation is registered
	 */
	boolean isEmpty();

	/**
	 * Specify if this holder has an annotation with the specified {@link ClassName}.
	 * @param className the class name of an annotation
	 * @return {@code true} if the annotation with the specified class name exists
	 */
	boolean has(ClassName className);

	/**
	 * Return the {@link Annotation annotations}.
	 * @return the annotations
	 */
	Stream<Annotation> values();

	/**
	 * Add a single {@link Annotation} with the specified class name and {@link Consumer}
	 * to customize it. If the annotation has already been added, the consumer can be used
	 * to further tune attributes
	 * @param className the class name of an annotation
	 * @param annotation a {@link Consumer} to customize the {@link Annotation}
	 */
	void add(ClassName className, Consumer<Builder> annotation);

	/**
	 * Add a single {@link Annotation} with the specified class name. Does nothing If the
	 * annotation has already been added.
	 * @param className the class name of an annotation
	 */
	void add(ClassName className);

	/**
	 * Remove the annotation with the specified {@link ClassName}.
	 * @param className the class name of the annotation
	 * @return {@code true} if such an annotation exists, {@code false} otherwise
	 */
	boolean remove(ClassName className);

	/**
	 * Create a deep copy of this annotation holder.
	 * @return a new annotation holder with the same annotations
	 */
	AnnotationHolder deepCopy();

}
