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
 * Base {@link Language} implementation.
 *
 * @author Stephane Nicoll
 */
public abstract class AbstractLanguage implements Language {

	private final String id;

	private final String jvmVersion;

	protected AbstractLanguage(String id, String jvmVersion) {
		this.id = id;
		this.jvmVersion = (jvmVersion != null) ? jvmVersion : DEFAULT_JVM_VERSION;
	}

	@Override
	public String id() {
		return this.id;
	}

	@Override
	public String jvmVersion() {
		return this.jvmVersion;
	}

	@Override
	public String toString() {
		return id();
	}

}
