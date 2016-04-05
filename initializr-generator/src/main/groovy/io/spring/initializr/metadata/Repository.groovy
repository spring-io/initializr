/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.metadata

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Define a repository to be represented in the generated project
 * if a dependency refers to it.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@EqualsAndHashCode
@ToString(includePackage = false)
class Repository {

	String name
	URL url
	boolean snapshotsEnabled

}
