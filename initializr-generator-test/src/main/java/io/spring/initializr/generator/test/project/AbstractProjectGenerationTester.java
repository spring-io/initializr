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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDirectoryFactory;
import io.spring.initializr.generator.project.ProjectGenerationContext;
import io.spring.initializr.generator.project.ProjectGenerationException;

/**
 * Base tester for project generation.
 *
 * @param <SELF> concrete type of the tester
 * @author Stephane Nicoll
 */
public abstract class AbstractProjectGenerationTester<SELF extends AbstractProjectGenerationTester<SELF>> {

	private final Map<Class<?>, Supplier<?>> beanDefinitions;

	private final Consumer<ProjectGenerationContext> contextInitializer;

	private final Consumer<MutableProjectDescription> descriptionCustomizer;

	protected AbstractProjectGenerationTester(Map<Class<?>, Supplier<?>> beanDefinitions,
			Consumer<ProjectGenerationContext> contextInitializer,
			Consumer<MutableProjectDescription> descriptionCustomizer) {
		this.beanDefinitions = new LinkedHashMap<>(beanDefinitions);
		this.descriptionCustomizer = descriptionCustomizer;
		this.contextInitializer = contextInitializer;
	}

	protected AbstractProjectGenerationTester() {
		this(Collections.emptyMap(), emptyContextInitializer(), defaultDescriptionCustomizer());
	}

	private static Consumer<ProjectGenerationContext> emptyContextInitializer() {
		return (context) -> {
		};
	}

	private static Consumer<MutableProjectDescription> defaultDescriptionCustomizer() {
		return (projectDescription) -> {
			if (projectDescription.getGroupId() == null) {
				projectDescription.setGroupId("com.example");
			}
			if (projectDescription.getArtifactId() == null) {
				projectDescription.setArtifactId("demo");
			}
			if (projectDescription.getVersion() == null) {
				projectDescription.setVersion("0.0.1-SNAPSHOT");
			}
			if (projectDescription.getApplicationName() == null) {
				projectDescription.setApplicationName("DemoApplication");
			}
		};
	}

	protected abstract SELF newInstance(Map<Class<?>, Supplier<?>> beanDefinitions,
			Consumer<ProjectGenerationContext> contextInitializer,
			Consumer<MutableProjectDescription> descriptionCustomizer);

	public <T> SELF withBean(Class<T> beanType, Supplier<T> beanDefinition) {
		LinkedHashMap<Class<?>, Supplier<?>> beans = new LinkedHashMap<>(this.beanDefinitions);
		beans.put(beanType, beanDefinition);
		return newInstance(beans, this.contextInitializer, this.descriptionCustomizer);
	}

	public SELF withDirectory(Path directory) {
		return withBean(ProjectDirectoryFactory.class,
				() -> (description) -> Files.createTempDirectory(directory, "project-"));
	}

	public SELF withIndentingWriterFactory() {
		return withBean(IndentingWriterFactory.class,
				() -> IndentingWriterFactory.create(new SimpleIndentStrategy("    ")));
	}

	public SELF withConfiguration(Class<?>... configurationClasses) {
		return withContextInitializer((context) -> context.register(configurationClasses));
	}

	public SELF withContextInitializer(Consumer<ProjectGenerationContext> context) {
		return newInstance(this.beanDefinitions, this.contextInitializer.andThen(context), this.descriptionCustomizer);
	}

	public SELF withDescriptionCustomizer(Consumer<MutableProjectDescription> description) {
		return newInstance(this.beanDefinitions, this.contextInitializer,
				this.descriptionCustomizer.andThen(description));
	}

	protected <T> T invokeProjectGeneration(MutableProjectDescription description,
			ProjectGenerationInvoker<T> invoker) {
		this.descriptionCustomizer.accept(description);
		try {
			return invoker.generate(beansConfigurer().andThen(this.contextInitializer));
		}
		catch (IOException ex) {
			throw new ProjectGenerationException("Failed to generated project", ex);
		}
	}

	private Consumer<ProjectGenerationContext> beansConfigurer() {
		return (context) -> this.beanDefinitions
				.forEach((type, definition) -> register(context, type, definition.get()));
	}

	// Restore proper generic signature to make sure the context resolve the bean properly
	private <T> void register(ProjectGenerationContext context, Class<T> type, Object instance) {
		T bean = type.cast(instance);
		context.registerBean(type, () -> bean);
	}

	protected interface ProjectGenerationInvoker<T> {

		T generate(Consumer<ProjectGenerationContext> contextInitializer) throws IOException;

	}

}
