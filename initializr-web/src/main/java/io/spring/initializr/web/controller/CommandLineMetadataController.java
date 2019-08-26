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

package io.spring.initializr.web.controller;

import java.io.IOException;

import io.spring.initializr.generator.io.template.TemplateRenderer;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.support.Agent;
import io.spring.initializr.web.support.Agent.AgentId;
import io.spring.initializr.web.support.CommandLineHelpGenerator;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * {@link Controller} that handles assistance for CLI support using a
 * {@link CommandLineHelpGenerator}.
 *
 * @author Stephane Nicoll
 */
@Controller
public class CommandLineMetadataController extends AbstractMetadataController {

	private final CommandLineHelpGenerator commandLineHelpGenerator;

	public CommandLineMetadataController(InitializrMetadataProvider metadataProvider,
			TemplateRenderer templateRenderer) {
		super(metadataProvider);
		this.commandLineHelpGenerator = new CommandLineHelpGenerator(templateRenderer);
	}

	@RequestMapping(path = "/", produces = "text/plain")
	public ResponseEntity<String> serviceCapabilitiesText(
			@RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent) throws IOException {
		String appUrl = generateAppUrl();
		InitializrMetadata metadata = this.metadataProvider.get();

		BodyBuilder builder = ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN);
		if (userAgent != null) {
			Agent agent = Agent.fromUserAgent(userAgent);
			if (agent != null) {
				if (AgentId.CURL.equals(agent.getId())) {
					String content = this.commandLineHelpGenerator.generateCurlCapabilities(metadata, appUrl);
					return builder.eTag(createUniqueId(content)).body(content);
				}
				if (AgentId.HTTPIE.equals(agent.getId())) {
					String content = this.commandLineHelpGenerator.generateHttpieCapabilities(metadata, appUrl);
					return builder.eTag(createUniqueId(content)).body(content);
				}
				if (AgentId.SPRING_BOOT_CLI.equals(agent.getId())) {
					String content = this.commandLineHelpGenerator.generateSpringBootCliCapabilities(metadata, appUrl);
					return builder.eTag(createUniqueId(content)).body(content);
				}
			}
		}
		String content = this.commandLineHelpGenerator.generateGenericCapabilities(metadata, appUrl);
		return builder.eTag(createUniqueId(content)).body(content);
	}

}
