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

import java.util.function.BiConsumer;

import io.spring.initializr.generator.project.diff.ProjectDescriptionDiff;

/**
 * Extends the base {@link ProjectDescriptionDiff} to provide convenient diff methods on
 * {@link CustomProjectDescription}.
 *
 * @author cbono
 */
class CustomProjectDescriptionDiff extends ProjectDescriptionDiff {

	CustomProjectDescriptionDiff(final CustomProjectDescription original) {
		super(original);
	}

	/**
	 * Optionally calls the specified consumer if the {@code customFlag} is different on
	 * the original source description and the specified current description.
	 * @param current the description to test against
	 * @param consumer to call if the property has changed
	 */
	void ifCUstomFlagChanged(final CustomProjectDescription current, final BiConsumer<Boolean, Boolean> consumer) {
		final CustomProjectDescription original = (CustomProjectDescription) super.getOriginal();
		if (original.isCustomFlag() != current.isCustomFlag()) {
			consumer.accept(original.isCustomFlag(), current.isCustomFlag());
		}
	}

}
