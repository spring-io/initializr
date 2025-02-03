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

package io.spring.initializr.generator.language.kotlin;

import java.io.IOException;
import java.io.InputStream;
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
 * Tests for {@link KotlinSourceCodeWriter}.
 *
 * @author Stephane Nicoll
 * @author Matt Berteaux
 * @author Moritz Halbritter
 */
class KotlinSourceCodeWriterTests {

	private static final Language LANGUAGE = new KotlinLanguage();

	@TempDir
	Path directory;

	private final KotlinSourceCodeWriter writer = new KotlinSourceCodeWriter(new KotlinLanguage(),
			IndentingWriterFactory.withDefaultSettings());

	@Test
	void nullPackageInvalidCompilationUnit() {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		assertThatIllegalArgumentException().isThrownBy(() -> sourceCode.createCompilationUnit(null, "Test"));
	}

	@Test
	void nullNameInvalidCompilationUnit() {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		assertThatIllegalArgumentException().isThrownBy(() -> sourceCode.createCompilationUnit("com.example", null));
	}

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
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		compilationUnit.createTypeDeclaration("Test");
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test");
	}

	@Test
	void emptyTypeDeclarationWithModifiers() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.modifiers(KotlinModifier.PUBLIC, KotlinModifier.ABSTRACT);
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "abstract class Test");
	}

	@Test
	void emptyTypeDeclarationWithSuperClass() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.extend("com.example.build.TestParent");
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "import com.example.build.TestParent", "",
				"class Test : TestParent()");
	}

	@Test
	void shouldImplementInterfaces() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.implement(List.of("com.example.build.Interface1", "com.example.build.Interface2"));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "import com.example.build.Interface1",
				"import com.example.build.Interface2", "", "class Test : Interface1, Interface2");
	}

	@Test
	void shouldExtendAndImplement() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.extend("com.example.build.TestParent");
		test.implement(List.of("com.example.build.Interface1", "com.example.build.Interface2"));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "import com.example.build.Interface1",
				"import com.example.build.Interface2", "import com.example.build.TestParent", "",
				"class Test : TestParent(), Interface1, Interface2");
	}

	@Test
	void function() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addFunctionDeclaration(KotlinFunctionDeclaration.function("reverse")
			.returning("java.lang.String")
			.parameters(Parameter.of("echo", String.class))
			.body(CodeBlock.ofStatement("return echo.reversed()")));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test {", "",
				"    fun reverse(echo: String): String {", "        return echo.reversed()", "    }", "", "}");
	}

	@Test
	void functionModifiers() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addFunctionDeclaration(KotlinFunctionDeclaration.function("toString")
			.modifiers(KotlinModifier.OVERRIDE, KotlinModifier.PUBLIC, KotlinModifier.OPEN)
			.returning("java.lang.String")
			.body(CodeBlock.ofStatement("return super.toString()")));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test {", "",
				"    open override fun toString(): String {", "        return super.toString()", "    }", "", "}");
	}

	@Test
	void valProperty() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addPropertyDeclaration(
				KotlinPropertyDeclaration.val("testProp").returning("java.lang.String").emptyValue());
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test {", "", "    val testProp: String", "",
				"}");
	}

	@Test
	void valPropertyImport() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addPropertyDeclaration(
				KotlinPropertyDeclaration.val("testProp").returning("com.example.another.One").emptyValue());
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "import com.example.another.One", "",
				"class Test {", "", "    val testProp: One", "", "}");
	}

	@Test
	void valGetterProperty() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addPropertyDeclaration(KotlinPropertyDeclaration.val("testProp")
			.returning("java.lang.String")
			.value(CodeBlock.of("$S", "This is a TEST")));
		test.addPropertyDeclaration(KotlinPropertyDeclaration.val("withGetter")
			.returning("java.lang.String")
			.getter()
			.withBody(CodeBlock.of("testProp.toLowerCase()"))
			.buildAccessor()
			.emptyValue());
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test {", "",
				"    val testProp: String = \"This is a TEST\"", "", "    val withGetter: String",
				"        get() = testProp.toLowerCase()", "", "}");
	}

	@Test
	void varProperty() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addPropertyDeclaration(KotlinPropertyDeclaration.var("testProp")
			.returning("java.lang.String")
			.value(CodeBlock.of("$S", "This is a test")));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test {", "",
				"    var testProp: String = \"This is a test\"", "", "}");
	}

	@Test
	void varSetterProperty() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addPropertyDeclaration(KotlinPropertyDeclaration.var("testProp")
			.returning("java.lang.String")
			.setter()
			.buildAccessor()
			.value(CodeBlock.of("$S", "This is a test")));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test {", "",
				"    var testProp: String = \"This is a test\"", "        set", "", "}");
	}

	@Test
	void varAnnotateSetterProperty() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addPropertyDeclaration(KotlinPropertyDeclaration.var("testProp")
			.returning("java.lang.String")
			.setter()
			.withAnnotation(ClassName.of("org.springframework.beans.factory.annotation.Autowired"))
			.buildAccessor()
			.value(CodeBlock.of("$S", "This is a test")));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test {", "",
				"    var testProp: String = \"This is a test\"", "        @Autowired set", "", "}");
	}

	@Test
	void varProperties() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addPropertyDeclaration(
				KotlinPropertyDeclaration.var("testProp").returning("Int").value(CodeBlock.of("42")));
		test.addPropertyDeclaration(
				KotlinPropertyDeclaration.var("testDouble").returning("Double").value(CodeBlock.of("1986.0")));
		test.addPropertyDeclaration(KotlinPropertyDeclaration.var("testFloat").value(CodeBlock.of("99.999f")));
		test.addPropertyDeclaration(
				KotlinPropertyDeclaration.var("testLong").returning("Long").value(CodeBlock.of("1986L")));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test {", "", "    var testProp: Int = 42",
				"", "    var testDouble: Double = 1986.0", "", "    var testFloat = 99.999f", "",
				"    var testLong: Long = 1986L", "", "}");
	}

	@Test
	void varEmptyProperty() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addPropertyDeclaration(KotlinPropertyDeclaration.var("testProp").returning("Int").empty());
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test {", "", "    var testProp: Int", "",
				"}");
	}

	@Test
	void varLateinitProperty() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addPropertyDeclaration(
				KotlinPropertyDeclaration.var("testProp").modifiers(KotlinModifier.LATEINIT).returning("Int").empty());
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "class Test {", "",
				"    lateinit var testProp: Int", "", "}");
	}

	@Test
	void importsFromSamePackageAreDiscarded() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addPropertyDeclaration(KotlinPropertyDeclaration.var("another").returning("com.example.Another").empty());
		test.addPropertyDeclaration(KotlinPropertyDeclaration.var("sibling").returning("com.example.Sibling").empty());
		test.addPropertyDeclaration(
				KotlinPropertyDeclaration.var("external").returning("com.example.another.External").empty());
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).doesNotContain("import com.example.Another")
			.doesNotContain("import com.example.Sibling")
			.contains("import com.example.another.External");
	}

	@Test
	void springBootApplication() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.annotations().add(ClassName.of("org.springframework.boot.autoconfigure.SpringBootApplication"));
		compilationUnit.addTopLevelFunction(KotlinFunctionDeclaration.function("main")
			.parameters(Parameter.of("args", "Array<String>"))
			.body(CodeBlock.ofStatement("$T<$L>(*args)", "org.springframework.boot.runApplication", "Test")));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "",
				"import org.springframework.boot.autoconfigure.SpringBootApplication",
				"import org.springframework.boot.runApplication", "", "@SpringBootApplication", "class Test", "",
				"fun main(args: Array<String>) {", "    runApplication<Test>(*args)", "}");
	}

	@Test
	void annotationWithSimpleAttribute() throws IOException {
		List<String> lines = writeClassAnnotation("org.springframework.test.TestApplication",
				(builder) -> builder.set("counter", 42));
		assertThat(lines).containsExactly("package com.example", "", "import org.springframework.test.TestApplication",
				"", "@TestApplication(counter = 42)", "class Test");
	}

	@Test
	void annotationWithSimpleEnumAttribute() throws IOException {
		List<String> lines = writeClassAnnotation("org.springframework.test.TestApplication",
				(builder) -> builder.set("unit", ChronoUnit.SECONDS));
		assertThat(lines).containsExactly("package com.example", "", "import java.time.temporal.ChronoUnit",
				"import org.springframework.test.TestApplication", "", "@TestApplication(unit = ChronoUnit.SECONDS)",
				"class Test");
	}

	@Test
	void annotationWithClassArrayAttribute() throws IOException {
		List<String> lines = writeClassAnnotation("org.springframework.test.TestApplication", (builder) -> builder
			.set("target", ClassName.of("com.example.another.One"), ClassName.of("com.example.another.Two")));
		assertThat(lines).containsExactly("package com.example", "", "import com.example.another.One",
				"import com.example.another.Two", "import org.springframework.test.TestApplication", "",
				"@TestApplication(target = [One::class, Two::class])", "class Test");
	}

	private List<String> writeClassAnnotation(String annotationClassName, Consumer<Builder> annotation)
			throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.annotations().add(ClassName.of(annotationClassName), annotation);
		return writeSingleType(sourceCode, "com/example/Test.kt");
	}

	@Test
	void functionWithSimpleAnnotation() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		KotlinFunctionDeclaration function = KotlinFunctionDeclaration.function("something").body(CodeBlock.of(""));
		function.annotations().add(ClassName.of("com.example.test.TestAnnotation"));
		test.addFunctionDeclaration(function);
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "import com.example.test.TestAnnotation", "",
				"class Test {", "", "    @TestAnnotation", "    fun something() {", "    }", "", "}");
	}

	@Test
	void functionWithParameterAnnotation() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		KotlinCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		KotlinTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addFunctionDeclaration(KotlinFunctionDeclaration.function("something")
			.parameters(Parameter.builder("service")
				.type(ClassName.of("com.example.another.MyService"))
				.annotate(ClassName.of("com.example.stereotype.Service"))
				.build())
			.body(CodeBlock.of("")));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.kt");
		assertThat(lines).containsExactly("package com.example", "", "import com.example.another.MyService",
				"import com.example.stereotype.Service", "", "class Test {", "",
				"    fun something(@Service service: MyService) {", "    }", "", "}");
	}

	@Test
	void reservedKeywordsStartPackageName() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		sourceCode.createCompilationUnit("fun.example.demo", "Test");
		List<String> lines = writeSingleType(sourceCode, "fun/example/demo/Test.kt");
		assertThat(lines).containsExactly("package `fun`.example.demo");
	}

	@Test
	void reservedKeywordsMiddlePackageName() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		sourceCode.createCompilationUnit("com.false.demo", "Test");
		List<String> lines = writeSingleType(sourceCode, "com/false/demo/Test.kt");
		assertThat(lines).containsExactly("package com.`false`.demo");
	}

	@Test
	void reservedKeywordsEndPackageName() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		sourceCode.createCompilationUnit("com.example.in", "Test");
		List<String> lines = writeSingleType(sourceCode, "com/example/in/Test.kt");
		assertThat(lines).containsExactly("package com.example.`in`");
	}

	@Test
	void reservedJavaKeywordsStartPackageName() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		sourceCode.createCompilationUnit("package.fun.example.demo", "Test");
		List<String> lines = writeSingleType(sourceCode, "package/fun/example/demo/Test.kt");
		assertThat(lines).containsExactly("package `package`.`fun`.example.demo");
	}

	@Test
	void reservedJavaKeywordsMiddlePackageName() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		sourceCode.createCompilationUnit("com.package.demo", "Test");
		List<String> lines = writeSingleType(sourceCode, "com/package/demo/Test.kt");
		assertThat(lines).containsExactly("package com.`package`.demo");
	}

	@Test
	void reservedJavaKeywordsEndPackageName() throws IOException {
		KotlinSourceCode sourceCode = new KotlinSourceCode();
		sourceCode.createCompilationUnit("com.example.package", "Test");
		List<String> lines = writeSingleType(sourceCode, "com/example/package/Test.kt");
		assertThat(lines).containsExactly("package com.example.`package`");
	}

	private List<String> writeSingleType(KotlinSourceCode sourceCode, String location) throws IOException {
		Path source = writeSourceCode(sourceCode).resolve(location);
		try (InputStream stream = Files.newInputStream(source)) {
			String[] lines = StreamUtils.copyToString(stream, StandardCharsets.UTF_8).split("\\r?\\n");
			return Arrays.asList(lines);
		}
	}

	private Path writeSourceCode(KotlinSourceCode sourceCode) throws IOException {
		Path srcDirectory = this.directory.resolve(UUID.randomUUID().toString());
		SourceStructure sourceStructure = new SourceStructure(srcDirectory, LANGUAGE);
		this.writer.writeTo(sourceStructure, sourceCode);
		return sourceStructure.getSourcesDirectory();
	}

}
