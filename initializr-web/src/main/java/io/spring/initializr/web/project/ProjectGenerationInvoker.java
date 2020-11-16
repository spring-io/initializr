/*
 * Copyright 2012-2020 the original author or authors.
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

package io.spring.initializr.web.project;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.BuildWriter;
import io.spring.initializr.generator.project.DefaultProjectAssetGenerator;
import io.spring.initializr.generator.project.ProjectAssetGenerator;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationContext;
import io.spring.initializr.generator.project.ProjectGenerationException;
import io.spring.initializr.generator.project.ProjectGenerator;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.support.MetadataBuildItemResolver;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.FileSystemUtils;

/**
 * Invokes the project generation API. This is an intermediate layer that can consume a
 * {@link ProjectRequest} and trigger project generation based on the request.
 *
 * @param <R> the concrete {@link ProjectRequest} type
 * @author Madhura Bhave
 * @see ProjectAssetGenerator
 */
public class ProjectGenerationInvoker<R extends ProjectRequest> {

	private final ApplicationContext parentApplicationContext;

	private final ApplicationEventPublisher eventPublisher;

	private final ProjectRequestToDescriptionConverter<R> requestConverter;

	private final ProjectAssetGenerator<Path> projectAssetGenerator = new DefaultProjectAssetGenerator();

	private final transient Map<Path, List<Path>> temporaryFiles = new ConcurrentHashMap<>();

	public ProjectGenerationInvoker(ApplicationContext parentApplicationContext,
			ProjectRequestToDescriptionConverter<R> requestConverter) {
		this(parentApplicationContext, parentApplicationContext, requestConverter);
	}

	protected ProjectGenerationInvoker(ApplicationContext parentApplicationContext,
			ApplicationEventPublisher eventPublisher, ProjectRequestToDescriptionConverter<R> requestConverter) {
		this.parentApplicationContext = parentApplicationContext;
		this.eventPublisher = eventPublisher;
		this.requestConverter = requestConverter;
	}

	/**
	 * Invokes the project generation API that generates the entire project structure for
	 * the specified {@link ProjectRequest}.
	 * @param request the project request
	 * @return the {@link ProjectGenerationResult}
	 */
	public ProjectGenerationResult invokeProjectStructureGeneration(R request) {
		InitializrMetadata metadata = this.parentApplicationContext.getBean(InitializrMetadataProvider.class).get();
		try {
			ProjectDescription description = this.requestConverter.convert(request, metadata);
			ProjectGenerator projectGenerator = new ProjectGenerator((
					projectGenerationContext) -> customizeProjectGenerationContext(projectGenerationContext, metadata));
			ProjectGenerationResult result = projectGenerator.generate(description,
					generateProject(description, request));
			addTempFile(result.getRootDirectory(), result.getRootDirectory());
			return result;
		}
		catch (ProjectGenerationException ex) {
			publishProjectFailedEvent(request, metadata, ex);
			throw ex;
		}
	}

	private ProjectAssetGenerator<ProjectGenerationResult> generateProject(ProjectDescription description, R request) {
		return (context) -> {
			Path projectDir = getProjectAssetGenerator(description).generate(context);
			publishProjectGeneratedEvent(request, context);
			return new ProjectGenerationResult(context.getBean(ProjectDescription.class), projectDir);
		};
	}

	/**
	 * Return the {@link ProjectAssetGenerator} to use to generate the project structure
	 * for the specified {@link ProjectDescription}.
	 * @param description the project description
	 * @return an asset generator for the specified request
	 */
	protected ProjectAssetGenerator<Path> getProjectAssetGenerator(ProjectDescription description) {
		return this.projectAssetGenerator;
	}

	/**
	 * Invokes the project generation API that knows how to just write the build file.
	 * Returns a directory containing the project for the specified
	 * {@link ProjectRequest}.
	 * @param request the project request
	 * @return the generated build content
	 */
	public byte[] invokeBuildGeneration(R request) {
		InitializrMetadata metadata = this.parentApplicationContext.getBean(InitializrMetadataProvider.class).get();
		try {
			ProjectDescription description = this.requestConverter.convert(request, metadata);
			ProjectGenerator projectGenerator = new ProjectGenerator((
					projectGenerationContext) -> customizeProjectGenerationContext(projectGenerationContext, metadata));
			return projectGenerator.generate(description, generateBuild(request));
		}
		catch (ProjectGenerationException ex) {
			publishProjectFailedEvent(request, metadata, ex);
			throw ex;
		}
	}

	private ProjectAssetGenerator<byte[]> generateBuild(R request) {
		return (context) -> {
			byte[] content = generateBuild(context);
			publishProjectGeneratedEvent(request, context);
			return content;
		};
	}

	/**
	 * Create a file in the same directory as the given directory using the directory name
	 * and extension.
	 * @param dir the directory used to determine the path and name of the new file
	 * @param extension the extension to use for the new file
	 * @return the newly created file
	 */
	public Path createDistributionFile(Path dir, String extension) {
		Path download = dir.resolveSibling(dir.getFileName() + extension);
		addTempFile(dir, download);
		return download;
	}

	private void addTempFile(Path group, Path file) {
		this.temporaryFiles.compute(group, (path, paths) -> {
			List<Path> newPaths = (paths != null) ? paths : new ArrayList<>();
			newPaths.add(file);
			return newPaths;
		});
	}

	/**
	 * Clean all the temporary files that are related to this root directory.
	 * @param dir the directory to clean
	 * @see #createDistributionFile
	 */
	public void cleanTempFiles(Path dir) {
		List<Path> tempFiles = this.temporaryFiles.remove(dir);
		if (!tempFiles.isEmpty()) {
			tempFiles.forEach((path) -> {
				try {
					FileSystemUtils.deleteRecursively(path);
				}
				catch (IOException ex) {
					// Continue
				}
			});
		}
	}

	private byte[] generateBuild(ProjectGenerationContext context) throws IOException {
		ProjectDescription description = context.getBean(ProjectDescription.class);
		StringWriter out = new StringWriter();
		BuildWriter buildWriter = context.getBeanProvider(BuildWriter.class).getIfAvailable();
		if (buildWriter != null) {
			buildWriter.writeBuild(out);
			return out.toString().getBytes();
		}
		else {
			throw new IllegalStateException("No BuildWriter implementation found for " + description.getLanguage());
		}
	}

	private void customizeProjectGenerationContext(AnnotationConfigApplicationContext context,
			InitializrMetadata metadata) {
		context.setParent(this.parentApplicationContext);
		context.registerBean(InitializrMetadata.class, () -> metadata);
		context.registerBean(BuildItemResolver.class, () -> new MetadataBuildItemResolver(metadata,
				context.getBean(ProjectDescription.class).getPlatformVersion()));
		context.registerBean(MetadataProjectDescriptionCustomizer.class,
				() -> new MetadataProjectDescriptionCustomizer(metadata));
	}

	private void publishProjectGeneratedEvent(R request, ProjectGenerationContext context) {
		InitializrMetadata metadata = context.getBean(InitializrMetadata.class);
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request, metadata);
		this.eventPublisher.publishEvent(event);
	}

	private void publishProjectFailedEvent(R request, InitializrMetadata metadata, Exception cause) {
		ProjectFailedEvent event = new ProjectFailedEvent(request, metadata, cause);
		this.eventPublisher.publishEvent(event);
	}

}
