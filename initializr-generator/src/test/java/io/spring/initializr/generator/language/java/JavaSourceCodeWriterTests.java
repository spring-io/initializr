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

package io.spring.initializr.generator.language.java;

import java.io.IOException;
import java.lang.reflect.Modifier;
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
 * Tests for {@link JavaSourceCodeWriter}.
 *
 * @author Andy Wilkinson
 */
class JavaSourceCodeWriterTests {

	@TempDir
	Path directory;

	private final JavaSourceCodeWriter writer = new JavaSourceCodeWriter(
			IndentingWriterFactory.withDefaultSettings());

	@Test
	void emptyCompilationUnit() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		sourceCode.createCompilationUnit("com.example", "Test");
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;");
	}

	@Test
	void emptyTypeDeclaration() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		compilationUnit.createTypeDeclaration("Test");
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "class Test {", "",
				"}");
	}

	@Test
	void emptyTypeDeclarationWithModifiers() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.modifiers(Modifier.PROTECTED | Modifier.ABSTRACT);
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "",
				"protected abstract class Test {", "", "}");
	}

	@Test
	void emptyTypeDeclarationWithSuperClass() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.extend("com.example.build.TestParent");
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "",
				"import com.example.build.TestParent;", "",
				"class Test extends TestParent {", "", "}");
	}

	@Test
	void method() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addMethodDeclaration(JavaMethodDeclaration.method("trim")
				.returning("java.lang.String").modifiers(Modifier.PUBLIC)
				.parameters(new Parameter("java.lang.String", "value"))
				.body(new JavaReturnStatement(
						new JavaMethodInvocation("value", "trim"))));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "class Test {", "",
				"    public String trim(String value) {", "        return value.trim();",
				"    }", "", "}");
	}

	@Test
	void springBootApplication() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.annotate(Annotation
				.name("org.springframework.boot.autoconfigure.SpringBootApplication"));
		test.addMethodDeclaration(JavaMethodDeclaration.method("main")
				.modifiers(Modifier.PUBLIC | Modifier.STATIC).returning("void")
				.parameters(new Parameter("java.lang.String[]", "args"))
				.body(new JavaExpressionStatement(new JavaMethodInvocation(
						"org.springframework.boot.SpringApplication", "run", "Test.class",
						"args"))));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "",
				"import org.springframework.boot.SpringApplication;",
				"import org.springframework.boot.autoconfigure.SpringBootApplication;",
				"", "@SpringBootApplication", "class Test {", "",
				"    public static void main(String[] args) {",
				"        SpringApplication.run(Test.class, args);", "    }", "", "}");
	}

	@Test
	void annotationWithSimpleAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(
				Annotation.name("org.springframework.test.TestApplication",
						(builder) -> builder.attribute("counter", Integer.class, "42")));
		assertThat(lines).containsExactly("package com.example;", "",
				"import org.springframework.test.TestApplication;", "",
				"@TestApplication(counter = 42)", "class Test {", "", "}");
	}

	@Test
	void annotationWithSimpleStringAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(
				Annotation.name("org.springframework.test.TestApplication",
						(builder) -> builder.attribute("name", String.class, "test")));
		assertThat(lines).containsExactly("package com.example;", "",
				"import org.springframework.test.TestApplication;", "",
				"@TestApplication(name = \"test\")", "class Test {", "", "}");
	}

	@Test
	void annotationWithOnlyValueAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(
				Annotation.name("org.springframework.test.TestApplication",
						(builder) -> builder.attribute("value", String.class, "test")));
		assertThat(lines).containsExactly("package com.example;", "",
				"import org.springframework.test.TestApplication;", "",
				"@TestApplication(\"test\")", "class Test {", "", "}");
	}

	@Test
	void annotationWithSimpleEnumAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(
				Annotation.name("org.springframework.test.TestApplication",
						(builder) -> builder.attribute("unit", Enum.class,
								"java.time.temporal.ChronoUnit.SECONDS")));
		assertThat(lines).containsExactly("package com.example;", "",
				"import java.time.temporal.ChronoUnit;",
				"import org.springframework.test.TestApplication;", "",
				"@TestApplication(unit = ChronoUnit.SECONDS)", "class Test {", "", "}");
	}

	@Test
	void annotationWithClassArrayAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(
				Annotation.name("org.springframework.test.TestApplication",
						(builder) -> builder.attribute("target", Class.class,
								"com.example.One", "com.example.Two")));
		assertThat(lines).containsExactly("package com.example;", "",
				"import com.example.One;", "import com.example.Two;",
				"import org.springframework.test.TestApplication;", "",
				"@TestApplication(target = { One.class, Two.class })", "class Test {", "",
				"}");
	}

	@Test
	void annotationWithSeveralAttributes() throws IOException {
		List<String> lines = writeClassAnnotation(Annotation.name(
				"org.springframework.test.TestApplication",
				(builder) -> builder.attribute("target", Class.class, "com.example.One")
						.attribute("unit", ChronoUnit.class,
								"java.time.temporal.ChronoUnit.NANOS")));
		assertThat(lines).containsExactly("package com.example;", "",
				"import com.example.One;", "import java.time.temporal.ChronoUnit;",
				"import org.springframework.test.TestApplication;", "",
				"@TestApplication(target = One.class, unit = ChronoUnit.NANOS)",
				"class Test {", "", "}");
	}

	private List<String> writeClassAnnotation(Annotation annotation) throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.annotate(annotation);
		return writeSingleType(sourceCode, "com/example/Test.java");
	}

	@Test
	void methodWithSimpleAnnotation() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode
				.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		JavaMethodDeclaration method = JavaMethodDeclaration.method("something")
				.returning("void").parameters().body();
		method.annotate(Annotation.name("com.example.test.TestAnnotation"));
		test.addMethodDeclaration(method);
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "",
				"import com.example.test.TestAnnotation;", "", "class Test {", "",
				"    @TestAnnotation", "    void something() {", "    }", "", "}");
	}

	private List<String> writeSingleType(JavaSourceCode sourceCode, String location)
			throws IOException {
		Path source = writeSourceCode(sourceCode).resolve(location);
		return TextTestUtils.readAllLines(source);
	}

	private Path writeSourceCode(JavaSourceCode sourceCode) throws IOException {
		Path projectDirectory = Files.createTempDirectory(this.directory, "project-");
		this.writer.writeTo(projectDirectory, sourceCode);
		return projectDirectory;
	}

}
