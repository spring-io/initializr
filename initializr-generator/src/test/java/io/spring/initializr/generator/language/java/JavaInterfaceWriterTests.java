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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.SourceStructure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JavaSourceCodeWriter}.
 *
 * @author Andy Wilkinson
 * @author Matt Berteaux
 * @author Tad Sanden
 */
class JavaInterfaceWriterTests {

	private static final Language LANGUAGE = new JavaLanguage();

	@TempDir
	Path directory;

	private final JavaInterfaceWriter writer = new JavaInterfaceWriter(IndentingWriterFactory.withDefaultSettings());

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
		assertThat(lines).containsExactly("package com.example;", "", "interface Test {", "", "}");
	}

	@Test
	void method() throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.addMethodDeclaration(JavaMethodDeclaration.method("trim").returning("java.lang.String")
				.modifiers(Modifier.PUBLIC).parameters(new Parameter("java.lang.String", "value"))
				.body(new JavaReturnStatement(new JavaMethodInvocation("value", "trim"))));
		List<String> lines = writeSingleType(sourceCode, "com/example/Test.java");
		assertThat(lines).containsExactly("package com.example;", "", "interface Test {", "",
				"    public String trim(String value);", "", "}");
	}

	@Test
	void annotationWithSimpleAttribute() throws IOException {
		List<String> lines = writeInterfaceAnnotation(Annotation.name("org.springframework.cloud.openfeign.FeignClient",
				(builder) -> builder.attribute("value", String.class, "ClientName")));
		assertThat(lines).containsExactly("package com.example;", "",
				"import org.springframework.cloud.openfeign.FeignClient;", "", "@FeignClient(\"ClientName\")",
				"interface Test {", "", "}");
	}

	private List<String> writeInterfaceAnnotation(Annotation annotation) throws IOException {
		JavaSourceCode sourceCode = new JavaSourceCode();
		JavaCompilationUnit compilationUnit = sourceCode.createCompilationUnit("com.example", "Test");
		JavaTypeDeclaration test = compilationUnit.createTypeDeclaration("Test");
		test.annotate(annotation);
		return writeSingleType(sourceCode, "com/example/Test.java");
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
