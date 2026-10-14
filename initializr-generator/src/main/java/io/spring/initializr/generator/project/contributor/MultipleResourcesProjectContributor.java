/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.project.contributor;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/**
 * A {@link ProjectContributor} that contributes all of the resources found beneath a root
 * location to a generated project.
 *
 * @author Andy Wilkinson
 * @see PathMatchingResourcePatternResolver
 */
public class MultipleResourcesProjectContributor implements ProjectContributor {

	private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

	private final String rootResource;

	private final Predicate<String> executable;

	/**
	 * Create a new instance with the {@code rootResource} to use to locate resources to
	 * copy to the project structure.
	 * @param rootResource the root resource path
	 * @see PathMatchingResourcePatternResolver#getResources(String)
	 */
	public MultipleResourcesProjectContributor(String rootResource) {
		this(rootResource, (filename) -> false);
	}

	public MultipleResourcesProjectContributor(String rootResource, Predicate<String> executable) {
		this.rootResource = StringUtils.trimTrailingCharacter(rootResource, '/');
		this.executable = executable;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		Resource root = this.resolver.getResource(this.rootResource);
		Resource[] resources = this.resolver.getResources(this.rootResource + "/**");
		for (Resource resource : resources) {
			if (resource.isReadable()) {
				String filename = extractFileName(root.getURI(), resource.getURI());
				Path output = projectRoot.resolve(filename);
				if (!Files.exists(output)) {
					Path parent = output.getParent();
					if (parent != null) {
						Files.createDirectories(parent);
					}
					Files.createFile(output);
				}
				FileCopyUtils.copy(resource.getInputStream(), Files.newOutputStream(output));
				// TODO Set executable using NIO
				output.toFile().setExecutable(this.executable.test(filename));
			}
		}
	}

	private String extractFileName(URI root, URI resource) {
		String candidate = resource.toString().substring(root.toString().length());
		return StringUtils.trimLeadingCharacter(candidate, '/');
	}

}
