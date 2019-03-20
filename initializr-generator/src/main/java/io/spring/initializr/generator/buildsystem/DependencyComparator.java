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

package io.spring.initializr.generator.buildsystem;

import java.util.Comparator;

/**
 * A {@link Comparator} that orders {@link Dependency dependencies} in a suitable form for
 * being referenced in the build.
 *
 * @author Stephane Nicoll
 */
public class DependencyComparator implements Comparator<Dependency> {

	/**
	 * A default stateless instance.
	 */
	public static final DependencyComparator INSTANCE = new DependencyComparator();

	@Override
	public int compare(Dependency o1, Dependency o2) {
		if (isSpringBootDependency(o1) && isSpringBootDependency(o2)) {
			return o1.getArtifactId().compareTo(o2.getArtifactId());
		}
		if (isSpringBootDependency(o1)) {
			return -1;
		}
		if (isSpringBootDependency(o2)) {
			return 1;
		}
		int group = o1.getGroupId().compareTo(o2.getGroupId());
		if (group != 0) {
			return group;
		}
		return o1.getArtifactId().compareTo(o2.getArtifactId());
	}

	private boolean isSpringBootDependency(Dependency dependency) {
		return dependency.getGroupId().startsWith("org.springframework.boot");
	}

}
