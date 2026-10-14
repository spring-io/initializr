/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;

/**
 * Project descriptions used by the application properties tests.
 *
 * @author Moritz Halbritter
 */
final class TestProjectDescriptions {

	private TestProjectDescriptions() {
	}

	/**
	 * Returns a description of a Maven and Java project.
	 * @return the project description
	 */
	static ProjectDescription mavenJava() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBuildSystem(new MavenBuildSystem());
		description.setLanguage(new JavaLanguage());
		return description;
	}

}
