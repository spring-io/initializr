/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language;

import java.io.StringWriter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Stream;

import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.language.Annotation.AttributeKind;
import io.spring.initializr.generator.language.CodeBlock.FormattingOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link Annotation}.
 *
 * @author Stephane Nicoll
 */
class AnnotationTests {

	private static final FormattingOptions BRACKET_FORMATTING_OPTIONS = new FormattingOptions() {

		@Override
		public String statementSeparator() {
			return "";
		}

		@Override
		public CodeBlock arrayOf(CodeBlock... values) {
			return CodeBlock.of("[$L]", CodeBlock.join(Arrays.asList(values), ", "));
		}

		@Override
		public CodeBlock classReference(ClassName className) {
			return CodeBlock.of("$T::class", className);
		}

	};

	@Test
	void annotationWithInvalidParameterValue() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> Annotation.of(ClassName.of("com.example.Test")).set("test", new StringWriter()))
			.withMessage(
					"Incompatible type. Found: 'java.io.StringWriter', required: primitive, String, Class, an Enum, an Annotation, or a CodeBlock");
	}

	@Test
	void annotationWithMixedParameterValues() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> Annotation.of(ClassName.of("com.example.Test")).set("test", "value", true))
			.withMessage("Parameter value must not have mixed types, got [STRING, PRIMITIVE]");
	}

	@Test
	void annotationWithAmendedValueAndTypeMismatch() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> Annotation.of(ClassName.of("com.example.Test")).set("test", "value").add("test", true))
			.withMessage("Incompatible type. 'STRING' is not compatible with 'PRIMITIVE'");
	}

	@Test
	void annotationWithNoAttribute() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).build();
		assertThat(write(test)).isEqualTo("@Test");
		assertThat(test.getImports()).containsOnly("com.example.Test");
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void annotationWithPrimitives(Object parameter, String expectedCode) {
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).set("test", parameter).build();
		assertThat(write(test)).isEqualTo("@Test(test = " + expectedCode + ")");
		assertThat(test.getImports()).containsOnly("com.example.Test");
	}

	static Stream<Arguments> parameters() {
		return Stream.of(Arguments.arguments(1, "1"), Arguments.arguments(0x4f, "79"),
				Arguments.arguments((short) 4, "4"), Arguments.arguments(500L, "500"),
				Arguments.arguments((float) 3.14, "3.14"), Arguments.arguments(3.156, "3.156"),
				Arguments.arguments(true, "true"), Arguments.arguments('t', "'t'"));
	}

	@Test
	void annotationWithString() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).set("test", "value").build();
		assertThat(write(test)).isEqualTo("@Test(test = \"value\")");
		assertThat(test.getImports()).containsOnly("com.example.Test");
	}

	@Test
	void annotationWithClass() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).set("test", Test.class).build();
		assertThat(write(test)).isEqualTo("@Test(test = Test.class)");
		assertThat(test.getImports()).containsOnly("com.example.Test", Test.class.getName());
	}

	@Test
	void annotationWithClassName() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).set("test", ClassName.of(Test.class)).build();
		assertThat(write(test)).isEqualTo("@Test(test = Test.class)");
		assertThat(test.getImports()).containsOnly("com.example.Test", Test.class.getName());
	}

	@Test
	void annotationWithTypeReferenceInvokeConfiguredFormattingOptions() {
		ClassName typeReference = ClassName.of("com.example.Another");
		FormattingOptions options = mock(FormattingOptions.class);
		given(options.classReference(typeReference)).willReturn(CodeBlock.of("$T::class", typeReference));
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).set("test", typeReference).build();
		assertThat(write(test, options)).isEqualTo("@Test(test = Another::class)");
		verify(options).classReference(typeReference);
		assertThat(test.getImports()).containsOnly("com.example.Test", typeReference.getName());
	}

	@Test
	void annotationWithEnum() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).set("test", ChronoUnit.CENTURIES).build();
		assertThat(write(test)).isEqualTo("@Test(test = ChronoUnit.CENTURIES)");
		assertThat(test.getImports()).containsOnly("com.example.Test", ChronoUnit.class.getName());
	}

	@Test
	void annotationWithEnumConstantWithClassBody() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test"))
			.set("test", SpecializedEnum.SPECIALIZED)
			.build();
		assertThat(write(test)).isEqualTo("@Test(test = SpecializedEnum.SPECIALIZED)");
		assertThat(test.getImports()).containsOnly("com.example.Test", SpecializedEnum.class.getName());
	}

	@Test
	void annotationWithEnumCodeBlock() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test"))
			.set("test", CodeBlock.of("$T.CENTURIES", ChronoUnit.class))
			.build();
		assertThat(write(test)).isEqualTo("@Test(test = ChronoUnit.CENTURIES)");
		assertThat(test.getImports()).containsOnly("com.example.Test", ChronoUnit.class.getName());
	}

	@Test
	void annotationWithNestedAnnotation() {
		Annotation nested = Annotation.of(ClassName.of("com.example.Nested")).set("counter", 42).build();
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).set("test", nested).build();
		assertThat(write(test)).isEqualTo("@Test(test = @Nested(counter = 42))");
		assertThat(test.getImports()).containsOnly("com.example.Test", "com.example.Nested");
	}

	@Test
	void annotationWithNestedAnnotationInvokeConfiguredFormattingOptions() {
		ClassName nestedClassName = ClassName.of("com.example.Nested");
		FormattingOptions options = mock(FormattingOptions.class);
		given(options.nestedAnnotation(nestedClassName)).willReturn(CodeBlock.of("$T", nestedClassName));
		Annotation nested = Annotation.of(nestedClassName).set("counter", 42).build();
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).set("test", nested).build();
		assertThat(write(test, options)).isEqualTo("@Test(test = Nested(counter = 42))");
		verify(options).nestedAnnotation(nestedClassName);
	}

	@Test
	void annotationWithOnlyValueUsesShortcut() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).set("value", "test").build();
		assertThat(write(test)).isEqualTo("@Test(\"test\")");
		assertThat(test.getImports()).containsOnly("com.example.Test");
	}

	@Test
	void annotationWithSeveralParameters() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test"))
			.set("enabled", false)
			.set("counter", 42)
			.build();
		assertThat(write(test)).isEqualTo("@Test(enabled = false, counter = 42)");
	}

	@Test
	void annotationWithParameterArray() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).set("counters", 2, 4, 8, 10).build();
		assertThat(write(test)).isEqualTo("@Test(counters = { 2, 4, 8, 10 })");
	}

	@Test
	void annotationWithParameterArrayAsValueUsesShortcut() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).set("value", 2, 4, 8, 10).build();
		assertThat(write(test)).isEqualTo("@Test({ 2, 4, 8, 10 })");
	}

	@Test
	void annotationWithParameterClassAndCodeBlock() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test"))
			.set("types", StringWriter.class, CodeBlock.of("$T.class", "com.example.io.AnotherWriter"))
			.build();
		assertThat(write(test)).isEqualTo("@Test(types = { StringWriter.class, AnotherWriter.class })");
		assertThat(test.getImports()).containsOnly("com.example.Test", StringWriter.class.getName(),
				"com.example.io.AnotherWriter");
	}

	@Test
	void annotationWithAmendedValues() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test"))
			.add("types", StringWriter.class)
			.add("types", CodeBlock.of("$T.class", "com.example.io.AnotherWriter"))
			.build();
		assertThat(write(test)).isEqualTo("@Test(types = { StringWriter.class, AnotherWriter.class })");
		assertThat(test.getImports()).containsOnly("com.example.Test", StringWriter.class.getName(),
				"com.example.io.AnotherWriter");
	}

	@Test
	void annotationWithDefaultAttributeKind() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).set("counter", 42).build();
		assertThat(test.getAttributes()).singleElement()
			.satisfies((attribute) -> assertThat(attribute.getKind()).isEqualTo(AttributeKind.INFERRED));
	}

	@Test
	void annotationWithArrayAttributeKind() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test"))
			.set("counters", AttributeKind.ARRAY, 42)
			.build();
		assertThat(test.getAttributes()).singleElement()
			.satisfies((attribute) -> assertThat(attribute.getKind()).isEqualTo(AttributeKind.ARRAY));
		assertThat(write(test, BRACKET_FORMATTING_OPTIONS)).isEqualTo("@Test(counters = [42])");
	}

	@Test
	void annotationWithAmendedValuesKeepsArrayAttributeKind() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test"))
			.add("counters", AttributeKind.ARRAY, 42)
			.add("counters")
			.build();
		assertThat(write(test, BRACKET_FORMATTING_OPTIONS)).isEqualTo("@Test(counters = [42])");
	}

	@Test
	void annotationWithAmendedValuesUpgradesToArrayAttributeKind() {
		Annotation test = Annotation.of(ClassName.of("com.example.Test"))
			.add("counters", 42)
			.add("counters", AttributeKind.ARRAY)
			.build();
		assertThat(write(test, BRACKET_FORMATTING_OPTIONS)).isEqualTo("@Test(counters = [42])");
	}

	@Test
	void annotationWithNestedArrayAttributeKind() {
		Annotation nested = Annotation.of(ClassName.of("com.example.Nested"))
			.set("counters", AttributeKind.ARRAY, 42)
			.build();
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).set("test", nested).build();
		assertThat(write(test, BRACKET_FORMATTING_OPTIONS)).isEqualTo("@Test(test = @Nested(counters = [42]))");
	}

	@Test
	void annotationFromKeepsArrayAttributeKind() {
		Annotation source = Annotation.of(ClassName.of("com.example.Test"))
			.set("counters", AttributeKind.ARRAY, 42)
			.build();
		Annotation test = Annotation.of(ClassName.of("com.example.Test")).from(source).build();
		assertThat(write(test, BRACKET_FORMATTING_OPTIONS)).isEqualTo("@Test(counters = [42])");
	}

	private String write(Annotation annotation) {
		return write(annotation, CodeBlock.JAVA_FORMATTING_OPTIONS);
	}

	private String write(Annotation annotation, FormattingOptions formattingOptions) {
		StringWriter out = new StringWriter();
		IndentingWriter writer = new IndentingWriter(out, new SimpleIndentStrategy("\t"));
		annotation.write(writer, formattingOptions);
		return out.toString();
	}

	private enum SpecializedEnum {

		SPECIALIZED {
		}

	}

}
