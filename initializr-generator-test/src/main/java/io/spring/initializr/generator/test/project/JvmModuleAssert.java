/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.test.project;

import java.nio.file.Path;

import io.spring.initializr.generator.language.Language;

/**
 * Assertions for a module with code for a particular {@link Language}.
 *
 * @author Stephane Nicoll
 */
public class JvmModuleAssert extends AbstractJvmModuleAssert<JvmModuleAssert> {

	public JvmModuleAssert(Path projectDirectory, Language language) {
		super(projectDirectory, language, JvmModuleAssert.class);
	}

}
