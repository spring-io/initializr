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

import org.springframework.util.Assert

/**
 * Wraps a user agent header.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class UserAgentWrapper {

	private static final TOOL_REGEX = '([^\\/]*)\\/([^ ]*).*'

	private static final STS_REGEX = 'STS (.*)'

	private final String userAgent

	UserAgentWrapper(String userAgent) {
		Assert.notNull(userAgent, "UserAgent must not be null")
		this.userAgent = userAgent.trim()
	}

	boolean isSpringBootCli() {
		return userAgent.startsWith(AgentId.SPRING_BOOT_CLI.name)
	}

	boolean isCurl() {
		return userAgent.startsWith(AgentId.CURL.name)
	}

	boolean isHttpie() {
		return userAgent.startsWith(AgentId.HTTPIE.name)
	}

	/**
	 * Detect the identifier of the agent and its version. Return {@code null} if the
	 * user agent managed by this instance is not recognized.
	 */
	AgentInformation extractAgentInformation() {
		def matcher = (userAgent =~ TOOL_REGEX)
		if (matcher.matches()) {
			String name = matcher.group(1)
			for (AgentId id : AgentId.values()) {
				if (name.equals(id.name)) {
					String version = matcher.group(2)
					return new AgentInformation(id, version)
				}
			}
		}
		matcher = userAgent =~ STS_REGEX
		if (matcher.matches()) {
			return new AgentInformation(AgentId.STS, matcher.group(1))
		}
		if (userAgent.equals(AgentId.INTELLIJ_IDEA.name)) {
			return new AgentInformation(AgentId.INTELLIJ_IDEA, null)
		}
		if (userAgent.contains('Mozilla/5.0')) { // Super heuristics
			return new AgentInformation(AgentId.BROWSER, null)
		}
		return null
	}

	static class AgentInformation {
		final AgentId id
		final String version

		AgentInformation(AgentId id, String version) {
			this.id = id
			this.version = version
		}
	}

	static enum AgentId {

		CURL('curl', 'curl'),

		HTTPIE('httpie', 'HTTPie'),

		SPRING_BOOT_CLI('spring', 'SpringBootCli'),

		STS('sts', 'STS'),

		INTELLIJ_IDEA('intellijidea', 'IntelliJ IDEA'),

		BROWSER('browser', 'Browser')

		final String id
		final String name

		AgentId(String id, String name) {
			this.id = id
			this.name = name
		}
	}

}
