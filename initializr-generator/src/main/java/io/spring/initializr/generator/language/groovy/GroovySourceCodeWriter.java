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

package io.spring.initializr.generator.language.groovy;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.Annotatable;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.ClassName;
import io.spring.initializr.generator.language.CodeBlock;
import io.spring.initializr.generator.language.CodeBlock.FormattingOptions;
import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.SourceCodeWriter;
import io.spring.initializr.generator.language.SourceStructure;

import org.springframework.util.CollectionUtils;

/**
 * A {@link SourceCodeWriter} that writes {@link SourceCode} in Groovy.
 *
 * @author Stephane Nicoll
 * @author Matt Berteaux
 */
public class GroovySourceCodeWriter implements SourceCodeWriter<GroovySourceCode> {

	private static final FormattingOptions FORMATTING_OPTIONS = new GroovyFormattingOptions();

	private static final Map<Predicate<Integer>, String> TYPE_MODIFIERS;

	private static final Map<Predicate<Integer>, String> FIELD_MODIFIERS;

	private static final Map<Predicate<Integer>, String> METHOD_MODIFIERS;

	static {
		Map<Predicate<Integer>, String> typeModifiers = new LinkedHashMap<>();
		typeModifiers.put(Modifier::isProtected, "protected");
		typeModifiers.put(Modifier::isPrivate, "private");
		typeModifiers.put(Modifier::isAbstract, "abstract");
		typeModifiers.put(Modifier::isStatic, "static");
		typeModifiers.put(Modifier::isFinal, "final");
		typeModifiers.put(Modifier::isStrict, "strictfp");
		TYPE_MODIFIERS = typeModifiers;
		Map<Predicate<Integer>, String> fieldModifiers = new LinkedHashMap<>();
		fieldModifiers.put(Modifier::isPublic, "public");
		fieldModifiers.put(Modifier::isProtected, "protected");
		fieldModifiers.put(Modifier::isPrivate, "private");
		fieldModifiers.put(Modifier::isStatic, "static");
		fieldModifiers.put(Modifier::isFinal, "final");
		fieldModifiers.put(Modifier::isTransient, "transient");
		fieldModifiers.put(Modifier::isVolatile, "volatile");
		FIELD_MODIFIERS = fieldModifiers;
		Map<Predicate<Integer>, String> methodModifiers = new LinkedHashMap<>(typeModifiers);
		methodModifiers.put(Modifier::isSynchronized, "synchronized");
		methodModifiers.put(Modifier::isNative, "native");
		METHOD_MODIFIERS = methodModifiers;
	}

	private final IndentingWriterFactory indentingWriterFactory;

	/**
	 * Creates a new instance.
	 * @param indentingWriterFactory the {@link IndentingWriterFactory}
	 */
	public GroovySourceCodeWriter(IndentingWriterFactory indentingWriterFactory) {
		this.indentingWriterFactory = indentingWriterFactory;
	}

	@Override
	public void writeTo(SourceStructure structure, GroovySourceCode sourceCode) throws IOException {
		for (GroovyCompilationUnit compilationUnit : sourceCode.getCompilationUnits()) {
			writeTo(structure, compilationUnit);
		}
	}

	private void writeTo(SourceStructure structure, GroovyCompilationUnit compilationUnit) throws IOException {
		Path output = structure.createSourceFile(compilationUnit.getPackageName(), compilationUnit.getName());
		try (IndentingWriter writer = this.indentingWriterFactory.createIndentingWriter("groovy",
				Files.newBufferedWriter(output))) {
			writer.println("package " + compilationUnit.getPackageName());
			writer.println();
			Set<String> imports = determineImports(compilationUnit);
			if (!imports.isEmpty()) {
				for (String importedType : imports) {
					writer.println("import " + importedType);
				}
				writer.println();
			}
			for (GroovyTypeDeclaration type : compilationUnit.getTypeDeclarations()) {
				writeAnnotations(writer, type);
				writeModifiers(writer, TYPE_MODIFIERS, type.getModifiers());
				writer.print("class " + type.getName());
				if (type.getExtends() != null) {
					writer.print(" extends " + getUnqualifiedName(type.getExtends()));
				}
				if (!CollectionUtils.isEmpty(type.getImplements())) {
					writeImplements(type, writer);
				}
				writer.println(" {");
				writer.println();
				List<GroovyFieldDeclaration> fieldDeclarations = type.getFieldDeclarations();
				if (!fieldDeclarations.isEmpty()) {
					writer.indented(() -> {
						for (GroovyFieldDeclaration fieldDeclaration : fieldDeclarations) {
							writeFieldDeclaration(writer, fieldDeclaration);
						}
					});
				}
				List<GroovyMethodDeclaration> methodDeclarations = type.getMethodDeclarations();
				if (!methodDeclarations.isEmpty()) {
					writer.indented(() -> {
						for (GroovyMethodDeclaration methodDeclaration : methodDeclarations) {
							writeMethodDeclaration(writer, methodDeclaration);
						}
					});
				}
				writer.println("}");
			}
		}
	}

	private void writeImplements(GroovyTypeDeclaration type, IndentingWriter writer) {
		writer.print(" implements ");
		Iterator<String> iterator = type.getImplements().iterator();
		while (iterator.hasNext()) {
			String name = iterator.next();
			writer.print(getUnqualifiedName(name));
			if (iterator.hasNext()) {
				writer.print(", ");
			}
		}
	}

