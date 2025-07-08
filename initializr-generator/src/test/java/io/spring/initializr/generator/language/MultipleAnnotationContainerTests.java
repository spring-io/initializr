package io.spring.initializr.generator.language;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link MultipleAnnotationContainer}.
 *
 * @author Sijun Yang
 */
class MultipleAnnotationContainerTests {

	private static final ClassName TEST_CLASS_NAME = ClassName.of("com.example.Test");

	private static final ClassName OTHER_CLASS_NAME = ClassName.of("com.example.Other");

	@Test
	void isEmptyWithEmptyContainer() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void isEmptyWithAnnotation() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		container.addToList(TEST_CLASS_NAME, builder -> builder.add("value", "test"));
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void hasWithMatchingAnnotation() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		container.addToList(TEST_CLASS_NAME, builder -> builder.add("value", "test"));
		assertThat(container.has(TEST_CLASS_NAME)).isTrue();
	}

	@Test
	void hasWithNonMatchingAnnotation() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		container.addToList(TEST_CLASS_NAME, builder -> builder.add("value", "test"));
		assertThat(container.has(OTHER_CLASS_NAME)).isFalse();
	}

	@Test
	void valuesShouldReturnAllAnnotations() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		container.addToList(TEST_CLASS_NAME, builder -> builder.add("value", "one"));
		container.addToList(TEST_CLASS_NAME, builder -> builder.add("value", "two"));
		List<Annotation> annotations = container.values().collect(Collectors.toList());
		assertThat(annotations).hasSize(2);
		assertThat(annotations).allMatch(annotation -> annotation.getClassName().equals(TEST_CLASS_NAME));
	}

	@Test
	void valuesOfShouldReturnMatchingAnnotationsOnly() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		container.addToList(TEST_CLASS_NAME, builder -> builder.add("value", "one"));
		container.addToList(OTHER_CLASS_NAME, builder -> builder.add("name", "other"));
		List<Annotation> annotations = container.valuesOf(TEST_CLASS_NAME).collect(Collectors.toList());
		assertThat(annotations).hasSize(1);
		assertThat(annotations.get(0).getAttributes()).singleElement().satisfies((attribute) -> {
			assertThat(attribute.getName()).isEqualTo("value");
			assertThat(attribute.getValues()).containsExactly("one");
		});
	}

	@Test
	void valuesOfShouldReturnEmptyStreamForNonExistingClassName() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		container.addToList(TEST_CLASS_NAME);
		assertThat(container.valuesOf(OTHER_CLASS_NAME)).isEmpty();
	}

	@Test
	void countOfShouldReturnCorrectCount() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		container.addToList(TEST_CLASS_NAME);
		container.addToList(TEST_CLASS_NAME);
		assertThat(container.countOf(TEST_CLASS_NAME)).isEqualTo(2);
		assertThat(container.countOf(OTHER_CLASS_NAME)).isZero();
	}

	@Test
	void removeAllShouldRemoveAllAnnotationsOfClassName() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		container.addToList(TEST_CLASS_NAME);
		container.addToList(TEST_CLASS_NAME);
		int removed = container.removeAll(TEST_CLASS_NAME);
		assertThat(removed).isEqualTo(2);
		assertThat(container.has(TEST_CLASS_NAME)).isFalse();
	}

	@Test
	void removeShouldReturnFalseIfAllAnnotationRemoved() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		container.addToList(TEST_CLASS_NAME);
		container.addToList(TEST_CLASS_NAME);
		assertThat(container.remove(TEST_CLASS_NAME)).isTrue();
		assertThat(container.has(TEST_CLASS_NAME)).isFalse();
	}

	@Test
	void removeShouldReturnFalseIfNoAnnotationRemoved() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		assertThat(container.remove(TEST_CLASS_NAME)).isFalse();
	}

	@Test
	void hasMultipleReturnsTrueForMultipleAnnotations() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		container.addToList(TEST_CLASS_NAME);
		container.addToList(TEST_CLASS_NAME);
		assertThat(container.hasMultiple(TEST_CLASS_NAME)).isTrue();
		assertThat(container.hasMultiple(OTHER_CLASS_NAME)).isFalse();
	}

	@Test
	void addUnsupportedMethodsThrowException() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		assertThatThrownBy(() -> container.add(TEST_CLASS_NAME)).isInstanceOf(UnsupportedOperationException.class);
		assertThatThrownBy(() -> container.add(TEST_CLASS_NAME, builder -> {
		})).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void deepCopyShouldCreateDistinctObjectReferences() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		container.addToList(TEST_CLASS_NAME, builder -> builder.add("value", "test"));

		MultipleAnnotationContainer copy = container.deepCopy();

		assertThat(copy).isNotSameAs(container);

		List<Annotation> originalAnnotations = container.valuesOf(TEST_CLASS_NAME).collect(Collectors.toList());
		List<Annotation> copiedAnnotations = copy.valuesOf(TEST_CLASS_NAME).collect(Collectors.toList());

		assertThat(copiedAnnotations).hasSize(originalAnnotations.size());
		for (int i = 0; i < originalAnnotations.size(); i++) {
			assertThat(copiedAnnotations.get(i)).isNotSameAs(originalAnnotations.get(i));
		}
	}

	@Test
	void deepCopyMutationShouldNotAffectOriginal() {
		MultipleAnnotationContainer container = new MultipleAnnotationContainer();
		container.addToList(TEST_CLASS_NAME, builder -> builder.add("value", "original"));

		MultipleAnnotationContainer copy = container.deepCopy();

		copy.addToList(TEST_CLASS_NAME, builder -> builder.add("value", "new"));
		copy.addToList(OTHER_CLASS_NAME, builder -> builder.add("other", "test"));

		assertThat(container.countOf(TEST_CLASS_NAME)).isEqualTo(1);
		assertThat(container.has(OTHER_CLASS_NAME)).isFalse();

		assertThat(copy.countOf(TEST_CLASS_NAME)).isEqualTo(2);
		assertThat(copy.has(OTHER_CLASS_NAME)).isTrue();
	}

}
