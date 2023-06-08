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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AnnotationContainer}.
 *
 * @author Stephane Nicoll
 */
class AnnotationContainerTests {

	private static final ClassName TEST_CLASS_NAME = ClassName.of("com.example.Test");

	private static final ClassName NESTED_CLASS_NAME = ClassName.of("com.example.Nested");

	@Test
	void isEmptyWithEmptyContainer() {
		AnnotationContainer container = new AnnotationContainer();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void isEmptyWithAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.add(TEST_CLASS_NAME, (annotation) -> annotation.add("value", "test"));
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void hasWithMatchingAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.add(TEST_CLASS_NAME, (annotation) -> annotation.add("value", "test"));
		assertThat(container.has(TEST_CLASS_NAME)).isTrue();
	}

	@Test
	void hasWithNonMatchingAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.add(TEST_CLASS_NAME, (annotation) -> annotation.add("value", "test"));
		assertThat(container.has(ClassName.of("com.example.Another"))).isFalse();
	}

	@Test
	void valuesWithSimpleAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.add(TEST_CLASS_NAME, (annotation) -> annotation.add("value", "test"));
		assertThat(container.values()).singleElement().satisfies((annotation) -> {
			assertThat(annotation.getClassName()).isEqualTo(TEST_CLASS_NAME);
			assertThat(annotation.getImports()).containsOnly("com.example.Test");
			assertThat(annotation.getAttributes()).singleElement().satisfies((attribute) -> {
				assertThat(attribute.getName()).isEqualTo("value");
				assertThat(attribute.getValues()).containsExactly("test");
			});
		});
	}

	@Test
	void addAnnotationSeveralTimeReuseConfiguration() {
		AnnotationContainer container = new AnnotationContainer();
		container.add(TEST_CLASS_NAME, (annotation) -> annotation.add("value", "test"));
		container.add(TEST_CLASS_NAME, (annotation) -> annotation.add("value", "another"));
		assertThat(container.values()).singleElement().satisfies((annotation) -> {
			assertThat(annotation.getClassName()).isEqualTo(TEST_CLASS_NAME);
			assertThat(annotation.getImports()).containsOnly("com.example.Test");
			assertThat(annotation.getAttributes()).singleElement().satisfies((attribute) -> {
				assertThat(attribute.getName()).isEqualTo("value");
				assertThat(attribute.getValues()).containsExactly("test", "another");
			});
		});
	}

	@Test
	void addAnnotationSeveralTimeCanReplaceAttribute() {
		AnnotationContainer container = new AnnotationContainer();
		container.add(TEST_CLASS_NAME,
				(annotation) -> annotation.add("value", Annotation.of(NESTED_CLASS_NAME).add("counter", 42).build()));
		container.add(TEST_CLASS_NAME,
				(annotation) -> annotation.set("value", Annotation.of(NESTED_CLASS_NAME).add("counter", 24).build()));
		assertThat(container.values()).singleElement().satisfies((annotation) -> {
			assertThat(annotation.getClassName()).isEqualTo(TEST_CLASS_NAME);
			assertThat(annotation.getImports()).containsOnly("com.example.Test", "com.example.Nested");
			assertThat(annotation.getAttributes()).singleElement().satisfies((attribute) -> {
				assertThat(attribute.getName()).isEqualTo("value");
				assertThat(attribute.getValues()).singleElement().isInstanceOfSatisfying(Annotation.class, (nested) -> {
					assertThat(nested.getClassName()).isEqualTo(NESTED_CLASS_NAME);
					assertThat(nested.getAttributes()).singleElement().satisfies((nestedAttribute) -> {
						assertThat(nestedAttribute.getName()).isEqualTo("counter");
						assertThat(nestedAttribute.getValues()).containsExactly(24);
					});
				});
			});
		});
	}

	@Test
	void removeWithMatchingAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.add(TEST_CLASS_NAME, (annotation) -> annotation.add("value", "test"));
		assertThat(container.remove(TEST_CLASS_NAME)).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeWithNonMatchingAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.add(TEST_CLASS_NAME, (annotation) -> annotation.add("value", "test"));
		assertThat(container.remove(ClassName.of("com.example.Another"))).isFalse();
		assertThat(container.isEmpty()).isFalse();
	}

}
