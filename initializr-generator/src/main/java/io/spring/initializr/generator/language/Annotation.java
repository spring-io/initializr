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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.language.CodeBlock.FormattingOptions;

import org.springframework.util.ClassUtils;

/**
 * An annotation.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public final class Annotation {

	private final ClassName className;

	private final List<Attribute> attributes;

	private final List<String> imports;

	private Annotation(Builder builder) {
		this.className = builder.className;
		this.attributes = List.copyOf(builder.attributes.values());
		this.imports = List.copyOf(builder.imports);
	}

	/**
	 * Return the {@link ClassName} of the annotation.
	 * @return the class name
	 */
	public ClassName getClassName() {
		return this.className;
	}

	/**
	 * Return the {@linkplain Attribute attributes} of the annotation or an empty list if
	 * the annotation has no defined attribute.
	 * @return the attributes
	 */
	public List<Attribute> getAttributes() {
		return this.attributes;
	}

	/**
	 * Return the imports this instance contributes.
	 * @return the imports.
	 */
	public List<String> getImports() {
		return this.imports;
	}

	/**
	 * Initialize an annotation {@link Builder} for the specified class name.
	 * @param className the class name of the annotation
	 * @return a builder
	 */
	public static Builder of(ClassName className) {
		return new Builder(className);
	}

	/**
	 * Write this annotation using the specified writer.
	 * @param writer the writer to use
	 * @param options the formatting options to use
	 */
	public void write(IndentingWriter writer, FormattingOptions options) {
		new AnnotationWriter(writer, options).write(this);
	}

	/**
	 * Builder for creating an {@link Annotation}.
	 */
	public static final class Builder {

		private final ClassName className;

		private final Set<String> imports = new HashSet<>();

		private final Map<String, Attribute> attributes = new LinkedHashMap<>();

		Builder(ClassName name) {
			this.className = name;
			if (!name.getPackageName().isEmpty()) {
				this.imports.add(name.getName());
			}
		}

		Builder(Builder copy) {
			this.className = copy.className;
			this.imports.addAll(copy.imports);
			this.attributes.putAll(copy.attributes);
		}

		/**
		 * Set the attribute with the specified name with the specified values. If the
		 * attribute exists, it is replaced by the specified values.
		 * @param name the name of the attribute
		 * @param values the values to associate with the attribute
		 * @return this for method chaining
		 */
		public Builder set(String name, Object... values) {
			AttributeType type = determineAttributeType(values);
			this.attributes.put(name, new Attribute(name, type, values));
			return this;
		}

		/**
		 * Add the specified values to the attribute with the specified name. If the
		 * attribute does not exist, it is created with the specified values.
		 * @param name the name of the attribute
		 * @param values the values to add to the attribute
		 * @return this for method chaining
		 */
		public Builder add(String name, Object... values) {
			AttributeType type = determineAttributeType(values);
			this.attributes.merge(name, new Attribute(name, type, values), this::append);
			return this;
		}

		/**
		 * Reset the state of the builder to the state of the specified annotation. This
		 * effectively replaces all customizations by the specified annotation.
		 * @param annotation the annotation to reset to
		 * @return this for method chaining
		 */
		public Builder from(Annotation annotation) {
			if (!this.className.equals(annotation.className)) {
				throw new IllegalArgumentException();
			}
			this.imports.clear();
			this.imports.addAll(annotation.imports);
			this.attributes.clear();
			annotation.attributes.forEach((attribute) -> this.attributes.put(attribute.getName(), attribute));
			return this;
		}

		private Attribute append(Attribute existing, Attribute additional) {
			AttributeType typeToUse = AttributeType.getMostSpecificType(existing.type, additional.type);
			return new Attribute(existing.name, typeToUse,
					Stream.concat(existing.values.stream(), additional.values.stream()).toArray());
		}

		private AttributeType determineAttributeType(Object... values) {
			AttributeType attributeType = AttributeType.of(values);
			Arrays.stream(values).map(attributeType::getImports).forEach(this.imports::addAll);
			return attributeType;
		}

		public Annotation build() {
			return new Annotation(this);
		}

	}

	/**
	 * Define an attribute of an annotation.
	 */
	public static final class Attribute {

		private final String name;

		private final AttributeType type;

		private final List<Object> values;

		private Attribute(String name, AttributeType type, Object... values) {
			this.name = name;
			this.type = type;
			this.values = Arrays.asList(values);
		}

		/**
		 * Return the attribute name.
		 * @return the attribute name
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Return the attribute type.
		 * @return the attribute type
		 */
		public AttributeType getType() {
			return this.type;
		}

		/**
		 * Return the values.
		 * @return the values
		 */
		public List<Object> getValues() {
			return this.values;
		}

	}

	/**
	 * Type of an attribute.
	 */
	public enum AttributeType {

		/**
		 * Primitive type.
		 */
		PRIMITIVE,

		/**
		 * String type.
		 */
		STRING,

		/**
		 * Class type.
		 */
		CLASS() {
			@Override
			protected Collection<String> getImports(Object value) {
				if (value instanceof Class type) {
					return List.of(type.getName());
				}
				else if (value instanceof ClassName name) {
					return List.of(name.getName());
				}
				return super.getImports(value);
			}
		},

		/**
		 * Enum type.
		 */
		ENUM() {
			@Override
			protected Collection<String> getImports(Object value) {
				if (value instanceof Enum enumeration) {
					return List.of(enumeration.getClass().getName());
				}
				return super.getImports(value);
			}
		},

		/**
		 * Annotation type.
		 */
		ANNOTATION() {
			@Override
			protected Collection<String> getImports(Object value) {
				if (value instanceof Annotation annotation) {
					return annotation.getImports();
				}
				return super.getImports(value);
			}
		},

		/**
		 * Code type.
		 */
		CODE {
			@Override
			protected boolean isCompatible(AttributeType attributeType) {
				return true;
			}
		};

		protected boolean isCompatible(AttributeType attributeType) {
			return this.equals(attributeType) || attributeType == AttributeType.CODE;
		}

		protected Collection<String> getImports(Object value) {
			if (value instanceof CodeBlock codeBlock) {
				return codeBlock.getImports();
			}
			return Collections.emptyList();
		}

		static AttributeType getMostSpecificType(AttributeType left, AttributeType right) {
			if (!left.isCompatible(right)) {
				throw new IllegalArgumentException(
						"Incompatible type. '%s' is not compatible with '%s'".formatted(left, right));
			}
			return (left == CODE) ? right : left;
		}

		static AttributeType of(Object... values) {
			List<AttributeType> types = Arrays.stream(values)
				.map(AttributeType::determineAttributeType)
				.filter((type) -> type != CODE)
				.distinct()
				.toList();
			if (types.size() > 1) {
				throw new IllegalArgumentException("Parameter value must not have mixed types, got ["
						+ types.stream().map(AttributeType::name).collect(Collectors.joining(", ")) + "]");
			}
			return (types.size() == 1) ? types.get(0) : CODE;
		}

		private static AttributeType determineAttributeType(Object value) {
			if (ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
				return PRIMITIVE;
			}
			else if (value instanceof CharSequence) {
				return STRING;
			}
			else if (value instanceof Class<?> || value instanceof ClassName) {
				return CLASS;
			}
			else if (value instanceof Enum<?>) {
				return ENUM;
			}
			else if (value instanceof Annotation) {
				return ANNOTATION;
			}
			else if (value instanceof CodeBlock) {
				return CODE;
			}
			else {
				throw new IllegalArgumentException(
						"Incompatible type. Found: '%s', required: primitive, String, Class, an Enum, an Annotation, or a CodeBlock"
							.formatted(value.getClass().getName()));
			}
		}

	}

	private static class AnnotationWriter {

		private final IndentingWriter writer;

		private final FormattingOptions formattingOptions;

		AnnotationWriter(IndentingWriter writer, FormattingOptions formattingOptions) {
			this.writer = writer;
			this.formattingOptions = formattingOptions;
		}

		void write(Annotation annotation) {
			generateAnnotationCode(annotation).write(this.writer, this.formattingOptions);
		}

		private CodeBlock generateAnnotationCode(Annotation annotation) {
			CodeBlock.Builder code = CodeBlock.builder();
			code.add("@$T", annotation.className);
			if (annotation.attributes.size() == 1 && annotation.attributes.get(0).getName().equals("value")) {
				code.add("($L)", generateAttributeValuesCode(annotation.attributes.get(0)));
			}
			else if (!annotation.attributes.isEmpty()) {
				CodeBlock attributes = annotation.attributes.stream()
					.map(this::generateAttributeCode)
					.collect(CodeBlock.joining(", "));
				code.add("($L)", attributes);
			}
			return code.build();
		}

		private CodeBlock generateAttributeCode(Attribute attribute) {
			return CodeBlock.of("$L = $L", attribute.name, generateAttributeValuesCode(attribute));
		}

		private CodeBlock generateAttributeValuesCode(Attribute attribute) {
			CodeBlock[] values = attribute.values.stream()
				.map((value) -> generateValueCode(attribute.type, value))
				.toArray(CodeBlock[]::new);
			return (values.length == 1) ? values[0] : this.formattingOptions.arrayOf(values);
		}

		private CodeBlock generateValueCode(AttributeType attributeType, Object value) {
			Class<?> valueType = ClassUtils.resolvePrimitiveIfNecessary(value.getClass());
			// CodeBlock can be anything
			if (value instanceof CodeBlock codeBlock) {
				return codeBlock;
			}
			return switch (attributeType) {
				case PRIMITIVE -> {
					if (valueType == Character.class) {
						yield CodeBlock.of("'$L'", value);
					}
					else {
						yield CodeBlock.of("$L", value);
					}
				}
				case STRING -> CodeBlock.of("$S", value);
				case CLASS -> {
					ClassName className = (value instanceof Class clazz) ? ClassName.of(clazz) : (ClassName) value;
					yield this.formattingOptions.classReference(className);
				}
				case ENUM -> {
					Enum<?> enumValue = (Enum<?>) value;
					yield CodeBlock.of("$T.$L", enumValue.getClass(), enumValue.name());
				}
				case ANNOTATION -> generateAnnotationCode((Annotation) value);
				case CODE -> (CodeBlock) value;
			};
		}

	}

}
