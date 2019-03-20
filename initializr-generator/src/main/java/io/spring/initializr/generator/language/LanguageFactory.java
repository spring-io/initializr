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

package io.spring.initializr.generator.language;

/**
 * A factory for creating a {@link Language}.
 *
 * @author Andy Wilkinson
 */
public interface LanguageFactory {

	/**
	 * Creates and returns a {@link Language} for the given id and JVM version. If the
	 * factory does not recognise the given {@code id}, {@code null} should be returned.
	 * @param id the id of the language
	 * @param jvmVersion the jvm version or {@code null} to use the default
	 * @return the language or {@code null}
	 */
	Language createLanguage(String id, String jvmVersion);

}
