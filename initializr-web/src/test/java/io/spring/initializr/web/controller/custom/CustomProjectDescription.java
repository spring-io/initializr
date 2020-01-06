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
