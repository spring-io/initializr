/*
 * Copyright 2012-2017 the original author or authors.
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

package io.spring.initializr.web.project;

import io.spring.initializr.test.generator.PomAssert;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.Test;

import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles({ "test-default", "test-custom-defaults" })
public class MainControllerDefaultsIntegrationTests
		extends AbstractInitializrControllerIntegrationTests {

	// see defaults customization

	@Test
	public void generateDefaultPom() {
		String content = getRestTemplate().getForObject(createUrl("/pom.xml?style=web"),
				String.class);
		PomAssert pomAssert = new PomAssert(content);
		pomAssert.hasGroupId("org.foo").hasArtifactId("foo-bar")
				.hasVersion("1.2.4-SNAPSHOT").hasPackaging("jar").hasName("FooBar")
				.hasDescription("FooBar Project");
	}

	@Test
	public void defaultsAppliedToHome() {
		String body = htmlHome();
		assertThat(body.contains("org.foo")).as("custom groupId not found").isTrue();
		assertThat(body.contains("foo-bar")).as("custom artifactId not found").isTrue();
		assertThat(body.contains("FooBar")).as("custom name not found").isTrue();
		assertThat(body.contains("FooBar Project")).as("custom description not found")
				.isTrue();
		assertThat(body.contains("org.foo.demo")).as("custom package not found").isTrue();
	}

}
