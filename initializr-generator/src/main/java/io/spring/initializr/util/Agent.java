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

package io.spring.initializr.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines the agent that submitted a request.
 *
 * @author Stephane Nicoll
 */
public class Agent {

	/**
	 * The {@link AgentId}.
	 */
	private final AgentId id;

	/**
	 * The version of the agent, if any.
	 */
	private final String version;

	public Agent(AgentId id, String version) {
		this.id = id;
		this.version = version;
	}

	public AgentId getId() {
		return this.id;
	}

	public String getVersion() {
		return this.version;
	}

	/**
	 * Create an {@link Agent} based on the specified {@code User-Agent} header.
	 * @param userAgent the user agent
	 * @return an {@link Agent} instance or {@code null}
	 */
	public static Agent fromUserAgent(String userAgent) {
		return UserAgentHandler.parse(userAgent);
	}

	/**
	 * Defines the various known agents.
	 */
	public enum AgentId {

		/**
		 * CURL.
		 */
		CURL("curl", "curl"),

		/**
		 * HTTPie.
		 */
		HTTPIE("httpie", "HTTPie"),

		/**
		 * JBoss Forge.
		 */
		JBOSS_FORGE("jbossforge", "SpringBootForgeCli"),

		/**
		 * The Spring Boot CLI.
		 */
		SPRING_BOOT_CLI("spring", "SpringBootCli"),

		/**
		 * Spring Tools Suite.
		 */
		STS("sts", "STS"),

		/**
		 * IntelliJ IDEA.
		 */
		INTELLIJ_IDEA("intellijidea", "IntelliJ IDEA"),

		/**
		 * Netbeans.
		 */
		NETBEANS("netbeans", "NetBeans"),

		/**
		 * Visual Studio Code.
		 */
		VSCODE("vscode", "vscode"),

		/**
		 * Jenkins X.
		 */
		JENKINSX("jenkinsx", "jx"),

		/**
		 * A generic browser.
		 */
		BROWSER("browser", "Browser");

		final String id;

		final String name;

		public String getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		AgentId(String id, String name) {
			this.id = id;
			this.name = name;
		}

	}

	private static class UserAgentHandler {

		private static final Pattern TOOL_REGEX = Pattern
				.compile("([^\\/]*)\\/([^ ]*).*");

		private static final Pattern STS_REGEX = Pattern.compile("STS (.*)");

		private static final Pattern NETBEANS_REGEX = Pattern
				.compile("nb-springboot-plugin\\/(.*)");

		public static Agent parse(String userAgent) {
			Matcher matcher = TOOL_REGEX.matcher(userAgent);
			if (matcher.matches()) {
				String name = matcher.group(1);
				for (AgentId id : AgentId.values()) {
					if (name.equals(id.name)) {
						String version = matcher.group(2);
						return new Agent(id, version);
					}
				}
			}
			matcher = STS_REGEX.matcher(userAgent);
			if (matcher.matches()) {
				return new Agent(AgentId.STS, matcher.group(1));
			}
			matcher = NETBEANS_REGEX.matcher(userAgent);
			if (matcher.matches()) {
				return new Agent(AgentId.NETBEANS, matcher.group(1));
			}

			if (userAgent.equals(AgentId.INTELLIJ_IDEA.name)) {
				return new Agent(AgentId.INTELLIJ_IDEA, null);
			}
			if (userAgent.contains("Mozilla/5.0")) { // Super heuristics
				return new Agent(AgentId.BROWSER, null);
			}
			return null;
		}

	}

}
