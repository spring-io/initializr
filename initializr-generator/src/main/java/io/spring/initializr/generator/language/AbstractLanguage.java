/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language;

import org.jspecify.annotations.Nullable;

/**
 * Base {@link Language} implementation.
 *
 * @author Stephane Nicoll
 */
public abstract class AbstractLanguage implements Language {

	private final String id;

	private final String jvmVersion;

	private final String sourceFileExtension;

	/**
	 * Creates a new instance.
	 * @param id the id
	 * @param jvmVersion the JVM version
	 * @param sourceFileExtension the source file extension
	 */
	protected AbstractLanguage(String id, @Nullable String jvmVersion, String sourceFileExtension) {
		this.id = id;
		this.jvmVersion = (jvmVersion != null) ? jvmVersion : DEFAULT_JVM_VERSION;
		this.sourceFileExtension = sourceFileExtension;
	}

	@Override
	public String id() {
		return this.id;
	}

	@Override
	public String jvmVersion() {
		return this.jvmVersion;
	}

	@Override
	public String sourceFileExtension() {
		return this.sourceFileExtension;
	}

	@Override
	public String toString() {
		return id();
	}

}
