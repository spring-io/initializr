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
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link DefaultProjectAssetGenerator}.
 *
 * @author Stephane Nicoll
 */
class DefaultProjectAssetGeneratorTests {

	@Test
	void generationWithExplicitFactoryDoesNotLookupBean(@TempDir Path tempDir) throws IOException {
		ProjectDescription description = new MutableProjectDescription();
		ProjectDirectoryFactory factory = mock(ProjectDirectoryFactory.class);
		Path expected = tempDir.resolve("does-not-exist");
		assertThat(expected).doesNotExist();
		given(factory.createProjectDirectory(description)).willReturn(expected);
		try (ProjectGenerationContext context = new ProjectGenerationContext()) {
			context.registerBean(ProjectDescription.class, () -> description);
			context.refresh();
			Path rootDir = new DefaultProjectAssetGenerator(factory).generate(context);
			assertThat(rootDir).isSameAs(expected);
			assertThat(expected).exists().isDirectory();
			verify(factory).createProjectDirectory(description);
		}
	}

	@Test
	void generationWithoutExplicitFactoryLookupsBean(@TempDir Path tempDir) throws IOException {
		ProjectDescription description = new MutableProjectDescription();
		ProjectDirectoryFactory factory = mock(ProjectDirectoryFactory.class);
		Path expected = tempDir.resolve("does-not-exist");
		assertThat(expected).doesNotExist();
		given(factory.createProjectDirectory(description)).willReturn(expected);
		try (ProjectGenerationContext context = new ProjectGenerationContext()) {
			context.registerBean(ProjectDescription.class, () -> description);
			context.registerBean(ProjectDirectoryFactory.class, () -> factory);
			context.refresh();
			Path rootDir = new DefaultProjectAssetGenerator().generate(context);
			assertThat(rootDir).isSameAs(expected);
			assertThat(expected).exists().isDirectory();
			verify(factory).createProjectDirectory(description);
		}
	}

	@Test
	void generationWithoutExplicitFactoryFailIfBeanIsNotPresent() {
		ProjectDescription description = new MutableProjectDescription();
		assertThatThrownBy(() -> {
			try (ProjectGenerationContext context = new ProjectGenerationContext()) {
				context.registerBean(ProjectDescription.class, () -> description);
				context.refresh();
				new DefaultProjectAssetGenerator().generate(context);
			}
		}).isInstanceOf(NoSuchBeanDefinitionException.class)
				.hasMessageContaining(ProjectDirectoryFactory.class.getName());
	}

	@Test
	void generationWithBaseDirCreatesBaseDirStructure(@TempDir Path tempDir) throws IOException {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBaseDirectory("my-project");
		ProjectDirectoryFactory factory = mock(ProjectDirectoryFactory.class);
		Path expected = tempDir.resolve("does-not-exist");
		assertThat(expected).doesNotExist();
		given(factory.createProjectDirectory(description)).willReturn(expected);
		try (ProjectGenerationContext context = new ProjectGenerationContext()) {
			context.registerBean(ProjectDescription.class, () -> description);
			context.refresh();
			Path rootDir = new DefaultProjectAssetGenerator(factory).generate(context);
			assertThat(rootDir).isSameAs(expected);
			assertThat(expected).exists().isDirectory();
			assertThat(rootDir.resolve("my-project")).exists().isDirectory();
			verify(factory).createProjectDirectory(description);
		}
	}

}
