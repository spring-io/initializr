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

import java.util.List;
import java.util.Objects;

import javax.lang.model.SourceVersion;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Type reference abstraction to refer to a {@link Class} that is not available on the
 * classpath.
 *
 * @author Stephane Nicoll
 */
public final class ClassName {

	private static final List<String> PRIMITIVE_NAMES = List.of("boolean", "byte", "short", "int", "long", "char",
			"float", "double", "void");

	private final String packageName;

	private final String simpleName;

	private final ClassName enclosingType;

	private String canonicalName;

	private ClassName(String packageName, String simpleName, ClassName enclosingType) {
		this.packageName = packageName;
		this.simpleName = simpleName;
		this.enclosingType = enclosingType;
	}

	/**
	 * Create a {@link ClassName} based on the specified fully qualified name. The format
	 * of the class name must follow {@linkplain Class#getName()}, in particular inner
	 * classes should be separated by a {@code $}.
	 * @param fqName the fully qualified name of the class
	 * @return a class name
	 */
	public static ClassName of(String fqName) {
		Assert.notNull(fqName, "'className' must not be null");
		if (!isValidClassName(fqName)) {
			throw new IllegalStateException("Invalid class name '" + fqName + "'");
		}
		if (!fqName.contains("$")) {
			return createClassName(fqName);
		}
		String[] elements = fqName.split("(?<!\\$)\\$(?!\\$)");
		ClassName className = createClassName(elements[0]);
		for (int i = 1; i < elements.length; i++) {
			className = new ClassName(className.getPackageName(), elements[i], className);
		}
		return className;
	}

	/**
	 * Create a {@link ClassName} based on the specified {@link Class}.
	 * @param type the class to wrap
	 * @return a class name
	 */
	public static ClassName of(Class<?> type) {
		return of(type.getName());
	}

	/**
	 * Return the fully qualified name.
	 * @return the reflection target name
	 */
	public String getName() {
		ClassName enclosingType = getEnclosingType();
		String simpleName = getSimpleName();
		return (enclosingType != null) ? (enclosingType.getName() + '$' + simpleName)
				: addPackageIfNecessary(simpleName);
	}

	/**
	 * Return the package name.
	 * @return the package name
	 */
	public String getPackageName() {
		return this.packageName;
	}

	/**
	 * Return the {@linkplain Class#getSimpleName() simple name}.
	 * @return the simple name
	 */
	public String getSimpleName() {
		return this.simpleName;
	}

	/**
	 * Return the enclosing class name, or {@code null} if this instance does not have an
	 * enclosing type.
	 * @return the enclosing type, if any
	 */
	public ClassName getEnclosingType() {
		return this.enclosingType;
	}

	/**
	 * Return the {@linkplain Class#getCanonicalName() canonical name}.
	 * @return the canonical name
	 */
	public String getCanonicalName() {
		if (this.canonicalName == null) {
			StringBuilder names = new StringBuilder();
			buildName(this, names);
			this.canonicalName = addPackageIfNecessary(names.toString());
		}
		return this.canonicalName;
	}

	private boolean isPrimitive() {
		return isPrimitive(getSimpleName());
	}

	private static boolean isPrimitive(String name) {
		return PRIMITIVE_NAMES.stream().anyMatch(name::startsWith);
	}

	private String addPackageIfNecessary(String part) {
		if (this.packageName.isEmpty() || this.packageName.equals("java.lang") && isPrimitive()) {
			return part;
		}
		return this.packageName + '.' + part;
	}

	private static boolean isValidClassName(String className) {
		for (String s : className.split("\\.", -1)) {
			String candidate = s.replace("[", "").replace("]", "");
			if (!SourceVersion.isIdentifier(candidate)) {
				return false;
			}
		}
		return true;
	}

	private static ClassName createClassName(String className) {
		int i = className.lastIndexOf('.');
		if (i != -1) {
			return new ClassName(className.substring(0, i), className.substring(i + 1), null);
		}
		else {
			String packageName = (isPrimitive(className)) ? "java.lang" : "";
			return new ClassName(packageName, className, null);
		}
	}

	private static void buildName(ClassName className, StringBuilder sb) {
		if (className == null) {
			return;
		}
		String typeName = (className.getEnclosingType() != null) ? "." + className.getSimpleName()
				: className.getSimpleName();
		sb.insert(0, typeName);
		buildName(className.getEnclosingType(), sb);
	}

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ClassName className)) {
			return false;
		}
		return getCanonicalName().equals(className.getCanonicalName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getCanonicalName());
	}

	@Override
	public String toString() {
		return getCanonicalName();
	}

}
