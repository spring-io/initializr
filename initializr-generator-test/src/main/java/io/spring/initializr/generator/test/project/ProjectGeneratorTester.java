/*
 * Copyright 2012 - present the original author or authors.
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
 * standard {@link ProjectGenerationConfiguration} classes are processed automatically.
 * Extra beans can be added using {@linkplain #withBean(Class, Supplier) bean
 * registration}, a {@linkplain #withConfiguration(Class[]) configuration class} or via
 * the {@linkplain #withContextInitializer(Consumer) customization of the project
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

	/**
	 * Generate a project.
	 * @param description the description
	 * @return the structure of the generated project
	 */
	public ProjectStructure generate(MutableProjectDescription description) {
		return invokeProjectGeneration(description, (contextInitializer) -> {
			Path directory = new ProjectGenerator(contextInitializer).generate(description,
					new DefaultProjectAssetGenerator());
			return new ProjectStructure(directory);
		});
	}

	/**
	 * Generate a project using the given asset generator.
	 * @param description the description
	 * @param projectAssetGenerator the asset generator
	 * @param <T> the type of the result
	 * @return the result of the asset generator
	 */
	public <T> T generate(MutableProjectDescription description, ProjectAssetGenerator<T> projectAssetGenerator) {
		return invokeProjectGeneration(description, (contextInitializer) -> new ProjectGenerator(contextInitializer)
			.generate(description, projectAssetGenerator));
	}

}
