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

package io.spring.initializr.generator.language.kotlin;

import java.util.Set;

import io.spring.initializr.generator.language.AbstractLanguage;
import io.spring.initializr.generator.language.Language;

/**
 * Kotlin {@link Language}.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public final class KotlinLanguage extends AbstractLanguage {

	// Taken from https://kotlinlang.org/docs/keyword-reference.html#hard-keywords
	// except keywords contains `!` or `?` because they should be handled as invalid
	// package names already
	private static final Set<String> KEYWORDS = Set.of("package", "as", "typealias", "class", "this", "super", "val",
			"var", "fun", "for", "null", "true", "false", "is", "in", "throw", "return", "break", "continue", "object",
			"if", "try", "else", "while", "do", "when", "interface", "typeof");

	/**
	 * Kotlin {@link Language} identifier.
	 */
	public static final String ID = "kotlin";

	/**
	 * Creates a new instance with the JVM version {@value #DEFAULT_JVM_VERSION}.
	 */
	public KotlinLanguage() {
		this(DEFAULT_JVM_VERSION);
	}

	/**
	 * Creates a new instance.
	 * @param jvmVersion the JVM version
	 */
	public KotlinLanguage(String jvmVersion) {
		super(ID, jvmVersion, "kt");
	}

	@Override
	public boolean supportsEscapingKeywordsInPackage() {
		return true;
	}

	@Override
	public boolean isKeyword(String input) {
		return KEYWORDS.contains(input);
	}

}
