/*
 * Copyright 2012-2018 the original author or authors.
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

package io.spring.initializr.generator;

/**
 * Event published when an error occurred trying to generate a project.
 *
 * @author Stephane Nicoll
 */
public class ProjectFailedEvent extends ProjectRequestEvent {

	private final Exception cause;

	public ProjectFailedEvent(ProjectRequest projectRequest, Exception cause) {
		super(projectRequest);
		this.cause = cause;
	}

	/**
	 * Return the cause of the failure.
	 * @return the cause of the failure
	 */
	public Exception getCause() {
		return this.cause;
	}

}
