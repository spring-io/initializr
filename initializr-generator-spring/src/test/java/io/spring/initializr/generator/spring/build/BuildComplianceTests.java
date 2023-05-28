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
package io.spring.initializr.generator.spring.build;

import java.util.stream.Stream;
import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.groovy.GroovyLanguage;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.spring.AbstractComplianceTests;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Build compliance tests.
 *
 * @author Stephane Nicoll
 * @author Jean-Baptiste Nizet
 */
class BuildComplianceTests extends AbstractComplianceTests {

    private static final Language java = new JavaLanguage();

    private static final Language groovy = new GroovyLanguage();

    private static final Language kotlin = new KotlinLanguage();

    static Stream<Arguments> parameters() {
        return Stream.of(Arguments.arguments(BuildSystem.forId(MavenBuildSystem.ID), "pom.xml"), Arguments.arguments(BuildSystem.forId(GradleBuildSystem.ID), "build.gradle"), Arguments.arguments(BuildSystem.forIdAndDialect(GradleBuildSystem.ID, GradleBuildSystem.DIALECT_KOTLIN), "build.gradle.kts"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void currentGenerationJarJava(BuildSystem build, String fileName) {
        testCurrentGenerationJar(java, build, fileName);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void currentGenerationJarGroovy(BuildSystem build, String fileName) {
        testCurrentGenerationJar(groovy, build, fileName);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void currentGenerationJarKotlin(BuildSystem build, String fileName) {
        testCurrentGenerationJar(kotlin, build, fileName);
    }

    private void testCurrentGenerationJar(Language language, BuildSystem build, String fileName) {
        assertThat(generateProject(language, build, "2.4.1")).textFile(fileName).hasSameContentAs(new ClassPathResource("project/" + language + "/standard/" + getAssertFileName(fileName)));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void nextGenerationJarGroovy(BuildSystem build, String fileName) {
        testNextGenerationJar(new GroovyLanguage("17"), build, fileName);
    }

    private void testNextGenerationJar(Language language, BuildSystem build, String fileName) {
        assertThat(generateProject(language, build, "3.0.0")).textFile(fileName).hasSameContentAs(new ClassPathResource("project/" + language + "/next/" + getAssertFileName(fileName)));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void currentGenerationWarJava(BuildSystem build, String fileName) {
        testCurrentGenerationWar(java, build, fileName);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void currentGenerationWarGroovy(BuildSystem build, String fileName) {
        testCurrentGenerationWar(groovy, build, fileName);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void currentGenerationWarKotlin(BuildSystem build, String fileName) {
        testCurrentGenerationWar(kotlin, build, fileName);
    }

    private void testCurrentGenerationWar(Language language, BuildSystem build, String fileName) {
        ProjectStructure project = generateProject(language, build, "2.4.1", (description) -> {
            description.addDependency("web", MetadataBuildItemMapper.toDependency(WEB));
            description.setPackaging(Packaging.forId("war"));
        });
        assertThat(project).textFile(fileName).hasSameContentAs(new ClassPathResource("project/" + language + "/standard/war-" + getAssertFileName(fileName)));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void kotlinJava11(BuildSystem build, String fileName) {
        ProjectStructure project = generateProject(kotlin, build, "2.4.1", (description) -> description.setLanguage(Language.forId(kotlin.id(), "11")));
        assertThat(project).textFile(fileName).hasSameContentAs(new ClassPathResource("project/" + build + "/kotlin-java11-" + getAssertFileName(fileName)));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void versionOverride(BuildSystem build, String fileName) {
        ProjectStructure project = generateProject(java, build, "2.4.1", (description) -> description.addDependency("web", MetadataBuildItemMapper.toDependency(WEB)), (projectGenerationContext) -> projectGenerationContext.registerBean(BuildCustomizer.class, () -> (projectBuild) -> projectBuild.properties().version(VersionProperty.of("spring-foo.version", false), "0.1.0.RELEASE").version(VersionProperty.of("spring-bar.version"), "0.2.0.RELEASE")));
        assertThat(project).textFile(fileName).hasSameContentAs(new ClassPathResource("project/" + build + "/version-override-" + getAssertFileName(fileName)));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void bomWithVersionProperty(BuildSystem build, String fileName) {
        Dependency foo = Dependency.withId("foo", "org.acme", "foo");
        foo.setBom("the-bom");
        BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom", "1.3.3");
        bom.setVersionProperty("foo.version");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().addDependencyGroup("foo", foo).addBom("the-bom", bom).build();
        ProjectStructure project = generateProject(java, build, "2.4.1", (description) -> description.addDependency("foo", MetadataBuildItemMapper.toDependency(foo)), metadata);
        assertThat(project).textFile(fileName).hasSameContentAs(new ClassPathResource("project/" + build + "/bom-property-" + getAssertFileName(fileName)));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void compileOnlyDependency(BuildSystem build, String fileName) {
        Dependency foo = Dependency.withId("foo", "org.acme", "foo");
        Dependency dataJpa = Dependency.withId("data-jpa", "org.springframework.boot", "spring-boot-starter-data-jpa");
        foo.setScope(Dependency.SCOPE_COMPILE_ONLY);
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().addDependencyGroup("core", "web", "data-jpa").addDependencyGroup("foo", foo).build();
        ProjectStructure project = generateProject(java, build, "2.4.1", (description) -> {
            description.addDependency("foo", MetadataBuildItemMapper.toDependency(foo));
            description.addDependency("web", MetadataBuildItemMapper.toDependency(WEB));
            description.addDependency("data-jpa", MetadataBuildItemMapper.toDependency(dataJpa));
        }, metadata);
        assertThat(project).textFile(fileName).hasSameContentAs(new ClassPathResource("project/" + build + "/compile-only-dependency-" + getAssertFileName(fileName)));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void annotationProcessorDependency(BuildSystem build, String fileName) {
        Dependency annotationProcessor = Dependency.withId("configuration-processor", "org.springframework.boot", "spring-boot-configuration-processor");
        Dependency dataJpa = Dependency.withId("data-jpa", "org.springframework.boot", "spring-boot-starter-data-jpa");
        annotationProcessor.setScope(Dependency.SCOPE_ANNOTATION_PROCESSOR);
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().addDependencyGroup("core", "web", "data-jpa").addDependencyGroup("configuration-processor", annotationProcessor).build();
        ProjectStructure project = generateProject(java, build, "2.4.1", (description) -> {
            description.addDependency("configuration-processor", MetadataBuildItemMapper.toDependency(annotationProcessor));
            description.addDependency("web", MetadataBuildItemMapper.toDependency(WEB));
            description.addDependency("data-jpa", MetadataBuildItemMapper.toDependency(dataJpa));
        }, metadata);
        assertThat(project).textFile(fileName).hasSameContentAs(new ClassPathResource("project/" + build + "/annotation-processor-dependency-" + getAssertFileName(fileName)));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void bomWithOrdering(BuildSystem build, String fileName) {
        Dependency foo = Dependency.withId("foo", "org.acme", "foo");
        foo.setBom("foo-bom");
        BillOfMaterials barBom = BillOfMaterials.create("org.acme", "bar-bom", "1.0");
        barBom.setOrder(50);
        BillOfMaterials bizBom = BillOfMaterials.create("org.acme", "biz-bom");
        bizBom.setOrder(40);
        bizBom.getAdditionalBoms().add("bar-bom");
        bizBom.getMappings().add(BillOfMaterials.Mapping.create("1.0.0.RELEASE", "1.0"));
        BillOfMaterials fooBom = BillOfMaterials.create("org.acme", "foo-bom", "1.0");
        fooBom.setOrder(20);
        fooBom.getAdditionalBoms().add("biz-bom");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().addDependencyGroup("foo", foo).addBom("foo-bom", fooBom).addBom("bar-bom", barBom).addBom("biz-bom", bizBom).build();
        ProjectStructure project = generateProject(java, build, "2.4.1", (description) -> description.addDependency("foo", MetadataBuildItemMapper.toDependency(foo)), metadata);
        assertThat(project).textFile(fileName).hasSameContentAs(new ClassPathResource("project/" + build + "/bom-ordering-" + getAssertFileName(fileName)));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void repositories(BuildSystem build, String fileName) {
        Dependency foo = Dependency.withId("foo", "org.acme", "foo");
        foo.setRepository("foo-repository");
        Dependency bar = Dependency.withId("bar", "org.acme", "bar");
        bar.setRepository("bar-repository");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().addDependencyGroup("test", foo, bar).addReleasesRepository("foo-repository", "foo-repo", "https://example.com/foo").addSnapshotsRepository("bar-repository", "bar-repo", "https://example.com/bar").build();
        ProjectStructure project = generateProject(java, build, "2.4.1", (description) -> {
            description.addDependency("foo", MetadataBuildItemMapper.toDependency(foo));
            description.addDependency("bar", MetadataBuildItemMapper.toDependency(bar));
        }, metadata);
        assertThat(project).textFile(fileName).hasSameContentAs(new ClassPathResource("project/" + build + "/repositories-" + getAssertFileName(fileName)));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void repositoriesMilestone(BuildSystem build, String fileName) {
        Dependency foo = Dependency.withId("foo", "org.acme", "foo");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().addDependencyGroup("test", foo).build();
        ProjectStructure project = generateProject(java, build, "2.4.1", (description) -> {
            description.setPlatformVersion(Version.parse("2.4.0-M1"));
            description.addDependency("foo", MetadataBuildItemMapper.toDependency(foo));
        }, metadata);
        assertThat(project).textFile(fileName).hasSameContentAs(new ClassPathResource("project/" + build + "/repositories-milestone-" + getAssertFileName(fileName)));
    }

    private String getAssertFileName(String fileName) {
        return fileName + ".gen";
    }
}
