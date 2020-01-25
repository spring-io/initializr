package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MavenProfileContainerTests {

    @Test
    void addProfile() {
        MavenProfileContainer profileContainer = new MavenProfileContainer();
        profileContainer.add("profile1");
        assertThat(profileContainer.values()).hasOnlyOneElementSatisfying((plugin) -> {
            assertThat(plugin.getId()).isEqualTo("profile1");;
        });
    }

    @Test
    @Disabled
    void addPluginWithConsumer() {
        MavenPluginContainer pluginContainer = new MavenPluginContainer();
        pluginContainer.add("com.example", "test-plugin",
                (plugin) -> plugin.version("1.0").execution("first", (first) -> first.goal("run-this")));
        assertThat(pluginContainer.values()).hasOnlyOneElementSatisfying((plugin) -> {
            assertThat(plugin.getGroupId()).isEqualTo("com.example");
            assertThat(plugin.getArtifactId()).isEqualTo("test-plugin");
            assertThat(plugin.getVersion()).isEqualTo("1.0");
            assertThat(plugin.getExecutions()).hasSize(1);
            assertThat(plugin.getExecutions().get(0).getId()).isEqualTo("first");
            assertThat(plugin.getExecutions().get(0).getGoals()).containsExactly("run-this");
        });
    }

    @Test
    void addProfileSeveralTimeReuseConfiguration() {
        MavenProfileContainer profileContainer = new MavenProfileContainer();
        profileContainer.add("profile1");
        profileContainer.add("profile1");
        assertThat(profileContainer.values()).hasOnlyOneElementSatisfying((plugin) -> {
            assertThat(plugin.getId()).isEqualTo("profile1");
        });
    }

    @Test
    void isEmptyWithEmptyContainer() {
        MavenProfileContainer profileContainer = new MavenProfileContainer();
        assertThat(profileContainer.isEmpty()).isTrue();
    }

    @Test
    void isEmptyWithRegisteredProfile() {
        MavenProfileContainer profileContainer = new MavenProfileContainer();
        profileContainer.add("profile1");
        assertThat(profileContainer.isEmpty()).isFalse();
    }

    @Test
    void hasPluginWithMatchingPlugin() {
        MavenProfileContainer profileContainer = new MavenProfileContainer();
        profileContainer.add("profile1");
        assertThat(profileContainer.has("profile1")).isTrue();
    }

    @Test
    void hasPluginWithNonMatchingPlugin() {
        MavenProfileContainer profileContainer = new MavenProfileContainer();
        profileContainer.add("profile1");
        assertThat(profileContainer.has("profile2")).isFalse();
    }

    @Test
    void removeWithMatchingPlugin() {
        MavenProfileContainer profileContainer = new MavenProfileContainer();
        profileContainer.add("profile1");
        assertThat(profileContainer.remove("profile1")).isTrue();
        assertThat(profileContainer.isEmpty()).isTrue();
    }

    @Test
    void removeWithNonMatchingPlugin() {
        MavenProfileContainer profileContainer = new MavenProfileContainer();
        profileContainer.add("profile1");
        assertThat(profileContainer.remove("profile2")).isFalse();
        assertThat(profileContainer.isEmpty()).isFalse();
    }

}