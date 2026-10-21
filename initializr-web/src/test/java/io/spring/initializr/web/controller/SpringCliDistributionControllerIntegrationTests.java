/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SpringCliDistributionController}.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
class SpringCliDistributionControllerIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void downloadCli() throws Exception {
		assertSpringCliRedirect("/spring", "zip");
	}

	@Test
	void downloadCliAsZip() throws Exception {
		assertSpringCliRedirect("/spring.zip", "zip");
	}

	@Test
	void downloadCliAsTarGz() throws Exception {
		assertSpringCliRedirect("/spring.tar.gz", "tar.gz");
	}

	@Test
	void downloadCliAsTgz() throws Exception {
		assertSpringCliRedirect("/spring.tgz", "tar.gz");
	}

	@Test
	void installer() {
		assertThat(this.mvc.get().uri("/install.sh")).hasStatusOk().bodyText().isNotEmpty();
	}

	private void assertSpringCliRedirect(String context, String extension) {
		String expected = "https://repo.spring.io/release/org/springframework/boot/spring-boot-cli/2.4.4/spring-boot-cli-2.4.4-bin."
				+ extension;
		assertThat(this.mvc.get().uri(context)).hasStatus(HttpStatus.FOUND).hasRedirectedUrl(expected);
	}

}
