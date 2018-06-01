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
 * Tests for {@link SpringSessionRequestPostProcessor}.
 *
 * @author Stephane Nicoll
 */
public class SpringSessionRequestPostProcessorTests
		extends AbstractRequestPostProcessorTests {

	@Test
	public void sessionWithSpringBoot15() {
		ProjectRequest request = createProjectRequest("session");
		request.setBootVersion("1.5.4.RELEASE");
		generateMavenPom(request)
				.hasDependency("org.springframework.session", "spring-session")
				.hasSpringBootStarterRootDependency().hasSpringBootStarterTest()
				.hasDependenciesCount(3);
	}

	@Test
	public void sessionWithRedisAndSpringBoot15() {
		ProjectRequest request = createProjectRequest("session", "data-redis");
		request.setBootVersion("1.5.4.RELEASE");
		generateMavenPom(request)
				.hasDependency("org.springframework.session", "spring-session")
				.hasSpringBootStarterDependency("data-redis").hasSpringBootStarterTest()
				.hasDependenciesCount(3);
	}

	@Test
	public void sessionWithJdbcAndSpringBoot15() {
		ProjectRequest request = createProjectRequest("session", "jdbc");
		request.setBootVersion("1.5.4.RELEASE");
		generateMavenPom(request)
				.hasDependency("org.springframework.session", "spring-session")
				.hasSpringBootStarterDependency("jdbc").hasSpringBootStarterTest()
				.hasDependenciesCount(3);
	}

	@Test
	public void sessionWithSpringBoot20M2() {
		ProjectRequest request = createProjectRequest("session");
		request.setBootVersion("2.0.0.M2");
		generateMavenPom(request)
				.hasDependency("org.springframework.session", "spring-session")
				.hasSpringBootStarterRootDependency().hasSpringBootStarterTest()
				.hasDependenciesCount(3);
	}

	@Test
	public void noSessionWithRedis() {
		ProjectRequest request = createProjectRequest("data-redis");
		request.setBootVersion("2.0.0.M3");
		generateMavenPom(request).hasSpringBootStarterDependency("data-redis")
				.hasSpringBootStarterTest().hasDependenciesCount(2);
	}

	@Test
	public void sessionWithNoStore() {
		ProjectRequest request = createProjectRequest("session", "data-jpa");
		request.setBootVersion("2.0.0.M3");
		generateMavenPom(request)
				.hasDependency("org.springframework.session", "spring-session-core")
				.hasSpringBootStarterDependency("data-jpa").hasSpringBootStarterTest()
				.hasDependenciesCount(3);
	}

	@Test
	public void sessionWithRedis() {
		ProjectRequest request = createProjectRequest("session", "data-redis");
		request.setBootVersion("2.0.0.M3");
		generateMavenPom(request).hasSpringBootStarterDependency("data-redis")
				.hasSpringBootStarterTest()
				.hasDependency(SpringSessionRequestPostProcessor.REDIS)
				.hasDependenciesCount(3);
	}

	@Test
	public void sessionWithRedisReactive() {
		ProjectRequest request = createProjectRequest("session", "data-redis-reactive");
		request.setBootVersion("2.0.0.M7");
		generateMavenPom(request).hasSpringBootStarterDependency("data-redis-reactive")
				.hasSpringBootStarterTest()
				.hasDependency(SpringSessionRequestPostProcessor.REDIS)
				.hasDependenciesCount(3);
	}

	@Test
	public void sessionWithJdbc() {
		ProjectRequest request = createProjectRequest("session", "jdbc");
		request.setBootVersion("2.0.0.M3");
		generateMavenPom(request).hasSpringBootStarterDependency("jdbc")
				.hasSpringBootStarterTest()
				.hasDependency(SpringSessionRequestPostProcessor.JDBC)
				.hasDependenciesCount(3);
	}

	@Test
	public void sessionWithRedisAndJdbc() {
		ProjectRequest request = createProjectRequest("session", "data-redis", "jdbc");
		request.setBootVersion("2.0.0.M3");
		generateMavenPom(request).hasSpringBootStarterDependency("data-redis")
				.hasSpringBootStarterDependency("jdbc").hasSpringBootStarterTest()
				.hasDependency(SpringSessionRequestPostProcessor.REDIS)
				.hasDependency(SpringSessionRequestPostProcessor.JDBC)
				.hasDependenciesCount(5);
	}

}
