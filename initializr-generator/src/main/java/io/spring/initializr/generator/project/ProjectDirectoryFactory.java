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
package io.spring.initializr.generator.project;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A factory of project directory.
 *
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface ProjectDirectoryFactory {

    /**
     * Create a dedicated project directory for the specified {@link ProjectDescription}.
     * @param description the description of a project to generate
     * @return a dedicated existing directory
     * @throws IOException if creating the directory failed
     */
    Path createProjectDirectory(ProjectDescription description) throws IOException;
}
