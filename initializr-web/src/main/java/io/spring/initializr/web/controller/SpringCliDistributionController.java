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

import io.spring.initializr.metadata.InitializrMetadataProvider;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * {@link Controller} that provides access to the Spring CLI.
 *
 * @author Stephane Nicoll
 */
@Controller
public class SpringCliDistributionController {

	private final InitializrMetadataProvider metadataProvider;

	public SpringCliDistributionController(InitializrMetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider;
	}

	@RequestMapping(path = { "/spring", "/spring.zip" })
	public String spring() {
		String url = this.metadataProvider.get().createCliDistributionURl("zip");
		return "redirect:" + url;
	}

	@RequestMapping(path = { "/spring.tar.gz", "spring.tgz" })
	public String springTgz() {
		String url = this.metadataProvider.get().createCliDistributionURl("tar.gz");
		return "redirect:" + url;
	}

}
