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

package io.spring.initializr.doc.generator.project;

import java.nio.file.Path;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.DefaultProjectAssetGenerator;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerator;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.context.support.StaticApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProjectGeneratorSetupExample}.
 *
 * @author Stephane Nicoll
 */
class ProjectGeneratorSetupExampleTests {

	@Test
	void generateSimpleProjectStructure(@TempDir Path tempDir) {
		StaticApplicationContext context = new StaticApplicationContext();
		context.registerBean(InitializrMetadata.class, () -> InitializrMetadataTestBuilder.withDefaults().build());
		context.registerBean(IndentingWriterFactory.class, IndentingWriterFactory::withDefaultSettings);
		context.registerBean(MustacheTemplateRenderer.class,
				() -> new MustacheTemplateRenderer("classpath:/templates"));
		context.refresh();
		DefaultProjectAssetGenerator assetGenerator = new DefaultProjectAssetGenerator((description) -> tempDir);
		ProjectGenerator projectGenerator = new ProjectGeneratorSetupExample().createProjectGenerator(context);
		Path directory = projectGenerator.generate(createProjectDescription(), assetGenerator);
		assertThat(directory).isSameAs(tempDir);
		Path helloFile = directory.resolve("hello.txt");
		assertThat(helloFile).exists().isRegularFile().hasContent("Test");
	}

	private MutableProjectDescription createProjectDescription() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setGroupId("com.example");
		description.setArtifactId("demo");
		description.setApplicationName("DemoApplication");
		description.setPlatformVersion(Version.parse("1.0.0.RELEASE"));
		description.setLanguage(Language.forId(JavaLanguage.ID, "11"));
		description.setBuildSystem(BuildSystem.forId(MavenBuildSystem.ID));
		return description;
	}

}
