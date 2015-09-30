/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.metadata

import groovy.transform.AutoClone
import groovy.transform.AutoCloneStyle

/**
 * An {@link ServiceCapabilityType#ACTION action} capability.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@AutoClone(style = AutoCloneStyle.COPY_CONSTRUCTOR)
class TypeCapability extends ServiceCapability<List<Type>> {

	final List<Type> content = []

	TypeCapability() {
		super('type', ServiceCapabilityType.ACTION, 'Type', 'project type')
	}

	/**
	 * Return the {@link Type} with the specified id or {@code null} if no
	 * such type exists.
	 */
	Type get(String id) {
		return content.find { id.equals(it.id) || id.equals(it.stsId) }
	}

	/**
	 * Return the default {@link Type}.
	 */
	Type getDefault() {
		return content.find { it.default }
	}

	@Override
	void merge(List<Type> otherContent) {
		otherContent.each {
			if (!get(it.id)) {
				content << it
			}
		}
	}

}
