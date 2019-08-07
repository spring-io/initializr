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

package io.spring.initializr.doc.generator.project;

import io.spring.initializr.generator.project.ProjectGenerator;

import org.springframework.context.ApplicationContext;

/**
 * Setup a {@link ProjectGenerator} with a parent context and a custom contributor.
 *
 * @author Stephane Nicoll
 */
public class ProjectGeneratorSetupExample {

	// tag::code[]
	public ProjectGenerator createProjectGenerator(ApplicationContext appContext) {
		return new ProjectGenerator((context) -> {
			context.setParent(appContext);
			context.registerBean(SampleContributor.class, SampleContributor::new);
		});
	}
	// end::code[]

}
