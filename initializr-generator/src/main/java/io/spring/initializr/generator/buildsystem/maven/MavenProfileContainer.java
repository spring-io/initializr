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

package io.spring.initializr.generator.buildsystem.maven;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MavenProfileContainer {

    private final Map<String, MavenProfile.Builder> profiles = new LinkedHashMap<>();

    /**
     * Specify if this container is empty.
     * @return {@code true} if no {@link MavenProfile} is added
     */
    public boolean isEmpty() {
        return this.profiles.isEmpty();
    }

    /**
     * Returns a {@link Stream} of registered {@link MavenProfile}s.
     * @return a stream of {@link MavenProfile}s
     */
    public Stream<MavenProfile> values() {
        return this.profiles.values().stream().map(MavenProfile.Builder::build);
    }

    /**
     * Add a {@link MavenProfile} with the specified {@code id} and
     * {@code activateByDefault}.
     * @param id the id of the profile
     */
    public void add(String id) {
        createProfileBuilder(id);
    }

    /**
     * Specify if this container has a plugin with the specified {@code groupId} and
     * {@code artifactId}.
     * @param id the groupId of the plugin
     * @return {@code true} if an item with the specified {@code groupId} and
     * {@code artifactId} exists
     */
    public boolean has(String id) {
        return this.profiles.containsKey(id);
    }

    /**
     * Add a {@link MavenProfile} with the specified {@code id} and
     * {@code activateByDefault} and {@link MavenProfile.Builder} to customize the
     * profile. If the profile has already been added, the profileBuilder can be used to
     * further tune the existing profile configuration.
     * @param id the id of the profile
     * @param profileBuilder a {@link MavenProfile.Builder} to customize the
     * {@link MavenProfile}
     */
    public void add(String id, Consumer<MavenProfile.Builder> profileBuilder) {
        profileBuilder.accept(createProfileBuilder(id));
    }

    /**
     * Remove the plugin with the specified {@code groupId} and {@code artifactId}.
     * @param id the groupId of the plugin to remove
     * @return {@code true} if such a plugin was registered, {@code false} otherwise
     */
    public boolean remove(String id) {
        return this.profiles.remove(id) != null;
    }

    private MavenProfile.Builder createProfileBuilder(String id) {
        MavenProfile.Builder profileBuilder = this.profiles.get(id);
        if (profileBuilder == null) {
            MavenProfile.Builder builder = new MavenProfile.Builder(id);
            this.profiles.put(id, builder);
            return builder;
        }
        else {
            return profileBuilder;
        }
    }

}
