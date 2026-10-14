/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.test.project;

import java.nio.file.Path;

/**
 * Assertions for a module.
 *
 * @author Stephane Nicoll
 */
public class ModuleAssert extends AbstractModuleAssert<ModuleAssert> {

	public ModuleAssert(Path projectDirectory) {
		super(projectDirectory, ModuleAssert.class);
	}

}
