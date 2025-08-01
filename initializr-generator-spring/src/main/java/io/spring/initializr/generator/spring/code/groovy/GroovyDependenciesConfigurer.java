/*
 * Copyright 2012 - present the original author or authors.
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

package io.spring.initializr.generator.spring.code.groovy;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * {@link BuildCustomizer} that adds the dependencies required by projects written in
 * Groovy.
 *
 * @author Stephane Nicoll
 */
class GroovyDependenciesConfigurer implements BuildCustomizer<Build> {

	private final boolean isUsingGroovy4;

	GroovyDependenciesConfigurer(boolean isUsingGroovy4) {
		this.isUsingGroovy4 = isUsingGroovy4;
	}

	@Override
	public void customize(Build build) {
		String groupId = this.isUsingGroovy4 ? "org.apache.groovy" : "org.codehaus.groovy";
		build.dependencies().add("groovy", groupId, "groovy", DependencyScope.COMPILE);
	}

}
