/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.controller;

import java.nio.charset.StandardCharsets;

import io.spring.initializr.metadata.InitializrConfiguration.Env;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import org.jspecify.annotations.Nullable;

import org.springframework.util.DigestUtils;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * A base controller that uses a {@link InitializrMetadataProvider}.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public abstract class AbstractMetadataController {

	protected final InitializrMetadataProvider metadataProvider;

	private final SingletonSupplier<Boolean> forceSsl;

	private final SingletonSupplier<@Nullable String> serviceUrl;

	protected AbstractMetadataController(InitializrMetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider;
		this.forceSsl = SingletonSupplier.of(() -> getEnv().isForceSsl());
		this.serviceUrl = SingletonSupplier.of(() -> getEnv().getServiceUrl());
	}

	/**
	 * Generate a full URL of the service, mostly for use in templates. Uses the
	 * configured service URL if set, the current request otherwise.
	 * @return the app URL
	 * @see io.spring.initializr.metadata.InitializrConfiguration.Env#getServiceUrl()
	 * @see io.spring.initializr.metadata.InitializrConfiguration.Env#isForceSsl()
	 */
	protected String generateAppUrl() {
		String serviceUrl = this.serviceUrl.get();
		if (serviceUrl != null) {
			return serviceUrl;
		}
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
		if (this.forceSsl.obtain()) {
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

	private Env getEnv() {
		return this.metadataProvider.get().getConfiguration().getEnv();
	}

}
