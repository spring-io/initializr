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

package io.spring.initializr.generator.language.groovy;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.SourceCodeWriter;

/**
 * A {@link SourceCodeWriter} that writes {@link SourceCode} in Groovy.
 *
 * @author Stephane Nicoll
 */
public class GroovySourceCodeWriter implements SourceCodeWriter<GroovySourceCode> {

	private static final Map<Predicate<Integer>, String> TYPE_MODIFIERS;

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
		Map<Predicate<Integer>, String> methodModifiers = new LinkedHashMap<>(
				typeModifiers);
		methodModifiers.put(Modifier::isSynchronized, "synchronized");
		methodModifiers.put(Modifier::isNative, "native");
		METHOD_MODIFIERS = methodModifiers;
	}

	private final IndentingWriterFactory indentingWriterFactory;

	public GroovySourceCodeWriter(IndentingWriterFactory indentingWriterFactory) {
		this.indentingWriterFactory = indentingWriterFactory;
	}

	@Override
	public void writeTo(Path directory, GroovySourceCode sourceCode) throws IOException {
		if (!Files.exists(directory)) {
			Files.createDirectories(directory);
		}
		for (GroovyCompilationUnit compilationUnit : sourceCode.getCompilationUnits()) {
			writeTo(directory, compilationUnit);
		}
	}

	private void writeTo(Path directory, GroovyCompilationUnit compilationUnit)
			throws IOException {
		Path output = fileForCompilationUnit(directory, compilationUnit);
		Files.createDirectories(output.getParent());
		try (IndentingWriter writer = this.indentingWriterFactory
				.createIndentingWriter("groovy", Files.newBufferedWriter(output))) {
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
				writer.println(" {");
				writer.println();
				List<GroovyMethodDeclaration> methodDeclarations = type
						.getMethodDeclarations();
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

	private void writeAnnotations(IndentingWriter writer, Annotatable annotatable) {
		annotatable.getAnnotations()
				.forEach((annotation) -> writeAnnotation(writer, annotation));
	}

	private void writeAnnotation(IndentingWriter writer, Annotation annotation) {
		writer.print("@" + getUnqualifiedName(annotation.getName()));
		List<Annotation.Attribute> attributes = annotation.getAttributes();
		if (!attributes.isEmpty()) {
			writer.print("(");
			if (attributes.size() == 1 && attributes.get(0).getName().equals("value")) {
				writer.print(formatAnnotationAttribute(attributes.get(0)));
			}
			else {
				writer.print(attributes.stream()
						.map((attribute) -> attribute.getName() + " = "
								+ formatAnnotationAttribute(attribute))
						.collect(Collectors.joining(", ")));
			}
			writer.print(")");
		}
		writer.println();
	}

	private String formatAnnotationAttribute(Annotation.Attribute attribute) {
		List<String> values = attribute.getValues();
		if (attribute.getType().equals(Class.class)) {
			return formatValues(values, this::getUnqualifiedName);
		}
		if (Enum.class.isAssignableFrom(attribute.getType())) {
			return formatValues(values, (value) -> {
				String enumValue = value.substring(value.lastIndexOf(".") + 1);
				String enumClass = value.substring(0, value.lastIndexOf("."));
				return String.format("%s.%s", getUnqualifiedName(enumClass), enumValue);
			});
		}
		if (attribute.getType().equals(String.class)) {
			return formatValues(values, (value) -> String.format("\"%s\"", value));
		}
		return formatValues(values, (value) -> String.format("%s", value));
	}

	private String formatValues(List<String> values, Function<String, String> formatter) {
		String result = values.stream().map(formatter).collect(Collectors.joining(", "));
		return (values.size() > 1) ? "{ " + result + " }" : result;
	}

	private void writeMethodDeclaration(IndentingWriter writer,
			GroovyMethodDeclaration methodDeclaration) {
		writeAnnotations(writer, methodDeclaration);
		writeModifiers(writer, METHOD_MODIFIERS, methodDeclaration.getModifiers());
		writer.print(getUnqualifiedName(methodDeclaration.getReturnType()) + " "
				+ methodDeclaration.getName() + "(");
		List<Parameter> parameters = methodDeclaration.getParameters();
		if (!parameters.isEmpty()) {
			writer.print(parameters
					.stream().map((parameter) -> getUnqualifiedName(parameter.getType())
							+ " " + parameter.getName())
					.collect(Collectors.joining(", ")));
		}
		writer.println(") {");
		writer.indented(() -> {
			List<GroovyStatement> statements = methodDeclaration.getStatements();
			for (GroovyStatement statement : statements) {
				if (statement instanceof GroovyExpressionStatement) {
					writeExpression(writer,
							((GroovyExpressionStatement) statement).getExpression());
				}
				else if (statement instanceof GroovyReturnStatement) {
					writeExpression(writer,
							((GroovyReturnStatement) statement).getExpression());
				}
				writer.println();
			}
		});
		writer.println("}");
		writer.println();
	}

	private void writeModifiers(IndentingWriter writer,
			Map<Predicate<Integer>, String> availableModifiers, int declaredModifiers) {
		String modifiers = availableModifiers.entrySet().stream()
				.filter((entry) -> entry.getKey().test(declaredModifiers))
				.map(Entry::getValue).collect(Collectors.joining(" "));
		if (!modifiers.isEmpty()) {
			writer.print(modifiers);
			writer.print(" ");
		}
	}

	private void writeExpression(IndentingWriter writer, GroovyExpression expression) {
		if (expression instanceof GroovyMethodInvocation) {
			writeMethodInvocation(writer, (GroovyMethodInvocation) expression);
		}
	}

	private void writeMethodInvocation(IndentingWriter writer,
			GroovyMethodInvocation methodInvocation) {
		writer.print(getUnqualifiedName(methodInvocation.getTarget()) + "."
				+ methodInvocation.getName() + "("
				+ String.join(", ", methodInvocation.getArguments()) + ")");
	}

	private Path fileForCompilationUnit(Path directory,
			GroovyCompilationUnit compilationUnit) {
		return directoryForPackage(directory, compilationUnit.getPackageName())
				.resolve(compilationUnit.getName() + ".groovy");
	}

	private Path directoryForPackage(Path directory, String packageName) {
		return directory.resolve(packageName.replace('.', '/'));
	}

	private Set<String> determineImports(GroovyCompilationUnit compilationUnit) {
		List<String> imports = new ArrayList<>();
		for (GroovyTypeDeclaration typeDeclaration : compilationUnit
				.getTypeDeclarations()) {
			if (requiresImport(typeDeclaration.getExtends())) {
				imports.add(typeDeclaration.getExtends());
			}
			imports.addAll(getRequiredImports(typeDeclaration.getAnnotations(),
					this::determineImports));
			for (GroovyMethodDeclaration methodDeclaration : typeDeclaration
					.getMethodDeclarations()) {
				if (requiresImport(methodDeclaration.getReturnType())) {
					imports.add(methodDeclaration.getReturnType());
				}
				imports.addAll(getRequiredImports(methodDeclaration.getAnnotations(),
						this::determineImports));
				imports.addAll(getRequiredImports(methodDeclaration.getParameters(),
						(parameter) -> Collections.singletonList(parameter.getType())));
				imports.addAll(getRequiredImports(
						methodDeclaration.getStatements().stream()
								.filter(GroovyExpressionStatement.class::isInstance)
								.map(GroovyExpressionStatement.class::cast)
								.map(GroovyExpressionStatement::getExpression)
								.filter(GroovyMethodInvocation.class::isInstance)
								.map(GroovyMethodInvocation.class::cast),
						(methodInvocation) -> Collections
								.singleton(methodInvocation.getTarget())));
			}
		}
		Collections.sort(imports);
		return new LinkedHashSet<>(imports);
	}

	private Collection<String> determineImports(Annotation annotation) {
		List<String> imports = new ArrayList<>();
		imports.add(annotation.getName());
		annotation.getAttributes().forEach((attribute) -> {
			if (attribute.getType() == Class.class) {
				imports.addAll(attribute.getValues());
			}
			if (Enum.class.isAssignableFrom(attribute.getType())) {
				imports.addAll(attribute.getValues().stream()
						.map((value) -> value.substring(0, value.lastIndexOf(".")))
						.collect(Collectors.toList()));
			}
		});
		return imports;
	}

	private <T> List<String> getRequiredImports(List<T> candidates,
			Function<T, Collection<String>> mapping) {
		return getRequiredImports(candidates.stream(), mapping);
	}

	private <T> List<String> getRequiredImports(Stream<T> candidates,
			Function<T, Collection<String>> mapping) {
		return candidates.map(mapping).flatMap(Collection::stream)
				.filter(this::requiresImport).collect(Collectors.toList());
	}

	private String getUnqualifiedName(String name) {
		if (!name.contains(".")) {
			return name;
		}
		return name.substring(name.lastIndexOf(".") + 1);
	}

	private boolean requiresImport(String name) {
		if (name == null || !name.contains(".")) {
			return false;
		}
		String packageName = name.substring(0, name.lastIndexOf('.'));
		return !"java.lang".equals(packageName);
	}

}
