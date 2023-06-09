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

import java.util.ArrayList;
import java.util.List;

import io.spring.initializr.generator.io.IndentingWriter;

import org.springframework.util.ClassUtils;

/**
 * A fragment of code, potentially containing declarations, or statements. CodeBlocks are
 * not validated.
 * <p>
 * Code blocks support placeholders identified by {@code $}. The following placeholders
 * are supported:
 * <ul>
 * <li>{@code $L} emits a literal value. Arguments for literals may be plain String,
 * primitives, another {@code CodeBlock}, or any type where the {@code toString()}
 * representation can be used.
 * <li>{@code $S} escapes the value as a string, wraps it with double quotes, and emits
 * that. Emit {@code "null"} if the value is {@code null}. Does not handle multi-line
 * strings.
 * <li>{@code $T} emits a type reference. Arguments for types may be plain
 * {@linkplain Class classes}, fully qualified class names, and fully qualified
 * functions.</li>
 * <li>{@code $$} emits a dollar sign.
 * <li>{@code $]} ends a statement and emits the configured
 * {@linkplain FormattingOptions#statementSeparator() statement separator}.
 * </ul>
 * <p>
 * Code blocks can be {@linkplain #write(IndentingWriter, FormattingOptions) rendered}
 * using an {@link IndentingWriter} and {@link FormattingOptions}.
 * <p>
 * This class is heavily inspired by JavaPoet.
 *
 * @author Stephane Nicoll
 */
public final class CodeBlock {

	/**
	 * Standard {@link FormattingOptions} for Java.
	 */
	public static final FormattingOptions JAVA_FORMATTING_OPTIONS = new JavaFormattingOptions();

	private final List<String> parts;

	private final List<Object> args;

	private final List<String> imports;

	private CodeBlock(Builder builder) {
		this.parts = List.copyOf(builder.parts);
		this.args = List.copyOf(builder.args);
		this.imports = List.copyOf(builder.imports);
	}

	/**
	 * Return the imports this instance contributes.
	 * @return the imports.
	 */
	public List<String> getImports() {
		return this.imports;
	}

	/**
	 * Write this instance using the specified writer.
	 * @param writer the writer to use
	 * @param options the formatting options to use
	 */
	public void write(IndentingWriter writer, FormattingOptions options) {
		int argIndex = 0;
		for (String part : this.parts) {
			switch (part) {
				case "$L" -> {
					Object value = this.args.get(argIndex++);
					if (value instanceof CodeBlock code) {
						code.write(writer, options);
					}
					else {
						writer.print(String.valueOf(value));
					}
				}
				case "$S" -> {
					String value = (String) this.args.get(argIndex++);
					String valueToEmit = (value != null) ? quote(value) : "null";
					writer.print(valueToEmit);
				}
				case "$T" -> {
					String className = (String) this.args.get(argIndex++);
					writer.print(className);
				}
				case "$]" -> writer.println(options.statementSeparator());
				case "$$" -> writer.print("$");
				default -> writer.print(part);
			}
		}
	}

