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

package io.spring.initializr.metadata;

import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

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
		assertThat(metadata.getGroupId().getContent()).isEqualTo("org.foo");
		assertThat(metadata.getArtifactId().getContent()).isEqualTo("foo-bar");
		assertThat(metadata.getVersion().getContent()).isEqualTo("1.2.4-SNAPSHOT");
		assertThat(metadata.getName().getContent()).isEqualTo("FooBar");
		assertThat(metadata.getDescription().getContent()).isEqualTo("FooBar Project");
		assertThat(metadata.getPackageName().getContent()).isEqualTo("org.foo.demo");
	}

	@Test
	public void mergeMetadata() {
		InitializrMetadata metadata = InitializrMetadataBuilder.create()
				.withInitializrMetadata(
						new ClassPathResource("metadata/config/test-min.json"))
				.build();
		assertThat(metadata.getConfiguration().getEnv().isForceSsl()).isEqualTo(false);
		assertThat(metadata.getDependencies().getContent()).hasSize(1);
		Dependency dependency = metadata.getDependencies().get("test");
		assertThat(dependency).isNotNull();
		assertThat(dependency.getGroupId()).isEqualTo("org.springframework.boot");
		assertThat(metadata.getTypes().getContent()).hasSize(1);
		assertThat(metadata.getBootVersions().getContent()).hasSize(2);
		assertThat(metadata.getPackagings().getContent()).hasSize(2);
		assertThat(metadata.getJavaVersions().getContent()).hasSize(1);
		assertThat(metadata.getLanguages().getContent()).hasSize(3);
		assertThat(metadata.getName().getContent()).isEqualTo("metadata-merge");
		assertThat(metadata.getDescription().getContent())
				.isEqualTo("Demo project for metadata merge");
		assertThat(metadata.getGroupId().getContent()).isEqualTo("org.acme");
		assertThat(metadata.getArtifactId().getContent()).isEqualTo("metadata");
		assertThat(metadata.getVersion().getContent()).isEqualTo("1.0.0-SNAPSHOT");
		assertThat(metadata.getPackageName().getContent()).isEqualTo("org.acme.demo");
	}

	@Test
	public void mergeMetadataWithBom() {
		InitializrMetadata metadata = InitializrMetadataBuilder.create()
				.withInitializrMetadata(
						new ClassPathResource("metadata/config/test-bom.json"))
				.build();

		Map<String, BillOfMaterials> boms = metadata.getConfiguration().getEnv()
				.getBoms();
		assertThat(boms).hasSize(2);
		BillOfMaterials myBom = boms.get("my-bom");
		assertThat(myBom).isNotNull();
		assertThat(myBom.getGroupId()).isEqualTo("org.acme");
		assertThat(myBom.getArtifactId()).isEqualTo("my-bom");
		assertThat(myBom.getVersion()).isEqualTo("1.2.3.RELEASE");

		BillOfMaterials anotherBom = boms.get("another-bom");
		assertThat(anotherBom).isNotNull();
		assertThat(anotherBom.getGroupId()).isEqualTo("org.acme");
		assertThat(anotherBom.getArtifactId()).isEqualTo("another-bom");
		assertThat(anotherBom.getVersion()).isEqualTo("4.5.6.RELEASE");
	}

	@Test
	public void mergeMetadataWithRepository() throws Exception {
		InitializrMetadata metadata = InitializrMetadataBuilder.create()
				.withInitializrMetadata(
						new ClassPathResource("metadata/config/test-repository.json"))
				.build();

		Map<String, Repository> repositories = metadata.getConfiguration().getEnv()
				.getRepositories();
		assertThat(repositories).hasSize(4); // 2 standard repos
		Repository myRepo = repositories.get("my-repo");
		assertThat(myRepo).isNotNull();
		assertThat(myRepo.getName()).isEqualTo("my repo");
		assertThat(myRepo.getUrl()).isEqualTo(new URL("http://example.com/my"));
		assertThat(myRepo.isSnapshotsEnabled()).isEqualTo(true);

		Repository anotherRepo = repositories.get("another-repo");
		assertThat(anotherRepo).isNotNull();
		assertThat(anotherRepo.getName()).isEqualTo("another repo");
		assertThat(anotherRepo.getUrl()).isEqualTo(new URL("http://example.com/another"));
		assertThat(anotherRepo.isSnapshotsEnabled()).isEqualTo(false);
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
		assertThat(actualEnv.getArtifactRepository())
				.isEqualTo(defaultEnv.getArtifactRepository());
		assertThat(actualEnv.getSpringBootMetadataUrl())
				.isEqualTo(defaultEnv.getSpringBootMetadataUrl());
		assertThat(actualEnv.getFallbackApplicationName())
				.isEqualTo(defaultEnv.getFallbackApplicationName());
		assertThat(actualEnv.isForceSsl()).isEqualTo(defaultEnv.isForceSsl());
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
		assertThat(actualEnv.getArtifactRepository())
				.isEqualTo("https://repo.spring.io/lib-release/");
		assertThat(actualEnv.getSpringBootMetadataUrl())
				.isEqualTo(defaultEnv.getSpringBootMetadataUrl());
		assertThat(actualEnv.getFallbackApplicationName()).isEqualTo("FooBarApplication");
		assertThat(actualEnv.isForceSsl()).isEqualTo(false);
		assertThat(actualEnv.getKotlin().getDefaultVersion())
				.isEqualTo("1.0.0-beta-2423");
	}

	@Test
	public void addDependencyInCustomizer() {
		DependencyGroup group = DependencyGroup.create("Extra");
		Dependency dependency = Dependency.withId("com.foo:foo:1.0.0");
		group.getContent().add(dependency);
		InitializrMetadata metadata = InitializrMetadataBuilder.create()
				.withCustomizer((m) -> m.getDependencies().getContent().add(group))
				.build();
		assertThat(metadata.getDependencies().getContent()).hasSize(1);
		assertThat(metadata.getDependencies().getContent().get(0)).isEqualTo(group);
	}

	private static void assertDefaultConfig(InitializrMetadata metadata) {
		assertThat(metadata).isNotNull();
		assertThat(metadata.getDependencies().getAll()).hasSize(9);
		assertThat(metadata.getDependencies().getContent()).hasSize(2);
		assertThat(metadata.getTypes().getContent()).hasSize(4);
	}

	private static InitializrProperties load(Resource resource) {
		ConfigurationPropertySource source = new MapConfigurationPropertySource(
				loadProperties(resource));
		Binder binder = new Binder(source);
		return binder.bind("initializr", InitializrProperties.class).get();
	}

	private static Properties loadProperties(Resource resource) {
		YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
		yamlFactory.setResources(resource);
		yamlFactory.afterPropertiesSet();
		return yamlFactory.getObject();
	}

}
