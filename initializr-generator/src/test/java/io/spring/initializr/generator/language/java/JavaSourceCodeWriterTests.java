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
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceStructure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link JavaSourceCodeWriter}.
 *
 * @author Andy Wilkinson
 * @author Matt Berteaux
 */
class JavaSourceCodeWriterTests {

	private static final Language LANGUAGE = new JavaLanguage();

	@TempDir
	Path directory;

	private final JavaSourceCodeWriter writer = new JavaSourceCodeWriter(IndentingWriterFactory.withDefaultSettings());

	@Test
	void nullPackageInvalidCompilationUnit() {
		JavaSourceCode sourceCode = new JavaSourceCode();
		assertThatIllegalArgumentException().isThrownBy(() -> sourceCode.createCompilationUnit(null, "Test"));
	}

	@Test
	void nullNameInvalidCompilationUnit() {
		JavaSourceCode sourceCode = new JavaSourceCode();
		assertThatIllegalArgumentException().isThrownBy(() -> sourceCode.createCompilationUnit("com.example", null));
	}

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
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		compilationUnit.createTypeDeclaration("Test");
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "class Test {", "", "}");
	}

	@Test
	void emptyTypeDeclarationWithModifiers() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.modifiers(Modifier.PROTECTED | Modifier.ABSTRACT);
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "protected abstract class Test {", "", "}");
	}

	@Test
	void emptyTypeDeclarationWithSuperClass() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.extend("com.example.build.TestParent");
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "import com.example.build.TestParent;", "",
				"class Test extends TestParent {", "", "}");
	}

	@Test
	void method() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addMethodDeclaration(JavaMethodDeclaration.method("trim").returning("java.lang.String")
				.modifiers(Modifier.PUBLIC).parameters(new JavaParameter("java.lang.String", "value"))
				.body(new JavaReturnStatement(new JavaMethodInvocation("value", "trim"))));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "class Test {", "",
				"    public String trim(String value) {", "        return value.trim();", "    }", "", "}");
	}

	@Test
	void field() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.modifiers(Modifier.PUBLIC);
		test.addFieldDeclaration(
				JavaFieldDeclaration.field("testString").modifiers(Modifier.PRIVATE).returning("java.lang.String"));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "public class Test {", "",
				"    private String testString;", "", "}");
	}

	@Test
	void fieldImport() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addFieldDeclaration(
				JavaFieldDeclaration.field("testString").modifiers(Modifier.PUBLIC).returning("com.example.One"));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "import com.example.One;", "", "class Test {", "",
				"    public One testString;", "", "}");
	}

	@Test
	void fieldAnnotation() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.modifiers(Modifier.PUBLIC);
		JavaFieldDeclaration field = JavaFieldDeclaration.field("testString").modifiers(Modifier.PRIVATE)
				.returning("java.lang.String");
		field.annotate(Annotation.name("org.springframework.beans.factory.annotation.Autowired"));
		test.addFieldDeclaration(field);
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "",
				"import org.springframework.beans.factory.annotation.Autowired;", "", "public class Test {", "",
				"    @Autowired", "    private String testString;", "", "}");
	}

	@Test
	void fields() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.modifiers(Modifier.PUBLIC);
		test.addFieldDeclaration(JavaFieldDeclaration.field("testString").modifiers(Modifier.PRIVATE)
				.value("\"Test String\"").returning("java.lang.String"));
		test.addFieldDeclaration(JavaFieldDeclaration.field("testChar").modifiers(Modifier.PRIVATE | Modifier.TRANSIENT)
				.value("'\\u03a9'").returning("char"));
		test.addFieldDeclaration(JavaFieldDeclaration.field("testInt").modifiers(Modifier.PRIVATE | Modifier.FINAL)
				.value(1337).returning("int"));
		test.addFieldDeclaration(
				JavaFieldDeclaration.field("testDouble").modifiers(Modifier.PRIVATE).value("3.14").returning("Double"));
		test.addFieldDeclaration(
				JavaFieldDeclaration.field("testLong").modifiers(Modifier.PRIVATE).value("1986L").returning("Long"));
		test.addFieldDeclaration(
				JavaFieldDeclaration.field("testFloat").modifiers(Modifier.PUBLIC).value("99.999f").returning("float"));
		test.addFieldDeclaration(JavaFieldDeclaration.field("testBool").value("true").returning("boolean"));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "public class Test {", "",
				"    private String testString = \"Test String\";", "",
				"    private transient char testChar = '\\u03a9';", "", "    private final int testInt = 1337;", "",
				"    private Double testDouble = 3.14;", "", "    private Long testLong = 1986L;", "",
				"    public float testFloat = 99.999f;", "", "    boolean testBool = true;", "", "}");
	}

	@Test
	void springBootApplication() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.annotate(Annotation.name("org.springframework.boot.autoconfigure.SpringBootApplication"));
		test.addMethodDeclaration(JavaMethodDeclaration.method("main").modifiers(Modifier.PUBLIC | Modifier.STATIC)
				.returning("void").parameters(new JavaParameter("java.lang.String[]", "args"))
				.body(new JavaExpressionStatement(new JavaMethodInvocation("org.springframework.boot.SpringApplication",
						"run", "Test.class", "args"))));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "",
				"import org.springframework.boot.SpringApplication;",
				"import org.springframework.boot.autoconfigure.SpringBootApplication;", "", "@SpringBootApplication",
				"class Test {", "", "    public static void main(String[] args) {",
				"        SpringApplication.run(Test.class, args);", "    }", "", "}");
	}

	@Test
	void annotationWithSimpleAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(Annotation.name("org.springframework.test.TestApplication",
				(builder) -> builder.attribute("counter", Integer.class, "42")));
		assertThat(lines).containsExactly("package com.example;", "",
				"import org.springframework.test.TestApplication;", "", "@TestApplication(counter = 42)",
				"class Test {", "", "}");
	}

	@Test
	void annotationWithSimpleStringAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(Annotation.name("org.springframework.test.TestApplication",
				(builder) -> builder.attribute("name", String.class, "test")));
		assertThat(lines).containsExactly("package com.example;", "",
				"import org.springframework.test.TestApplication;", "", "@TestApplication(name = \"test\")",
				"class Test {", "", "}");
	}

	@Test
	void annotationWithOnlyValueAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(Annotation.name("org.springframework.test.TestApplication",
				(builder) -> builder.attribute("value", String.class, "test")));
		assertThat(lines).containsExactly("package com.example;", "",
				"import org.springframework.test.TestApplication;", "", "@TestApplication(\"test\")", "class Test {",
				"", "}");
	}

	@Test
	void annotationWithSimpleEnumAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(Annotation.name("org.springframework.test.TestApplication",
				(builder) -> builder.attribute("unit", Enum.class, "java.time.temporal.ChronoUnit.SECONDS")));
		assertThat(lines).containsExactly("package com.example;", "", "import java.time.temporal.ChronoUnit;",
				"import org.springframework.test.TestApplication;", "", "@TestApplication(unit = ChronoUnit.SECONDS)",
				"class Test {", "", "}");
	}

	@Test
	void annotationWithClassArrayAttribute() throws IOException {
		List<String> lines = writeClassAnnotation(Annotation.name("org.springframework.test.TestApplication",
				(builder) -> builder.attribute("target", Class.class, "com.example.One", "com.example.Two")));
		assertThat(lines).containsExactly("package com.example;", "", "import com.example.One;",
				"import com.example.Two;", "import org.springframework.test.TestApplication;", "",
				"@TestApplication(target = { One.class, Two.class })", "class Test {", "", "}");
	}

	@Test
	void annotationWithSeveralAttributes() throws IOException {
		List<String> lines = writeClassAnnotation(Annotation.name("org.springframework.test.TestApplication",
				(builder) -> builder.attribute("target", Class.class, "com.example.One").attribute("unit",
						ChronoUnit.class, "java.time.temporal.ChronoUnit.NANOS")));
		assertThat(lines).containsExactly("package com.example;", "", "import com.example.One;",
				"import java.time.temporal.ChronoUnit;", "import org.springframework.test.TestApplication;", "",
				"@TestApplication(target = One.class, unit = ChronoUnit.NANOS)", "class Test {", "", "}");
	}

	private List<String> writeClassAnnotation(Annotation annotation) throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.annotate(annotation);
		return writeSingleType(sourceCode, "com/example/Test.java");
	}

	@Test
	void methodWithSimpleAnnotation() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		JavaMethodDeclaration method = JavaMethodDeclaration.method("something").returning("void").parameters().body();
		method.annotate(Annotation.name("com.example.test.TestAnnotation"));
		test.addMethodDeclaration(method);
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "import com.example.test.TestAnnotation;", "",
				"class Test {", "", "    @TestAnnotation", "    void something() {", "    }", "", "}");
	}

	private List<String> writeSingleType(JavaSourceCode sourceCode, String location) throws IOException {
		Path source = writeSourceCode(sourceCode).resolve(location);
		try (InputStream stream = Files.newInputStream(source)) {
			String[] lines = StreamUtils.copyToString(stream, StandardCharsets.UTF_8).split("\\r?\\n");
			return Arrays.asList(lines);
		}
	}

	private Path writeSourceCode(JavaSourceCode sourceCode) throws IOException {
		Path srcDirectory = this.directory.resolve(UUID.randomUUID().toString());
		SourceStructure sourceStructure = new SourceStructure(srcDirectory, LANGUAGE);
		this.writer.writeTo(sourceStructure, sourceCode);
		return sourceStructure.getSourcesDirectory();
	}

	@Test
	void testParameterWithSimpleAnnotation() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		JavaParameter parameter = new JavaParameter("java.lang.String", "arg0");
		parameter.annotate(Annotation.name("javax.validation.constraints.NotNull"));
		JavaMethodDeclaration method = JavaMethodDeclaration.method("something").returning("void").parameters(parameter)
				.body();
		test.addMethodDeclaration(method);
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "import javax.validation.constraints.NotNull;",
				"", "class Test {", "", "    void something(@NotNull String arg0) {", "    }", "", "}");
	}

	@Test
	void testMethodWithMultiParametersAndParameterWithMultiAnnotations() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		JavaParameter arg0 = new JavaParameter("java.lang.String", "arg0");
		arg0.annotate(Annotation.name("javax.validation.constraints.NotNull"));
		JavaParameter arg1 = new JavaParameter("int", "arg1");
		arg1.annotate(
				Annotation.name("javax.validation.constraints.Max", (t) -> t.attribute("value", int.class, "10")));
		arg1.annotate(Annotation.name("javax.validation.constraints.Min", (t) -> t.attribute("value", int.class, "1")));
		JavaMethodDeclaration method = JavaMethodDeclaration.method("something").returning("void")
				.parameters(arg0, arg1).body();
		test.addMethodDeclaration(method);
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "import javax.validation.constraints.Max;",
				"import javax.validation.constraints.Min;", "import javax.validation.constraints.NotNull;", "",
				"class Test {", "", "    void something(@NotNull String arg0, @Max(10) @Min(1) int arg1) {", "    }",
				"", "}");
	}

}
