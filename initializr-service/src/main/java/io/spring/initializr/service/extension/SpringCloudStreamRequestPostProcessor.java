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

package io.spring.initializr.service.extension;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.stereotype.Component;

/**
 * Determine the appropriate Spring Cloud stream dependency to use based on the selected
 * integration technology.
 * <p>
 * Does not replace the integration technology jar by the relevant binder. If more than
 * one tech is selected, it is far more easier to remove the unnecessary binder jar than
 * to figure out the name of the tech jar to add to keep support for that technology.
 *
 * @author Stephane Nicoll
 */
@Component
class SpringCloudStreamRequestPostProcessor extends AbstractProjectRequestPostProcessor {

	static final Dependency KAFKA_BINDER = Dependency.withId("cloud-stream-binder-kafka",
			"org.springframework.cloud", "spring-cloud-stream-binder-kafka");

	static final Dependency KAFKA_STREAMS_BINDER = Dependency.withId(
			"cloud-stream-binder-kafka-streams", "org.springframework.cloud",
			"spring-cloud-stream-binder-kafka-streams");

	static final Dependency RABBIT_BINDER = Dependency.withId(
			"cloud-stream-binder-rabbit", "org.springframework.cloud",
			"spring-cloud-stream-binder-rabbit");

	static final Dependency SCS_TEST = Dependency.withId("cloud-stream-test",
			"org.springframework.cloud", "spring-cloud-stream-test-support", null,
			Dependency.SCOPE_TEST);

	@Override
	public void postProcessAfterResolution(ProjectRequest request,
			InitializrMetadata metadata) {
		boolean hasSpringCloudStream = hasDependency(request, "cloud-stream");
		boolean hasReactiveSpringCloudStream = hasDependency(request,
				"reactive-cloud-stream");
		boolean hasSpringCloudBus = hasDependency(request, "cloud-bus");
		boolean hasSpringCloudTurbineStream = hasDependency(request,
				"cloud-turbine-stream");
		if (hasSpringCloudStream || hasReactiveSpringCloudStream || hasSpringCloudBus
				|| hasSpringCloudTurbineStream) {
			if (hasDependencies(request, "amqp")) {
				request.getResolvedDependencies().add(RABBIT_BINDER);
			}
			if (hasDependencies(request, "kafka")) {
				request.getResolvedDependencies().add(KAFKA_BINDER);
			}
		}
		// Spring Cloud Stream specific
		if (hasSpringCloudStream || hasReactiveSpringCloudStream) {
			if (hasDependencies(request, "kafka-streams")) {
				request.getResolvedDependencies().add(KAFKA_STREAMS_BINDER);
			}
			request.getResolvedDependencies().add(SCS_TEST);
		}
	}

}
