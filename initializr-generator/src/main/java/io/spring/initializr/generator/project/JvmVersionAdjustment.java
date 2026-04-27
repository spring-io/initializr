/*
 * Copyright 2012 - present the original author or authors.
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

package io.spring.initializr.generator.project;

import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;

/**
 * A single JVM level change applied while customizing a {@link ProjectDescription}, with
 * enough context to explain the change to the user.
 *
 * @param fromVersion JVM level before the change (as in
 * {@link io.spring.initializr.generator.language.Language#jvmVersion()})
 * @param toVersion JVM level after the change
 * @param reason the cause of the adjustment
 * @param dependencyId optional dependency id when
 * {@link JvmVersionAdjustmentReason#SELECTED_DEPENDENCY}
 * @param detail optional extra text (e.g. Kotlin version, or a human-readable dependency
 * name)
 * @author Moritz Halbritter
 */
public record JvmVersionAdjustment(String fromVersion, String toVersion, JvmVersionAdjustmentReason reason,
		@Nullable String dependencyId, @Nullable String detail) {

	public JvmVersionAdjustment {
		Assert.hasText(fromVersion, "'fromVersion' must not be empty");
		Assert.hasText(toVersion, "'toVersion' must not be empty");
		Assert.notNull(reason, "'reason' must not be null");
	}

	/**
	 * Create an adjustment with no optional fields.
	 */
	public JvmVersionAdjustment(String fromVersion, String toVersion, JvmVersionAdjustmentReason reason) {
		this(fromVersion, toVersion, reason, null, null);
	}

}
