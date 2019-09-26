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
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.spring.initializr.generator.project.DefaultProjectAssetGenerator;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectAssetGenerator;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.ProjectGenerationContext;
import io.spring.initializr.generator.project.ProjectGenerator;

/**
 * A tester class for {@link ProjectGenerator}. Contrary to {@link ProjectAssetTester},
 * standard {@link ProjectGenerationConfiguration} classes are processed
 * automatically.Extra beans can be added using {@linkplain #withBean(Class, Supplier)
 * bean registration}, a {@linkplain #withConfiguration(Class[]) configuration class} or
 * via the {@linkplain #withContextInitializer(Consumer) customization of the project
 * generation context}.
 *
 * @author Stephane Nicoll
 */
public class ProjectGeneratorTester extends AbstractProjectGenerationTester<ProjectGeneratorTester> {

	public ProjectGeneratorTester() {
		super();
	}

	private ProjectGeneratorTester(Map<Class<?>, Supplier<?>> beanDefinitions,
			Consumer<ProjectGenerationContext> contextInitializer,
			Consumer<MutableProjectDescription> descriptionCustomizer) {
		super(beanDefinitions, contextInitializer, descriptionCustomizer);
	}

	@Override
	protected ProjectGeneratorTester newInstance(Map<Class<?>, Supplier<?>> beanDefinitions,
			Consumer<ProjectGenerationContext> contextInitializer,
			Consumer<MutableProjectDescription> descriptionCustomizer) {
		return new ProjectGeneratorTester(beanDefinitions, contextInitializer, descriptionCustomizer);
	}

	public ProjectStructure generate(MutableProjectDescription description) {
		return invokeProjectGeneration(description, (contextInitializer) -> {
			Path directory = new ProjectGenerator(contextInitializer).generate(description,
					new DefaultProjectAssetGenerator());
			return new ProjectStructure(directory);
		});
	}

	public <T> T generate(MutableProjectDescription description, ProjectAssetGenerator<T> projectAssetGenerator) {
		return invokeProjectGeneration(description, (contextInitializer) -> new ProjectGenerator(contextInitializer)
				.generate(description, projectAssetGenerator));
	}

}
