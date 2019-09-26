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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectAssetGenerator;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectDirectoryFactory;
import io.spring.initializr.generator.project.ProjectGenerationContext;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ContextConsumer;

/**
 * A tester for project asset that does not detect available {@link ProjectContributor
 * contributors} and does not register any bean to the context. Contributors can be added
 * using {@linkplain #withBean(Class, Supplier) bean registration}, a
 * {@linkplain #withConfiguration(Class[]) configuration class} or via the
 * {@linkplain #withContextInitializer(Consumer) customization of the project generation
 * context}.
 * <p>
 * Alternatively, the context can be queried the same way {@link ApplicationContextRunner}
 * works by {@linkplain #configure(MutableProjectDescription, ContextConsumer)
 * configuring} the context.
 *
 * @author Stephane Nicoll
 */
public class ProjectAssetTester extends AbstractProjectGenerationTester<ProjectAssetTester> {

	public ProjectAssetTester() {
		super();
	}

	private ProjectAssetTester(Map<Class<?>, Supplier<?>> beanDefinitions,
			Consumer<ProjectGenerationContext> contextInitializer,
			Consumer<MutableProjectDescription> descriptionCustomizer) {
		super(beanDefinitions, contextInitializer, descriptionCustomizer);
	}

	@Override
	protected ProjectAssetTester newInstance(Map<Class<?>, Supplier<?>> beanDefinitions,
			Consumer<ProjectGenerationContext> contextInitializer,
			Consumer<MutableProjectDescription> descriptionCustomizer) {
		return new ProjectAssetTester(beanDefinitions, contextInitializer, descriptionCustomizer);
	}

	/**
	 * Configure a {@link ProjectGenerationContext} using the specified
	 * {@code description} and use the {@link ContextConsumer} to assert the context.
	 * @param description the description of the project to configure
	 * @param consumer the consumer of the created {@link ProjectGenerationContext}
	 * @see ApplicationContextRunner#run(ContextConsumer)
	 */
	public void configure(MutableProjectDescription description,
			ContextConsumer<AssertableApplicationContext> consumer) {
		invokeProjectGeneration(description, (contextInitializer) -> {
			new ApplicationContextRunner(ProjectGenerationContext::new).withInitializer((ctx) -> {
				ProjectGenerationContext projectGenerationContext = (ProjectGenerationContext) ctx;
				projectGenerationContext.registerBean(ProjectDescription.class, () -> description);
				contextInitializer.accept(projectGenerationContext);
			}).run(consumer);
			return null;
		});
	}

	/**
	 * Generate a project asset using the specified {@link ProjectAssetGenerator}.
	 * @param description the description of the project to generate
	 * @param projectAssetGenerator the {@link ProjectAssetGenerator} to invoke
	 * @param <T> the project asset type
	 * @return the project asset
	 * @see #withConfiguration(Class[])
	 */
	public <T> T generate(MutableProjectDescription description, ProjectAssetGenerator<T> projectAssetGenerator) {
		return invokeProjectGeneration(description, (contextInitializer) -> {
			try (ProjectGenerationContext context = new ProjectGenerationContext()) {
				context.registerBean(ProjectDescription.class, () -> description);
				contextInitializer.accept(context);
				context.refresh();
				return projectAssetGenerator.generate(context);
			}
		});
	}

	/**
	 * Generate a project structure using only explicitly configured
	 * {@link ProjectContributor contributors}.
	 * @param description the description of the project to generateProject
	 * @return the {@link ProjectStructure} of the generated project
	 * @see #withConfiguration(Class[])
	 */
	public ProjectStructure generate(MutableProjectDescription description) {
		return generate(description, runAllAvailableContributors());
	}

	private ProjectAssetGenerator<ProjectStructure> runAllAvailableContributors() {
		return (context) -> {
			Path projectDirectory = context.getBean(ProjectDirectoryFactory.class)
					.createProjectDirectory(context.getBean(ProjectDescription.class));
			List<ProjectContributor> projectContributors = context.getBeanProvider(ProjectContributor.class)
					.orderedStream().collect(Collectors.toList());
			for (ProjectContributor projectContributor : projectContributors) {
				projectContributor.contribute(projectDirectory);
			}
			return new ProjectStructure(projectDirectory);
		};
	}

}
