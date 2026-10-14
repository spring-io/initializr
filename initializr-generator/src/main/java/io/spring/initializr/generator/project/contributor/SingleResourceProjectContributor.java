/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project.contributor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

/**
 * {@link ProjectContributor} that contributes a single file, identified by a resource
 * pattern, to a generated project.
 *
 * @author Andy Wilkinson
 * @see PathMatchingResourcePatternResolver
 */
public class SingleResourceProjectContributor implements ProjectContributor {

	private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

	private final String relativePath;

	private final String resourcePattern;

	/**
	 * Create a new instance.
	 * @param relativePath the {@linkplain Path#resolve(String) relative path} in the
	 * generated structure.
	 * @param resourcePattern the pattern to use to locate the resource to copy to the
	 * project structure
	 * @see PathMatchingResourcePatternResolver#getResource(String)
	 */
	public SingleResourceProjectContributor(String relativePath, String resourcePattern) {
		this.relativePath = relativePath;
		this.resourcePattern = resourcePattern;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		Path output = projectRoot.resolve(this.relativePath);
		if (!Files.exists(output)) {
			Path parent = output.getParent();
			if (parent != null) {
				Files.createDirectories(parent);
			}
			Files.createFile(output);
		}
		Resource resource = this.resolver.getResource(this.resourcePattern);
		FileCopyUtils.copy(resource.getInputStream(), Files.newOutputStream(output, StandardOpenOption.APPEND));
	}

}
