/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.web.controller.custom;

import io.spring.initializr.web.project.ProjectRequest;
import io.spring.initializr.web.project.WebProjectRequest;

/**
 * A custom {@link ProjectRequest} with an additional custom boolean flag. This type has
 * to be public for the {@code customFlag} request attribute to be mapped properly.
 *
 * @author Stephane Nicoll
 */
public class CustomProjectRequest extends WebProjectRequest {

	private boolean customFlag;

	public boolean isCustomFlag() {
		return this.customFlag;
	}

	public void setCustomFlag(boolean customFlag) {
		this.customFlag = customFlag;
	}

}
