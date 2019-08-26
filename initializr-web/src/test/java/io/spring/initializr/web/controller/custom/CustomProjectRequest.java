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
