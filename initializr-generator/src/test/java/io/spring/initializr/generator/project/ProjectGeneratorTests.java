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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.project.contributor.TestProjectGenerationConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;

import org.springframework.beans.factory.support.BeanDefinitionOverrideException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Tests for {@link ProjectGenerator}.
 *
 * @author Stephane Nicoll
 */
public class ProjectGeneratorTests {

	@Test
	void generateRegisterProjectDescription() {
		Consumer<ProjectGenerationContext> contextInitializer = mockContextInitializr();
		ProjectGenerator generator = new ProjectGenerator(contextInitializer);
		MutableProjectDescription description = new MutableProjectDescription();
		ProjectDescription beanDescription = generator.generate(description, (context) -> {
			assertThat(context.getBeansOfType(ProjectDescription.class)).hasSize(1);
			return context.getBean(ProjectDescription.class);
		});
		assertThat(description).isSameAs(beanDescription);
	}

	@Test
	void generateProvideDefaultProjectDescriptionDiff() {
		ProjectGenerator generator = new ProjectGenerator(mockContextInitializr());
		MutableProjectDescription description = new MutableProjectDescription();
		ProjectDescriptionDiff diff = generator.generate(description, (context) -> {
			assertThat(context.getBeansOfType(ProjectDescriptionDiff.class)).hasSize(1);
			return context.getBean(ProjectDescriptionDiff.class);
		});
		assertThat(diff).isInstanceOf(ProjectDescriptionDiff.class);
	}

	@Test
	void generateUseAvailableProjectDescriptionDiffFactory() {
		ProjectDescriptionDiff diff = mock(ProjectDescriptionDiff.class);
		ProjectDescriptionDiffFactory diffFactory = mock(ProjectDescriptionDiffFactory.class);
		MutableProjectDescription description = new MutableProjectDescription();
		given(diffFactory.create(description)).willReturn(diff);
		ProjectGenerator generator = new ProjectGenerator(
				(context) -> context.registerBean(ProjectDescriptionDiffFactory.class, () -> diffFactory));
		ProjectDescriptionDiff actualDiff = generator.generate(description, (context) -> {
			assertThat(context.getBeansOfType(ProjectDescriptionDiff.class)).hasSize(1);
			return context.getBean(ProjectDescriptionDiff.class);
		});
		assertThat(actualDiff).isSameAs(diff);
		verify(diffFactory).create(description);
	}

	@Test
	void generateInvokeContextInitializerBeforeContextIsRefreshed() {
		ProjectGenerator generator = new ProjectGenerator((context) -> {
			assertThat(context.isActive()).isFalse();
			context.registerBean(String.class, () -> "Test");
		});
		String customBean = generator.generate(new MutableProjectDescription(), (context) -> {
			assertThat(context.getBeansOfType(String.class)).hasSize(1);
			return context.getBean(String.class);
		});
		assertThat(customBean).isEqualTo("Test");
	}

	@Test
	void generateInvokeProjectDescriptionCustomizer() {
		ProjectGenerator generator = new ProjectGenerator((context) -> context.registerBean(
				ProjectDescriptionCustomizer.class, () -> (description) -> description.setGroupId("com.acme")));
		MutableProjectDescription description = new MutableProjectDescription();
		description.setGroupId("com.example");
		ProjectDescription descriptionFromContext = generator.generate(description,
				(context) -> context.getBean(ProjectDescription.class));
		assertThat(descriptionFromContext.getGroupId()).isEqualTo("com.acme");
	}

	@Test
	void generateInvokeProjectDescriptionCustomizersInOrder() {
		ProjectDescriptionCustomizer firstCustomizer = mock(ProjectDescriptionCustomizer.class);
		given(firstCustomizer.getOrder()).willReturn(5);
		ProjectDescriptionCustomizer secondCustomizer = mock(ProjectDescriptionCustomizer.class);
		given(secondCustomizer.getOrder()).willReturn(10);
		ProjectGenerator generator = new ProjectGenerator((context) -> {
			context.registerBean("first", ProjectDescriptionCustomizer.class, () -> secondCustomizer);
			context.registerBean("second", ProjectDescriptionCustomizer.class, () -> firstCustomizer);
		});
		MutableProjectDescription description = new MutableProjectDescription();
		generator.generate(description, (context) -> null);
		InOrder inOrder = inOrder(firstCustomizer, secondCustomizer);
		inOrder.verify(firstCustomizer).customize(description);
		inOrder.verify(secondCustomizer).customize(description);
	}

