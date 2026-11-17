/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
