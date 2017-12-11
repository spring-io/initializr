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

package io.spring.initializr.metadata;

import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link InitializrMetadataBuilder}.
 * 
 * @author Stephane Nicoll
 */
public class InitializrMetadataBuilderTests {

	@Test
	public void loadDefaultConfig() {
		InitializrProperties bean = load(
				new ClassPathResource("application-test-default.yml"));
		InitializrMetadata metadata = InitializrMetadataBuilder
				.fromInitializrProperties(bean).build();
		assertDefaultConfig(metadata);
	}

	@Test
	public void mergeIdenticalConfig() {
		InitializrProperties bean = load(
				new ClassPathResource("application-test-default.yml"));
		InitializrMetadata metadata = InitializrMetadataBuilder
				.fromInitializrProperties(bean).withInitializrProperties(bean, true)
				.build();
		assertDefaultConfig(metadata);
	}

	@Test
	public void mergeConfig() {
		InitializrProperties config = load(
				new ClassPathResource("application-test-default.yml"));
		InitializrProperties customDefaultsConfig = load(
				new ClassPathResource("application-test-custom-defaults.yml"));
		InitializrMetadata metadata = InitializrMetadataBuilder
				.fromInitializrProperties(config)
				.withInitializrProperties(customDefaultsConfig).build();
		assertDefaultConfig(metadata);
		assertEquals("org.foo", metadata.getGroupId().getContent());
		assertEquals("foo-bar", metadata.getArtifactId().getContent());
		assertEquals("1.2.4-SNAPSHOT", metadata.getVersion().getContent());
		assertEquals("FooBar", metadata.getName().getContent());
		assertEquals("FooBar Project", metadata.getDescription().getContent());
		assertEquals("org.foo.demo", metadata.getPackageName().getContent());
	}

	@Test
	public void mergeMetadata() {
		InitializrMetadata metadata = InitializrMetadataBuilder.create()
				.withInitializrMetadata(
						new ClassPathResource("metadata/config/test-min.json"))
				.build();
		assertEquals(false, metadata.getConfiguration().getEnv().isForceSsl());
		assertEquals(1, metadata.getDependencies().getContent().size());
		Dependency dependency = metadata.getDependencies().get("test");
		assertNotNull(dependency);
		assertEquals("org.springframework.boot", dependency.getGroupId());
		assertEquals(1, metadata.getTypes().getContent().size());
		assertEquals(2, metadata.getBootVersions().getContent().size());
		assertEquals(2, metadata.getPackagings().getContent().size());
		assertEquals(1, metadata.getJavaVersions().getContent().size());
		assertEquals(3, metadata.getLanguages().getContent().size());
		assertEquals("metadata-merge", metadata.getName().getContent());
		assertEquals("Demo project for metadata merge",
				metadata.getDescription().getContent());
		assertEquals("org.acme", metadata.getGroupId().getContent());
		assertEquals("metadata", metadata.getArtifactId().getContent());
		assertEquals("1.0.0-SNAPSHOT", metadata.getVersion().getContent());
		assertEquals("org.acme.demo", metadata.getPackageName().getContent());
	}

	@Test
	public void mergeMetadataWithBom() {
		InitializrMetadata metadata = InitializrMetadataBuilder.create()
				.withInitializrMetadata(
						new ClassPathResource("metadata/config/test-bom.json"))
				.build();

		Map<String, BillOfMaterials> boms = metadata.getConfiguration().getEnv()
				.getBoms();
		assertEquals(2, boms.size());
		BillOfMaterials myBom = boms.get("my-bom");
		assertNotNull(myBom);
		assertEquals("org.acme", myBom.getGroupId());
		assertEquals("my-bom", myBom.getArtifactId());
		assertEquals("1.2.3.RELEASE", myBom.getVersion());

		BillOfMaterials anotherBom = boms.get("another-bom");
		assertNotNull(anotherBom);
		assertEquals("org.acme", anotherBom.getGroupId());
		assertEquals("another-bom", anotherBom.getArtifactId());
		assertEquals("4.5.6.RELEASE", anotherBom.getVersion());
	}

