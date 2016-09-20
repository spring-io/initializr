/*
 * Copyright 2012-2016 the original author or authors.
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

package io.spring.initializr.generator

import io.spring.initializr.util.Agent
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

/**
 * Project generator tests for {@code .gitignore}.
 *
 * @author Stephane Nicoll
 */
@RunWith(Parameterized.class)
class ProjectGeneratorGitIgnoreTests extends AbstractProjectGeneratorTests {

	@Parameterized.Parameters(name = "{0}")
	static Object[] parameters() {
		def list = []
		list << 'STS 3.7.2'
		list << 'IntelliJ IDEA'
		list << 'nb-springboot-plugin/0.1'
		list << 'HTTPie/0.8.0'
		list << 'Googlebot-Mobile'
		list
	}

	private final String userAgent
	private final Agent agent

	ProjectGeneratorGitIgnoreTests(String userAgent) {
		this.userAgent = userAgent
		this.agent = Agent.fromUserAgent(userAgent)
	}

	@Test
	void gitIgnoreMaven() {
		def request = createProjectRequest()
		request.type = 'maven-project'
		def project = generateProject(request)
		project.sourceCodeAssert(".gitignore")
				.equalsTo(getResourceFor('maven'))
	}

	@Test
	void gitIgnoreGradle() {
		def request = createProjectRequest()
		request.type = 'gradle-project'
		def project = generateProject(request)
		project.sourceCodeAssert(".gitignore")
				.equalsTo(getResourceFor('gradle'))
	}

	private Resource getResourceFor(String build) {
		String id = agent ? agent.id.id : 'none'
		return new ClassPathResource("project/$build/gitignore-${id}.gen")
	}

	@Override
	ProjectRequest createProjectRequest(String... styles) {
		def request = super.createProjectRequest(styles)
		request.parameters['user-agent'] = this.userAgent
		request
	}

}
