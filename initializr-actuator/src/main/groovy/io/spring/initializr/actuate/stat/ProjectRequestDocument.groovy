/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.actuate.stat

import groovy.transform.ToString

/**
 * Define the statistics of a project generation.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@ToString(ignoreNulls = true, includePackage = false, includeNames = true)
class ProjectRequestDocument {

	long generationTimestamp

	String requestIp
	String requestIpv4
	String requestCountry
	String clientId
	String clientVersion

	String groupId
	String artifactId
	String packageName
	String bootVersion
	String javaVersion
	String language
	String packaging
	String type
	final List<String> dependencies = []

	String errorMessage
	boolean invalid
	boolean invalidJavaVersion
	boolean invalidLanguage
	boolean invalidPackaging
	boolean invalidType
	final List<String> invalidDependencies = []

}