	@Test
	void generateIgnoreProjectDescriptionCustomizerOnNonMutableDescription() {
		ProjectDescriptionCustomizer customizer = mock(ProjectDescriptionCustomizer.class);
		ProjectGenerator generator = new ProjectGenerator(
				(context) -> context.registerBean(ProjectDescriptionCustomizer.class, () -> customizer));
		ProjectDescription description = mock(ProjectDescription.class);
		ProjectDescription descriptionFromContext = generator.generate(description,
				(context) -> context.getBean(ProjectDescription.class));
		assertThat(descriptionFromContext).isSameAs(description);
		verifyNoInteractions(customizer);
	}

	@Test
	void generateWithIoExceptionThrowsProjectGenerationException() throws IOException {
		ProjectGenerator generator = new ProjectGenerator(mockContextInitializr());
		ProjectAssetGenerator<?> assetGenerator = mock(ProjectAssetGenerator.class);
		IOException exception = new IOException("test");
		given(assetGenerator.generate(any())).willThrow(exception);
		assertThatThrownBy(() -> generator.generate(new MutableProjectDescription(), assetGenerator))
				.isInstanceOf(ProjectGenerationException.class).hasCause(exception);
	}

	@Test
	void generateDoesNotAllowBeanDefinitionOverridingByDefault() {
		ProjectGenerator generator = new ProjectGenerator((context) -> {
			context.registerBean("testBean", String.class, () -> "test");
			context.registerBean("testBean", String.class, () -> "duplicate");
		});
		ProjectAssetGenerator<?> assetGenerator = mock(ProjectAssetGenerator.class);
		assertThatThrownBy(() -> generator.generate(new MutableProjectDescription(), assetGenerator))
				.isInstanceOf(BeanDefinitionOverrideException.class).hasMessageContaining("testBean");
	}

	@Test
	void generateCanBeConfiguredToAllowBeanDefinitionOverriding() {
		ProjectGenerator generator = new ProjectGenerator((context) -> {
			context.registerBean("testBean", String.class, () -> "test");
			context.registerBean("testBean", String.class, () -> "duplicate");
		}, ProjectGenerationContext::new);
		Map<String, String> candidates = generator.generate(new MutableProjectDescription(),
				(context) -> context.getBeansOfType(String.class));
		assertThat(candidates).containsOnly(entry("testBean", "duplicate"));
	}

	@Test
	void generateCanBeExtendedToFilterProjectContributors(@TempDir Path projectDir) {
		ProjectDescription description = mock(ProjectDescription.class);
		given(description.getArtifactId()).willReturn("test-custom-contributor");
		given(description.getBuildSystem()).willReturn(new MavenBuildSystem());
		ProjectGenerator generator = new ProjectGenerator(mockContextInitializr()) {
			@Override
			protected List<String> getCandidateProjectGenerationConfigurations(ProjectDescription description) {
				assertThat(description).isSameAs(description);
				return Collections.singletonList(TestProjectGenerationConfiguration.class.getName());
			}
		};
		DefaultProjectAssetGenerator assetGenerator = new DefaultProjectAssetGenerator((desc) -> projectDir);
		Path outputDir = generator.generate(description, assetGenerator);
		Path expectedFile = outputDir.resolve("artifact-id.txt");
		assertThat(expectedFile).isRegularFile();
		assertThat(expectedFile).hasContent("test-custom-contributor");
		verify(description).getArtifactId();
		verify(description).getBuildSystem();
	}

	@SuppressWarnings("unchecked")
	private Consumer<ProjectGenerationContext> mockContextInitializr() {
		return mock(Consumer.class);
	}

}
