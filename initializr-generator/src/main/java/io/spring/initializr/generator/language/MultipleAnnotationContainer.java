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

import io.spring.initializr.generator.language.Annotation.Builder;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * An {@link AnnotationHolder} implementation that can hold multiple annotations per type.
 *
 * @author Sijun Yang
 */
public class MultipleAnnotationContainer implements AnnotationHolder {

    private final Map<ClassName, List<Builder>> annotations;

    public MultipleAnnotationContainer() {
        this(new LinkedHashMap<>());
    }

    private MultipleAnnotationContainer(Map<ClassName, List<Builder>> annotations) {
        this.annotations = annotations;
    }

    @Override
    public boolean isEmpty() {
        return this.annotations.isEmpty() ||
                this.annotations.values().stream().allMatch(List::isEmpty);
    }

    @Override
    public boolean has(ClassName className) {
        List<Builder> builders = this.annotations.get(className);
        return builders != null && !builders.isEmpty();
    }

    @Override
    public Stream<Annotation> values() {
        return this.annotations.values().stream()
                .flatMap(List::stream)
                .map(Builder::build);
    }

    /**
     * Add operation is not supported for {@link MultipleAnnotationContainer}.
     * Use {@link #addToList(ClassName, Consumer)} instead to explicitly add to the list.
     * @throws UnsupportedOperationException always
     */
    @Override
    public void add(ClassName className, Consumer<Builder> annotation) {
        throw new UnsupportedOperationException(
                "Add operation with potential overwrite is not supported for MultipleAnnotationContainer. " +
                        "Use addToList() to explicitly add annotations to the list.");
    }

    /**
     * Add operation is not supported for {@link MultipleAnnotationContainer}.
     * Use {@link #addToList(ClassName)} instead to explicitly add to the list.
     * @throws UnsupportedOperationException always
     */
    @Override
    public void add(ClassName className) {
        throw new UnsupportedOperationException(
                "Add operation with potential overwrite is not supported for MultipleAnnotationContainer. " +
                        "Use addToList() to explicitly add annotations to the list.");
    }

    /**
     * Add an annotation to the list of annotations with the specified class name.
     * Always adds a new annotation.
     * @param className the class name of an annotation
     * @param annotation a {@link Consumer} to customize the {@link Annotation}
     */
    public void addToList(ClassName className, Consumer<Builder> annotation) {
        List<Builder> builders = this.annotations.computeIfAbsent(className, (key) -> new ArrayList<>());
        Builder builder = new Builder(className);
        if (annotation != null) {
            annotation.accept(builder);
        }
        builders.add(builder);
    }

    /**
     * Add an annotation to the list of annotations with the specified class name.
     * Always adds a new annotation.
     * @param className the class name of an annotation
     */
    public void addToList(ClassName className) {
        addToList(className, null);
    }

    /**
     * Return all annotations with the specified class name.
     * @param className the class name of an annotation
     * @return a stream of all annotations with the specified class name
     */
    public Stream<Annotation> valuesOf(ClassName className) {
        List<Builder> builders = this.annotations.get(className);
        if (builders == null || builders.isEmpty()) {
            return Stream.empty();
        }
        return builders.stream().map(Builder::build);
    }

    /**
     * Return the number of annotations with the specified class name.
     * @param className the class name of an annotation
     * @return the count of annotations with the specified class name
     */
    public int countOf(ClassName className) {
        List<Builder> builders = this.annotations.get(className);
        return builders != null ? builders.size() : 0;
    }

    /**
     * Remove all annotations with the specified class name.
     * @param className the class name of the annotation
     * @return the number of annotations that were removed
     */
    public int removeAll(ClassName className) {
        List<Builder> builders = this.annotations.remove(className);
        return builders != null ? builders.size() : 0;
    }

    @Override
    public boolean remove(ClassName className) {
        int removedCount = removeAll(className);
        return removedCount > 0;
    }

    /**
     * Check if this container has multiple annotations with the specified class name.
     * @param className the class name of an annotation
     * @return {@code true} if there are multiple annotations with the specified class name
     */
    public boolean hasMultiple(ClassName className) {
        List<Builder> builders = this.annotations.get(className);
        return builders != null && builders.size() > 1;
    }

    @Override
    public MultipleAnnotationContainer deepCopy() {
        Map<ClassName, List<Builder>> copy = new LinkedHashMap<>();
        this.annotations.forEach((className, builders) -> {
            List<Builder> buildersCopy = new ArrayList<>();
            builders.forEach(builder -> buildersCopy.add(new Builder(builder)));
            copy.put(className, buildersCopy);
        });
        return new MultipleAnnotationContainer(copy);
    }

}
