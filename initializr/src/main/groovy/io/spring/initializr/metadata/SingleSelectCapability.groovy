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

/**
 * A {@link ServiceCapabilityType#SINGLE_SELECT single select} capability.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class SingleSelectCapability extends ServiceCapability<List<DefaultMetadataElement>> {

	final List<DefaultMetadataElement> content = []

	SingleSelectCapability(String id) {
		super(id, ServiceCapabilityType.SINGLE_SELECT)
	}

	/**
	 * Return the default element of this capability.
	 */
	DefaultMetadataElement getDefault() {
	   return content.find { it.default }
	}

}
