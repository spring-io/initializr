/*
 * Copyright 2012 - present the original author or authors.
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