	private static String quote(String value) {
		StringBuilder result = new StringBuilder(value.length() + 2);
		result.append('"');
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '\'') {
				result.append("'");
				continue;
			}
			if (c == '\"') {
				result.append("\\\"");
				continue;
			}
			result.append(c);
		}
		result.append('"');
		return result.toString();
	}

	/**
	 * Create a code block using the specified code and optional arguments. To create a
	 * single statement, consider {@link #ofStatement(String, Object...)} instead.
	 * @param format the code
	 * @param args the arguments, if any
	 * @return a new instance
	 * @see #builder()
	 */
	public static CodeBlock of(String format, Object... args) {
		return new Builder().add(format, args).build();
	}

	/**
	 * Create a code block with a single statement using the specified code and optional
	 * arguments.
	 * @param format the statement
	 * @param args the arguments, if any
	 * @return a new instance
	 * @see #builder()
	 */
	public static CodeBlock ofStatement(String format, Object... args) {
		return new Builder().addStatement(format, args).build();
	}

	/**
	 * Initialize a new builder.
	 * @return a code block builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private final List<String> parts = new ArrayList<>();

		private final List<Object> args = new ArrayList<>();

		private final List<String> imports = new ArrayList<>();

		private Builder() {
		}

		/**
		 * Add the specified {@link CodeBlock}, without any extra line or statement
		 * separator.
		 * @param codeBlock the code to add
		 * @return this for method chaining
		 */
		public Builder add(CodeBlock codeBlock) {
			this.parts.addAll(codeBlock.parts);
			this.args.addAll(codeBlock.args);
			this.imports.addAll(codeBlock.imports);
			return this;
		}

		/**
		 * Add more code using the specified code and optional arguments.
		 * @param format the code
		 * @param args the arguments, if any
		 * @return this for method chaining
		 */
		public Builder add(String format, Object... args) {
			int relativeParameterCount = 0;

			for (int p = 0; p < format.length();) {
				if (format.charAt(p) != '$') {
					int nextP = format.indexOf('$', p + 1);
					nextP = (nextP != -1) ? nextP : format.length();
					this.parts.add(format.substring(p, nextP));
					p = nextP;
					continue;
				}
				p++; // placeholder
				if (p >= format.length()) {
					throw new IllegalArgumentException("Should not end with '$': '%s'".formatted(format));
				}
				char placeHolderType = format.charAt(p++);
				if (!isNoArgPlaceholder(placeHolderType)) {
					int index = relativeParameterCount;
					relativeParameterCount++;
					if (index >= args.length) {
						throw new IllegalArgumentException("Argument mismatch for '%s', expected at least %s %s, got %s"
							.formatted(format, relativeParameterCount,
									(relativeParameterCount > 1) ? "arguments" : "argument", args.length));
					}
					addArgument(format, placeHolderType, args[index]);
				}
				this.parts.add("$" + placeHolderType);
			}
			if (relativeParameterCount != args.length) {
				throw new IllegalArgumentException(
						"Argument mismatch for '%s', expected %s %s, got %s".formatted(format, relativeParameterCount,
								(relativeParameterCount > 1) ? "arguments" : "argument", args.length));
			}
			return this;

		}

		private boolean isNoArgPlaceholder(char c) {
			return c == '$' || c == ']';
		}

		private void addArgument(String format, char c, Object arg) {
			switch (c) {
				case 'L' -> this.args.add(arg);
				case 'S' -> this.args.add(argToString(arg));
				case 'T' -> this.args.add(argToType(arg));
				default -> throw new IllegalArgumentException(
						String.format("Unsupported placeholder '$%s' for '%s'", c, format));
			}
		}

		private String argToString(Object o) {
			return (o != null) ? String.valueOf(o) : null;
		}

		private String argToType(Object arg) {
			if (arg instanceof Class type) {
				this.imports.add(type.getName());
				return type.getSimpleName();
			}
			if (arg instanceof String className) {
				this.imports.add(className);
				return ClassUtils.getShortName(className);
			}
			throw new IllegalArgumentException("Failed to extract type from '%s'".formatted(arg));
		}

		/**
		 * Add the specified {@link CodeBlock} as a statement.
		 * @param codeBlock the code to add
		 * @return this for method chaining
		 */
		public Builder addStatement(CodeBlock codeBlock) {
			add(codeBlock);
			this.parts.add("$]");
			return this;
		}

		/**
		 * Add more code using the specified code and optional arguments and indicate that
		 * this statement is finished.
		 * @param format the code
		 * @param args the arguments, if any
		 * @return this for method chaining
		 */
		public Builder addStatement(String format, Object... args) {
			add(format, args);
			this.parts.add("$]");
			return this;
		}

		/**
		 * Build a {@link CodeBlock} with the current state of this builder.
		 * @return a {@link CodeBlock}
		 */
		public CodeBlock build() {
			return new CodeBlock(this);
		}

	}

	/**
	 * Strategy interface to customize formatting of generated code block.
	 */
	public interface FormattingOptions {

		/**
		 * Return the separator to use to end a statement.
		 * @return the statement separator
		 */
		String statementSeparator();

	}

	private static class JavaFormattingOptions implements FormattingOptions {

		@Override
		public String statementSeparator() {
			return ";";
		}

	}

}
