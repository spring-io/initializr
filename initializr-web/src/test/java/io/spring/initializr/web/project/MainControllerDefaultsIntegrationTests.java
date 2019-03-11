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

package io.spring.initializr.web.project;

import io.spring.initializr.generator.spring.test.build.PomAssert;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles({ "test-default", "test-custom-defaults" })
class MainControllerDefaultsIntegrationTests
		extends AbstractInitializrControllerIntegrationTests {

	// see defaults customization

	@Test
	void generateDefaultPom() {
		String content = getRestTemplate().getForObject(createUrl("/pom.xml?style=web"),
				String.class);
		PomAssert pomAssert = new PomAssert(content);
		pomAssert.hasGroupId("org.foo").hasArtifactId("foo-bar")
				.hasVersion("1.2.4-SNAPSHOT").doesNotHaveNode("/project/packaging")
				.hasName("FooBar").hasDescription("FooBar Project");
	}

}
