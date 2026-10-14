/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import io.spring.initializr.metadata.InitializrMetadataProvider;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

	/**
	 * Redirect to the zip distribution of the Spring CLI.
	 * @return the redirect
	 */
	@GetMapping(path = { "/spring", "/spring.zip" })
	public String spring() {
		String url = this.metadataProvider.get().createCliDistributionURl("zip");
		return "redirect:" + url;
	}

	/**
	 * Redirect to the tgz distribution of the Spring CLI.
	 * @return the redirect
	 */
	@GetMapping(path = { "/spring.tar.gz", "spring.tgz" })
	public String springTgz() {
		String url = this.metadataProvider.get().createCliDistributionURl("tar.gz");
		return "redirect:" + url;
	}

}
