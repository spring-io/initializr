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

import java.util.Set;

import io.spring.initializr.generator.language.AbstractLanguage;
import io.spring.initializr.generator.language.Language;

/**
 * Groovy {@link Language}.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public final class GroovyLanguage extends AbstractLanguage {

	// See https://docs.groovy-lang.org/latest/html/documentation/#_keywords
	private static final Set<String> KEYWORDS = Set.of("abstract", "assert", "break", "case", "catch", "class", "const",
			"continue", "def", "default", "do", "else", "enum", "extends", "final", "finally", "for", "goto", "if",
			"implements", "import", "instanceof", "interface", "native", "new", "null", "non-sealed", "package",
			"public", "protected", "private", "return", "static", "strictfp", "super", "switch", "synchronized", "this",
			"threadsafe", "throw", "throws", "transient", "try", "while");

	/**
	 * Groovy {@link Language} identifier.
	 */
	public static final String ID = "groovy";

	/**
	 * Creates a new instance with the JVM version {@value #DEFAULT_JVM_VERSION}.
	 */
	public GroovyLanguage() {
		this(DEFAULT_JVM_VERSION);
	}

	/**
	 * Creates a new instance.
	 * @param jvmVersion the JVM version
	 */
	public GroovyLanguage(String jvmVersion) {
		super(ID, jvmVersion, "groovy");
	}

	@Override
	public boolean supportsEscapingKeywordsInPackage() {
		return false;
	}

	@Override
	public boolean isKeyword(String input) {
		return KEYWORDS.contains(input);
	}

}
