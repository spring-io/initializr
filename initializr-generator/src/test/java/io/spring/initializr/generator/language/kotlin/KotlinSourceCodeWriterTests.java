/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.generator.language.kotlin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.List;

import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.test.io.TextTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinSourceCodeWriter}.
 *
 * @author Stephane Nicoll
 */
class KotlinSourceCodeWriterTests {

	@TempDir
	Path directory;

	private final KotlinSourceCodeWriter writer = new KotlinSourceCodeWriter(
			IndentingWriterFactory.withDefaultSettings());

	@Test
	void emptyCompilationUnit() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		sourceCode.createCompilationUnit("com.example", "Test");
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example");
	}

	@Test
	void emptyTypeDeclaration() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		compilationUnit.createTypeDeclaration("Test");
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test");
	}

	@Test
	void emptyTypeDeclarationWithModifiers() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.modifiers(KotlinModifier.PUBLIC, KotlinModifier.ABSTRACT);
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "",
				"abstract class Test");
	}

	@Test
	void emptyTypeDeclarationWithSuperClass() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.extend("com.example.build.TestParent");
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "",
				"import com.example.build.TestParent", "", "class Test : TestParent()");
	}

	@Test
	void function() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addFunctionDeclaration(KotlinFunctionDeclaration.function("reverse")
				.returning("java.lang.String")
				.parameters(new Parameter("java.lang.String", "echo"))
				.body(new KotlinReturnStatement(
						new KotlinFunctionInvocation("echo", "reversed"))));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test {", "",
				"    fun reverse(echo: String): String {",
				"        return echo.reversed()", "    }", "", "}");
	}

	@Test
	void functionModifiers() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addFunctionDeclaration(KotlinFunctionDeclaration.function("toString")
				.modifiers(KotlinModifier.OVERRIDE, KotlinModifier.PUBLIC,
						KotlinModifier.OPEN)
				.returning("java.lang.String").body(new KotlinReturnStatement(
						new KotlinFunctionInvocation("super", "toString"))));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test {", "",
				"    open override fun toString(): String {",
				"        return super.toString()", "    }", "", "}");
	}

	@Test
	void springBootApplication() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.annotate(Annotation
				.name("org.springframework.boot.autoconfigure.SpringBootApplication"));
		compilationUnit.addTopLevelFunction(KotlinFunctionDeclaration.function("main")
				.parameters(new Parameter("Array<String>", "args"))
				.body(new KotlinExpressionStatement(new KotlinReifiedFunctionInvocation(
						"org.springframework.boot.runApplication", "Test", "*args"))));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "",
				"import org.springframework.boot.autoconfigure.SpringBootApplication",
				"import org.springframework.boot.runApplication", "",
				"@SpringBootApplication", "class Test", "",
				"fun main(args: Array<String>) {", "    runApplication<Test>(*args)",
				"}");
	}

	@Test
	void annotationWithSimpleAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(
				Annotation.name("org.springframework.test.TestApplication",
						(builder) -> builder.attribute("counter", Integer.class, "42")));
		assertThat(lines).containsExactly("package com.example", "",
				"import org.springframework.test.TestApplication", "",
				"@TestApplication(counter = 42)", "class Test");
	}

	@Test
	void annotationWithSimpleStringAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(
				Annotation.name("org.springframework.test.TestApplication",
						(builder) -> builder.attribute("name", String.class, "test")));
		assertThat(lines).containsExactly("package com.example", "",
				"import org.springframework.test.TestApplication", "",
				"@TestApplication(name = \"test\")", "class Test");
	}

	@Test
	void annotationWithOnlyValueAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(
				Annotation.name("org.springframework.test.TestApplication",
						(builder) -> builder.attribute("value", String.class, "test")));
		assertThat(lines).containsExactly("package com.example", "",
				"import org.springframework.test.TestApplication", "",
				"@TestApplication(\"test\")", "class Test");
	}

	@Test
	void annotationWithSimpleEnumAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(
				Annotation.name("org.springframework.test.TestApplication",
						(builder) -> builder.attribute("unit", Enum.class,
								"java.time.temporal.ChronoUnit.SECONDS")));
		assertThat(lines).containsExactly("package com.example", "",
				"import java.time.temporal.ChronoUnit",
				"import org.springframework.test.TestApplication", "",
				"@TestApplication(unit = ChronoUnit.SECONDS)", "class Test");
	}

	@Test
	void annotationWithClassArrayAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(
				Annotation.name("org.springframework.test.TestApplication",
						(builder) -> builder.attribute("target", Class.class,
								"com.example.One", "com.example.Two")));
		assertThat(lines).containsExactly("package com.example", "",
				"import com.example.One", "import com.example.Two",
				"import org.springframework.test.TestApplication", "",
				"@TestApplication(target = [One::class, Two::class])", "class Test");
	}

	@Test
	void annotationWithSeveralAttributes() throws IOException {
		List<String> lines = writeClassAnnotation(Annotation.name(
				"org.springframework.test.TestApplication",
				(builder) -> builder.attribute("target", Class.class, "com.example.One")
						.attribute("unit", ChronoUnit.class,
								"java.time.temporal.ChronoUnit.NANOS")));
		assertThat(lines).containsExactly("package com.example", "",
				"import com.example.One", "import java.time.temporal.ChronoUnit",
				"import org.springframework.test.TestApplication", "",
				"@TestApplication(target = One::class, unit = ChronoUnit.NANOS)",
				"class Test");
	}

	private List<String> writeClassAnnotation(Annotation annotation) throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.annotate(annotation);
		return writeSingleType(sourceCode, "com/example/Test.kt");
	}

	@Test
	void functionWithSimpleAnnotation() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		KotlinFunctionDeclaration function = KotlinFunctionDeclaration
				.function("something").body();
		function.annotate(Annotation.name("com.example.test.TestAnnotation"));
		test.addFunctionDeclaration(function);
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "",
				"import com.example.test.TestAnnotation", "", "class Test {", "",
				"    @TestAnnotation", "    fun something() {", "    }", "", "}");
	}

	private List<String> writeSingleType(KotlinSourceCode sourceCode, String location)
			throws IOException {
		Path source = writeSourceCode(sourceCode).resolve(location);
		return TextTestUtils.readAllLines(source);
	}

	private Path writeSourceCode(KotlinSourceCode sourceCode) throws IOException {
		Path projectDirectory = Files.createTempDirectory(this.directory, "project-");
		this.writer.writeTo(projectDirectory, sourceCode);
		return projectDirectory;
	}

}
