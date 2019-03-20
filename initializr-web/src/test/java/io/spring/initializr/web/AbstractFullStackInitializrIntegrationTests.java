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

package io.spring.initializr.web;

import io.spring.initializr.web.AbstractInitializrIntegrationTests.Config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

/**
 * @author Stephane Nicoll
 * @author Dave Syer
 */
@SpringBootTest(classes = Config.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractFullStackInitializrIntegrationTests
		extends AbstractInitializrIntegrationTests {

	@LocalServerPort
	protected int port;

	protected String host = "localhost";

	@Override
	protected String createUrl(String context) {
		return "http://" + this.host + ":" + this.port
				+ (context.startsWith("/") ? context : "/" + context);
	}

}
