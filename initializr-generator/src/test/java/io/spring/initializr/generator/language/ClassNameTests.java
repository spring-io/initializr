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

import java.util.stream.Stream;

import com.example.Example;
import com.example.Example.Inner;
import com.example.Example.Inner.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link ClassName}.
 *
 * @author Stephane Nicoll
 */
class ClassNameTests {

	@Test
	void classNameWithTopLevelClassName() {
		classNameWithTopLevelClass(ClassName.of("com.example.Example"));
	}

	@Test
	void classNameWithTopLevelClass() {
		classNameWithTopLevelClass(ClassName.of(Example.class));
	}

	private void classNameWithTopLevelClass(ClassName className) {
		assertThat(className.getName()).isEqualTo("com.example.Example");
		assertThat(className.getCanonicalName()).isEqualTo("com.example.Example");
		assertThat(className.getPackageName()).isEqualTo("com.example");
		assertThat(className.getSimpleName()).isEqualTo("Example");
		assertThat(className.getEnclosingType()).isNull();
	}

	@Test
	void classNameWithInnerClassName() {
		classNameWithInnerClass(ClassName.of("com.example.Example$Inner"));
	}

	@Test
	void classNameWithInnerClass() {
		classNameWithInnerClass(ClassName.of(Inner.class));
	}

	private void classNameWithInnerClass(ClassName className) {
		assertThat(className.getName()).isEqualTo("com.example.Example$Inner");
		assertThat(className.getCanonicalName()).isEqualTo("com.example.Example.Inner");
		assertThat(className.getPackageName()).isEqualTo("com.example");
		assertThat(className.getSimpleName()).isEqualTo("Inner");
		assertThat(className.getEnclosingType()).satisfies((enclosingType) -> {
			assertThat(enclosingType.getCanonicalName()).isEqualTo("com.example.Example");
			assertThat(enclosingType.getPackageName()).isEqualTo("com.example");
			assertThat(enclosingType.getSimpleName()).isEqualTo("Example");
			assertThat(enclosingType.getEnclosingType()).isNull();
		});
	}

	@Test
	void classNameWithNestedInnerClassName() {
		classNameWithNestedInnerClass(ClassName.of("com.example.Example$Inner$Nested"));
	}

	@Test
	void classNameWithNestedInnerClass() {
		classNameWithNestedInnerClass(ClassName.of(Nested.class));
	}

	private void classNameWithNestedInnerClass(ClassName className) {
		assertThat(className.getName()).isEqualTo("com.example.Example$Inner$Nested");
		assertThat(className.getCanonicalName()).isEqualTo("com.example.Example.Inner.Nested");
		assertThat(className.getPackageName()).isEqualTo("com.example");
		assertThat(className.getSimpleName()).isEqualTo("Nested");
		assertThat(className.getEnclosingType()).satisfies((enclosingType) -> {
			assertThat(enclosingType.getCanonicalName()).isEqualTo("com.example.Example.Inner");
			assertThat(enclosingType.getPackageName()).isEqualTo("com.example");
			assertThat(enclosingType.getSimpleName()).isEqualTo("Inner");
			assertThat(enclosingType.getEnclosingType()).satisfies((parentEnclosingType) -> {
				assertThat(parentEnclosingType.getCanonicalName()).isEqualTo("com.example.Example");
				assertThat(parentEnclosingType.getPackageName()).isEqualTo("com.example");
				assertThat(parentEnclosingType.getSimpleName()).isEqualTo("Example");
				assertThat(parentEnclosingType.getEnclosingType()).isNull();
			});
		});
	}

	@ParameterizedTest
	@MethodSource("primitivesAndPrimitivesArray")
	void primitivesAreHandledProperly(ClassName className, String expectedName) {
		assertThat(className.getName()).isEqualTo(expectedName);
		assertThat(className.getCanonicalName()).isEqualTo(expectedName);
		assertThat(className.getPackageName()).isEqualTo("java.lang");
	}

	static Stream<Arguments> primitivesAndPrimitivesArray() {
		return Stream.of(Arguments.of(ClassName.of("boolean"), "boolean"), Arguments.of(ClassName.of("byte"), "byte"),
				Arguments.of(ClassName.of("short"), "short"), Arguments.of(ClassName.of("int"), "int"),
				Arguments.of(ClassName.of("long"), "long"), Arguments.of(ClassName.of("char"), "char"),
				Arguments.of(ClassName.of("float"), "float"), Arguments.of(ClassName.of("double"), "double"),
				Arguments.of(ClassName.of("boolean[]"), "boolean[]"), Arguments.of(ClassName.of("byte[]"), "byte[]"),
				Arguments.of(ClassName.of("short[]"), "short[]"), Arguments.of(ClassName.of("int[]"), "int[]"),
				Arguments.of(ClassName.of("long[]"), "long[]"), Arguments.of(ClassName.of("char[]"), "char[]"),
				Arguments.of(ClassName.of("float[]"), "float[]"), Arguments.of(ClassName.of("double[]"), "double[]"));
	}

	@ParameterizedTest
	@MethodSource("arrays")
	void arraysHaveSuitableReflectionTargetName(ClassName typeReference, String expectedName) {
		assertThat(typeReference.getName()).isEqualTo(expectedName);
	}

	static Stream<Arguments> arrays() {
		return Stream.of(Arguments.of(ClassName.of("java.lang.Object[]"), "java.lang.Object[]"),
				Arguments.of(ClassName.of("java.lang.Integer[]"), "java.lang.Integer[]"),
				Arguments.of(ClassName.of("com.example.Test[]"), "com.example.Test[]"));
	}

	@Test
	void classNameInRootPackage() {
		ClassName type = ClassName.of("MyRootClass");
		assertThat(type.getCanonicalName()).isEqualTo("MyRootClass");
		assertThat(type.getPackageName()).isEmpty();
	}

	@ParameterizedTest(name = "{0}")
	@ValueSource(strings = { "com.example.Tes(t", "com.example..Test" })
	void classNameWithInvalidClassName(String invalidClassName) {
		assertThatIllegalStateException().isThrownBy(() -> ClassName.of(invalidClassName))
			.withMessageContaining("Invalid class name");
	}

	@Test
	void equalsWithIdenticalNameIsTrue() {
		assertThat(ClassName.of(String.class)).isEqualTo(ClassName.of("java.lang.String"));
	}

	@Test
	void equalsWithNonClassNameIsFalse() {
		assertThat(ClassName.of(String.class)).isNotEqualTo("java.lang.String");
	}

	@Test
	void toStringUsesCanonicalName() {
		assertThat(ClassName.of(String.class)).hasToString("java.lang.String");
	}

}