	@Test
	public void mergeMetadataWithRepository() throws Exception {
		InitializrMetadata metadata = InitializrMetadataBuilder.create()
				.withInitializrMetadata(
						new ClassPathResource("metadata/config/test-repository.json"))
				.build();

		Map<String, Repository> repositories = metadata.getConfiguration().getEnv()
				.getRepositories();
		assertEquals(4, repositories.size()); // 2 standard repos
		Repository myRepo = repositories.get("my-repo");
		assertNotNull(myRepo);
		assertEquals("my repo", myRepo.getName());
		assertEquals(new URL("http://example.com/my"), myRepo.getUrl());
		assertEquals(true, myRepo.isSnapshotsEnabled());

		Repository anotherRepo = repositories.get("another-repo");
		assertNotNull(anotherRepo);
		assertEquals("another repo", anotherRepo.getName());
		assertEquals(new URL("http://example.com/another"), anotherRepo.getUrl());
		assertEquals(false, anotherRepo.isSnapshotsEnabled());
	}

	@Test
	public void mergeConfigurationDisabledByDefault() {
		InitializrProperties config = load(
				new ClassPathResource("application-test-default.yml"));
		InitializrProperties customDefaultsConfig = load(
				new ClassPathResource("application-test-custom-env.yml"));
		InitializrMetadata metadata = InitializrMetadataBuilder
				.fromInitializrProperties(config)
				.withInitializrProperties(customDefaultsConfig).build();
		InitializrConfiguration.Env defaultEnv = new InitializrConfiguration().getEnv();
		InitializrConfiguration.Env actualEnv = metadata.getConfiguration().getEnv();
		assertEquals(defaultEnv.getArtifactRepository(),
				actualEnv.getArtifactRepository());
		assertEquals(defaultEnv.getSpringBootMetadataUrl(),
				actualEnv.getSpringBootMetadataUrl());
		assertEquals(defaultEnv.getFallbackApplicationName(),
				actualEnv.getFallbackApplicationName());
		assertEquals(defaultEnv.isForceSsl(), actualEnv.isForceSsl());
	}

	@Test
	public void mergeConfiguration() {
		InitializrProperties config = load(
				new ClassPathResource("application-test-default.yml"));
		InitializrProperties customDefaultsConfig = load(
				new ClassPathResource("application-test-custom-env.yml"));
		InitializrMetadata metadata = InitializrMetadataBuilder
				.fromInitializrProperties(config)
				.withInitializrProperties(customDefaultsConfig, true).build();
		InitializrConfiguration.Env defaultEnv = new InitializrConfiguration().getEnv();
		InitializrConfiguration.Env actualEnv = metadata.getConfiguration().getEnv();
		assertEquals("https://repo.spring.io/lib-release/",
				actualEnv.getArtifactRepository());
		assertEquals(defaultEnv.getSpringBootMetadataUrl(),
				actualEnv.getSpringBootMetadataUrl());
		assertEquals("FooBarApplication", actualEnv.getFallbackApplicationName());
		assertEquals(false, actualEnv.isForceSsl());
		assertEquals("1.0.0-beta-2423", actualEnv.getKotlin().getDefaultVersion());
	}

	@Test
	public void addDependencyInCustomizer() {
		DependencyGroup group = DependencyGroup.create("Extra");
		Dependency dependency = Dependency.withId("com.foo:foo:1.0.0");
		group.getContent().add(dependency);
		InitializrMetadata metadata = InitializrMetadataBuilder.create()
				.withCustomizer(m -> m.getDependencies().getContent().add(group)).build();
		assertEquals(1, metadata.getDependencies().getContent().size());
		assertEquals(group, metadata.getDependencies().getContent().get(0));
	}

	private static void assertDefaultConfig(InitializrMetadata metadata) {
		assertNotNull(metadata);
		assertEquals("Wrong number of dependencies", 9,
				metadata.getDependencies().getAll().size());
		assertEquals("Wrong number of dependency group", 2,
				metadata.getDependencies().getContent().size());
		assertEquals("Wrong number of types", 4, metadata.getTypes().getContent().size());
	}

	private static InitializrProperties load(Resource resource) {
		PropertiesConfigurationFactory<InitializrProperties> factory = new PropertiesConfigurationFactory<>(
				InitializrProperties.class);
		factory.setTargetName("initializr");
		MutablePropertySources sources = new MutablePropertySources();
		sources.addFirst(new PropertiesPropertySource("main", loadProperties(resource)));
		factory.setPropertySources(sources);
		try {
			factory.afterPropertiesSet();
			return factory.getObject();
		}
		catch (Exception e) {
			throw new IllegalStateException("Could not create InitializrProperties", e);
		}
	}

	private static Properties loadProperties(Resource resource) {
		YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
		yamlFactory.setResources(resource);
		yamlFactory.afterPropertiesSet();
		return yamlFactory.getObject();
	}

}
