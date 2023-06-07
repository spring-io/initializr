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

package io.spring.initializr.generator.version;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that provides java version constant for a particular jvm version.
 *
 * @author prithvi singh
 */
public final class JavaVersionConstantProvider {

	private static final Map<String, String> sourceCompatibilityToVersionConstant = new HashMap<>();

	private JavaVersionConstantProvider() {
	}

	public static String forVersion(String jvmVersion) {
		return sourceCompatibilityToVersionConstant.computeIfAbsent(jvmVersion, (key) -> {
			StringBuilder sb = new StringBuilder("JavaVersion.");
			if (jvmVersion == null) {
				return sb.append("VERSION_11").toString();
			}
			int generation = (jvmVersion.startsWith("1.") ? Integer.parseInt(jvmVersion.substring(2))
					: Integer.parseInt(jvmVersion));
			if (generation >= 1 && generation <= 10) {
				sb.append("VERSION_1_").append(generation);
			}
			else if (generation <= 17) {
				sb.append("VERSION_").append(generation);
			}
			else {
				sb.append("VERSION_HIGHER");
			}
			return sb.toString();
		});
	}

}
