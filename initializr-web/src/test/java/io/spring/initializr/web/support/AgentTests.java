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

package io.spring.initializr.web.support;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Agent}.
 *
 * @author Stephane Nicoll
 */
class AgentTests {

	@Test
	void checkCurl() {
		Agent agent = Agent.fromUserAgent("curl/1.2.4");
		assertThat(agent.getId()).isEqualTo(Agent.AgentId.CURL);
		assertThat(agent.getVersion()).isEqualTo("1.2.4");
	}

	@Test
	void checkHttpie() {
		Agent agent = Agent.fromUserAgent("HTTPie/0.8.0");
		assertThat(agent.getId()).isEqualTo(Agent.AgentId.HTTPIE);
		assertThat(agent.getVersion()).isEqualTo("0.8.0");
	}

	@Test
	void checkJBossForge() {
		Agent agent = Agent.fromUserAgent("SpringBootForgeCli/1.0.0.Alpha4");
		assertThat(agent.getId()).isEqualTo(Agent.AgentId.JBOSS_FORGE);
		assertThat(agent.getVersion()).isEqualTo("1.0.0.Alpha4");
	}

	@Test
	void checkSpringBootCli() {
		Agent agent = Agent.fromUserAgent("SpringBootCli/1.3.1.RELEASE");
		assertThat(agent.getId()).isEqualTo(Agent.AgentId.SPRING_BOOT_CLI);
		assertThat(agent.getVersion()).isEqualTo("1.3.1.RELEASE");
	}

	@Test
	void checkSts() {
		Agent agent = Agent.fromUserAgent("STS 3.7.0.201506251244-RELEASE");
		assertThat(agent.getId()).isEqualTo(Agent.AgentId.STS);
		assertThat(agent.getVersion()).isEqualTo("3.7.0.201506251244-RELEASE");
	}

	@Test
	void checkIntelliJIDEA() {
		Agent agent = Agent.fromUserAgent("IntelliJ IDEA");
		assertThat(agent.getId()).isEqualTo(Agent.AgentId.INTELLIJ_IDEA);
		assertThat(agent.getVersion()).isNull();
	}

	@Test
	void checkIntelliJIDEAWithVersion() {
		Agent agent = Agent
				.fromUserAgent("IntelliJ IDEA/144.2 (Community edition; en-us)");
		assertThat(agent.getId()).isEqualTo(Agent.AgentId.INTELLIJ_IDEA);
		assertThat(agent.getVersion()).isEqualTo("144.2");
	}

	@Test
	void checkNetBeans() {
		Agent agent = Agent.fromUserAgent("nb-springboot-plugin/0.1");
		assertThat(agent.getId()).isEqualTo(Agent.AgentId.NETBEANS);
		assertThat(agent.getVersion()).isEqualTo("0.1");
	}

	@Test
	void checkVsCode() {
		Agent agent = Agent.fromUserAgent("vscode/0.2.0");
		assertThat(agent.getId()).isEqualTo(Agent.AgentId.VSCODE);
		assertThat(agent.getVersion()).isEqualTo("0.2.0");
	}

	@Test
	void checkJenkinsX() {
		Agent agent = Agent.fromUserAgent("jx/1.1.71");
		assertThat(agent.getId()).isEqualTo(Agent.AgentId.JENKINSX);
		assertThat(agent.getVersion()).isEqualTo("1.1.71");
	}

	@Test
	void checkGenericBrowser() {
		Agent agent = Agent.fromUserAgent(
				"Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5 Build/MMB29K) ");
		assertThat(agent.getId()).isEqualTo(Agent.AgentId.BROWSER);
		assertThat(agent.getVersion()).isNull();
	}

	@Test
	void checkRobot() {
		Agent agent = Agent.fromUserAgent("Googlebot-Mobile");
		assertThat(agent).isNull();
	}

}
