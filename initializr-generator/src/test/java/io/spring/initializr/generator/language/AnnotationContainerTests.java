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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link AnnotationContainer}.
 *
 * @author Stephane Nicoll
 * @author Sijun Yang
 */
class AnnotationContainerTests {

	private static final ClassName TEST_CLASS_NAME = ClassName.of("com.example.Test");

	private static final ClassName NESTED_CLASS_NAME = ClassName.of("com.example.Nested");

	private static final ClassName ANOTHER_CLASS_NAME = ClassName.of("com.example.Another");

	@Test
	void isEmptyWithEmptyContainer() {
		AnnotationContainer container = new AnnotationContainer();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	@SuppressWarnings("removal")
	void isEmptyWithAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.add(TEST_CLASS_NAME, (annotation) -> annotation.add("value", "test"));
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void isEmptyWithSingleAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME);
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void isEmptyWithRepeatableAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addRepeatable(TEST_CLASS_NAME);
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	@SuppressWarnings("removal")
	void hasWithMatchingAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.add(TEST_CLASS_NAME, (annotation) -> annotation.add("value", "test"));
		assertThat(container.has(TEST_CLASS_NAME)).isTrue();
	}

	@Test
	@SuppressWarnings("removal")
	void hasWithNonMatchingAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.add(TEST_CLASS_NAME, (annotation) -> annotation.add("value", "test"));
		assertThat(container.has(ClassName.of("com.example.Another"))).isFalse();
	}

	@Test
	void hasWithMatchingSingleAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME);
		assertThat(container.has(TEST_CLASS_NAME)).isTrue();
	}

	@Test
	void hasWithMatchingRepeatableAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addRepeatable(TEST_CLASS_NAME);
		assertThat(container.has(TEST_CLASS_NAME)).isTrue();
	}

	@Test
	void hasWithNonMatchingSingleAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME);
		assertThat(container.has(ANOTHER_CLASS_NAME)).isFalse();
	}

	@Test
	@SuppressWarnings("removal")
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
	void valuesWithSingleAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME, (builder) -> builder.add("value", "test"));
		assertThat(container.values()).singleElement().satisfies((annotation) -> {
			assertThat(annotation.getClassName()).isEqualTo(TEST_CLASS_NAME);
			assertThat(annotation.getAttributes()).singleElement().satisfies((attribute) -> {
				assertThat(attribute.getName()).isEqualTo("value");
				assertThat(attribute.getValues()).containsExactly("test");
			});
		});
	}

	@Test
	void valuesWithRepeatableAnnotations() {
		AnnotationContainer container = new AnnotationContainer();
		container.addRepeatable(TEST_CLASS_NAME, (builder) -> builder.add("value", "test1"));
		container.addRepeatable(TEST_CLASS_NAME, (builder) -> builder.add("value", "test2"));
		assertThat(container.values()).hasSize(2);
	}

	@Test
	void valuesWithMixedAnnotations() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME);
		container.addRepeatable(ANOTHER_CLASS_NAME);
		assertThat(container.values()).hasSize(2);
	}

	@Test
	@SuppressWarnings("removal")
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
	@SuppressWarnings("removal")
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
	@SuppressWarnings("removal")
	void removeWithMatchingAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.add(TEST_CLASS_NAME, (annotation) -> annotation.add("value", "test"));
		assertThat(container.remove(TEST_CLASS_NAME)).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	@SuppressWarnings("removal")
	void removeWithNonMatchingAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.add(TEST_CLASS_NAME, (annotation) -> annotation.add("value", "test"));
		assertThat(container.remove(ClassName.of("com.example.Another"))).isFalse();
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void removeWithSingleAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME);
		assertThat(container.remove(TEST_CLASS_NAME)).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeWithRepeatableAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addRepeatable(TEST_CLASS_NAME);
		assertThat(container.remove(TEST_CLASS_NAME)).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeWithNonMatchingSingleAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME);
		assertThat(container.remove(ANOTHER_CLASS_NAME)).isFalse();
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void removeWithNonMatchingRepeatableAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addRepeatable(TEST_CLASS_NAME);
		assertThat(container.remove(ANOTHER_CLASS_NAME)).isFalse();
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void hasSingleWithSingleAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME);
		assertThat(container.hasSingle(TEST_CLASS_NAME)).isTrue();
		assertThat(container.hasSingle(ANOTHER_CLASS_NAME)).isFalse();
	}

	@Test
	void hasSingleWithRepeatableAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addRepeatable(TEST_CLASS_NAME);
		assertThat(container.hasSingle(TEST_CLASS_NAME)).isFalse();
	}

	@Test
	void addSingle() {
		AnnotationContainer container = new AnnotationContainer();
		assertThat(container.addSingle(TEST_CLASS_NAME)).isTrue();
		assertThat(container.hasSingle(TEST_CLASS_NAME)).isTrue();
	}

	@Test
	void addSingleTwiceReturnsFalse() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME);
		assertThat(container.addSingle(TEST_CLASS_NAME)).isFalse();
	}

	@Test
	void addSingleWithBuilder() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME, (builder) -> builder.add("value", 123));
		assertThat(container.values()).singleElement().satisfies((annotation) -> {
			assertThat(annotation.getClassName()).isEqualTo(TEST_CLASS_NAME);
			assertThat(annotation.getAttributes()).singleElement().satisfies((attribute) -> {
				assertThat(attribute.getName()).isEqualTo("value");
				assertThat(attribute.getValues()).containsExactly(123);
			});
		});
	}

	@Test
	void customizeSingle() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME, (builder) -> builder.add("value", "test"));
		container.customizeSingle(TEST_CLASS_NAME, (builder) -> builder.add("value", "another"));
		assertThat(container.values()).singleElement()
			.satisfies((annotation) -> assertThat(annotation.getAttributes().get(0).getValues()).containsExactly("test",
					"another"));
	}

	@Test
	void customizeSingleCanReplaceAttribute() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME,
				(builder) -> builder.add("value", Annotation.of(NESTED_CLASS_NAME).add("counter", 42).build()));
		container.customizeSingle(TEST_CLASS_NAME,
				(builder) -> builder.set("value", Annotation.of(NESTED_CLASS_NAME).add("counter", 24).build()));
		assertThat(container.values()).singleElement().satisfies((annotation) -> {
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
	void customizeSingleOnNonMatchingAnnotationDoesNothing() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(NESTED_CLASS_NAME);
		container.customizeSingle(ANOTHER_CLASS_NAME, (builder) -> builder.add("value", "test"));
		assertThat(container.values()).singleElement()
			.satisfies((annotation) -> assertThat(annotation.getAttributes()).isEmpty());
	}

	@Test
	void removeSingle() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME);
		assertThat(container.removeSingle(TEST_CLASS_NAME)).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeSingleWithNonMatchingAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME);
		assertThat(container.removeSingle(ANOTHER_CLASS_NAME)).isFalse();
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void hasRepeatableWithSingleAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME);
		assertThat(container.hasRepeatable(TEST_CLASS_NAME)).isFalse();
	}

	@Test
	void hasRepeatableWithRepeatableAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addRepeatable(TEST_CLASS_NAME);
		assertThat(container.hasRepeatable(TEST_CLASS_NAME)).isTrue();
		assertThat(container.hasRepeatable(ANOTHER_CLASS_NAME)).isFalse();
	}

	@Test
	void addRepeatable() {
		AnnotationContainer container = new AnnotationContainer();
		container.addRepeatable(TEST_CLASS_NAME);
		assertThat(container.hasRepeatable(TEST_CLASS_NAME)).isTrue();
		assertThat(container.values()).hasSize(1);
	}

	@Test
	void addRepeatableTwice() {
		AnnotationContainer container = new AnnotationContainer();
		container.addRepeatable(TEST_CLASS_NAME);
		container.addRepeatable(TEST_CLASS_NAME);
		assertThat(container.hasRepeatable(TEST_CLASS_NAME)).isTrue();
		assertThat(container.values()).hasSize(2);
	}

	@Test
	void addRepeatableWithBuilder() {
		AnnotationContainer container = new AnnotationContainer();
		container.addRepeatable(TEST_CLASS_NAME, (builder) -> builder.add("value", "test1"));
		container.addRepeatable(TEST_CLASS_NAME, (builder) -> builder.add("value", "test2"));
		assertThat(container.values()).satisfiesExactly(
				(annotation) -> assertThat(annotation.getAttributes().get(0).getValues()).containsExactly("test1"),
				(annotation) -> assertThat(annotation.getAttributes().get(0).getValues()).containsExactly("test2"));
	}

	@Test
	void removeAllRepeatable() {
		AnnotationContainer container = new AnnotationContainer();
		container.addRepeatable(TEST_CLASS_NAME);
		container.addRepeatable(TEST_CLASS_NAME);
		assertThat(container.removeAllRepeatable(TEST_CLASS_NAME)).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeAllRepeatableWithNonMatchingAnnotation() {
		AnnotationContainer container = new AnnotationContainer();
		container.addRepeatable(TEST_CLASS_NAME);
		assertThat(container.removeAllRepeatable(ANOTHER_CLASS_NAME)).isFalse();
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void addSingleWhenAlreadyRepeatable() {
		AnnotationContainer container = new AnnotationContainer();
		container.addRepeatable(TEST_CLASS_NAME);
		assertThatIllegalStateException()
			.isThrownBy(() -> container.addSingle(TEST_CLASS_NAME, (builder) -> builder.add("value", "test")))
			.withMessageContaining("has already been used for repeatable annotations");
	}

	@Test
	void addRepeatableWhenAlreadySingle() {
		AnnotationContainer container = new AnnotationContainer();
		container.addSingle(TEST_CLASS_NAME);
		assertThatIllegalStateException()
			.isThrownBy(() -> container.addRepeatable(TEST_CLASS_NAME, (builder) -> builder.add("value", "test")))
			.withMessageContaining("has already been added as a single annotation");
	}

	@Test
	void deepCopy() {
		AnnotationContainer original = new AnnotationContainer();
		original.addSingle(TEST_CLASS_NAME, (builder) -> builder.add("value", "test"));
		original.addRepeatable(ANOTHER_CLASS_NAME, (builder) -> builder.add("value", "test"));
		AnnotationContainer copy = original.deepCopy();
		assertThat(copy).isNotSameAs(original);
		assertThat(copy.values()).hasSize(2);
		original.customizeSingle(TEST_CLASS_NAME, (builder) -> builder.add("value", "another"));
		original.addRepeatable(ANOTHER_CLASS_NAME);
		assertThat(copy.values()).hasSize(2);
		assertThat(copy.values()).satisfiesExactly(
				(annotation) -> assertThat(annotation.getAttributes().get(0).getValues()).containsExactly("test"),
				(annotation) -> assertThat(annotation.getAttributes().get(0).getValues()).containsExactly("test"));
	}

}
