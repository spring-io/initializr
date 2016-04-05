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

/**
 * Defines the agent that submitted a request.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class Agent {

	/**
	 * The {@link AgentId}.
	 */
	final AgentId id

	/**
	 * The version of the agent, if any
	 */
	final String version

	Agent(AgentId id, String version) {
		this.id = id
		this.version = version
	}

	/**
	 * Create an {@link Agent} based on the specified {@code User-Agent} header.
	 * @param userAgent the user agent
	 * @return an {@link Agent} instance or {@code null}
	 */
	static Agent fromUserAgent(String userAgent) {
		UserAgentHandler.parse(userAgent)
	}

	/**
	 * Defines the various known agents.
	 */
	static enum AgentId {

		CURL('curl', 'curl'),

		HTTPIE('httpie', 'HTTPie'),

		SPRING_BOOT_CLI('spring', 'SpringBootCli'),

		STS('sts', 'STS'),

		INTELLIJ_IDEA('intellijidea', 'IntelliJ IDEA'),

		BROWSER('browser', 'Browser')

		final String id
		final String name

		private AgentId(String id, String name) {
			this.id = id
			this.name = name
		}
	}

	private static class UserAgentHandler {

		private static final TOOL_REGEX = '([^\\/]*)\\/([^ ]*).*'

		private static final STS_REGEX = 'STS (.*)'

		static Agent parse(String userAgent) {
			def matcher = (userAgent =~ TOOL_REGEX)
			if (matcher.matches()) {
				String name = matcher.group(1)
				for (AgentId id : AgentId.values()) {
					if (name.equals(id.name)) {
						String version = matcher.group(2)
						return new Agent(id, version)
					}
				}
			}
			matcher = userAgent =~ STS_REGEX
			if (matcher.matches()) {
				return new Agent(AgentId.STS, matcher.group(1))
			}
			if (userAgent.equals(AgentId.INTELLIJ_IDEA.name)) {
				return new Agent(AgentId.INTELLIJ_IDEA, null)
			}
			if (userAgent.contains('Mozilla/5.0')) { // Super heuristics
				return new Agent(AgentId.BROWSER, null)
			}
			return null
		}

	}

}
