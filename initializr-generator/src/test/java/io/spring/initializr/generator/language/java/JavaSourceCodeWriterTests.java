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
import java.util.function.Consumer;

import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.Annotation.Builder;
import io.spring.initializr.generator.language.ClassName;
import io.spring.initializr.generator.language.CodeBlock;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.Parameter;
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
 * @author Moritz Halbritter
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
	void shouldAddImplements() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.implement(List.of("com.example.build.Interface1", "com.example.build.Interface2"));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "import com.example.build.Interface1;",
				"import com.example.build.Interface2;", "", "class Test implements Interface1, Interface2 {", "", "}");
	}

	@Test
	void shouldAddExtendsAndImplements() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.extend("com.example.build.TestParent");
		test.implement(List.of("com.example.build.Interface1", "com.example.build.Interface2"));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "import com.example.build.Interface1;",
				"import com.example.build.Interface2;", "import com.example.build.TestParent;", "",
				"class Test extends TestParent implements Interface1, Interface2 {", "", "}");
	}

	@Test
	void method() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addMethodDeclaration(JavaMethodDeclaration.method("trim")
			.returning("java.lang.String")
			.modifiers(Modifier.PUBLIC)
			.parameters(Parameter.of("value", String.class))
			.body(CodeBlock.ofStatement("return value.trim()")));
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
				JavaFieldDeclaration.field("testString").modifiers(Modifier.PUBLIC).returning("com.another.One"));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "import com.another.One;", "", "class Test {", "",
				"    public One testString;", "", "}");
	}

	@Test
	void fieldAnnotation() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.modifiers(Modifier.PUBLIC);
		JavaFieldDeclaration field = JavaFieldDeclaration.field("testString")
			.modifiers(Modifier.PRIVATE)
			.returning("java.lang.String");
		field.annotations().add(ClassName.of("org.springframework.beans.factory.annotation.Autowired"));
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
		test.addFieldDeclaration(JavaFieldDeclaration.field("testString")
			.modifiers(Modifier.PRIVATE)
			.value("\"Test String\"")
			.returning("java.lang.String"));
		test.addFieldDeclaration(JavaFieldDeclaration.field("testChar")
			.modifiers(Modifier.PRIVATE | Modifier.TRANSIENT)
			.value("'\\u03a9'")
			.returning("char"));
		test.addFieldDeclaration(JavaFieldDeclaration.field("testInt")
			.modifiers(Modifier.PRIVATE | Modifier.FINAL)
			.value(1337)
			.returning("int"));
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
	void importsFromSamePackageAreDiscarded() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addFieldDeclaration(JavaFieldDeclaration.field("another").returning("com.example.Another"));
		test.addFieldDeclaration(JavaFieldDeclaration.field("sibling").returning("com.example.Sibling"));
		test.addFieldDeclaration(JavaFieldDeclaration.field("external").returning("com.example.another.External"));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).doesNotContain("import com.example.Another;")
			.doesNotContain("import com.example.Sibling;")
			.contains("import com.example.another.External;");
	}

	@Test
	void springBootApplication() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.annotations().add(ClassName.of("org.springframework.boot.autoconfigure.SpringBootApplication"));
		test.addMethodDeclaration(JavaMethodDeclaration.method("main")
			.modifiers(Modifier.PUBLIC | Modifier.STATIC)
			.returning("void")
			.parameters(Parameter.of("args", String[].class))
			.body(CodeBlock.ofStatement("$T.run($L.class, args)", "org.springframework.boot.SpringApplication",
					"Test")));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "",
				"import org.springframework.boot.SpringApplication;",
				"import org.springframework.boot.autoconfigure.SpringBootApplication;", "", "@SpringBootApplication",
				"class Test {", "", "    public static void main(String[] args) {",
				"        SpringApplication.run(Test.class, args);", "    }", "", "}");
	}

	@Test
	void annotationWithSimpleAttribute() throws IOException {
		List<String> lines = writeClassAnnotation("org.springframework.test.TestApplication",
				(builder) -> builder.set("counter", 42));
		assertThat(lines).containsExactly("package com.example;", "",
				"import org.springframework.test.TestApplication;", "", "@TestApplication(counter = 42)",
				"class Test {", "", "}");
	}

	@Test
	void annotationWithSimpleEnumAttribute() throws IOException {
		List<String> lines = writeClassAnnotation("org.springframework.test.TestApplication",
				(builder) -> builder.set("unit", ChronoUnit.SECONDS));
		assertThat(lines).containsExactly("package com.example;", "", "import java.time.temporal.ChronoUnit;",
				"import org.springframework.test.TestApplication;", "", "@TestApplication(unit = ChronoUnit.SECONDS)",
				"class Test {", "", "}");
	}

	@Test
	void annotationWithClassArrayAttribute() throws IOException {
		List<String> lines = writeClassAnnotation("org.springframework.test.TestApplication",
				(builder) -> builder.set("target", ClassName.of("com.another.One"), ClassName.of("com.another.Two")));
		assertThat(lines).containsExactly("package com.example;", "", "import com.another.One;",
				"import com.another.Two;", "import org.springframework.test.TestApplication;", "",
				"@TestApplication(target = { One.class, Two.class })", "class Test {", "", "}");
	}

	private List<String> writeClassAnnotation(String annotationClassName, Consumer<Builder> annotation)
			throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.annotations().add(ClassName.of(annotationClassName), annotation);
		return writeSingleType(sourceCode, "com/example/Test.java");
	}

	@Test
	void methodWithSimpleAnnotation() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		JavaMethodDeclaration method = JavaMethodDeclaration.method("something")
			.returning("void")
			.parameters()
			.body(CodeBlock.of(""));
		method.annotations().add(ClassName.of("com.example.test.TestAnnotation"));
		test.addMethodDeclaration(method);
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "import com.example.test.TestAnnotation;", "",
				"class Test {", "", "    @TestAnnotation", "    void something() {", "    }", "", "}");
	}

	@Test
	void methodWithParameterAnnotation() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addMethodDeclaration(JavaMethodDeclaration.method("something")
			.returning("void")
			.parameters(Parameter.builder("service")
				.type(ClassName.of("com.example.another.MyService"))
				.annotate(ClassName.of("com.example.stereotype.Service"))
				.build())
			.body(CodeBlock.of("")));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "import com.example.another.MyService;",
				"import com.example.stereotype.Service;", "", "class Test {", "",
				"    void something(@Service MyService service) {", "    }", "", "}");
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

}
