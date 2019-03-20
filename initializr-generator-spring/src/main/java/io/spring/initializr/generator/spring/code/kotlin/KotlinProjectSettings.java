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

package io.spring.initializr.generator.spring.code.kotlin;

import java.util.Collections;
import java.util.List;

/**
 * Commons settings for Kotlin projects.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public interface KotlinProjectSettings {

	/**
	 * Return the version of Kotlin to use.
	 * @return the kotlin version
	 */
	String getVersion();

	/**
	 * Return the {@code jvmTarget} to use.
	 * @return the jvm target
	 */
	default String getJvmTarget() {
		return "1.8";
	}

	/**
	 * Return the compiler arguments.
	 * @return the compiler arguments
	 */
	default List<String> getCompilerArgs() {
		return Collections.singletonList("-Xjsr305=strict");
	}

}
