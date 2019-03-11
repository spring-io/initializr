/*
 * Copyright 2012-2018 the original author or authors.
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

package io.spring.initializr.web.mapper;

import java.util.Arrays;

import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.DependencyGroup;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Link;
import io.spring.initializr.web.AbstractInitializrIntegrationTests;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class DefaultInitializrMetadataJsonMapperProviderTest
		extends AbstractInitializrIntegrationTests {

	private static final InitializrMetadata metadata;

	static {
		Dependency web = Dependency.withId("web");
		web.setName("Web");
		web.setDescription("Web dependency description");
		web.setLinks(Arrays.asList(
				Link.create("guide", "https://example.com/guide",
						"Building a RESTful Web Service"),
				Link.create("reference", "https://example.com/doc")));
		Dependency security = Dependency.withId("security");
		security.setName("Security");
		Dependency dataJpa = Dependency.withId("data-jpa");
		dataJpa.setName("Data JPA");

		Dependency foo = Dependency.withId("org.acme:foo");
		foo.setName("Foo");
		foo.setLinks(Arrays.asList(
				Link.create("reference", "https://example.com/{bootVersion}/doc", true),
				Link.create("guide", "https://example.com/guide1"), Link.create("guide",
						"https://example.com/guide2", "Some guide for foo")));
		Dependency bar = Dependency.withId("org.acme:bar");
		bar.setName("Bar");
		Dependency myApi = Dependency.withId("my-api");
		myApi.setName("My API");

		metadata = new InitializrMetadataTestBuilder()
				.addBootVersion("1.5.17.RELEASE", false, "1.5.17")
				.addBootVersion("2.1.4.RELEASE", true, "2.1.4")
				.addBootVersion("2.2.0.BUILD-SNAPSHOT", false, "Latest SNAPSHOT")
				.addType("maven-build", false, "/pom.xml", "maven", "build", "Maven POM")
				.addType("maven-project", true, "/starter.zip", "maven", "project",
						"Maven Project")
				.addType("gradle-build", false, "/build.gradle", "gradle", "build",
						"Gradle Config")
				.addType("gradle-project", false, "/starter.zip", "gradle", "project",
						"Gradle Project")
				.addLanguage("groovy", false, "Groovy").addLanguage("java", true, "Java")
				.addLanguage("kotlin", false, "Kotlin").addDefaultJavaVersions()
				.addPackaging("war", false, "War").addPackaging("jar", true, "Jar")
				.addDependencyGroup("Core", web, security, dataJpa)
				.addDependencyGroup("Other", foo, bar, myApi).build();
	}

	private final DefaultInitializrMetadataJsonMapperProvider jsonMapperProvider = new DefaultInitializrMetadataJsonMapperProvider();

	@Test
	void version2() {
		final String json = this.jsonMapperProvider.get(InitializrMetadataVersion.V2)
				.write(metadata, "http://");

		try {
			JSONObject actual = new JSONObject(json);
			JSONObject expected = readJsonFrom("metadata/test-default-2.0.0.json");

			JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
		}
		catch (JSONException ex) {
			throw new IllegalArgumentException("Invalid json", ex);
		}
	}

	@Test
	void version2_1() {
		DependencyGroup other = metadata.getDependencies().getContent().stream()
				.filter((group) -> "Other".equals(group.getName())).findFirst()
				.orElseThrow(
						() -> new IllegalStateException("No 'Other' dependency group"));
		Dependency biz = Dependency.withId("org.acme:biz");
		biz.setName("Biz");
		biz.setVersionRange("2.2.0.BUILD-SNAPSHOT");
		other.getContent().add(biz);
		Dependency bur = Dependency.withId("org.acme:bur");
		bur.setName("Bur");
		bur.setVersionRange("[2.1.4.RELEASE,2.2.0.BUILD-SNAPSHOT)");
		other.getContent().add(bur);
		InitializrMetadata combinedMetadata = new InitializrMetadataTestBuilder()
				.addDependencyGroup(other).build();
		combinedMetadata.merge(metadata);

		final String json = this.jsonMapperProvider.get(InitializrMetadataVersion.V2_1)
				.write(combinedMetadata, "http://");

		try {
			JSONObject actual = new JSONObject(json);
			JSONObject expected = readJsonFrom("metadata/test-default-2.1.0.json");

			JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
		}
		catch (JSONException ex) {
			throw new IllegalArgumentException("Invalid json", ex);
		}
	}

	@Override
	protected String createUrl(String context) {
		return null;
	}

}
