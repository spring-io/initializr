/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import java.nio.charset.StandardCharsets;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import org.jspecify.annotations.Nullable;

import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * A base controller that uses a {@link InitializrMetadataProvider}.
 *
 * @author Stephane Nicoll
 */
public abstract class AbstractMetadataController {

	protected final InitializrMetadataProvider metadataProvider;

	private @Nullable Boolean forceSsl;

	protected AbstractMetadataController(InitializrMetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider;
	}

	/**
	 * Generate a full URL of the service, mostly for use in templates.
	 * @return the app URL
	 * @see io.spring.initializr.metadata.InitializrConfiguration.Env#isForceSsl()
	 */
	protected String generateAppUrl() {
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
		if (isForceSsl()) {
			builder.scheme("https");
		}
		return builder.build().toString();
	}

	/**
	 * Create a unique ID for the given content, used as ETag.
	 * @param content the content
	 * @return the unique ID
	 */
	protected String createUniqueId(String content) {
		StringBuilder builder = new StringBuilder();
		DigestUtils.appendMd5DigestAsHex(content.getBytes(StandardCharsets.UTF_8), builder);
		return builder.toString();
	}

	private boolean isForceSsl() {
		if (this.forceSsl == null) {
			this.forceSsl = this.metadataProvider.get().getConfiguration().getEnv().isForceSsl();
		}
		return this.forceSsl;
	}

}
