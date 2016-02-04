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

package io.spring.initializr.generator

/**
 * The base settings of a project request. Only these can be bound by user's
 * input.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class BasicProjectRequest {

	List<String> style = []
	List<String> dependencies = []
	String name
	String type
	String description
	String groupId
	String artifactId
	String version
	String bootVersion
	String packaging
	String applicationName
	String language
	String packageName
	String javaVersion

	// The base directory to create in the archive - no baseDir by default
	String baseDir

}
