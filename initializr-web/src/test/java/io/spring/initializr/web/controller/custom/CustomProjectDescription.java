/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.web.controller.custom;

import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;

/**
 * A custom {@link ProjectDescription} to convey the additional flags to contributors.
 *
 * @author Stephane Nicoll
 */
class CustomProjectDescription extends MutableProjectDescription {

	private boolean customFlag;

	CustomProjectDescription() {
	}

	CustomProjectDescription(CustomProjectDescription source) {
		super(source);
		this.customFlag = source.isCustomFlag();
	}

	@Override
	public CustomProjectDescription createCopy() {
		return new CustomProjectDescription(this);
	}

	boolean isCustomFlag() {
		return this.customFlag;
	}

	void setCustomFlag(boolean customFlag) {
		this.customFlag = customFlag;
	}

}
