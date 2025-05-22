/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.generator.buildsystem.maven;

import java.io.StringWriter;
import java.util.Comparator;
import java.util.function.Consumer;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.Dependency.Exclusion;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.maven.MavenLicense.Distribution;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenBuildWriter}.
 *
 * @author Stephane Nicoll
 * @author Olga Maciaszek-Sharma
 * @author Jafer Khan Shamshad
 * @author Joachim Pasquali
 * @author Maurice Zeijen
 */
class MavenBuildWriterTests {

	@Test
	void basicPom() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo").version("1.0.1-SNAPSHOT");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/modelVersion").isEqualTo("4.0.0");
			assertThat(pom).textAtPath("/project/groupId").isEqualTo("com.example.demo");
			assertThat(pom).textAtPath("/project/artifactId").isEqualTo("demo");
			assertThat(pom).textAtPath("/project/version").isEqualTo("1.0.1-SNAPSHOT");
		});
	}

	@Test
	void pomWithNameAndDescription() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo").name("demo project").description("A demo project");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/modelVersion").isEqualTo("4.0.0");
			assertThat(pom).textAtPath("/project/groupId").isEqualTo("com.example.demo");
			assertThat(pom).textAtPath("/project/artifactId").isEqualTo("demo");
			assertThat(pom).textAtPath("/project/version").isEqualTo("0.0.1-SNAPSHOT");
			assertThat(pom).textAtPath("/project/name").isEqualTo("demo project");
			assertThat(pom).textAtPath("/project/description").isEqualTo("A demo project");
		});
	}

	@Test
	void pomWithParent() {
		MavenBuild build = new MavenBuild();
		build.settings()
			.coordinates("com.example.demo", "demo")
			.parent("org.springframework.boot", "spring-boot-starter-parent", "2.1.0.RELEASE");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/parent/groupId").isEqualTo("org.springframework.boot");
			assertThat(pom).textAtPath("/project/parent/artifactId").isEqualTo("spring-boot-starter-parent");
			assertThat(pom).textAtPath("/project/parent/version").isEqualTo("2.1.0.RELEASE");
			assertThat(pom).textAtPath("/project/parent/relativePath").isEmpty();
		});
	}

	@Test
	void pomWithParentAndRelativePath() {
		MavenBuild build = new MavenBuild();
		build.settings()
			.coordinates("com.example.demo", "demo")
			.parent("org.springframework.boot", "spring-boot-starter-parent", "2.1.0.RELEASE", "../parent/pom.xml");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/parent/groupId").isEqualTo("org.springframework.boot");
			assertThat(pom).textAtPath("/project/parent/artifactId").isEqualTo("spring-boot-starter-parent");
			assertThat(pom).textAtPath("/project/parent/version").isEqualTo("2.1.0.RELEASE");
			assertThat(pom).textAtPath("/project/parent/relativePath").isEqualTo("../parent/pom.xml");
		});
	}

	@Test
	void pomWithParentAndNullRelativePath() {
		MavenBuild build = new MavenBuild();
		build.settings()
			.coordinates("com.example.demo", "demo")
			.parent("org.springframework.boot", "spring-boot-starter-parent", "2.1.0.RELEASE", null);
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/parent/groupId").isEqualTo("org.springframework.boot");
			assertThat(pom).textAtPath("/project/parent/artifactId").isEqualTo("spring-boot-starter-parent");
			assertThat(pom).textAtPath("/project/parent/version").isEqualTo("2.1.0.RELEASE");
			assertThat(pom).nodeAtPath("/project/parent/relativePath").isNull();
		});
	}

	@Test
	void pomWithPackaging() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo").packaging("war");
		generatePom(build, (pom) -> assertThat(pom).textAtPath("/project/packaging").isEqualTo("war"));
	}

	@Test
	void pomWithNoLicense() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo").build();
		generatePom(build, (pom) -> assertThat(pom.nodeAtPath("/project/licenses")).isNull());
	}

	@Test
	void pomWithBasicLicense() {
		MavenBuild build = new MavenBuild();
		build.settings()
			.coordinates("com.example.demo", "demo")
			.licenses(new MavenLicense.Builder().name("Apache License, Version 2.0")
				.url("https://www.apache.org/licenses/LICENSE-2.0")
				.build());
		generatePom(build, (pom) -> {
			NodeAssert license = pom.nodeAtPath("/project/licenses/license");
			assertThat(license).textAtPath("name").isEqualTo("Apache License, Version 2.0");
			assertThat(license).textAtPath("url").isEqualTo("https://www.apache.org/licenses/LICENSE-2.0");
			assertThat(license).textAtPath("distribution").isNullOrEmpty();
			assertThat(license).textAtPath("comments").isNullOrEmpty();
		});
	}

	@Test
	void pomWithFullLicense() {
		MavenBuild build = new MavenBuild();
		build.settings()
			.coordinates("com.example.demo", "demo")
			.licenses(new MavenLicense.Builder().name("Apache License, Version 2.0")
				.url("https://www.apache.org/licenses/LICENSE-2.0")
				.distribution(Distribution.REPO)
				.comments("A business-friendly OSS license")
				.build());
		generatePom(build, (pom) -> {
			NodeAssert licenses = pom.nodeAtPath("/project/licenses");
			assertThat(licenses).isNotNull();
			NodeAssert license = licenses.nodeAtPath("license");
			assertThat(license).isNotNull();
			assertThat(license).textAtPath("name").isEqualTo("Apache License, Version 2.0");
			assertThat(license).textAtPath("url").isEqualTo("https://www.apache.org/licenses/LICENSE-2.0");
			assertThat(license).textAtPath("distribution").isEqualTo("repo");
			assertThat(license).textAtPath("comments").isEqualTo("A business-friendly OSS license");
		});
	}

	@Test
	void pomWithNoDeveloper() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo").build();
		generatePom(build, (pom) -> assertThat(pom.nodeAtPath("/project/developers")).isNull());
	}

	@Test
	void pomWithBasicDeveloper() {
		MavenBuild build = new MavenBuild();
		build.settings()
			.coordinates("com.example.demo", "demo")
			.developers(
					new MavenDeveloper.Builder().id("jsmith").name("John Smith").email("jsmith@example.com").build())
			.build();
		generatePom(build, (pom) -> {
			NodeAssert developer = pom.nodeAtPath("/project/developers/developer");
			assertThat(developer).textAtPath("id").isEqualTo("jsmith");
			assertThat(developer).textAtPath("name").isEqualTo("John Smith");
			assertThat(developer).textAtPath("email").isEqualTo("jsmith@example.com");
			assertThat(developer).textAtPath("url").isNullOrEmpty();
			assertThat(developer).textAtPath("organization").isNullOrEmpty();
			assertThat(developer).textAtPath("organizationUrl").isNullOrEmpty();
			assertThat(developer.nodeAtPath("roles")).isNull();
			assertThat(developer).textAtPath("timezone").isNullOrEmpty();
			assertThat(developer.nodeAtPath("properties")).isNull();
		});
	}

	@Test
	void pomWithFullDeveloper() {
		MavenBuild build = new MavenBuild();
		build.settings()
			.coordinates("com.example.demo", "demo")
			.developers(new MavenDeveloper.Builder().id("jsmith")
				.name("John Smith")
				.email("jsmith@example.com")
				.url("https://example.com/jsmith")
				.organization("Acme Corp")
				.organizationUrl("https://example.com")
				.timezone("Asia/Karachi")
				.role("developer")
				.role("tester")
				.property("prop1", "test1")
				.property("prop2", "test2")
				.build());
		generatePom(build, (pom) -> {
			NodeAssert developers = pom.nodeAtPath("/project/developers");
			assertThat(developers).isNotNull();
			NodeAssert developer = developers.nodeAtPath("developer");
			assertThat(developer).isNotNull();
			assertThat(developer).textAtPath("id").isEqualTo("jsmith");
			assertThat(developer).textAtPath("name").isEqualTo("John Smith");
			assertThat(developer).textAtPath("email").isEqualTo("jsmith@example.com");
			assertThat(developer).textAtPath("url").isEqualTo("https://example.com/jsmith");
			assertThat(developer).textAtPath("organization").isEqualTo("Acme Corp");
			assertThat(developer).textAtPath("organizationUrl").isEqualTo("https://example.com");
			assertThat(developer).textAtPath("timezone").isEqualTo("Asia/Karachi");
			NodeAssert roles = developer.nodeAtPath("roles");
			roles.nodesAtPath("role").hasSize(2);
			assertThat(roles).textAtPath("role[1]").isEqualTo("developer");
			assertThat(roles).textAtPath("role[2]").isEqualTo("tester");
			NodeAssert properties = developer.nodeAtPath("properties");
			assertThat(properties).textAtPath("prop1").isEqualTo("test1");
			assertThat(properties).textAtPath("prop2").isEqualTo("test2");
		});
	}

	@Test
	void pomWithNoScm() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo").build();
		generatePom(build, (pom) -> assertThat(pom.nodeAtPath("/project/scm")).isNull());
	}

	@Test
	void pomWithScm() {
		MavenBuild build = new MavenBuild();
		build.settings()
			.scm((scm) -> scm.connection("connection")
				.developerConnection("developerConnection")
				.tag("tag")
				.url("url"));
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/scm");
			assertThat(dependency).textAtPath("connection").isEqualTo("connection");
			assertThat(dependency).textAtPath("developerConnection").isEqualTo("developerConnection");
			assertThat(dependency).textAtPath("tag").isEqualTo("tag");
			assertThat(dependency).textAtPath("url").isEqualTo("url");
		});
	}

	@Test
	void pomWithProperties() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.properties().property("java.version", "1.8").property("alpha", "a");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/properties/java.version").isEqualTo("1.8");
			assertThat(pom).textAtPath("/project/properties/alpha").isEqualTo("a");
		});
	}

	@Test
	void pomWithPropertiesAndEmptyValue() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.properties().property("alpha", "");
		generatePom(build, (pom) -> {
			assertThat(pom).nodeAtPath("/project/properties/alpha").isNotNull();
			assertThat(pom).textAtPath("/project/properties/alpha").isEmpty();
		});
		String pom = writePom(new MavenBuildWriter(), build);
		assertThat(pom).containsSubsequence("<properties>", "<alpha/>", "</properties>");
	}

	@Test
	void pomWithVersionProperties() {
		MavenBuild build = new MavenBuild();
		build.properties()
			.version(VersionProperty.of("version.property", false), "1.2.3")
			.version(VersionProperty.of("internal.property", true), "4.5.6")
			.version("external.property", "7.8.9");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/properties/version.property").isEqualTo("1.2.3");
			assertThat(pom).textAtPath("/project/properties/internal.property").isEqualTo("4.5.6");
			assertThat(pom).textAtPath("/project/properties/external.property").isEqualTo("7.8.9");
		});
	}

	@Test
	void pomWithAnnotationProcessorDependency() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.dependencies()
			.add("annotation-processor", "org.springframework.boot", "spring-boot-configuration-processor",
					DependencyScope.ANNOTATION_PROCESSOR);
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("org.springframework.boot");
			assertThat(dependency).textAtPath("artifactId").isEqualTo("spring-boot-configuration-processor");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isNullOrEmpty();
			assertThat(dependency).textAtPath("optional").isEqualTo("true");
		});
	}

	@Test
	void pomWithCompileOnlyDependency() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.dependencies()
			.add("foo-bar", "org.springframework.boot", "spring-boot-foo-bar", DependencyScope.COMPILE_ONLY);
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("org.springframework.boot");
			assertThat(dependency).textAtPath("artifactId").isEqualTo("spring-boot-foo-bar");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isNullOrEmpty();
			assertThat(dependency).textAtPath("optional").isEqualTo("true");
		});
	}

	@Test
	void pomWithCompileDependency() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.dependencies().add("root", "org.springframework.boot", "spring-boot-starter", DependencyScope.COMPILE);
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("org.springframework.boot");
			assertThat(dependency).textAtPath("artifactId").isEqualTo("spring-boot-starter");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isNullOrEmpty();
			assertThat(dependency).textAtPath("optional").isNullOrEmpty();
		});
	}

	@Test
	void pomWithNoScopeDependencyDefaultsToCompile() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.dependencies().add("root", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter"));
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("org.springframework.boot");
			assertThat(dependency).textAtPath("artifactId").isEqualTo("spring-boot-starter");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isNullOrEmpty();
			assertThat(dependency).textAtPath("optional").isNullOrEmpty();
		});
	}

	@Test
	void pomWithRuntimeDependency() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.dependencies().add("hikari", "com.zaxxer", "HikariCP", DependencyScope.RUNTIME);
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("com.zaxxer");
			assertThat(dependency).textAtPath("artifactId").isEqualTo("HikariCP");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isEqualTo("runtime");
			assertThat(dependency).textAtPath("optional").isNullOrEmpty();
		});
	}

	@Test
	void pomWithProvidedRuntimeDependency() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.dependencies()
			.add("tomcat", "org.springframework.boot", "spring-boot-starter-tomcat", DependencyScope.PROVIDED_RUNTIME);
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("org.springframework.boot");
			assertThat(dependency).textAtPath("artifactId").isEqualTo("spring-boot-starter-tomcat");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isEqualTo("provided");
			assertThat(dependency).textAtPath("optional").isNullOrEmpty();
		});
	}

	@Test
	void pomWithTestCompileDependency() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.dependencies()
			.add("test", "org.springframework.boot", "spring-boot-starter-test", DependencyScope.TEST_COMPILE);
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("org.springframework.boot");
			assertThat(dependency).textAtPath("artifactId").isEqualTo("spring-boot-starter-test");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isEqualTo("test");
			assertThat(dependency).textAtPath("optional").isNullOrEmpty();
		});
	}

	@Test
	void pomWithTestRuntimeDependency() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.dependencies()
			.add("embed-mongo", "de.flapdoodle.embed", "de.flapdoodle.embed.mongo", DependencyScope.TEST_RUNTIME);
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("de.flapdoodle.embed");
			assertThat(dependency).textAtPath("artifactId").isEqualTo("de.flapdoodle.embed.mongo");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isEqualTo("test");
			assertThat(dependency).textAtPath("optional").isNullOrEmpty();
		});
	}

	@Test
	void pomWithClassifierDependency() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.dependencies().add("foo-bar", Dependency.withCoordinates("com.example", "acme").classifier("test-jar"));
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("com.example");
			assertThat(dependency).textAtPath("artifactId").isEqualTo("acme");
			assertThat(dependency).textAtPath("classifier").isEqualTo("test-jar");
		});
	}

	@Test
	void pomWithExclusions() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.dependencies()
			.add("test",
					Dependency.withCoordinates("com.example", "test")
						.scope(DependencyScope.COMPILE)
						.exclusions(new Exclusion("com.example.legacy", "legacy-one"),
								new Exclusion("com.example.another", "legacy-two")));
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("com.example");
			assertThat(dependency).textAtPath("artifactId").isEqualTo("test");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isNullOrEmpty();
			assertThat(dependency).textAtPath("optional").isNullOrEmpty();
			NodeAssert exclusions = assertThat(dependency).nodeAtPath("exclusions");
			NodeAssert firstExclusion = assertThat(exclusions).nodeAtPath("exclusion[1]");
			assertThat(firstExclusion).textAtPath("groupId").isEqualTo("com.example.legacy");
			assertThat(firstExclusion).textAtPath("artifactId").isEqualTo("legacy-one");
			NodeAssert secondExclusion = assertThat(exclusions).nodeAtPath("exclusion[2]");
			assertThat(secondExclusion).textAtPath("groupId").isEqualTo("com.example.another");
			assertThat(secondExclusion).textAtPath("artifactId").isEqualTo("legacy-two");
		});
	}

	@Test
	void pomWithOptionalDependency() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.dependencies()
			.add("annotation-processor",
					MavenDependency.withCoordinates("org.springframework.boot", "spring-boot-configuration-processor")
						.scope(DependencyScope.COMPILE)
						.optional(true));
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("org.springframework.boot");
			assertThat(dependency).textAtPath("artifactId").isEqualTo("spring-boot-configuration-processor");
			assertThat(dependency).textAtPath("version").isNullOrEmpty();
			assertThat(dependency).textAtPath("scope").isNullOrEmpty();
			assertThat(dependency).textAtPath("optional").isEqualTo("true");
		});
	}

	@Test
	void pomWithNonNullArtifactTypeDependency() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.dependencies()
			.add("root",
					Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter")
						.scope(DependencyScope.COMPILE)
						.type("tar.gz"));
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencies/dependency");
			assertThat(dependency).textAtPath("type").isEqualTo("tar.gz");
		});
	}

	@Test
	void pomWithOrderedDependencies() {
		MavenBuild build = new MavenBuild();
		build.dependencies().add("beta", Dependency.withCoordinates("com.example", "beta"));
		build.dependencies().add("alpha", Dependency.withCoordinates("com.example", "alpha"));
		build.dependencies()
			.add("web", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter-web"));
		build.dependencies().add("root", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter"));
		String pom = writePom(new MavenBuildWriter(), build);
		assertThat(pom).containsSubsequence("<artifactId>spring-boot-starter</artifactId>",
				"<artifactId>spring-boot-starter-web</artifactId>", "<artifactId>alpha</artifactId>",
				"<artifactId>beta</artifactId>");
	}

	@Test
	void pomWithOrderedDependenciesAndCustomComparator() {
		MavenBuild build = new MavenBuild();
		build.dependencies().add("beta", Dependency.withCoordinates("com.example", "beta"));
		build.dependencies().add("alpha", Dependency.withCoordinates("com.example", "alpha"));
		build.dependencies()
			.add("web", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter-web"));
		build.dependencies().add("root", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter"));
		MavenBuildWriter writer = new MavenBuildWriter() {
			@Override
			protected Comparator<Dependency> getDependencyComparator() {
				return Comparator.comparing(Dependency::getArtifactId);
			}
		};
		String pom = writePom(writer, build);
		assertThat(pom).containsSubsequence("<artifactId>alpha</artifactId>", "<artifactId>beta</artifactId>",
				"<artifactId>spring-boot-starter</artifactId>", "<artifactId>spring-boot-starter-web</artifactId>");
	}

	@Test
	void pomWithBom() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.boms()
			.add("test", BillOfMaterials.withCoordinates("com.example", "my-project-dependencies")
				.version(VersionReference.ofValue("1.0.0.RELEASE")));
		generatePom(build, (pom) -> {
			NodeAssert dependency = pom.nodeAtPath("/project/dependencyManagement/dependencies/dependency");
			assertBom(dependency, "com.example", "my-project-dependencies", "1.0.0.RELEASE");
		});
	}

	@Test
	void pomWithOrderedBoms() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.boms()
			.add("bom1",
					BillOfMaterials.withCoordinates("com.example", "my-project-dependencies")
						.version(VersionReference.ofValue("1.0.0.RELEASE"))
						.order(5));
		build.boms()
			.add("bom2",
					BillOfMaterials.withCoordinates("com.example", "root-dependencies")
						.version(VersionReference.ofProperty("root.version"))
						.order(2));
		generatePom(build, (pom) -> {
			NodeAssert dependencies = pom.nodeAtPath("/project/dependencyManagement/dependencies");
			NodeAssert firstBom = assertThat(dependencies).nodeAtPath("dependency[1]");
			assertBom(firstBom, "com.example", "root-dependencies", "${root.version}");
			NodeAssert secondBom = assertThat(dependencies).nodeAtPath("dependency[2]");
			assertBom(secondBom, "com.example", "my-project-dependencies", "1.0.0.RELEASE");
		});
	}

	private void assertBom(NodeAssert firstBom, String groupId, String artifactId, String version) {
		assertThat(firstBom).textAtPath("groupId").isEqualTo(groupId);
		assertThat(firstBom).textAtPath("artifactId").isEqualTo(artifactId);

		assertThat(firstBom).textAtPath("version").isEqualTo(version);
		assertThat(firstBom).textAtPath("type").isEqualTo("pom");
		assertThat(firstBom).textAtPath("scope").isEqualTo("import");
	}

	@Test
	void pomWithResources() {
		MavenBuild build = new MavenBuild();
		build.resources().add("src/main/custom", (resource) -> resource.includes("**/*.properties"));
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/build/resources/resource/directory").isEqualTo("src/main/custom");
			assertThat(pom).textAtPath("/project/build/resources/resource/targetPath").isNullOrEmpty();
			assertThat(pom).textAtPath("/project/build/resources/resource/filtering").isNullOrEmpty();
			assertThat(pom).textAtPath("/project/build/resources/resource/includes/include")
				.isEqualTo("**/*.properties");
			assertThat(pom).textAtPath("/project/build/resources/resource/excludes").isNullOrEmpty();
			assertThat(pom).textAtPath("/project/build/testResources").isNullOrEmpty();
		});
	}

	@Test
	void pomWithTestResources() {
		MavenBuild build = new MavenBuild();
		build.testResources()
			.add("src/test/custom", (resource) -> resource.excludes("**/*.gen").filtering(true).targetPath("test"));
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/build/resources").isNullOrEmpty();
			assertThat(pom).textAtPath("/project/build/testResources/testResource/directory")
				.isEqualTo("src/test/custom");
			assertThat(pom).textAtPath("/project/build/testResources/testResource/targetPath").isEqualTo("test");
			assertThat(pom).textAtPath("/project/build/testResources/testResource/filtering").isEqualTo("true");
			assertThat(pom).textAtPath("/project/build/testResources/testResource/includes").isNullOrEmpty();
			assertThat(pom).textAtPath("/project/build/testResources/testResource/excludes/exclude")
				.isEqualTo("**/*.gen");
		});
	}

	@Test
	void pomWithPluginManagement() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.pluginManagementPlugins()
			.add("org.springframework.boot", "spring-boot-maven-plugin", (plugin) -> plugin.version("1.2.3"));
		generatePom(build, (pom) -> {
			NodeAssert plugin = pom.nodeAtPath("/project/build/pluginManagement/plugins/plugin");
			assertThat(plugin).textAtPath("groupId").isEqualTo("org.springframework.boot");
			assertThat(plugin).textAtPath("artifactId").isEqualTo("spring-boot-maven-plugin");
			assertThat(plugin).textAtPath("version").isEqualTo("1.2.3");
		});
	}

	@Test
	void pomWithPlugin() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.plugins().add("org.springframework.boot", "spring-boot-maven-plugin");
		generatePom(build, (pom) -> {
			NodeAssert plugin = pom.nodeAtPath("/project/build/plugins/plugin");
			assertThat(plugin).textAtPath("groupId").isEqualTo("org.springframework.boot");
			assertThat(plugin).textAtPath("artifactId").isEqualTo("spring-boot-maven-plugin");
			assertThat(plugin).textAtPath("version").isNullOrEmpty();
			assertThat(plugin).textAtPath("inherited").isNullOrEmpty();
			assertThat(plugin).textAtPath("extensions").isNullOrEmpty();
		});
	}

	@Test
	void pomWithPluginWithConfiguration() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.plugins()
			.add("org.jetbrains.kotlin", "kotlin-maven-plugin", (plugin) -> plugin.configuration((configuration) -> {
				configuration.configure("args", (args) -> args.add("arg", "-Xjsr305=strict"));
				configuration.configure("compilerPlugins",
						(compilerPlugins) -> compilerPlugins.add("plugin", "spring"));
			}));
		generatePom(build, (pom) -> {
			NodeAssert plugin = pom.nodeAtPath("/project/build/plugins/plugin");
			assertThat(plugin).textAtPath("groupId").isEqualTo("org.jetbrains.kotlin");
			assertThat(plugin).textAtPath("artifactId").isEqualTo("kotlin-maven-plugin");
			assertThat(plugin).textAtPath("version").isNullOrEmpty();
			NodeAssert configuration = plugin.nodeAtPath("configuration");
			assertThat(configuration).textAtPath("args/arg").isEqualTo("-Xjsr305=strict");
			assertThat(configuration).textAtPath("compilerPlugins/plugin").isEqualTo("spring");
		});
	}

	@Test
	void pomWithPluginWithExecution() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.plugins().add("org.asciidoctor", "asciidoctor-maven-plugin", (plugin) -> {
			plugin.version("1.5.3");
			plugin.execution("generateProject-docs", (execution) -> {
				execution.goal("process-asciidoc");
				execution.phase("generateProject-resources");
				execution.configuration((configuration) -> {
					configuration.add("doctype", "book");
					configuration.add("backend", "html");
				});
			});
		});
		generatePom(build, (pom) -> {
			NodeAssert plugin = pom.nodeAtPath("/project/build/plugins/plugin");
			assertThat(plugin).textAtPath("groupId").isEqualTo("org.asciidoctor");
			assertThat(plugin).textAtPath("artifactId").isEqualTo("asciidoctor-maven-plugin");
			assertThat(plugin).textAtPath("version").isEqualTo("1.5.3");
			NodeAssert execution = plugin.nodeAtPath("executions/execution");
			assertThat(execution).textAtPath("id").isEqualTo("generateProject-docs");
			assertThat(execution).textAtPath("goals/goal").isEqualTo("process-asciidoc");
			assertThat(execution).textAtPath("phase").isEqualTo("generateProject-resources");
			NodeAssert configuration = execution.nodeAtPath("configuration");
			assertThat(configuration).textAtPath("doctype").isEqualTo("book");
			assertThat(configuration).textAtPath("backend").isEqualTo("html");
		});
	}

	@Test
	void pomWithPluginWithExecutionAndIDEHint() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.plugins().add("org.asciidoctor", "asciidoctor-maven-plugin", (plugin) -> {
			plugin.version("1.5.3");
			plugin.execution("generateProject-docs", (execution) -> {
				execution.m2e("ignore");
				execution.goal("process-asciidoc");
			});
		});
		generatePom(build, (pom) -> {
			NodeAssert plugin = pom.nodeAtPath("/project/build/plugins/plugin");
			assertThat(plugin).textAtPath("groupId").isEqualTo("org.asciidoctor");
			assertThat(plugin).textAtPath("artifactId").isEqualTo("asciidoctor-maven-plugin");
			assertThat(plugin).textAtPath("version").isEqualTo("1.5.3");
			NodeAssert execution = plugin.nodeAtPath("executions/execution");
			assertThat(execution).textAtPath("id").isEqualTo("generateProject-docs");
			assertThat(execution).matches((node) -> {
				Node child = node.getFirstChild();
				while (child != null) {
					if (child.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
						return child.getNodeName().equals("m2e") && child.getTextContent().equals("ignore");
					}
					child = child.getNextSibling();
				}
				return false;
			}, "m2e:ignore");
		});
	}

	@Test
	void pomWithPluginWithDependency() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.plugins()
			.add("org.jetbrains.kotlin", "kotlin-maven-plugin",
					(plugin) -> plugin.dependency("org.jetbrains.kotlin", "kotlin-maven-allopen", "${kotlin.version}"));
		generatePom(build, (pom) -> {
			NodeAssert plugin = pom.nodeAtPath("/project/build/plugins/plugin");
			assertThat(plugin).textAtPath("groupId").isEqualTo("org.jetbrains.kotlin");
			assertThat(plugin).textAtPath("artifactId").isEqualTo("kotlin-maven-plugin");
			NodeAssert dependency = plugin.nodeAtPath("dependencies/dependency");
			assertThat(dependency).textAtPath("groupId").isEqualTo("org.jetbrains.kotlin");
			assertThat(dependency).textAtPath("artifactId").isEqualTo("kotlin-maven-allopen");
			assertThat(dependency).textAtPath("version").isEqualTo("${kotlin.version}");
		});
	}

	@Test
	void pomWithNonInheritedPlugin() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.plugins().add("com.example.demo", "demo-plugin", (plugin) -> plugin.inherited(false));
		generatePom(build, (pom) -> {
			NodeAssert plugin = pom.nodeAtPath("/project/build/plugins/plugin");
			assertThat(plugin).textAtPath("groupId").isEqualTo("com.example.demo");
			assertThat(plugin).textAtPath("artifactId").isEqualTo("demo-plugin");
			assertThat(plugin).textAtPath("inherited").isEqualTo("false");
		});
	}

	@Test
	void pomWithPluginWithExtensions() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.plugins().add("com.example.demo", "demo-plugin", (plugin) -> plugin.extensions(true));
		generatePom(build, (pom) -> {
			NodeAssert plugin = pom.nodeAtPath("/project/build/plugins/plugin");
			assertThat(plugin).textAtPath("groupId").isEqualTo("com.example.demo");
			assertThat(plugin).textAtPath("artifactId").isEqualTo("demo-plugin");
			assertThat(plugin).textAtPath("extensions").isEqualTo("true");
		});
	}

	@Test
	void pomWithoutExtensions() {
		MavenBuild build = new MavenBuild();
		generatePom(build, (pom) -> assertThat(pom).nodeAtPath("/project/build/extensions").isNull());
	}

	@Test
	void pomWithExtension() {
		MavenBuild build = new MavenBuild();
		build.extensions().add("com.example", "testExtension", "1.6.1");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/build/extensions/extension/groupId").isEqualTo("com.example");
			assertThat(pom).textAtPath("/project/build/extensions/extension/artifactId").isEqualTo("testExtension");
			assertThat(pom).textAtPath("/project/build/extensions/extension/version").isEqualTo("1.6.1");
		});
	}

	@Test
	void pomWithEmptyBuild() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		generatePom(build, (pom) -> assertThat(pom).textAtPath("/project/build/").isNullOrEmpty());
	}

	@Test
	void pomWithMavenCentral() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.repositories().add("maven-central");
		generatePom(build, (pom) -> {
			assertThat(pom).nodeAtPath("/project/repositories").isNull();
			assertThat(pom).nodeAtPath("/project/pluginRepositories").isNull();
		});
	}

	@Test
	void pomWithReleasesOnlyRepository() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.repositories()
			.add(MavenRepository.withIdAndUrl("spring-milestones", "https://repo.spring.io/milestone")
				.name("Spring Milestones"));
		generatePom(build, (pom) -> {
			NodeAssert repository = pom.nodeAtPath("/project/repositories/repository");
			assertThat(repository).textAtPath("id").isEqualTo("spring-milestones");
			assertThat(repository).textAtPath("name").isEqualTo("Spring Milestones");
			assertThat(repository).textAtPath("url").isEqualTo("https://repo.spring.io/milestone");
			assertThat(repository).nodeAtPath("releases").isNull();
			assertThat(repository).textAtPath("snapshots/enabled").isEqualTo("false");
			assertThat(pom).nodeAtPath("/project/pluginRepositories").isNull();
		});
	}

	@Test
	void pomWithReleasesOnlyPluginRepository() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.pluginRepositories()
			.add(MavenRepository.withIdAndUrl("spring-milestones", "https://repo.spring.io/milestone")
				.name("Spring Milestones"));
		generatePom(build, (pom) -> {
			NodeAssert repository = pom.nodeAtPath("/project/pluginRepositories/pluginRepository");
			assertThat(repository).textAtPath("id").isEqualTo("spring-milestones");
			assertThat(repository).textAtPath("name").isEqualTo("Spring Milestones");
			assertThat(repository).textAtPath("url").isEqualTo("https://repo.spring.io/milestone");
			assertThat(repository).nodeAtPath("releases").isNull();
			assertThat(repository).textAtPath("snapshots/enabled").isEqualTo("false");
			assertThat(pom).nodeAtPath("/project/repositories").isNull();
		});
	}

	@Test
	void pomWithSnapshotsOnlyRepository() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.repositories()
			.add(MavenRepository.withIdAndUrl("spring-snapshots", "https://repo.spring.io/snapshot")
				.name("Spring Snapshots")
				.onlySnapshots());
		generatePom(build, (pom) -> {
			NodeAssert repository = pom.nodeAtPath("/project/repositories/repository");
			assertThat(repository).textAtPath("id").isEqualTo("spring-snapshots");
			assertThat(repository).textAtPath("name").isEqualTo("Spring Snapshots");
			assertThat(repository).textAtPath("url").isEqualTo("https://repo.spring.io/snapshot");
			assertThat(repository).textAtPath("releases/enabled").isEqualTo("false");
			assertThat(repository).nodeAtPath("snapshots").isNull();
			assertThat(pom).nodeAtPath("/project/pluginRepositories").isNull();
		});
	}

	@Test
	void pomWithSnapshotsOnlyPluginRepository() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.pluginRepositories()
			.add(MavenRepository.withIdAndUrl("spring-snapshots", "https://repo.spring.io/snapshot")
				.name("Spring Snapshots")
				.onlySnapshots());
		generatePom(build, (pom) -> {
			NodeAssert repository = pom.nodeAtPath("/project/pluginRepositories/pluginRepository");
			assertThat(repository).textAtPath("id").isEqualTo("spring-snapshots");
			assertThat(repository).textAtPath("name").isEqualTo("Spring Snapshots");
			assertThat(repository).textAtPath("url").isEqualTo("https://repo.spring.io/snapshot");
			assertThat(repository).textAtPath("releases/enabled").isEqualTo("false");
			assertThat(repository).nodeAtPath("snapshots").isNull();
			assertThat(pom).nodeAtPath("/project/repositories").isNull();
		});
	}

	@Test
	void pomWithReleasesAndSnapshotsRepository() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.repositories()
			.add(MavenRepository.withIdAndUrl("example", "https://repo.example.com/")
				.name("Example Repo")
				.releasesEnabled(true)
				.snapshotsEnabled(true));
		generatePom(build, (pom) -> {
			NodeAssert repository = pom.nodeAtPath("/project/repositories/repository");
			assertThat(repository).textAtPath("id").isEqualTo("example");
			assertThat(repository).textAtPath("name").isEqualTo("Example Repo");
			assertThat(repository).textAtPath("url").isEqualTo("https://repo.example.com/");
			assertThat(repository).nodeAtPath("releases").isNull();
			assertThat(repository).nodeAtPath("snapshots").isNull();
			assertThat(pom).nodeAtPath("/project/pluginRepositories").isNull();
		});
	}

	@Test
	void pomWithReleasesAndSnapshotsPluginRepository() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		build.pluginRepositories()
			.add(MavenRepository.withIdAndUrl("example", "https://repo.example.com/")
				.name("Example Repo")
				.releasesEnabled(true)
				.snapshotsEnabled(true));
		generatePom(build, (pom) -> {
			NodeAssert repository = pom.nodeAtPath("/project/pluginRepositories/pluginRepository");
			assertThat(repository).textAtPath("id").isEqualTo("example");
			assertThat(repository).textAtPath("name").isEqualTo("Example Repo");
			assertThat(repository).textAtPath("url").isEqualTo("https://repo.example.com/");
			assertThat(repository).nodeAtPath("releases").isNull();
			assertThat(repository).nodeAtPath("snapshots").isNull();
			assertThat(pom).nodeAtPath("/project/repositories").isNull();
		});
	}

	@Test
	void pomWithNoDefaultGoal() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo").build();
		generatePom(build, (pom) -> assertThat(pom.nodeAtPath("/project/build/defaultGoal")).isNull());
	}

	@Test
	void pomWithDefaultGoal() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo").defaultGoal("clean package");
		generatePom(build,
				(pom) -> assertThat(pom).textAtPath("/project/build/defaultGoal").isEqualTo("clean package"));
	}

	@Test
	void pomWithNoFinalName() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo").build();
		generatePom(build, (pom) -> assertThat(pom.nodeAtPath("/project/build/finalName")).isNull());
	}

	@Test
	void pomWithFinalName() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo").finalName("demo.jar");
		generatePom(build, (pom) -> assertThat(pom).textAtPath("/project/build/finalName").isEqualTo("demo.jar"));
	}

	@Test
	void pomWithCustomSourceDirectories() {
		MavenBuild build = new MavenBuild();
		build.settings()
			.coordinates("com.example.demo", "demo")
			.sourceDirectory("${project.basedir}/src/main/kotlin")
			.testSourceDirectory("${project.basedir}/src/test/kotlin");
		generatePom(build, (pom) -> {
			assertThat(pom).textAtPath("/project/build/sourceDirectory")
				.isEqualTo("${project.basedir}/src/main/kotlin");
			assertThat(pom).textAtPath("/project/build/testSourceDirectory")
				.isEqualTo("${project.basedir}/src/test/kotlin");
		});
	}

	@Test
	void pomWithCustomVersion() {
		MavenBuild build = new MavenBuild();
		build.settings().version("1.2.4.RELEASE");
		generatePom(build, (pom) -> assertThat(pom).textAtPath("/project/version").isEqualTo("1.2.4.RELEASE"));
	}

	@Test
	void pomWithDistributionManagementEmpty() {
		MavenBuild build = new MavenBuild();
		generatePom(build, (pom) -> assertThat(pom).nodeAtPath("/project/distributionManagement").isNull());
	}

	@Test
	void pomWithDistributionManagementDownloadUrl() {
		MavenBuild build = new MavenBuild();
		build.distributionManagement().downloadUrl("https://example.com/download");
		generatePom(build, (pom) -> {
			NodeAssert distributionManagement = pom.nodeAtPath("/project/distributionManagement");
			assertThat(distributionManagement).textAtPath("downloadUrl").isEqualTo("https://example.com/download");
			assertThat(distributionManagement).nodeAtPath("repository").isNull();
			assertThat(distributionManagement).nodeAtPath("snapshotRepository").isNull();
			assertThat(distributionManagement).nodeAtPath("site").isNull();
			assertThat(distributionManagement).nodeAtPath("relocation").isNull();
		});
	}

	@Test
	void pomWithDistributionManagementRepository() {
		MavenBuild build = new MavenBuild();
		build.distributionManagement()
			.repository((repository) -> repository.id("released-repo")
				.name("released repo")
				.url("https://upload.example.com/releases"));
		generatePom(build, (pom) -> {
			NodeAssert distributionManagement = pom.nodeAtPath("/project/distributionManagement");
			assertThat(distributionManagement).textAtPath("downloadUrl").isNullOrEmpty();
			assertThat(distributionManagement).textAtPath("repository/id").isEqualTo("released-repo");
			assertThat(distributionManagement).textAtPath("repository/name").isEqualTo("released repo");
			assertThat(distributionManagement).textAtPath("repository/url")
				.isEqualTo("https://upload.example.com/releases");
			assertThat(distributionManagement).textAtPath("repository/layout").isNullOrEmpty();
			assertThat(distributionManagement).textAtPath("repository/uniqueVersion").isNullOrEmpty();
			assertThat(distributionManagement).nodeAtPath("snapshotRepository").isNull();
			assertThat(distributionManagement).nodeAtPath("site").isNull();
			assertThat(distributionManagement).nodeAtPath("relocation").isNull();
		});
	}

	@Test
	void pomWithDistributionManagementSnapshotRepository() {
		MavenBuild build = new MavenBuild();
		build.distributionManagement()
			.snapshotRepository((repository) -> repository.id("snapshot-repo")
				.name("snapshot repo")
				.url("scp://upload.example.com/snapshots")
				.layout("legacy")
				.uniqueVersion(true));
		generatePom(build, (pom) -> {
			NodeAssert distributionManagement = pom.nodeAtPath("/project/distributionManagement");
			assertThat(distributionManagement).textAtPath("downloadUrl").isNullOrEmpty();
			assertThat(distributionManagement).nodeAtPath("repository").isNull();
			assertThat(distributionManagement).textAtPath("snapshotRepository/id").isEqualTo("snapshot-repo");
			assertThat(distributionManagement).textAtPath("snapshotRepository/name").isEqualTo("snapshot repo");
			assertThat(distributionManagement).textAtPath("snapshotRepository/url")
				.isEqualTo("scp://upload.example.com/snapshots");
			assertThat(distributionManagement).textAtPath("snapshotRepository/layout").isEqualTo("legacy");
			assertThat(distributionManagement).textAtPath("snapshotRepository/uniqueVersion").isEqualTo("true");
			assertThat(distributionManagement).nodeAtPath("site").isNull();
			assertThat(distributionManagement).nodeAtPath("relocation").isNull();
		});
	}

	@Test
	void pomWithDistributionManagementSite() {
		MavenBuild build = new MavenBuild();
		build.distributionManagement()
			.site((site) -> site.id("website").name("web site"))
			.site((site) -> site.url("scp://www.example.com/www/docs/project"));
		generatePom(build, (pom) -> {
			NodeAssert distributionManagement = pom.nodeAtPath("/project/distributionManagement");
			assertThat(distributionManagement).textAtPath("downloadUrl").isNullOrEmpty();
			assertThat(distributionManagement).nodeAtPath("repository").isNull();
			assertThat(distributionManagement).nodeAtPath("snapshotRepository").isNull();
			assertThat(distributionManagement).textAtPath("site/id").isEqualTo("website");
			assertThat(distributionManagement).textAtPath("site/name").isEqualTo("web site");
			assertThat(distributionManagement).textAtPath("site/url")
				.isEqualTo("scp://www.example.com/www/docs/project");
			assertThat(distributionManagement).nodeAtPath("relocation").isNull();
		});
	}

	@Test
	void pomWithDistributionManagementRelocation() {
		MavenBuild build = new MavenBuild();
		build.distributionManagement()
			.relocation((relocation) -> relocation.groupId("com.example.new")
				.artifactId("project")
				.version("1.0.0")
				.message("moved"));
		generatePom(build, (pom) -> {
			NodeAssert distributionManagement = pom.nodeAtPath("/project/distributionManagement");
			assertThat(distributionManagement).textAtPath("downloadUrl").isNullOrEmpty();
			assertThat(distributionManagement).nodeAtPath("repository").isNull();
			assertThat(distributionManagement).nodeAtPath("snapshotRepository").isNull();
			assertThat(distributionManagement).nodeAtPath("site").isNull();
			assertThat(distributionManagement).textAtPath("relocation/groupId").isEqualTo("com.example.new");
			assertThat(distributionManagement).textAtPath("relocation/artifactId").isEqualTo("project");
			assertThat(distributionManagement).textAtPath("relocation/version").isEqualTo("1.0.0");
			assertThat(distributionManagement).textAtPath("relocation/message").isEqualTo("moved");
		});
	}

	@Test
	void pomWithReservedCharacters() {
		MavenBuild build = new MavenBuild();
		build.settings()
			.coordinates("com.example.demo", "demo")
			.name("<demo project>")
			.description("A \"demo\" project for 'developers' & 'testers'");
		String pom = writePom(new MavenBuildWriter(), build);
		assertThat(pom).contains("<name>&lt;demo project&gt;</name>")
			.contains(
					"<description>A &quot;demo&quot; project for &apos;developers&apos; &amp; &apos;testers&apos;</description>");
	}

	@Test
	void pomWithNoProfile() {
		MavenBuild build = new MavenBuild();
		build.settings().coordinates("com.example.demo", "demo");
		generatePom(build, (pom) -> assertThat(pom).textAtPath("/project/profiles/").isNullOrEmpty());
	}

	@Test
	void powWithEmptyProfile() {
		MavenBuild build = new MavenBuild();
		build.profiles().id("profile1");
		generatePom(build, (pom) -> {
			NodeAssert profile = pom.nodeAtPath("/project/profiles/profile");
			assertThat(profile).textAtPath("id").isEqualTo("profile1");
			assertThat(profile).nodeAtPath("activation").isNull();
			assertThat(profile).nodeAtPath("build").isNull();
			assertThat(profile).nodeAtPath("repositories").isNull();
			assertThat(profile).nodeAtPath("pluginRepositories").isNull();
			assertThat(profile).nodeAtPath("dependencies").isNull();
			assertThat(profile).nodeAtPath("dependencyManagement").isNull();
			assertThat(profile).nodeAtPath("distributionManagement").isNull();
		});
	}

	@Test
	void powWithProfileActivationActiveByDefaultAndJdk() {
		MavenBuild build = new MavenBuild();
		build.profiles().id("profile1").activation().activeByDefault(true).jdk("11");
		generatePom(build, (pom) -> {
			NodeAssert profile = pom.nodeAtPath("/project/profiles/profile");
			assertThat(profile).textAtPath("id").isEqualTo("profile1");
			assertThat(profile).textAtPath("activation/activeByDefault").isEqualTo("true");
			assertThat(profile).textAtPath("activation/jdk").isEqualTo("11");
			assertThat(profile).nodeAtPath("activation/os").isNull();
			assertThat(profile).nodeAtPath("activation/property").isNull();
			assertThat(profile).nodeAtPath("activation/file").isNull();
		});
	}

	@Test
	void powWithProfileActivationOs() {
		MavenBuild build = new MavenBuild();
		build.profiles().id("profile1").activation().os("linux", "intel", "x68", "1.0");
		generatePom(build, (pom) -> {
			NodeAssert profile = pom.nodeAtPath("/project/profiles/profile");
			assertThat(profile).textAtPath("id").isEqualTo("profile1");
			assertThat(profile).nodeAtPath("activation/jdk").isNull();
			assertThat(profile).textAtPath("activation/os/name").isEqualTo("linux");
			assertThat(profile).textAtPath("activation/os/family").isEqualTo("intel");
			assertThat(profile).textAtPath("activation/os/arch").isEqualTo("x68");
			assertThat(profile).textAtPath("activation/os/version").isEqualTo("1.0");
			assertThat(profile).nodeAtPath("activation/property").isNull();
			assertThat(profile).nodeAtPath("activation/file").isNull();
		});
	}

	@Test
	void powWithProfileActivationProperty() {
		MavenBuild build = new MavenBuild();
		build.profiles().id("profile1").activation().property("name1", "value1");
		generatePom(build, (pom) -> {
			NodeAssert profile = pom.nodeAtPath("/project/profiles/profile");
			assertThat(profile).textAtPath("id").isEqualTo("profile1");
			assertThat(profile).nodeAtPath("activation/jdk").isNull();
			assertThat(profile).nodeAtPath("activation/os").isNull();
			assertThat(profile).textAtPath("activation/property/name").isEqualTo("name1");
			assertThat(profile).textAtPath("activation/property/value").isEqualTo("value1");
			assertThat(profile).nodeAtPath("activation/file").isNull();
		});
	}

	@Test
	void powWithProfileActivationFileExists() {
		MavenBuild build = new MavenBuild();
		build.profiles().id("profile1").activation().fileExists("test.txt");
		generatePom(build, (pom) -> {
			NodeAssert profile = pom.nodeAtPath("/project/profiles/profile");
			assertThat(profile).textAtPath("id").isEqualTo("profile1");
			assertThat(profile).nodeAtPath("activation/jdk").isNull();
			assertThat(profile).nodeAtPath("activation/os").isNull();
			assertThat(profile).nodeAtPath("activation/property").isNull();
			assertThat(profile).textAtPath("activation/file/exists").isEqualTo("test.txt");
			assertThat(profile).nodeAtPath("activation/file/missing").isNull();
		});
	}

	@Test
	void powWithProfileActivationFileMissing() {
		MavenBuild build = new MavenBuild();
		build.profiles().id("profile1").activation().fileMissing("test.txt");
		generatePom(build, (pom) -> {
			NodeAssert profile = pom.nodeAtPath("/project/profiles/profile");
			assertThat(profile).textAtPath("id").isEqualTo("profile1");
			assertThat(profile).nodeAtPath("activation/jdk").isNull();
			assertThat(profile).nodeAtPath("activation/os").isNull();
			assertThat(profile).nodeAtPath("activation/property").isNull();
			assertThat(profile).nodeAtPath("activation/file/exists").isNull();
			assertThat(profile).textAtPath("activation/file/missing").isEqualTo("test.txt");
		});
	}

	@Test
	void powWithProfileSettings() {
		MavenBuild build = new MavenBuild();
		build.profiles().id("profile1").settings().defaultGoal("compile").finalName("app");
		generatePom(build, (pom) -> {
			NodeAssert profile = pom.nodeAtPath("/project/profiles/profile");
			assertThat(profile).textAtPath("id").isEqualTo("profile1");
			assertThat(profile).textAtPath("build/defaultGoal").isEqualTo("compile");
			assertThat(profile).textAtPath("build/finalName").isEqualTo("app");
		});
	}

	@Test
	void pomWithProfileResources() {
		MavenBuild build = new MavenBuild();
		build.profiles()
			.id("profile1")
			.resources()
			.add("src/main/custom", (resource) -> resource.includes("**/*.properties"));
		generatePom(build, (pom) -> {
			NodeAssert profile = pom.nodeAtPath("/project/profiles/profile");
			assertThat(profile).textAtPath("id").isEqualTo("profile1");
			assertThat(profile).textAtPath("build/resources/resource/directory").isEqualTo("src/main/custom");
			assertThat(profile).textAtPath("build/resources/resource/targetPath").isNullOrEmpty();
			assertThat(profile).textAtPath("build/resources/resource/filtering").isNullOrEmpty();
			assertThat(profile).textAtPath("build/resources/resource/includes/include").isEqualTo("**/*.properties");
			assertThat(profile).textAtPath("build/resources/resource/excludes").isNullOrEmpty();
			assertThat(profile).textAtPath("build/testResources").isNullOrEmpty();
		});
	}

	@Test
	void pomWithProfileTestResources() {
		MavenBuild build = new MavenBuild();
		build.profiles()
			.id("profile1")
			.testResources()
			.add("src/test/custom", (resource) -> resource.excludes("**/*.gen").filtering(true).targetPath("test"));
		generatePom(build, (pom) -> {
			NodeAssert profile = pom.nodeAtPath("/project/profiles/profile");
			assertThat(profile).textAtPath("id").isEqualTo("profile1");
			assertThat(profile).textAtPath("build/resources").isNullOrEmpty();
			assertThat(profile).textAtPath("build/testResources/testResource/directory").isEqualTo("src/test/custom");
			assertThat(profile).textAtPath("build/testResources/testResource/targetPath").isEqualTo("test");
			assertThat(profile).textAtPath("build/testResources/testResource/filtering").isEqualTo("true");
			assertThat(profile).textAtPath("build/testResources/testResource/includes").isNullOrEmpty();
			assertThat(profile).textAtPath("build/testResources/testResource/excludes/exclude").isEqualTo("**/*.gen");
		});
	}

	@Test
	void pomWithProfilePluginManagement() {
		MavenBuild build = new MavenBuild();
		build.profiles()
			.id("profile1")
			.pluginManagementPlugins()
			.add("org.springframework.boot", "spring-boot-maven-plugin", (plugin) -> plugin.version("1.2.3"));
		generatePom(build, (pom) -> {
			NodeAssert profile = pom.nodeAtPath("/project/profiles/profile");
			assertThat(profile).textAtPath("id").isEqualTo("profile1");
			NodeAssert plugin = profile.nodeAtPath("build/pluginManagement/plugins/plugin");
			assertThat(plugin).textAtPath("groupId").isEqualTo("org.springframework.boot");
			assertThat(plugin).textAtPath("artifactId").isEqualTo("spring-boot-maven-plugin");
			assertThat(plugin).textAtPath("version").isEqualTo("1.2.3");
		});
	}

	@Test
	void pomWithProfilePlugin() {
		MavenBuild build = new MavenBuild();
		build.profiles().id("profile1").plugins().add("org.springframework.boot", "spring-boot-maven-plugin");
		generatePom(build, (pom) -> {
			NodeAssert profile = pom.nodeAtPath("/project/profiles/profile");
			assertThat(profile).textAtPath("id").isEqualTo("profile1");
			NodeAssert plugin = profile.nodeAtPath("build/plugins/plugin");
			assertThat(plugin).textAtPath("groupId").isEqualTo("org.springframework.boot");
			assertThat(plugin).textAtPath("artifactId").isEqualTo("spring-boot-maven-plugin");
			assertThat(plugin).textAtPath("version").isNullOrEmpty();
			assertThat(plugin).textAtPath("inherited").isNullOrEmpty();
			assertThat(plugin).textAtPath("extensions").isNullOrEmpty();
		});
	}

	@Test
	void pomWithProfileDistributionManagement() {
		MavenBuild build = new MavenBuild();
		build.profiles().id("profile1").distributionManagement().downloadUrl("https://example.com/download");
		generatePom(build, (pom) -> {
			NodeAssert profile = pom.nodeAtPath("/project/profiles/profile");
			assertThat(profile).textAtPath("id").isEqualTo("profile1");
			NodeAssert distributionManagement = profile.nodeAtPath("distributionManagement");
			assertThat(distributionManagement).textAtPath("downloadUrl").isEqualTo("https://example.com/download");
			assertThat(distributionManagement).nodeAtPath("repository").isNull();
			assertThat(distributionManagement).nodeAtPath("snapshotRepository").isNull();
			assertThat(distributionManagement).nodeAtPath("site").isNull();
			assertThat(distributionManagement).nodeAtPath("relocation").isNull();
		});
	}

	private void generatePom(MavenBuild mavenBuild, Consumer<NodeAssert> consumer) {
		consumer.accept(new NodeAssert(writePom(new MavenBuildWriter(), mavenBuild)));
	}

	private String writePom(MavenBuildWriter writer, MavenBuild mavenBuild) {
		StringWriter out = new StringWriter();
		writer.writeTo(new IndentingWriter(out), mavenBuild);
		return out.toString();
	}

}
