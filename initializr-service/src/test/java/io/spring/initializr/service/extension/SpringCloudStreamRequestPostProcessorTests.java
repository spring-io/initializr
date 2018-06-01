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
import org.junit.Test;

/**
 * Tests for {@link SpringCloudStreamRequestPostProcessor}.
 *
 * @author Stephane Nicoll
 */
public class SpringCloudStreamRequestPostProcessorTests
		extends AbstractRequestPostProcessorTests {

	@Test
	public void springCloudStreamWithRabbit() {
		ProjectRequest request = createProjectRequest("cloud-stream", "amqp");
		generateMavenPom(request).hasDependency(getDependency("cloud-stream"))
				.hasDependency(getDependency("amqp"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.RABBIT_BINDER)
				.hasSpringBootStarterTest()
				.hasDependency(SpringCloudStreamRequestPostProcessor.SCS_TEST)
				.hasDependenciesCount(5);
	}

	@Test
	public void springCloudStreamWithKafka() {
		ProjectRequest request = createProjectRequest("cloud-stream", "kafka");
		generateMavenPom(request).hasDependency(getDependency("cloud-stream"))
				.hasDependency(getDependency("kafka"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.KAFKA_BINDER)
				.hasSpringBootStarterTest()
				.hasDependency(SpringCloudStreamRequestPostProcessor.SCS_TEST)
				.hasDependenciesCount(5);
	}

	@Test
	public void springCloudStreamWithKafkaStreams() {
		ProjectRequest request = createProjectRequest("cloud-stream", "kafka-streams");
		request.setBootVersion("2.0.0.RELEASE");
		generateMavenPom(request).hasDependency(getDependency("cloud-stream"))
				.hasDependency(getDependency("kafka-streams"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.KAFKA_STREAMS_BINDER)
				.hasSpringBootStarterTest()
				.hasDependency(SpringCloudStreamRequestPostProcessor.SCS_TEST)
				.hasDependenciesCount(5);
	}

	@Test
	public void springCloudStreamWithAllBinders() {
		ProjectRequest request = createProjectRequest("cloud-stream", "amqp", "kafka",
				"kafka-streams");
		generateMavenPom(request).hasDependency(getDependency("cloud-stream"))
				.hasDependency(getDependency("amqp"))
				.hasDependency(getDependency("kafka"))
				.hasDependency(getDependency("kafka-streams"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.RABBIT_BINDER)
				.hasDependency(SpringCloudStreamRequestPostProcessor.KAFKA_BINDER)
				.hasDependency(SpringCloudStreamRequestPostProcessor.KAFKA_STREAMS_BINDER)
				.hasSpringBootStarterTest()
				.hasDependency(SpringCloudStreamRequestPostProcessor.SCS_TEST)
				.hasDependenciesCount(9);
	}

	@Test
	public void reactiveSpringCloudStreamWithRabbit() {
		ProjectRequest request = createProjectRequest("reactive-cloud-stream", "amqp");
		request.setBootVersion("2.0.0.RELEASE");
		generateMavenPom(request).hasDependency(getDependency("reactive-cloud-stream"))
				.hasDependency(getDependency("amqp"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.RABBIT_BINDER)
				.hasSpringBootStarterTest()
				.hasDependency(SpringCloudStreamRequestPostProcessor.SCS_TEST)
				.hasDependenciesCount(5);
	}

	@Test
	public void reactiveSpringCloudStreamWithKafka() {
		ProjectRequest request = createProjectRequest("reactive-cloud-stream", "kafka");
		request.setBootVersion("2.0.0.RELEASE");
		generateMavenPom(request).hasDependency(getDependency("reactive-cloud-stream"))
				.hasDependency(getDependency("kafka"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.KAFKA_BINDER)
				.hasSpringBootStarterTest()
				.hasDependency(SpringCloudStreamRequestPostProcessor.SCS_TEST)
				.hasDependenciesCount(5);
	}

	@Test
	public void reactiveSpringCloudStreamWithKafkaStreams() {
		ProjectRequest request = createProjectRequest("reactive-cloud-stream",
				"kafka-streams");
		request.setBootVersion("2.0.0.RELEASE");
		generateMavenPom(request).hasDependency(getDependency("reactive-cloud-stream"))
				.hasDependency(getDependency("kafka-streams"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.KAFKA_STREAMS_BINDER)
				.hasSpringBootStarterTest()
				.hasDependency(SpringCloudStreamRequestPostProcessor.SCS_TEST)
				.hasDependenciesCount(5);
	}

	@Test
	public void reactiveSpringCloudStreamWithAllBinders() {
		ProjectRequest request = createProjectRequest("reactive-cloud-stream", "amqp",
				"kafka", "kafka-streams");
		request.setBootVersion("2.0.0.RELEASE");
		generateMavenPom(request).hasDependency(getDependency("reactive-cloud-stream"))
				.hasDependency(getDependency("amqp"))
				.hasDependency(getDependency("kafka"))
				.hasDependency(getDependency("kafka-streams"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.RABBIT_BINDER)
				.hasDependency(SpringCloudStreamRequestPostProcessor.KAFKA_BINDER)
				.hasDependency(SpringCloudStreamRequestPostProcessor.KAFKA_STREAMS_BINDER)
				.hasSpringBootStarterTest()
				.hasDependency(SpringCloudStreamRequestPostProcessor.SCS_TEST)
				.hasDependenciesCount(9);
	}

	@Test
	public void springCloudBusWithRabbit() {
		ProjectRequest request = createProjectRequest("cloud-bus", "amqp");
		generateMavenPom(request).hasDependency(getDependency("cloud-bus"))
				.hasDependency(getDependency("amqp"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.RABBIT_BINDER)
				.hasSpringBootStarterTest().hasDependenciesCount(4);
	}

	@Test
	public void springCloudBusWithKafka() {
		ProjectRequest request = createProjectRequest("cloud-bus", "amqp");
		generateMavenPom(request).hasDependency(getDependency("cloud-bus"))
				.hasDependency(getDependency("amqp"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.RABBIT_BINDER)
				.hasSpringBootStarterTest().hasDependenciesCount(4);
	}

	@Test
	public void springCloudBusWithAllBinders() {
		ProjectRequest request = createProjectRequest("cloud-bus", "amqp", "kafka",
				"kafka-streams");
		generateMavenPom(request).hasDependency(getDependency("cloud-bus"))
				.hasDependency(getDependency("amqp"))
				.hasDependency(getDependency("kafka"))
				.hasDependency(getDependency("kafka-streams"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.RABBIT_BINDER)
				.hasDependency(SpringCloudStreamRequestPostProcessor.KAFKA_BINDER)
				.hasSpringBootStarterTest().hasDependenciesCount(7);
	}

	@Test
	public void springCloudTurbineStreamWithRabbit() {
		ProjectRequest request = createProjectRequest("cloud-turbine-stream", "amqp");
		request.setBootVersion("2.0.0.RELEASE");
		generateMavenPom(request).hasDependency(getDependency("cloud-turbine-stream"))
				.hasDependency(getDependency("amqp"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.RABBIT_BINDER)
				.hasSpringBootStarterTest().hasDependenciesCount(4);
	}

	@Test
	public void springCloudTurbineStreamWithKafka() {
		ProjectRequest request = createProjectRequest("cloud-turbine-stream", "kafka");
		request.setBootVersion("2.0.0.RELEASE");
		generateMavenPom(request).hasDependency(getDependency("cloud-turbine-stream"))
				.hasDependency(getDependency("kafka"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.KAFKA_BINDER)
				.hasSpringBootStarterTest().hasDependenciesCount(4);
	}

	@Test
	public void springCloudTurbineStreamWithAllBinders() {
		ProjectRequest request = createProjectRequest("cloud-turbine-stream", "amqp",
				"kafka", "kafka-streams");
		request.setBootVersion("2.0.0.RELEASE");
		generateMavenPom(request).hasDependency(getDependency("cloud-turbine-stream"))
				.hasDependency(getDependency("amqp"))
				.hasDependency(getDependency("kafka"))
				.hasDependency(getDependency("kafka-streams"))
				.hasDependency(SpringCloudStreamRequestPostProcessor.RABBIT_BINDER)
				.hasDependency(SpringCloudStreamRequestPostProcessor.KAFKA_BINDER)
				.hasSpringBootStarterTest().hasDependenciesCount(7);
	}

}
