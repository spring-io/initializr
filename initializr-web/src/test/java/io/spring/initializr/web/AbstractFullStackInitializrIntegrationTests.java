/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web;

import io.spring.initializr.web.AbstractInitializrIntegrationTests.Config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * @author Stephane Nicoll
 * @author Dave Syer
 */
@SpringBootTest(classes = Config.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractFullStackInitializrIntegrationTests extends AbstractInitializrIntegrationTests {

	@LocalServerPort
	protected int port;

	protected String host = "localhost";

	@Override
	protected String createUrl(String context) {
		return "http://" + this.host + ":" + this.port + (context.startsWith("/") ? context : "/" + context);
	}

}
