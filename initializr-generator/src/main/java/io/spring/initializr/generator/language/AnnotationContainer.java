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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.spring.initializr.generator.language.Annotation.Builder;

/**
 * An {@link AnnotationHolder} implementation that holds at most one annotation per type.
 *
 * @author Stephane Nicoll
 * @author Sijun Yang
 */
public class AnnotationContainer implements AnnotationHolder {

	private final Map<ClassName, Builder> annotations;

	public AnnotationContainer() {
		this(new LinkedHashMap<>());
	}

	private AnnotationContainer(Map<ClassName, Builder> annotations) {
		this.annotations = annotations;
	}

	@Override
	public boolean isEmpty() {
		return this.annotations.isEmpty();
	}

	@Override
	public boolean has(ClassName className) {
		return this.annotations.containsKey(className);
	}

	@Override
	public Stream<Annotation> values() {
		return this.annotations.values().stream().map(Builder::build);
	}

	@Override
	public void add(ClassName className, Consumer<Builder> annotation) {
		Builder builder = this.annotations.computeIfAbsent(className, (key) -> new Builder(className));
		if (annotation != null) {
			annotation.accept(builder);
		}
	}

	@Override
	public void add(ClassName className) {
		add(className, null);
	}

	@Override
	public boolean remove(ClassName className) {
		return this.annotations.remove(className) != null;
	}

	@Override
	public AnnotationContainer deepCopy() {
		Map<ClassName, Builder> copy = new LinkedHashMap<>();
		this.annotations.forEach((className, builder) -> copy.put(className, new Builder(builder)));
		return new AnnotationContainer(copy);
	}

}
