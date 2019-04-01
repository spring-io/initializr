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

/**
 * A String type in the Groovy language.
 *
 * @author Matt Berteaux
 */
public final class GroovyString implements GroovyExpression {

	/**
	 * The class name of this type.
	 */
	public static final String CLASS_NAME = "java.lang.String";

	private final String value;

	private GroovyString(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "\"" + value + "\"";
	}

	public static GroovyString stringValue(String value) {
		return new GroovyString(value);
	}

}
