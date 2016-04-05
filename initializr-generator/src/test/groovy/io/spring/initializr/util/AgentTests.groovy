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

package io.spring.initializr.util

import org.junit.Test

import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.CoreMatchers.nullValue
import static org.junit.Assert.assertThat

/**
 * Tests for {@link Agent}.
 *
 * @author Stephane Nicoll
 */
class AgentTests {

	@Test
	void checkCurl() {
		Agent agent = Agent.fromUserAgent('curl/1.2.4')
		assertThat(agent.id, equalTo(Agent.AgentId.CURL))
		assertThat(agent.version, equalTo('1.2.4'))
	}

	@Test
	void checkHttpie() {
		Agent agent = Agent.fromUserAgent('HTTPie/0.8.0')
		assertThat(agent.id, equalTo(Agent.AgentId.HTTPIE))
		assertThat(agent.version, equalTo('0.8.0'))
	}

	@Test
	void checkSpringBootCli() {
		Agent agent = Agent.fromUserAgent('SpringBootCli/1.3.1.RELEASE')
		assertThat(agent.id, equalTo(Agent.AgentId.SPRING_BOOT_CLI))
		assertThat(agent.version, equalTo('1.3.1.RELEASE'))
	}

	@Test
	void checkSts() {
		Agent agent = Agent.fromUserAgent('STS 3.7.0.201506251244-RELEASE')
		assertThat(agent.id, equalTo(Agent.AgentId.STS))
		assertThat(agent.version, equalTo('3.7.0.201506251244-RELEASE'))
	}

	@Test
	void checkIntelliJIDEA() {
		Agent agent = Agent.fromUserAgent('IntelliJ IDEA')
		assertThat(agent.id, equalTo(Agent.AgentId.INTELLIJ_IDEA))
		assertThat(agent.version, is(nullValue()))
	}

	@Test
	void checkIntelliJIDEAWithVersion() {
		Agent agent = Agent.fromUserAgent('IntelliJ IDEA/144.2 (Community edition; en-us)')
		assertThat(agent.id, equalTo(Agent.AgentId.INTELLIJ_IDEA))
		assertThat(agent.version, is('144.2'))
	}

	@Test
	void checkGenericBrowser() {
		Agent agent = Agent.fromUserAgent(
				'Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5 Build/MMB29K) ')
		assertThat(agent.id, equalTo(Agent.AgentId.BROWSER))
		assertThat(agent.version, is(nullValue()))
	}

	@Test
	void checkRobot() {
		Agent agent = Agent.fromUserAgent('Googlebot-Mobile')
		assertThat(agent, is(nullValue()))
	}

}
