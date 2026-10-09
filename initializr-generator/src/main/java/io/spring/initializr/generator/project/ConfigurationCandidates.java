/*
 * Copyright 2012 - present the original author or authors.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.log.LogMessage;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

/**
 * Loads the class names of {@link ProjectGenerationConfiguration} candidates.
 * <p>
 * Candidates are read from all
 * {@code META-INF/spring/io.spring.initializr.generator.project.ProjectGenerationConfiguration.imports}
 * files, one class name per line, {@code #} starting a comment. Registrations in
 * {@code META-INF/spring.factories} are still honored, but deprecated.
 *
 * @author Moritz Halbritter
 */
final class ConfigurationCandidates {

	private static final Log logger = LogFactory.getLog(ConfigurationCandidates.class);

	private static final String IMPORTS_LOCATION = "META-INF/spring/" + ProjectGenerationConfiguration.class.getName()
			+ ".imports";

	private static final String COMMENT_START = "#";

	private static final Map<ClassLoader, List<String>> cache = new ConcurrentReferenceHashMap<>();

	private ConfigurationCandidates() {
	}

	/**
	 * Load the candidates, imports files first, without duplicates.
	 * @param classLoader the class loader to use to find the resources
	 * @return the class names of the candidates
	 */
	static List<String> load(ClassLoader classLoader) {
		return cache.computeIfAbsent(classLoader, ConfigurationCandidates::loadCandidates);
	}

	private static List<String> loadCandidates(ClassLoader classLoader) {
		Set<String> candidates = new LinkedHashSet<>(loadImports(classLoader));
		candidates.addAll(loadFactories(classLoader));
		return List.copyOf(candidates);
	}

	private static List<String> loadImports(ClassLoader classLoader) {
		List<String> candidates = new ArrayList<>();
		try {
			Enumeration<URL> urls = classLoader.getResources(IMPORTS_LOCATION);
			while (urls.hasMoreElements()) {
				candidates.addAll(readImports(urls.nextElement()));
			}
		}
		catch (IOException ex) {
			throw new IllegalStateException("Unable to load candidates from " + IMPORTS_LOCATION, ex);
		}
		return candidates;
	}

	private static List<String> readImports(URL url) throws IOException {
		List<String> candidates = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new UrlResource(url).getInputStream(), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String candidate = stripComment(line).trim();
				if (!StringUtils.hasText(candidate)) {
					continue;
				}
				candidates.add(candidate);
			}
		}
		return candidates;
	}

	private static String stripComment(String line) {
		int commentStart = line.indexOf(COMMENT_START);
		return (commentStart != -1) ? line.substring(0, commentStart) : line;
	}

	@SuppressWarnings("deprecation")
	private static List<String> loadFactories(ClassLoader classLoader) {
		List<String> candidates = SpringFactoriesLoader.loadFactoryNames(ProjectGenerationConfiguration.class,
				classLoader);
		if (!CollectionUtils.isEmpty(candidates)) {
			logger.warn(LogMessage.format("Registering %s in %s is deprecated, move %s to %s",
					ProjectGenerationConfiguration.class.getName(), SpringFactoriesLoader.FACTORIES_RESOURCE_LOCATION,
					candidates, IMPORTS_LOCATION));
		}
		return candidates;
	}

}
