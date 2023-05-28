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
package io.spring.initializr.metadata;

import java.util.Arrays;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link Dependency}.
 *
 * @author Stephane Nicoll
 */
class DependencyTests {

    @Test
    void createRootSpringBootStarter() {
        Dependency d = Dependency.createSpringBootStarter("");
        assertThat(d.getGroupId()).isEqualTo("org.springframework.boot");
        assertThat(d.getArtifactId()).isEqualTo("spring-boot-starter");
    }

    @Test
    void setCoordinatesFromId() {
        Dependency dependency = Dependency.withId("org.foo:bar:1.2.3");
        dependency.resolve();
        assertThat(dependency.getGroupId()).isEqualTo("org.foo");
        assertThat(dependency.getArtifactId()).isEqualTo("bar");
        assertThat(dependency.getVersion()).isEqualTo("1.2.3");
        assertThat(dependency.getId()).isEqualTo("org.foo:bar:1.2.3");
    }

    @Test
    void setCoordinatesFromIdNoVersion() {
        Dependency dependency = Dependency.withId("org.foo:bar");
        dependency.resolve();
        assertThat(dependency.getGroupId()).isEqualTo("org.foo");
        assertThat(dependency.getArtifactId()).isEqualTo("bar");
        assertThat(dependency.getVersion()).isNull();
        assertThat(dependency.getId()).isEqualTo("org.foo:bar");
    }

