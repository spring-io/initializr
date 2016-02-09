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
 * @author Stephane Nicoll
 */
class UserAgentWrapperTests {

	@Test
	void checkCurl() {
		UserAgentWrapper userAgent = new UserAgentWrapper('curl/1.2.4')
		assertThat(userAgent.isCurl(), is(true))
		assertThat(userAgent.isHttpie(), is(false))
		assertThat(userAgent.isSpringBootCli(), is(false))
		def information = userAgent.extractAgentInformation()
		assertThat(information.id, equalTo(UserAgentWrapper.AgentId.CURL))
		assertThat(information.version, equalTo('1.2.4'))
	}

	@Test
	void checkHttpie() {
		UserAgentWrapper userAgent = new UserAgentWrapper('HTTPie/0.8.0')
		assertThat(userAgent.isCurl(), is(false))
		assertThat(userAgent.isHttpie(), is(true))
		assertThat(userAgent.isSpringBootCli(), is(false))
		def information = userAgent.extractAgentInformation()
		assertThat(information.id, equalTo(UserAgentWrapper.AgentId.HTTPIE))
		assertThat(information.version, equalTo('0.8.0'))
	}

	@Test
	void checkSpringBootCli() {
		UserAgentWrapper userAgent = new UserAgentWrapper('SpringBootCli/1.3.1.RELEASE')
		assertThat(userAgent.isCurl(), is(false))
		assertThat(userAgent.isHttpie(), is(false))
		assertThat(userAgent.isSpringBootCli(), is(true))
		def information = userAgent.extractAgentInformation()
		assertThat(information.id, equalTo(UserAgentWrapper.AgentId.SPRING_BOOT_CLI))
		assertThat(information.version, equalTo('1.3.1.RELEASE'))
	}

	@Test
	void checkSts() {
		UserAgentWrapper userAgent = new UserAgentWrapper('STS 3.7.0.201506251244-RELEASE')
		assertThat(userAgent.isCurl(), is(false))
		assertThat(userAgent.isHttpie(), is(false))
		assertThat(userAgent.isSpringBootCli(), is(false))
		def information = userAgent.extractAgentInformation()
		assertThat(information.id, equalTo(UserAgentWrapper.AgentId.STS))
		assertThat(information.version, equalTo('3.7.0.201506251244-RELEASE'))
	}

	@Test
	void checkIntelliJIDEA() {
		UserAgentWrapper userAgent = new UserAgentWrapper('IntelliJ IDEA')
		assertThat(userAgent.isCurl(), is(false))
		assertThat(userAgent.isHttpie(), is(false))
		assertThat(userAgent.isSpringBootCli(), is(false))
		def information = userAgent.extractAgentInformation()
		assertThat(information.id, equalTo(UserAgentWrapper.AgentId.INTELLIJ_IDEA))
		assertThat(information.version, is(nullValue()))
	}

	@Test
	void checkIntelliJIDEAWithVersion() {
		UserAgentWrapper userAgent = new UserAgentWrapper('IntelliJ IDEA/144.2 (Community edition; en-us)')
		assertThat(userAgent.isCurl(), is(false))
		assertThat(userAgent.isHttpie(), is(false))
		assertThat(userAgent.isSpringBootCli(), is(false))
		def information = userAgent.extractAgentInformation()
		assertThat(information.id, equalTo(UserAgentWrapper.AgentId.INTELLIJ_IDEA))
		assertThat(information.version, is('144.2'))
	}

	@Test
	void checkGenericBrowser() {
		UserAgentWrapper userAgent =
				new UserAgentWrapper('Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5 Build/MMB29K) ')
		assertThat(userAgent.isCurl(), is(false))
		assertThat(userAgent.isHttpie(), is(false))
		assertThat(userAgent.isSpringBootCli(), is(false))
		def information = userAgent.extractAgentInformation()
		assertThat(information.id, equalTo(UserAgentWrapper.AgentId.BROWSER))
		assertThat(information.version, is(nullValue()))
	}

	@Test
	void checkRobot() {
		UserAgentWrapper userAgent =
				new UserAgentWrapper('Googlebot-Mobile')
		assertThat(userAgent.isCurl(), is(false))
		assertThat(userAgent.isHttpie(), is(false))
		assertThat(userAgent.isSpringBootCli(), is(false))
		def information = userAgent.extractAgentInformation()
		assertThat(information, is(nullValue()))
	}

}
