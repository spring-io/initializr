/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator;

import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Type;
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder;
import io.spring.initializr.util.TemplateRenderer;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * @author Stephane Nicoll
 */
public class CommandLineHelpGeneratorTests {

	private CommandLineHelpGenerator generator;

	@Before
	public void init() {
		generator = new CommandLineHelpGenerator(new TemplateRenderer());
	}

	@Test
	public void generateGenericCapabilities() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", createDependency("id-b", "depB"),
						createDependency("id-a", "depA", "and some description"))
				.build();
		String content = generator.generateGenericCapabilities(metadata,
				"https://fake-service");
		assertCommandLineCapabilities(content);
		assertThat(content, containsString("id-a | and some description |"));
		assertThat(content, containsString("id-b | depB"));
		assertThat(content, containsString("https://fake-service"));
		assertThat(content, not(containsString("Examples:")));
		assertThat(content, not(containsString("curl")));
	}

	@Test
	public void generateCapabilitiesWithTypeDescription() {
		Type type = new Type();
		type.setId("foo");
		type.setName("foo-name");
		type.setDescription("foo-desc");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addType(type).build();
		String content = generator.generateGenericCapabilities(metadata,
				"https://fake-service");
		assertCommandLineCapabilities(content);
		assertThat(content, containsString("| foo"));
		assertThat(content, containsString("| foo-desc"));
	}

	@Test
	public void generateCurlCapabilities() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", createDependency("id-b", "depB"),
						createDependency("id-a", "depA", "and some description"))
				.build();
		String content = generator.generateCurlCapabilities(metadata,
				"https://fake-service");
		assertCommandLineCapabilities(content);
		assertThat(content, containsString("id-a | and some description |"));
		assertThat(content, containsString("id-b | depB"));
		assertThat(content, containsString("https://fake-service"));
		assertThat(content, containsString("Examples:"));
		assertThat(content, containsString("curl https://fake-service"));
	}

	@Test
	public void generateHttpCapabilities() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", createDependency("id-b", "depB"),
						createDependency("id-a", "depA", "and some description"))
				.build();
		String content = generator.generateHttpieCapabilities(metadata,
				"https://fake-service");
		assertCommandLineCapabilities(content);
		assertThat(content, containsString("id-a | and some description |"));
		assertThat(content, containsString("id-b | depB"));
		assertThat(content, containsString("https://fake-service"));
		assertThat(content, containsString("Examples:"));
		assertThat(content, not(containsString("curl")));
		assertThat(content, containsString("http https://fake-service"));
	}

	@Test
	public void generateSpringBootCliCapabilities() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder
				.withDefaults()
				.addDependencyGroup("test",
						createDependency("id-b", "depB"),
						createDependency("id-a", "depA", "and some description"))
				.build();
		String content = generator.generateSpringBootCliCapabilities(metadata, "https://fake-service");
		assertThat(content, containsString("| Id"));
		assertThat(content, containsString("| Tags"));
		assertThat(content, containsString("id-a | and some description |"));
		assertThat(content, containsString("id-b | depB"));
		assertThat(content, containsString("https://fake-service"));
		assertThat(content, not(containsString("Examples:")));
		assertThat(content, not(containsString("curl")));
		assertThat(content, not(containsString("| Rel")));
		assertThat(content, not(containsString("| dependencies")));
		assertThat(content, not(containsString("| applicationName")));
		assertThat(content, not(containsString("| baseDir")));
	}

	@Test
	public void generateCapabilitiesWithVersionRange() {
		Dependency first = Dependency.withId("first");
		first.setDescription("first desc");
		first.setVersionRange("1.2.0.RELEASE");
		Dependency second = Dependency.withId("second");
		second.setDescription("second desc");
		second.setVersionRange(" [1.2.0.RELEASE,1.3.0.M1)  ");
		InitializrMetadata metadata = InitializrMetadataTestBuilder
				.withDefaults()
				.addDependencyGroup("test", first, second).build();
		String content = generator.generateSpringBootCliCapabilities(
				metadata, "https://fake-service");
		assertThat(content, containsString(
				"| first  | first desc  | >=1.2.0.RELEASE               |"));
		assertThat(content, containsString(
				"| second | second desc | >=1.2.0.RELEASE and <1.3.0.M1 |"));
	}

	private static void assertCommandLineCapabilities(String content) {
		assertThat(content, containsString("| Rel"));
		assertThat(content, containsString("| dependencies"));
		assertThat(content, containsString("| applicationName"));
		assertThat(content, containsString("| baseDir"));
		assertThat(content, not(containsString("| Tags")));
	}

	private static Dependency createDependency(String id, String name) {
		return createDependency(id, name, null);
	}

	private static Dependency createDependency(String id, String name,
			String description) {
		Dependency dependency = Dependency.withId(id);
		dependency.setDescription(description);
		dependency.setName(name);
		return dependency;
	}

}