    @Test
    void setIdFromCoordinates() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.foo");
        dependency.setArtifactId("bar");
        dependency.setVersion("1.0");
        dependency.resolve();
        assertThat(dependency.getId()).isEqualTo("org.foo:bar");
    }

    @Test
    void setIdFromCoordinatesNoVersion() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.foo");
        dependency.setArtifactId("bar");
        dependency.resolve();
        assertThat(dependency.getId()).isEqualTo("org.foo:bar");
    }

    @Test
    void setIdFromSimpleName() {
        Dependency dependency = Dependency.withId("web");
        dependency.resolve();
        assertThat(dependency.getGroupId()).isEqualTo("org.springframework.boot");
        assertThat(dependency.getArtifactId()).isEqualTo("spring-boot-starter-web");
        assertThat(dependency.getVersion()).isNull();
        assertThat(dependency.getId()).isEqualTo("web");
    }

    @Test
    void invalidDependency() {
        assertThatExceptionOfType(InvalidInitializrMetadataException.class).isThrownBy(() -> new Dependency().resolve());
    }

    @Test
    void invalidDependencyScope() {
        Dependency dependency = Dependency.withId("web");
        assertThatExceptionOfType(InvalidInitializrMetadataException.class).isThrownBy(() -> dependency.setScope("whatever"));
    }

    @Test
    void invalidSpringBootRange() {
        Dependency dependency = Dependency.withId("web");
        dependency.setCompatibilityRange("A.B.C");
        assertThatExceptionOfType(InvalidInitializrMetadataException.class).isThrownBy(dependency::resolve).withMessageContaining("A.B.C");
    }

    @Test
    void invalidIdFormatTooManyColons() {
        Dependency dependency = Dependency.withId("org.foo:bar:1.0:test:external");
        assertThatExceptionOfType(InvalidInitializrMetadataException.class).isThrownBy(dependency::resolve);
    }

    @Test
    void invalidLink() {
        Dependency dependency = Dependency.withId("foo");
        dependency.getLinks().add(Link.create(null, "https://example.com"));
        assertThatExceptionOfType(InvalidInitializrMetadataException.class).isThrownBy(dependency::resolve);
    }

    @Test
    void generateIdWithNoGroupId() {
        Dependency dependency = new Dependency();
        dependency.setArtifactId("bar");
        assertThatIllegalArgumentException().isThrownBy(dependency::generateId);
    }

    @Test
    void generateIdWithNoArtifactId() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("foo");
        assertThatIllegalArgumentException().isThrownBy(dependency::generateId);
    }

    @Test
    void resolveNoMapping() {
        Dependency dependency = Dependency.withId("web");
        dependency.resolve();
        assertThat(dependency.resolve(Version.parse("1.2.0.RELEASE"))).isSameAs(dependency);
    }

    @Test
    void resolveInvalidMapping() {
        Dependency dependency = Dependency.withId("web");
        dependency.getMappings().add(Dependency.Mapping.create("foo-bar", null, null, "0.1.0.RELEASE", null, null, null));
        assertThatExceptionOfType(InvalidInitializrMetadataException.class).isThrownBy(dependency::resolve).withMessageContaining("foo-bar");
    }

    @Test
    void resolveVersionRequirement() {
        Dependency dependency = Dependency.withId("web");
        dependency.getMappings().add(Dependency.Mapping.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "0.1.0.RELEASE", null, null, null));
        dependency.resolve();
        Dependency resolved = dependency.resolve(Version.parse("1.1.5.RELEASE"));
        assertThat(resolved.getVersionRequirement()).isEqualTo(">=1.1.0.RELEASE and <1.2.0.RELEASE");
    }

    @Test
    void resolveMatchingVersionMapping() {
        Dependency dependency = Dependency.withId("web", null, null, "0.3.0.RELEASE");
        dependency.setDescription("A web dependency");
        dependency.getKeywords().addAll(Arrays.asList("foo", "bar"));
        dependency.getAliases().add("the-web");
        dependency.getFacets().add("web");
        dependency.getMappings().add(Dependency.Mapping.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "0.1.0.RELEASE", null, null, null));
        dependency.getMappings().add(Dependency.Mapping.create("[1.2.0.RELEASE, 1.3.0.RELEASE)", null, null, "0.2.0.RELEASE", null, null, null));
        dependency.resolve();
        validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")), "org.springframework.boot", "spring-boot-starter-web", "0.1.0.RELEASE", true);
        validateResolvedWebDependency(dependency.resolve(Version.parse("1.2.0.RELEASE")), "org.springframework.boot", "spring-boot-starter-web", "0.2.0.RELEASE", true);
        validateResolvedWebDependency(dependency.resolve(Version.parse("2.1.3.M1")), "org.springframework.boot", "spring-boot-starter-web", "0.3.0.RELEASE", // default
        true);
    }

    @Test
    void resolveMatchArtifactMapping() {
        Dependency dependency = Dependency.withId("web", null, null, "0.3.0.RELEASE");
        dependency.setDescription("A web dependency");
        dependency.getKeywords().addAll(Arrays.asList("foo", "bar"));
        dependency.getAliases().add("the-web");
        dependency.getFacets().add("web");
        dependency.getMappings().add(Dependency.Mapping.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", "org.spring.boot", null, null, null, null, null));
        dependency.getMappings().add(Dependency.Mapping.create("[1.2.0.RELEASE, 1.3.0.RELEASE)", null, "starter-web", null, null, null, null));
        dependency.resolve();
        validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")), "org.spring.boot", "spring-boot-starter-web", "0.3.0.RELEASE", true);
        validateResolvedWebDependency(dependency.resolve(Version.parse("1.2.0.RELEASE")), "org.springframework.boot", "starter-web", "0.3.0.RELEASE", true);
        validateResolvedWebDependency(dependency.resolve(Version.parse("2.1.3.M1")), "org.springframework.boot", "spring-boot-starter-web", "0.3.0.RELEASE", // default
        true);
    }

    @Test
    void resolveMatchingVersionWithVariablePatch() {
        Dependency dependency = Dependency.withId("web", null, null, "0.3.0.RELEASE");
        dependency.setDescription("A web dependency");
        dependency.getKeywords().addAll(Arrays.asList("foo", "bar"));
        dependency.getAliases().add("the-web");
        dependency.getFacets().add("web");
        dependency.getMappings().add(Dependency.Mapping.create("[1.1.0.RELEASE, 1.1.x.RELEASE]", null, null, "0.1.0.RELEASE", null, null, null));
        dependency.getMappings().add(Dependency.Mapping.create("[1.1.x.BUILD-SNAPSHOT, 1.2.0.RELEASE)", null, null, "0.2.0.RELEASE", null, null, null));
        dependency.resolve();
        dependency.updateCompatibilityRange(new VersionParser(Arrays.asList(Version.parse("1.1.5.RELEASE"), Version.parse("1.1.6.BUILD-SNAPSHOT"))));
        validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")), "org.springframework.boot", "spring-boot-starter-web", "0.1.0.RELEASE", true);
        validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.6.BUILD-SNAPSHOT")), "org.springframework.boot", "spring-boot-starter-web", "0.2.0.RELEASE", true);
        validateResolvedWebDependency(dependency.resolve(Version.parse("2.1.3.M1")), "org.springframework.boot", "spring-boot-starter-web", "0.3.0.RELEASE", // default
        true);
        dependency.updateCompatibilityRange(new VersionParser(Arrays.asList(Version.parse("1.1.6.RELEASE"), Version.parse("1.1.7.BUILD-SNAPSHOT"))));
        validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")), "org.springframework.boot", "spring-boot-starter-web", "0.1.0.RELEASE", true);
        validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.6.RELEASE")), "org.springframework.boot", "spring-boot-starter-web", "0.1.0.RELEASE", true);
        validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.7.BUILD-SNAPSHOT")), "org.springframework.boot", "spring-boot-starter-web", "0.2.0.RELEASE", true);
        validateResolvedWebDependency(dependency.resolve(Version.parse("2.1.3.M1")), "org.springframework.boot", "spring-boot-starter-web", "0.3.0.RELEASE", // default
        true);
    }

    @Test
    void resolveMatchingWithCustomGroupId() {
        Dependency dependency = Dependency.withId("foo", "com.acme", "foo", "0.3.0.RELEASE");
        dependency.getMappings().add(Dependency.Mapping.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "1.0.0.RELEASE", null, null, null));
        dependency.getMappings().add(Dependency.Mapping.create("[1.2.0.RELEASE, 1.3.0.RELEASE)", null, "bar", null, null, null, null));
        dependency.resolve();
        validateResolvedDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")), "foo", "com.acme", "foo", "1.0.0.RELEASE", true, null, null);
        validateResolvedDependency(dependency.resolve(Version.parse("1.2.5.RELEASE")), "foo", "com.acme", "bar", "0.3.0.RELEASE", true, null, null);
    }

    @Test
    void resolveMatchingWithMappingThatDisablesStarter() {
        Dependency dependency = Dependency.withId("foo", "com.acme", "foo", "0.3.0.RELEASE");
        dependency.getMappings().add(Dependency.Mapping.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "1.0.0.RELEASE", false, null, null));
        dependency.resolve();
        validateResolvedDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")), "foo", "com.acme", "foo", "1.0.0.RELEASE", false, null, null);
    }

    @Test
    void resolveMatchingWithMappingThatEnablesStarter() {
        Dependency dependency = Dependency.withId("foo", "com.acme", "foo", "0.3.0.RELEASE");
        dependency.setStarter(false);
        dependency.getMappings().add(Dependency.Mapping.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "1.0.0.RELEASE", true, null, null));
        dependency.resolve();
        validateResolvedDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")), "foo", "com.acme", "foo", "1.0.0.RELEASE", true, null, null);
    }

    @Test
    void resolveMatchingWithMappingWithCustomBom() {
        Dependency dependency = Dependency.withId("foo", "com.acme", "foo", "0.3.0.RELEASE");
        dependency.setBom("basic-bom");
        dependency.getMappings().add(Dependency.Mapping.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "1.0.0.RELEASE", null, "my-bom", null));
        dependency.resolve();
        validateResolvedDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")), "foo", "com.acme", "foo", "1.0.0.RELEASE", true, "my-bom", null);
    }

    @Test
    void resolveMatchingWithMappingWithBom() {
        Dependency dependency = Dependency.withId("foo", "com.acme", "foo", "0.3.0.RELEASE");
        dependency.getMappings().add(Dependency.Mapping.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "1.0.0.RELEASE", true, "basic-bom", null));
        dependency.resolve();
        validateResolvedDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")), "foo", "com.acme", "foo", "1.0.0.RELEASE", true, "basic-bom", null);
    }

    @Test
    void resolveMatchingWithMappingWithCustomRepository() {
        Dependency dependency = Dependency.withId("foo", "com.acme", "foo", "0.3.0.RELEASE");
        dependency.setRepository("basic-repository");
        dependency.getMappings().add(Dependency.Mapping.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "1.0.0.RELEASE", null, null, "my-repository"));
        dependency.resolve();
        validateResolvedDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")), "foo", "com.acme", "foo", "1.0.0.RELEASE", true, null, "my-repository");
    }

    @Test
    void resolveMatchingWithMappingWithRepository() {
        Dependency dependency = Dependency.withId("foo", "com.acme", "foo", "0.3.0.RELEASE");
        dependency.getMappings().add(Dependency.Mapping.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "1.0.0.RELEASE", true, null, "basic-repository"));
        dependency.resolve();
        validateResolvedDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")), "foo", "com.acme", "foo", "1.0.0.RELEASE", true, null, "basic-repository");
    }

    @Test
    void resolveVersionWithX() {
        Dependency dependency1 = Dependency.withId("foo1", "com.acme", "foo1", "0.3.0.RELEASE");
        dependency1.setCompatibilityRange("1.2.x.RELEASE");
        dependency1.resolve();
        assertThat(dependency1.getCompatibilityRange()).isEqualTo("1.2.999.RELEASE");
    }

    @Test
    void resolveCompatibilityRangeWithX() {
        Dependency dependency = Dependency.withId("foo1", "com.acme", "foo1", "0.3.0.RELEASE");
        dependency.setCompatibilityRange("[1.1.0.RELEASE, 1.2.x.RELEASE)");
        dependency.resolve();
        assertThat(dependency.getCompatibilityRange()).isEqualTo("[1.1.0.RELEASE,1.2.999.RELEASE)");
    }

    private static void validateResolvedWebDependency(Dependency dependency, String expectedGroupId, String expectedArtifactId, String expectedVersion, boolean expectedStarter) {
        validateResolvedDependency(dependency, "web", expectedGroupId, expectedArtifactId, expectedVersion, expectedStarter, null, null);
        assertThat(dependency.getKeywords()).hasSize(2);
        assertThat(dependency.getAliases()).hasSize(1);
        assertThat(dependency.getFacets()).hasSize(1);
    }

    private static void validateResolvedDependency(Dependency dependency, String id, String expectedGroupId, String expectedArtifactId, String expectedVersion, boolean expectedStarter, String expectedBom, String expectedRepository) {
        assertThat(dependency.getId()).isEqualTo(id);
        assertThat(dependency.getGroupId()).isEqualTo(expectedGroupId);
        assertThat(dependency.getArtifactId()).isEqualTo(expectedArtifactId);
        assertThat(dependency.getVersion()).isEqualTo(expectedVersion);
        assertThat(dependency.isStarter()).isEqualTo(expectedStarter);
        assertThat(dependency.getBom()).isEqualTo(expectedBom);
        assertThat(dependency.getRepository()).isEqualTo(expectedRepository);
    }
}
