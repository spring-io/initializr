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

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConfigurationCandidates}.
 *
 * @author Moritz Halbritter
 */
class ConfigurationCandidatesTests {

	private static final String IMPORTS_FILE = "META-INF/spring/io.spring.initializr.generator.project.ProjectGenerationConfiguration.imports";

	private static final String FACTORIES_FILE = "META-INF/spring.factories";

	private static final String FACTORIES_KEY = "io.spring.initializr.generator.project.ProjectGenerationConfiguration=";

	@TempDir
	Path first;

	@TempDir
	Path second;

	private final List<URLClassLoader> classLoaders = new ArrayList<>();

	// Release jar/file handles so @TempDir cleanup works on Windows
	@AfterEach
	void closeClassLoaders() throws IOException {
		for (URLClassLoader classLoader : this.classLoaders) {
			classLoader.close();
		}
	}

	@Test
	void loadWithoutRegistrations() throws IOException {
		assertThat(ConfigurationCandidates.load(classLoader(this.first))).isEmpty();
	}

	@Test
	void loadFromImports() throws IOException {
		write(this.first, IMPORTS_FILE, """
				# Leading comment
				com.example.First

				  com.example.Second  # Trailing comment
				""");
		assertThat(ConfigurationCandidates.load(classLoader(this.first))).containsExactly("com.example.First",
				"com.example.Second");
	}

	@Test
	void loadFromSeveralImports() throws IOException {
		write(this.first, IMPORTS_FILE, "com.example.First\n");
		write(this.second, IMPORTS_FILE, "com.example.Second\n");
		assertThat(ConfigurationCandidates.load(classLoader(this.first, this.second)))
			.containsExactly("com.example.First", "com.example.Second");
	}

	@Test
	void loadFromSpringFactories() throws IOException {
		write(this.first, FACTORIES_FILE, FACTORIES_KEY + "com.example.First,com.example.Second\n");
		assertThat(ConfigurationCandidates.load(classLoader(this.first))).containsExactly("com.example.First",
				"com.example.Second");
	}

	@Test
	void loadFromImportsAndSpringFactories() throws IOException {
		write(this.first, IMPORTS_FILE, "com.example.First\ncom.example.Second\n");
		write(this.second, FACTORIES_FILE, FACTORIES_KEY + "com.example.Second,com.example.Third\n");
		assertThat(ConfigurationCandidates.load(classLoader(this.first, this.second)))
			.containsExactly("com.example.First", "com.example.Second", "com.example.Third");
	}

	@Test
	void loadIsCachedPerClassLoader() throws IOException {
		ClassLoader classLoader = classLoader(this.first);
		write(this.first, IMPORTS_FILE, "com.example.First\n");
		assertThat(ConfigurationCandidates.load(classLoader)).containsExactly("com.example.First");
		write(this.first, IMPORTS_FILE, "com.example.Second\n");
		assertThat(ConfigurationCandidates.load(classLoader)).containsExactly("com.example.First");
	}

	private static void write(Path root, String location, String content) throws IOException {
		Path file = root.resolve(location);
		Files.createDirectories(file.getParent());
		Files.writeString(file, content);
	}

	private ClassLoader classLoader(Path... roots) throws IOException {
		URL[] urls = new URL[roots.length];
		for (int i = 0; i < roots.length; i++) {
			urls[i] = roots[i].toUri().toURL();
		}
		URLClassLoader classLoader = new URLClassLoader(urls, null);
		this.classLoaders.add(classLoader);
		return classLoader;
	}

}