	private void writeAnnotations(IndentingWriter writer, Annotatable annotatable, Runnable separator) {
		annotatable.annotations().values().forEach((annotation) -> {
			annotation.write(writer, FORMATTING_OPTIONS);
			separator.run();
		});
	}

	private void writeAnnotations(IndentingWriter writer, Annotatable annotatable) {
		writeAnnotations(writer, annotatable, writer::println);
	}

	private void writeFieldDeclaration(IndentingWriter writer, GroovyFieldDeclaration fieldDeclaration) {
		writeAnnotations(writer, fieldDeclaration);
		writeModifiers(writer, FIELD_MODIFIERS, fieldDeclaration.getModifiers());
		writer.print(getUnqualifiedName(fieldDeclaration.getReturnType()));
		writer.print(" ");
		writer.print(fieldDeclaration.getName());
		if (fieldDeclaration.isInitialized()) {
			writer.print(" = ");
			writer.print(String.valueOf(fieldDeclaration.getValue()));
		}
		writer.println();
		writer.println();
	}

	private void writeMethodDeclaration(IndentingWriter writer, GroovyMethodDeclaration methodDeclaration) {
		writeAnnotations(writer, methodDeclaration);
		writeModifiers(writer, METHOD_MODIFIERS, methodDeclaration.getModifiers());
		writer.print(getUnqualifiedName(methodDeclaration.getReturnType()) + " " + methodDeclaration.getName() + "(");
		writeParameters(writer, methodDeclaration.getParameters());
		writer.println(") {");
		writer.indented(() -> methodDeclaration.getCode().write(writer, FORMATTING_OPTIONS));
		writer.println("}");
		writer.println();
	}

	private void writeParameters(IndentingWriter writer, List<Parameter> parameters) {
		if (parameters.isEmpty()) {
			return;
		}
		Iterator<Parameter> it = parameters.iterator();
		while (it.hasNext()) {
			Parameter parameter = it.next();
			writeAnnotations(writer, parameter, () -> writer.print(" "));
			writer.print(getUnqualifiedName(parameter.getType()) + " " + parameter.getName());
			if (it.hasNext()) {
				writer.print(", ");
			}
		}
	}

	private void writeModifiers(IndentingWriter writer, Map<Predicate<Integer>, String> availableModifiers,
			int declaredModifiers) {
		String modifiers = availableModifiers.entrySet()
			.stream()
			.filter((entry) -> entry.getKey().test(declaredModifiers))
			.map(Entry::getValue)
			.collect(Collectors.joining(" "));
		if (!modifiers.isEmpty()) {
			writer.print(modifiers);
			writer.print(" ");
		}
	}

	private Set<String> determineImports(GroovyCompilationUnit compilationUnit) {
		List<String> imports = new ArrayList<>();
		for (GroovyTypeDeclaration typeDeclaration : compilationUnit.getTypeDeclarations()) {
			imports.add(typeDeclaration.getExtends());
			imports.addAll(typeDeclaration.getImplements());
			imports.addAll(appendImports(typeDeclaration.annotations().values(), Annotation::getImports));
			for (GroovyFieldDeclaration fieldDeclaration : typeDeclaration.getFieldDeclarations()) {
				imports.add(fieldDeclaration.getReturnType());
				imports.addAll(appendImports(fieldDeclaration.annotations().values(), Annotation::getImports));
			}
			for (GroovyMethodDeclaration methodDeclaration : typeDeclaration.getMethodDeclarations()) {
				imports.add(methodDeclaration.getReturnType());
				imports.addAll(appendImports(methodDeclaration.annotations().values(), Annotation::getImports));
				for (Parameter parameter : methodDeclaration.getParameters()) {
					imports.add(parameter.getType());
					imports.addAll(appendImports(parameter.annotations().values(), Annotation::getImports));
				}
				imports.addAll(methodDeclaration.getCode().getImports());
			}
		}
		return imports.stream()
			.filter((candidate) -> isImportCandidate(compilationUnit, candidate))
			.sorted()
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private <T> List<String> appendImports(Stream<T> candidates, Function<T, Collection<String>> mapping) {
		return candidates.map(mapping).flatMap(Collection::stream).collect(Collectors.toList());
	}

	private String getUnqualifiedName(String name) {
		if (!name.contains(".")) {
			return name;
		}
		return name.substring(name.lastIndexOf(".") + 1);
	}

	private boolean isImportCandidate(CompilationUnit<?> compilationUnit, String name) {
		if (name == null || !name.contains(".")) {
			return false;
		}
		String packageName = name.substring(0, name.lastIndexOf('.'));
		return !"java.lang".equals(packageName) && !compilationUnit.getPackageName().equals(packageName);
	}

	static class GroovyFormattingOptions implements FormattingOptions {

		@Override
		public String statementSeparator() {
			return "";
		}

		@Override
		public CodeBlock arrayOf(CodeBlock... values) {
			return CodeBlock.of("[ $L ]", CodeBlock.join(Arrays.asList(values), ", "));
		}

		@Override
		public CodeBlock classReference(ClassName className) {
			return CodeBlock.of("$T", className);
		}

	}

}
