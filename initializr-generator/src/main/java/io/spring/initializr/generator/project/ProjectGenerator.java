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

package io.spring.initializr.generator.project;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * Main entry point for project generation that processes a {@link ProjectDescription} by
 * creating a dedicated {@link ProjectGenerationContext} with all available
 * {@link ProjectGenerationConfiguration} classes. Once the context has been started for a
 * particular {@link ProjectDescription}, a {@link ProjectAssetGenerator} can query it and
 * generate an appropriate asset (for instance, a project structure on disk).
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class ProjectGenerator {

	private final Consumer<ProjectGenerationContext> contextConsumer;

	private final Supplier<? extends ProjectGenerationContext> contextFactory;

	/**
	 * Create an instance with a customizer for the project generator application context
	 * and a factory for the {@link ProjectGenerationContext}.
	 * @param contextConsumer a consumer of the project generation context after
	 * contributors and the {@link ProjectDescription} have been registered but before it
	 * is refreshed
	 * @param contextFactory the factory to use to create {@link ProjectGenerationContext}
	 * instances
	 */
	public ProjectGenerator(Consumer<ProjectGenerationContext> contextConsumer,
			Supplier<? extends ProjectGenerationContext> contextFactory) {
		this.contextConsumer = contextConsumer;
		this.contextFactory = contextFactory;
	}

	/**
	 * Create an instance with a customizer for the {@link ProjectGenerationContext} and a
	 * default factory for the {@link ProjectGenerationContext} that disables bean
	 * definition overriding.
	 * @param contextConsumer a consumer of the project generation context after
	 * contributors and the {@link ProjectDescription} have been registered but before it
	 * is refreshed
	 * @see GenericApplicationContext#setAllowBeanDefinitionOverriding(boolean)
	 */
	public ProjectGenerator(Consumer<ProjectGenerationContext> contextConsumer) {
		this(contextConsumer, defaultContextFactory());
	}

	private static Supplier<ProjectGenerationContext> defaultContextFactory() {
		return () -> {
			ProjectGenerationContext context = new ProjectGenerationContext();
			context.setAllowBeanDefinitionOverriding(false);
			return context;
		};
	}

	/**
	 * Generate project assets using the specified {@link ProjectAssetGenerator} for the
	 * specified {@link ProjectDescription}.
	 * <p>
	 * Create a dedicated {@link ProjectGenerationContext} using the supplied
	 * {@link #ProjectGenerator(Consumer, Supplier) contextFactory} and then apply the
	 * following:
	 * <ul>
	 * <li>Register a {@link ProjectDescription} bean based on the given
	 * {@code description} post-processed by available
	 * {@link ProjectDescriptionCustomizer} beans.</li>
	 * <li>Process all registered {@link ProjectGenerationConfiguration} classes.</li>
	 * <li>Apply the {@link #ProjectGenerator(Consumer, Supplier) contextConsumer} to
	 * further customize the context before it is refreshed.</li>
	 * </ul>
	 * @param description the description of the project to generate
	 * @param projectAssetGenerator the {@link ProjectAssetGenerator} to invoke
	 * @param <T> the type that gathers the project assets
	 * @return the generated content
	 * @throws ProjectGenerationException if an error occurs while generating the project
	 */
	public <T> T generate(ProjectDescription description, ProjectAssetGenerator<T> projectAssetGenerator)
			throws ProjectGenerationException {
		try (ProjectGenerationContext context = this.contextFactory.get()) {
			registerProjectDescription(context, description);
			registerProjectContributors(context, description);
			this.contextConsumer.accept(context);
			context.refresh();
			try {
				return projectAssetGenerator.generate(context);
			}
			catch (IOException ex) {
				throw new ProjectGenerationException("Failed to generate project", ex);
			}
		}
	}

	/**
	 * Return the {@link ProjectGenerationConfiguration} class names that should be
	 * considered. By default this method will load candidates using
	 * {@link SpringFactoriesLoader} with {@link ProjectGenerationConfiguration}.
	 * @param description the description of the project to generate
	 * @return a list of candidate configurations
	 */
	protected List<String> getCandidateProjectGenerationConfigurations(ProjectDescription description) {
		return SpringFactoriesLoader.loadFactoryNames(ProjectGenerationConfiguration.class,
				getClass().getClassLoader());
	}

	private void registerProjectDescription(ProjectGenerationContext context, ProjectDescription description) {
		context.registerBean(ProjectDescription.class, resolve(description, context));
	}

	private void registerProjectContributors(ProjectGenerationContext context, ProjectDescription description) {
		getCandidateProjectGenerationConfigurations(description).forEach((configurationClassName) -> {
			GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
			beanDefinition.setBeanClassName(configurationClassName);
			context.registerBeanDefinition(configurationClassName, beanDefinition);
		});
	}

	private Supplier<ProjectDescription> resolve(ProjectDescription description, ProjectGenerationContext context) {
		return () -> {
			if (description instanceof MutableProjectDescription) {
				MutableProjectDescription mutableDescription = (MutableProjectDescription) description;
				ProjectDescriptionDiffFactory diffFactory = context.getBeanProvider(ProjectDescriptionDiffFactory.class)
						.getIfAvailable(DefaultProjectDescriptionDiffFactory::new);
				// Create the diff here so that it takes a copy of the description
				// immediately
				ProjectDescriptionDiff diff = diffFactory.create(mutableDescription);
				context.registerBean(ProjectDescriptionDiff.class, () -> diff);
				context.getBeanProvider(ProjectDescriptionCustomizer.class).orderedStream()
						.forEach((customizer) -> customizer.customize(mutableDescription));
			}
			return description;
		};
	}

}
