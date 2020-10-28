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

import java.util.List;
import java.util.stream.Collectors;

import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.SourceCodeWriter;

/**
 * A {@link SourceCodeWriter} that writes {@link SourceCode} in Java.
 *
 * @author Andy Wilkinson
 * @author Matt Berteaux
 * @author Tad Sanden
 */
public class JavaInterfaceWriter extends JavaCodeWriter {

	public JavaInterfaceWriter(IndentingWriterFactory indentingWriterFactory) {
		super(indentingWriterFactory);
	}

	protected void writeTopTypeDeclaration(IndentingWriter writer, JavaTypeDeclaration type) {
		writer.print("interface " + type.getName());
		writer.println(" {");
		writer.println();
	}

	protected void writeMethodDeclaration(IndentingWriter writer, JavaMethodDeclaration methodDeclaration) {
		writeAnnotations(writer, methodDeclaration);
		writeModifiers(writer, METHOD_MODIFIERS, methodDeclaration.getModifiers());
		writer.print(getUnqualifiedName(methodDeclaration.getReturnType()) + " " + methodDeclaration.getName() + "(");
		List<Parameter> parameters = methodDeclaration.getParameters();
		if (!parameters.isEmpty()) {
			writer.print(parameters.stream()
					.map((parameter) -> getUnqualifiedName(parameter.getType()) + " " + parameter.getName())
					.collect(Collectors.joining(", ")));
		}
		writer.println(");");
		writer.println();
	}

}
