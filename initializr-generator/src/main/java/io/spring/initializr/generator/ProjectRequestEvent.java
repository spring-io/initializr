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
 * Event published when a {@link ProjectRequest} has been processed.
 *
 * @author Stephane Nicoll
 * @see ProjectGeneratedEvent
 * @see ProjectFailedEvent
 */
public abstract class ProjectRequestEvent {

	private final ProjectRequest projectRequest;

	private final long timestamp;

	protected ProjectRequestEvent(ProjectRequest projectRequest) {
		this.projectRequest = projectRequest;
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * Return the {@link ProjectRequest} used to generate the project.
	 * @return the project request
	 */
	public ProjectRequest getProjectRequest() {
		return this.projectRequest;
	}

	/**
	 * Return the timestamp at which the request was processed.
	 * @return the timestamp that the request was processed
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

}
