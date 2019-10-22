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

package io.spring.initializr.generator.project.diff;

import io.spring.initializr.generator.project.ProjectDescription;

/**
 * A factory for {@link ProjectDescriptionDiff} objects.
 *
 * @param <T> the type of {@link ProjectDescription}
 * @author Chris Bono
 */
public interface ProjectDescriptionDiffFactory<T extends ProjectDescription> {

	/**
	 * Construct a diff for the specified {@link ProjectDescription}.
	 * <p>
	 * @param description the project description to use as the source of the diff
	 * @return a diff instance using the current state of the specified description as its
	 * source
	 */
	ProjectDescriptionDiff create(T description);

}
