package io.spring.initializr.generator.buildsystem.maven;

import io.spring.initializr.generator.buildsystem.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MavenProfileContainerTests {
    @Mock
    private BuildItemResolver buildItemResolver;

    @Test
    void addProfile() {
        MavenProfileContainer profileContainer = new MavenProfileContainer(buildItemResolver);
        profileContainer.add("profile1");
        assertThat(profileContainer.values()).hasOnlyOneElementSatisfying((profile) -> {
            assertThat(profile.getId()).isEqualTo("profile1");
            assertThat(profile.getActivation()).isNull();
            assertThat(profile.getBuild()).isNull();
            assertThat(profile.getModules()).isNull();
            assertThat(profile.getRepositories()).isNull();
            assertThat(profile.getPluginRepositories()).isNull();
            assertThat(profile.getDependencies()).isNull();
            assertThat(profile.getReporting()).isNull();
            assertThat(profile.getDependencyManagement()).isNull();
            assertThat(profile.getDistributionManagement()).isNull();
            assertThat(profile.getProperties()).isNull();
        });
    }

    @Test
    void addProfileWithConsumer() {
        MavenProfileContainer profileContainer = new MavenProfileContainer(buildItemResolver);
        profileContainer.add("profile1",
                (profile) -> profile
                        .activation(activation -> activation.activeByDefault(true))
                        .build(build -> build.defaultGoal("goal1"))
                        .module("module1")
                        .module("module2")
                        .repositories(repositories -> repositories.add("repository1", MavenRepository.withIdAndUrl("repository1", "url").build()))
                        .pluginRepositories(pluginRepositories -> pluginRepositories.add("pluginRepository1", MavenRepository.withIdAndUrl("pluginRepository1", "url2").build()))
                        .dependencies(dependencies -> dependencies.add("dependency1", Dependency.withCoordinates("com.example", "demo").build()))
                        .reporting(reporting -> reporting.outputDirectory("directory1"))
                        .dependencyManagement(dependencyManagement -> dependencyManagement.add("dependencyManagement1", BillOfMaterials.withCoordinates("com.example1", "demo1").build()))
                        .distributionManagement(distributionManagement -> distributionManagement.downloadUrl("url"))
                        .properties(properties -> properties.add("name1", "value1"))
        );

        assertThat(profileContainer.values()).hasOnlyOneElementSatisfying((profile) -> {
            assertThat(profile.getId()).isEqualTo("profile1");
            assertThat(profile.getActivation()).isNotNull();
            assertThat(profile.getActivation().getActiveByDefault()).isTrue();
            assertThat(profile.getBuild()).isNotNull();
            assertThat(profile.getBuild().getDefaultGoal()).isEqualTo("goal1");
            assertThat(profile.getModules()).isNotNull();
            assertThat(profile.getModules()).isEqualTo(Arrays.asList("module1", "module2"));
            assertThat(profile.getRepositories()).isNotNull();
            assertThat(profile.getRepositories().has("repository1")).isTrue();
            assertThat(profile.getPluginRepositories()).isNotNull();
            assertThat(profile.getPluginRepositories().has("pluginRepository1")).isTrue();
            assertThat(profile.getDependencies()).isNotNull();
            assertThat(profile.getDependencies().has("dependency1")).isTrue();
            assertThat(profile.getReporting()).isNotNull();
            assertThat(profile.getReporting().getOutputDirectory()).isEqualTo("directory1");
            assertThat(profile.getDependencyManagement()).isNotNull();
            assertThat(profile.getDependencyManagement().has("dependencyManagement1")).isTrue();
            assertThat(profile.getProperties()).isNotNull();
            assertThat(profile.getProperties().getSettings()).hasOnlyOneElementSatisfying(settings -> {
                assertThat(settings.getName()).isEqualTo("name1");
                assertThat(settings.getValue()).isEqualTo("value1");
            });
        });
    }

    @Test
    void addProfileSeveralTimeReuseConfiguration() {
        MavenProfileContainer profileContainer = new MavenProfileContainer(buildItemResolver);
        profileContainer.add("profile1", (profile) -> profile.activation(activation -> activation.activeByDefault(true)));
        profileContainer.add("profile1", (profile) -> profile.activation(activation -> activation.activeByDefault(false)));
        assertThat(profileContainer.values()).hasOnlyOneElementSatisfying((profile) -> {
            assertThat(profile.getId()).isEqualTo("profile1");
            assertThat(profile.getActivation()).isNotNull();
            assertThat(profile.getActivation().getActiveByDefault()).isFalse();
            assertThat(profile.getBuild()).isNull();
            assertThat(profile.getModules()).isNull();
            assertThat(profile.getRepositories()).isNull();
            assertThat(profile.getPluginRepositories()).isNull();
            assertThat(profile.getDependencies()).isNull();
            assertThat(profile.getReporting()).isNull();
            assertThat(profile.getDependencyManagement()).isNull();
            assertThat(profile.getDistributionManagement()).isNull();
            assertThat(profile.getProperties()).isNull();
        });
    }

    @Test
    void isEmptyWithEmptyContainer() {
        MavenProfileContainer profileContainer = new MavenProfileContainer(buildItemResolver);
        assertThat(profileContainer.isEmpty()).isTrue();
    }

    @Test
    void isEmptyWithRegisteredProfile() {
        MavenProfileContainer profileContainer = new MavenProfileContainer(buildItemResolver);
        profileContainer.add("profile1");
        assertThat(profileContainer.isEmpty()).isFalse();
    }

    @Test
    void hasProfileWithMatchingProfile() {
        MavenProfileContainer profileContainer = new MavenProfileContainer(buildItemResolver);
        profileContainer.add("profile1");
        assertThat(profileContainer.has("profile1")).isTrue();
    }

    @Test
    void hasProfileWithNonMatchingProfile() {
        MavenProfileContainer profileContainer = new MavenProfileContainer(buildItemResolver);
        profileContainer.add("profile1");
        assertThat(profileContainer.has("profile2")).isFalse();
    }

    @Test
    void removeWithMatchingProfile() {
        MavenProfileContainer profileContainer = new MavenProfileContainer(buildItemResolver);
        profileContainer.add("profile1");
        assertThat(profileContainer.remove("profile1")).isTrue();
        assertThat(profileContainer.isEmpty()).isTrue();
    }

    @Test
    void removeWithNonMatchingProfile() {
        MavenProfileContainer profileContainer = new MavenProfileContainer(buildItemResolver);
        profileContainer.add("profile1");
        assertThat(profileContainer.remove("profile2")).isFalse();
        assertThat(profileContainer.isEmpty()).isFalse();
    }

}