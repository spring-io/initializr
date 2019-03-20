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

package io.spring.initializr.generator.test.project;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.project.DefaultProjectAssetGenerator;
import io.spring.initializr.generator.project.ProjectAssetGenerator;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationContext;
import io.spring.initializr.generator.project.ProjectGenerator;

/**
 * A tester class for {@link ProjectGenerator}.
 *
 * @author Stephane Nicoll
 */
public class ProjectGeneratorTester
		extends AbstractProjectGenerationTester<ProjectGeneratorTester> {

	private ProjectGeneratorTester(Map<Class<?>, Supplier<?>> beanDefinitions,
			Consumer<ProjectGenerationContext> contextInitializer,
			Consumer<ProjectDescription> descriptionCustomizer) {
		super(beanDefinitions, contextInitializer, descriptionCustomizer);
	}

	public ProjectGeneratorTester() {
		super(defaultBeans());
	}

	private static Map<Class<?>, Supplier<?>> defaultBeans() {
		Map<Class<?>, Supplier<?>> beans = new HashMap<>();
		beans.put(IndentingWriterFactory.class,
				() -> IndentingWriterFactory.create(new SimpleIndentStrategy("    ")));
		beans.put(MustacheTemplateRenderer.class,
				() -> new MustacheTemplateRenderer("classpath:/templates"));
		return beans;
	}

	@Override
	protected ProjectGeneratorTester newInstance(
			Map<Class<?>, Supplier<?>> beanDefinitions,
			Consumer<ProjectGenerationContext> contextInitializer,
			Consumer<ProjectDescription> descriptionCustomizer) {
		return new ProjectGeneratorTester(beanDefinitions, contextInitializer,
				descriptionCustomizer);
	}

	public ProjectStructure generate(ProjectDescription description) {
		return invokeProjectGeneration(description, (contextInitializer) -> {
			Path directory = new ProjectGenerator(contextInitializer)
					.generate(description, new DefaultProjectAssetGenerator());
			return new ProjectStructure(directory);
		});
	}

	public <T> T generate(ProjectDescription description,
			ProjectAssetGenerator<T> projectAssetGenerator) {
		return invokeProjectGeneration(description,
				(contextInitializer) -> new ProjectGenerator(contextInitializer)
						.generate(description, projectAssetGenerator));
	}

}
